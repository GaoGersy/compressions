package com.basic.compress;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * @author Gersy
 * @date 2024/1/9
 * @since 1.0
 */
public class Test {
    public static void main(String[] args) {
        String path = "D:\\临时文件\\GF1_PMS1_E110.2_N21.6_20220108_L1A0006205056.tar.gz";
        String path1 = "C:\\Users\\user\\Desktop\\新建文件夹 (6)\\ccc\\rar\\GF1_PMS1_E110.2_N21.6_20220108_L1A0006205056_坏.tar.gz";
//        String path = "D:\\临时文件\\product\\satellite\\satellite.7z";
//        String path = "D:\\临时文件\\GF1_PMS1_E110.2_N21.6_20220108_L1A0006205056.tar.gz";
//        String path = "D:\\临时文件\\java-png-compress-util-1.0.0.jar";
        String parent = "D:\\临时文件\\test";
        try {
            long startTime = System.currentTimeMillis();
            Compressions.compress(new File(parent), new File(parent, "test.zip"));
//            copyBadFile(path, parent);
            Compressions.compress(new File(parent), new File(parent, "test.zip"), new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    if (dir.getFileName().toString().equals("testq")) {
                        return FileVisitResult.SKIP_SUBTREE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.getFileName().toString().endsWith("zip")) {
                        return FileVisitResult.TERMINATE;
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
//            Compressions.uncompress(new File(parent,"test.zip"), new File(parent,"test"), true);
            System.out.println("aaa");
            Compressions.uncompress(new File(path), new SimpleFileVisitor<>() {
                @Override
                public FileVisitResult preVisitDirectory(FileEntry dir, BasicFileAttributes attrs) throws IOException {
                    System.out.println("preVisitDirectory:" + dir.getPath());
//                    //                    return super.preVisitDirectory(dir, attrs);
//                    if (dir.getPath().equals("0310")) {
//                        return FileVisitResult.SKIP_SUBTREE;
//                    }
//                    if (dir.getPath().contains("2023")) {
//                        return FileVisitResult.SKIP_SUBTREE;
//                    }
                    File file = new File(parent, dir.getPath());
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    return super.preVisitDirectory(dir, attrs);
                }

                @Override
                public FileVisitResult visitFile(FileEntry fileEntry, BasicFileAttributes attrs) throws IOException {
                    System.out.println("visitFile:" + fileEntry.getPath());
                    if (fileEntry.getPath().endsWith("jpg")){
                        File file = new File(parent, fileEntry.getPath());
//                    log.info("visitFile:{},size:{},fileSize:{}", fileEntry.getPath(), attrs.size(),file.length());
//                    if (!file.exists() || (file.length() != attrs.size() && file.lastModified() == attrs.lastModifiedTime().toMillis())) {
//                    }
                        fileEntry.transferTo(file);
                    }
                    return super.visitFile(fileEntry, attrs);
                }

                @Override
                public FileVisitResult visitFileFailed(FileEntry<?> file, IOException exc) throws IOException {
                    //读取出异常的压缩包全路径
                    String path2 = file.getPath();
                    return FileVisitResult.CONTINUE;
                }
            });
            System.out.println("结束解压,耗时" + ((System.currentTimeMillis() - startTime) / 1000) + " S");
//            log.debug("结束解压,耗时[{}s]...", stopWatch.getTime(TimeUnit.SECONDS));
        } catch (Exception e) {
//            throw new RuntimeException(e);
            System.out.println(e);
        }
    }

    private static void copyBadFile(String path, String parent) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(path);
        FileOutputStream fileOutputStream = new FileOutputStream(new File(parent, "GF1_PMS1_E110.2_N21.6_20220108_L1A0006205056_坏.tar.gz"));
        byte[] buffer = new byte[1024 * 8];
        int length = 0;
        while ((length = fileInputStream.read(buffer)) > 0) {
            if (length<buffer.length){
//                    fileOutputStream.write(buffer, 0, length-10);
            }else {
                fileOutputStream.write(buffer, 0, length);
            }
        }
        fileInputStream.close();
        fileOutputStream.close();
    }
}
