package com.basic.compress;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 文件树浏览器
 *
 * @author Gersy
 * @date 2023/12/6
 * @since 1.0
 */
public abstract class FileTreeWalker<E> implements Closeable {

    private static final Logger log = LoggerFactory.getLogger(FileTreeWalker.class);

    protected File file;
    protected FileEntry<E> lastDirectoryEntry = null;
    protected int prefixLength;

    public FileTreeWalker(File file) {
        this.file = file;
    }

    public Event<E> readNext() {
        try {
            FileEntry<E> fileEntry = getFileEntry();
            if (fileEntry == null) {
                return null;
            }
            E entry = fileEntry.getEntry();
            BasicFileAttributes attributes = getAttributes(entry);
            if (attributes.isDirectory()) {
                //TODO 待实现END_DIRECTORY功能
//                if (lastDirectoryEntry != null) {
//                    if (fileEntry.getPath().startsWith(lastDirectoryEntry.getPath()) {
//                        return new Event<>(EventType.END_DIRECTORY, fileEntry, attributes);
//                    }
//                }
                return new Event<>(EventType.START_DIRECTORY, fileEntry, attributes);
            }
            return new Event<>(EventType.ENTRY, fileEntry, attributes);
        } catch (IOException e) {
            log.error("压缩包读取异常：", e);
            return new Event<>(EventType.ENTRY, new FileEntry<>(file.getAbsolutePath()), e);
        }
    }

    protected String getEntryName(File file) {
        return file.getAbsolutePath().substring(prefixLength);
    }

    public abstract void writeNext(File file) throws IOException;

    public void compress(File beCompressFile, FileVisitor<Path> visitor) throws IOException {
        compressFiles(beCompressFile, visitor);
        close();
    }

    protected void compressFiles(File beCompressFile, FileVisitor<Path> visitor) throws IOException {
        if (beCompressFile.isFile()) {
            writeNext(beCompressFile);
        } else {
            compressDir(beCompressFile, visitor);
        }
    }

    protected void compressDir(File beCompressFile, FileVisitor<Path> visitor) throws IOException {
        prefixLength = beCompressFile.getAbsolutePath().length() + 1;
        String filePath = file.getAbsolutePath();
        Files.walkFileTree(beCompressFile.toPath(), getFileVisitor(visitor, filePath));
    }

    private SimpleFileVisitor<Path> getFileVisitor(FileVisitor<Path> visitor, String filePath) {
        SimpleFileVisitor<Path> simpleFileVisitor;
        if (visitor == null) {
            simpleFileVisitor = new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    String pathStr = path.toString();
                    if (pathStr.equals(filePath)) {
                        return FileVisitResult.CONTINUE;
                    }
                    writeNext(path.toFile());
                    return super.visitFile(path, attrs);
                }
            };
        } else {
            simpleFileVisitor = new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return visitor.preVisitDirectory(dir, attrs);
                }

                @Override
                public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
                    String pathStr = path.toString();
                    if (pathStr.equals(filePath)) {
                        return FileVisitResult.CONTINUE;
                    }
                    FileVisitResult result = visitor.visitFile(path, attrs);
                    if (result == FileVisitResult.CONTINUE) {
                        writeNext(path.toFile());
                    }
                    return result;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return visitor.visitFileFailed(file, exc);
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return visitor.postVisitDirectory(dir, exc);
                }
            };
        }
        return simpleFileVisitor;
    }

    protected void writeStream(File file, OutputStream outputStream) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8 * 1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                outputStream.write(buffer, 0, length);
            }
        }
    }

    public void uncompress(FileVisitor<FileEntry<?>> visitor) throws IOException {
        String skipPath = null;
        Event<?> event = readNext();
        while (event != null) {
            FileTreeWalker.EventType type = event.type();
            IOException ioException = event.ioeException();
            FileVisitResult result;
            if (ioException != null) {
                result = visitor.visitFileFailed(null, ioException);
            } else {
                FileEntry<?> entry = event.entry();
                String path = entry.getPath();
                if (skipPath != null && path.startsWith(skipPath)) {
                    event = readNext();
                    continue;
                }
                skipPath = null;
                if (type == FileTreeWalker.EventType.ENTRY) {
                    result = visitor.visitFile(entry, event.attributes());
                } else if (type == FileTreeWalker.EventType.START_DIRECTORY) {
                    result = visitor.preVisitDirectory(entry, event.attributes());
                    if (result == FileVisitResult.SKIP_SUBTREE ||
                            result == FileVisitResult.SKIP_SIBLINGS) {
                        skipPath = path;
                    }
                } else {
                    result = visitor.postVisitDirectory(entry, ioException);
                }
            }
            if (result == FileVisitResult.TERMINATE) {
                break;
            }
            event = readNext();
        }
        close();
    }

    protected abstract BasicFileAttributes getAttributes(E entry);

    protected abstract FileEntry<E> getFileEntry() throws IOException;

//    Event walk(Path file) {
//        if (closed)
//            throw new IllegalStateException("Closed");
//
//        Event ev = visit(file,
//                false,   // ignoreSecurityException
//                false);  // canUseCached
//        assert ev != null;
//        return ev;
//    }

    /**
     * The event types.
     */
    public static enum EventType {
        /**
         * Start of a directory
         */
        START_DIRECTORY,
        /**
         * End of a directory
         */
        END_DIRECTORY,
        /**
         * An entry in a directory
         */
        ENTRY;
    }

    public static class Event<T> {
        private final EventType type;
        private final FileEntry<T> entry;

        private final BasicFileAttributes attrs;
        private final IOException ioe;

        public Event(EventType type, FileEntry<T> entry, BasicFileAttributes attrs, IOException ioe) {
            this.type = type;
            this.entry = entry;
            this.attrs = attrs;
            this.ioe = ioe;
        }

        public Event(EventType type, FileEntry<T> entry, BasicFileAttributes attrs) {
            this(type, entry, attrs, null);
        }

        public Event(EventType type, FileEntry<T> entry, IOException ioe) {
            this(type, entry, null, ioe);
        }

        public Event(EventType type, IOException ioe) {
            this(type, null, null, ioe);
        }

        EventType type() {
            return type;
        }

        FileEntry<T> entry() {
            return entry;
        }

        BasicFileAttributes attributes() {
            return attrs;
        }

        IOException ioeException() {
            return ioe;
        }
    }

    public abstract void close() throws IOException;

}
