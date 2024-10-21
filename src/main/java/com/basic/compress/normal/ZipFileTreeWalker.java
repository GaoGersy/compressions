package com.basic.compress.normal;

import com.basic.compress.FileEntry;
import com.basic.compress.FileTreeWalker;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
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
public class ZipFileTreeWalker extends FileTreeWalker<ZipArchiveEntry> {
    private ZipArchiveInputStream archiveInputStream;
    private ZipArchiveOutputStream archiveOutputStream;

    public ZipFileTreeWalker(File file) {
        super(file);
    }

    @Override
    public void compress(File beCompressFile, FileVisitor<Path> visitor) throws IOException {
        archiveOutputStream = new ZipArchiveOutputStream(new FileOutputStream(file));
        super.compress(beCompressFile, visitor);
    }

    @Override
    public void uncompress(FileVisitor<FileEntry<?>> visitor) throws IOException {
        archiveInputStream = new ZipArchiveInputStream(new FileInputStream(file), "GBK");
        super.uncompress(visitor);
    }

    @Override
    public void writeNext(File file) throws IOException {
        archiveOutputStream.putArchiveEntry(new ZipArchiveEntry(file, getEntryName(file)));
        writeStream(file,archiveOutputStream);
        archiveOutputStream.closeArchiveEntry();
    }

    @Override
    protected FileEntry<ZipArchiveEntry> getFileEntry() throws IOException {
        ZipArchiveEntry nextZipEntry = archiveInputStream.getNextZipEntry();
        if (nextZipEntry == null) {
            return null;
        }
        return new FileEntry<>(archiveInputStream, nextZipEntry, nextZipEntry.getName());
    }

    @Override
    protected BasicFileAttributes getAttributes(ZipArchiveEntry entry) {
        return new BasicFileAttributes() {
            @Override
            public FileTime lastModifiedTime() {
                return entry.getLastModifiedTime();
            }

            @Override
            public FileTime lastAccessTime() {
                return entry.getLastAccessTime();
            }

            @Override
            public FileTime creationTime() {
                return entry.getCreationTime();
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
        IOUtils.closeQuietly(archiveInputStream);
        IOUtils.closeQuietly(archiveOutputStream);
    }

}
