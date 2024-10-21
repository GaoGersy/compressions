package com.basic.compress.normal;

import com.basic.compress.FileEntry;
import com.basic.compress.FileTreeWalker;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.sevenz.SevenZOutputFile;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

/**
 * @author Gersy
 * @date 2023/12/6
 * @since 1.0
 */
public class SevenZFileTreeWalker extends FileTreeWalker<SevenZArchiveEntry> {
    private SevenZFile sevenZFile;
    private SevenZOutputFile sevenZOutputFile;

    public SevenZFileTreeWalker(File file) {
        super(file);
    }

    @Override
    public void writeNext(File file) throws IOException {
        SevenZArchiveEntry archiveEntry = new SevenZArchiveEntry();
        sevenZOutputFile.putArchiveEntry(archiveEntry);
        writeStream(file,sevenZOutputFile);
        sevenZOutputFile.closeArchiveEntry();
    }

    protected void writeStream(File file, SevenZOutputFile sevenZOutputFile) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8 * 1024];
            int length;
            while ((length = fis.read(buffer)) >= 0) {
                sevenZOutputFile.write(buffer, 0, length);
            }
        }
    }

    @Override
    public void compress(File beCompressFile, FileVisitor<Path> visitor) throws IOException {
        sevenZOutputFile = new SevenZOutputFile(file);
        super.compress(beCompressFile, visitor);
    }

    @Override
    public void uncompress(FileVisitor<FileEntry<?>> visitor) throws IOException {
        sevenZFile = new SevenZFile(file);
        super.uncompress(visitor);
    }

    @Override
    protected FileEntry<SevenZArchiveEntry> getFileEntry() throws IOException {
        SevenZArchiveEntry entry = sevenZFile.getNextEntry();
        if (entry == null) {
            return null;
        }
        return new FileEntry<>(sevenZFile.getInputStream(entry), entry, entry.getName());
    }

    @Override
    protected BasicFileAttributes getAttributes(SevenZArchiveEntry entry) {
        return new BasicFileAttributes() {
            @Override
            public FileTime lastModifiedTime() {
                return FileTime.fromMillis(entry.getLastModifiedDate().getTime());
            }

            @Override
            public FileTime lastAccessTime() {
                return FileTime.fromMillis(entry.getAccessDate().getTime());
            }

            @Override
            public FileTime creationTime() {
                return FileTime.fromMillis(entry.getCreationDate().getTime());
            }

            @Override
            public boolean isRegularFile() {
                return !entry.isDirectory();
            }

            @Override
            public boolean isDirectory() {
                return entry.isDirectory();
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
                return entry.getSize();
            }

            @Override
            public Object fileKey() {
                return entry;
            }
        };
    }


    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(sevenZFile);
        IOUtils.closeQuietly(sevenZOutputFile);
    }
}
