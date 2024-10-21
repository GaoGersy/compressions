package com.basic.compress.sevenz;

import com.basic.compress.FileEntry;
import com.basic.compress.FileTreeWalker;
import com.basic.compress.UnsupportedFileTypeException;

import net.sf.sevenzipjbinding.ArchiveFormat;
import net.sf.sevenzipjbinding.IInArchive;
import net.sf.sevenzipjbinding.IOutCreateArchive;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZip;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchive;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

import org.apache.commons.compress.utils.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gersy
 * @date 2024/1/9
 * @since 1.0
 */
public class SevenZipBindingFileTreeWalker extends FileTreeWalker<ISimpleInArchiveItem> {

    private String password;
    private IOutCreateArchive<IOutItemAllFormats> outArchive;
    private RandomAccessFileOutStream fileOutStream;
    private final ArchiveFormat archiveFormat;
    private IInArchive inArchive;
    private int index;

    private RandomAccessFileInStream inStream;
    private ISimpleInArchive simpleInArchive;

    private List<Item> items;

    public SevenZipBindingFileTreeWalker(File file, String format) throws UnsupportedFileTypeException {
        super(file);
        this.archiveFormat = getFormat(format);
    }

    private ArchiveFormat getFormat(String format) throws UnsupportedFileTypeException {
        format = format.toUpperCase();
        for (ArchiveFormat value : ArchiveFormat.values()) {
            if (value.name().equals(format)) {
                return value;
            }
        }
        throw new UnsupportedFileTypeException("不支持的压缩文件类型：" + format);
    }

    public SevenZipBindingFileTreeWalker(File file, ArchiveFormat archiveFormat) {
        super(file);
        this.archiveFormat = archiveFormat;
    }

    @Override
    public void writeNext(File file) throws IOException {
        items.add(new Item(getEntryName(file), file));
    }

    @Override
    public void compress(File beCompressFile, FileVisitor<Path> visitor) throws IOException {
        fileOutStream = new RandomAccessFileOutStream(new RandomAccessFile(file, "rw"));
        outArchive = SevenZip.openOutArchive(archiveFormat);
        items = new ArrayList<>();
        super.compressFiles(beCompressFile, visitor);
        outArchive.createArchive(fileOutStream, items.size(), new CompressCallback(items));
        close();
    }

    @Override
    public void uncompress(FileVisitor<FileEntry<?>> visitor) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
        inStream = new RandomAccessFileInStream(randomAccessFile);
        // 自动模式
        if (password != null) {
            inArchive = SevenZip.openInArchive(null, inStream, password);
        } else {
            inArchive = SevenZip.openInArchive(null, inStream);
        }
        simpleInArchive = inArchive.getSimpleInterface();
        super.uncompress(visitor);
    }

    @Override
    protected BasicFileAttributes getAttributes(ISimpleInArchiveItem entry) {
        return new BasicFileAttributes() {
            @Override
            public FileTime lastModifiedTime() {
                try {
                    return FileTime.fromMillis(entry.getLastWriteTime().getTime());
                } catch (SevenZipException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public FileTime lastAccessTime() {
                try {
                    return FileTime.fromMillis(entry.getLastAccessTime().getTime());
                } catch (SevenZipException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public FileTime creationTime() {
                try {
                    return FileTime.fromMillis(entry.getCreationTime().getTime());
                } catch (SevenZipException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean isRegularFile() {
                try {
                    return !entry.isFolder();
                } catch (SevenZipException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean isDirectory() {
                try {
                    return entry.isFolder();
                } catch (SevenZipException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public boolean isSymbolicLink() {
                return false;
            }

            @Override
            public boolean isOther() {
                return false;
            }

            @Override
            public long size() {
                try {
                    return entry.getSize();
                } catch (SevenZipException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public Object fileKey() {
                return entry;
            }

        };
    }

    @Override
    protected FileEntry<ISimpleInArchiveItem> getFileEntry() throws IOException {
        if (index >= simpleInArchive.getNumberOfItems()) {
            return null;
        }
        ISimpleInArchiveItem entry = simpleInArchive.getArchiveItem(index++);
        return new SevenZipFileEntry(entry, entry.getPath());
    }

    private void closeItemStreams() {
        if (items != null) {
            for (Item item : items) {
                ISequentialInStream itemInStream = item.getInStream();
                if (itemInStream != null) {
                    IOUtils.closeQuietly(itemInStream);
                }
            }
        }
    }

    @Override
    public void close() throws IOException {
        closeItemStreams();
        IOUtils.closeQuietly(outArchive);
        IOUtils.closeQuietly(inArchive);
        if (inStream != null) {
            IOUtils.closeQuietly(inStream);
        }
        if (fileOutStream != null) {
            fileOutStream.close();
        }
    }
}
