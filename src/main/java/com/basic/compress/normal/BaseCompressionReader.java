package com.basic.compress.normal;//package com.htcloud.monitor.automatic.common.utils.compression;
//
//
//import com.htcloud.monitor.automatic.common.utils.compression.sevenZ.SevenZFileAttributes;
//
//import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
//import org.apache.commons.compress.archivers.sevenz.SevenZFile;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.FileVisitor;
//import java.nio.file.attribute.BasicFileAttributes;
//
///**
// * 7zip文件读取
// *
// * @author Gersy
// * @date 2023/12/5
// * @since 1.0
// */
//public abstract class BaseCompressionReader<T,E> implements CompressionReader {
//    @Override
//    public void read(File file, int maxDepth, FileVisitor<? super FileEntry> visitor) throws IOException {
//        try (SevenZFile sevenZFile = new SevenZFile(file)) {
//            SevenZArchiveEntry entry = null;
//            FileEntry lastDirectoryEntry = null;
//            int depth = 0;
//            while ((entry = sevenZFile.getNextEntry()) != null) {
//                if (entry.isDirectory()) {
//                    if (lastDirectoryEntry != null) {
//                        visitor.postVisitDirectory(lastDirectoryEntry, null);
//                    }
//                    depth++;
//                    if (depth > maxDepth) {
//                        break;
//                    }
//                    lastDirectoryEntry = new FileEntry(sevenZFile.getInputStream(entry));
////                    visitor.preVisitDirectory(lastDirectoryEntry, getFileAttributes(entry));
//                } else {
//                    visitor.visitFile(new FileEntry(sevenZFile.getInputStream(entry)), new SevenZFileAttributes(entry));
//                }
//            }
//            if (lastDirectoryEntry != null) {
//                visitor.postVisitDirectory(lastDirectoryEntry, null);
//            }
//
//        }
//    }
//
//
//    protected abstract InputStream getInputStream(T t,E e);
//
//    protected abstract BasicFileAttributes getFileAttributes(E e);
//}
