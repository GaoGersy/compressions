package com.basic.compress.normal;

import com.basic.compress.FileEntry;
import com.basic.compress.FileTreeWalker;

import org.apache.commons.io.IOUtils;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;
import org.apache.tools.tar.TarOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author Gersy
 * @date 2023/12/6
 * @since 1.0
 */
public class TarGzFileTreeWalker extends FileTreeWalker<TarEntry> {
    private TarInputStream archiveInputStream;
    private TarOutputStream archiveOutputStream;

    public TarGzFileTreeWalker(File file) {
        super(file);
    }

    @Override
    public void writeNext(File file) throws IOException {
        archiveOutputStream.putNextEntry(new TarEntry(file, getEntryName(file)));
        writeStream(file,archiveOutputStream);
        archiveOutputStream.closeEntry();
    }

    @Override
    public void compress(File beCompressFile, FileVisitor<Path> visitor) throws IOException {
        archiveOutputStream = new TarOutputStream(new GZIPOutputStream(new FileOutputStream(file)), 1024 * 8);
        super.compress(beCompressFile, visitor);
    }

    @Override
    public void uncompress(FileVisitor<FileEntry<?>> visitor) throws IOException {
        archiveInputStream = new TarInputStream(new GZIPInputStream(new FileInputStream(file)), 1024 * 8);
        super.uncompress(visitor);
    }

    @Override
    protected FileEntry<TarEntry> getFileEntry() throws IOException {
        TarEntry nextZipEntry = archiveInputStream.getNextEntry();
        if (nextZipEntry == null) {
            return null;
        }
        return new FileEntry<>(archiveInputStream, nextZipEntry, nextZipEntry.getName());
    }

    @Override
    protected BasicFileAttributes getAttributes(TarEntry entry) {
        return new BasicFileAttributes() {
            @Override
            public FileTime lastModifiedTime() {
                return FileTime.fromMillis(entry.getModTime().getTime());
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
