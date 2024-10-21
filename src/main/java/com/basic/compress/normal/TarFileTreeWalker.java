package com.basic.compress.normal;


import com.basic.compress.FileEntry;
import com.basic.compress.FileTreeWalker;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
public class TarFileTreeWalker extends FileTreeWalker<TarArchiveEntry> {
    private TarArchiveInputStream archiveInputStream;
    private TarArchiveOutputStream archiveOutputStream;

    public TarFileTreeWalker(File file) {
        super(file);
    }

    @Override
    public void writeNext(File file) throws IOException {
        archiveOutputStream.putArchiveEntry(new TarArchiveEntry(file, getEntryName(file)));
        writeStream(file,archiveOutputStream);
        archiveOutputStream.closeArchiveEntry();
    }

    @Override
    public void compress(File beCompressFile, FileVisitor<Path> visitor) throws IOException {
        archiveOutputStream = new TarArchiveOutputStream(new FileOutputStream(file), 1024 * 8, "GBK");
        super.compress(beCompressFile, visitor);
    }

    @Override
    public void uncompress(FileVisitor<FileEntry<?>> visitor) throws IOException {
        archiveInputStream = new TarArchiveInputStream(new FileInputStream(file), 1024 * 8, "GBK");
        super.uncompress(visitor);
    }

    @Override
    protected FileEntry<TarArchiveEntry> getFileEntry() throws IOException {
        TarArchiveEntry entry = archiveInputStream.getNextTarEntry();
        if (entry == null) {
            return null;
        }
        return new FileEntry<>(archiveInputStream, entry, entry.getName());
    }

    @Override
    protected BasicFileAttributes getAttributes(TarArchiveEntry entry) {
        return new BasicFileAttributes() {
            @Override
            public FileTime lastModifiedTime() {
                return FileTime.fromMillis(entry.getLastModifiedDate().getTime());
            }

            @Override
            public FileTime lastAccessTime() {
                throw new UnsupportedOperationException();
            }

            @Override
            public FileTime creationTime() {
                throw new UnsupportedOperationException();
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
                return entry.getRealSize();
            }

            @Override
            public Object fileKey() {
                return entry;
            }
        };
    }


    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(archiveInputStream);
        IOUtils.closeQuietly(archiveOutputStream);
    }
}
