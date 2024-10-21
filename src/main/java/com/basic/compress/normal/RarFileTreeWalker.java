//package com.htcloud.compress.normal;
//
//import com.github.junrar.Archive;
//import com.github.junrar.exception.RarException;
//import com.github.junrar.rarfile.FileHeader;
//import com.htcloud.compress.FileEntry;
//import com.htcloud.compress.FileTreeWalker;
//
//import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
//import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
//import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
//import org.apache.commons.io.IOUtils;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.nio.file.FileVisitor;
//import java.nio.file.Path;
//import java.nio.file.attribute.BasicFileAttributes;
//import java.nio.file.attribute.FileTime;
//
///**
// * @author Gersy
// * @date 2023/12/6
// * @since 1.0
// */
//public class RarFileTreeWalker extends FileTreeWalker<FileHeader> {
//    private Archive archive;
//
//    public RarFileTreeWalker(File file) {
//        super(file);
//    }
//    @Override
//    public FileTreeWalker<FileHeader> compress(File beCompressFile, FileVisitor<Path> visitor) throws IOException {
////        archive = new Archive(file);
//        super.compress(beCompressFile,visitor);
//        return this;
//    }
//
//    @Override
//    public void writeNext(File file) throws IOException {
//    }
//
//    @Override
//    public FileTreeWalker<FileHeader> uncompress() throws IOException {
////        archive = new Archive(file);
//        return null;
//    }
//
//    @Override
//    protected FileEntry<FileHeader> getFileEntry() throws IOException {
//        FileHeader entry = archive.nextFileHeader();
//        if (entry == null) {
//            return null;
//        }
//        return new FileEntry<>(archive.getInputStream(entry), entry, entry.getFileName());
//    }
//
//    @Override
//    protected BasicFileAttributes getAttributes(FileHeader entry) {
//        return new BasicFileAttributes() {
//            @Override
//            public FileTime lastModifiedTime() {
//                return entry.getLastModifiedTime();
//            }
//
//            @Override
//            public FileTime lastAccessTime() {
//                return entry.getLastAccessTime();
//            }
//
//            @Override
//            public FileTime creationTime() {
//                return entry.getCreationTime();
//            }
//
//            @Override
//            public boolean isRegularFile() {
//                return !entry.isDirectory();
//            }
//
//            @Override
//            public boolean isDirectory() {
//                return entry.isDirectory();
//            }
//
//            @Override
//            public boolean isSymbolicLink() {
//                return false;
//            }
//
//            @Override
//            public boolean isOther() {
//                return false;
//            }
//
//            @Override
//            public long size() {
//                return entry.getDataSize();
//            }
//
//            @Override
//            public Object fileKey() {
//                return entry;
//            }
//        };
//    }
//
//
//    @Override
//    public void close() throws IOException {
//        IOUtils.closeQuietly(archive);
//    }
//}
