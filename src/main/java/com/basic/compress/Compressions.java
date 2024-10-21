package com.basic.compress;

import com.basic.compress.sevenz.SevenZipBindingFileTreeWalker;
import com.basic.compress.normal.TarGzFileTreeWalker;

import net.sf.sevenzipjbinding.ArchiveFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 压缩相关处理类
 *
 * @author Gersy
 * @date 2023/12/5
 * @since 1.0
 */
public class Compressions {

    private static final Logger logger = LoggerFactory.getLogger(Compressions.class);

    /**
     * 压缩目录下的所有的文件
     *
     * @param file       待压缩的文件或目录
     * @param outputFile 输出的压缩文件
     * @return 压缩后的文件
     */
    public static File compress(File file, File outputFile) throws IOException, UnsupportedFileTypeException {
        return compress(file, outputFile, null);
    }

    /**
     * 支持自定义选择文件的压缩
     *
     * @param file       待压缩的文件或目录
     * @param outputFile 输出的压缩文件
     * @param visitor    文件访问器，遍历目录下的所有文件，并可自由决定是否需要加入到压缩文件中
     * @return 压缩后的文件
     */
    public static File compress(File file, File outputFile, FileVisitor<Path> visitor) throws IOException, UnsupportedFileTypeException {
        getFileTreeWalker(outputFile).compress(file, visitor);
        return outputFile;
    }

    private static File uncompress(File file, int maxDepth, FileVisitor<FileEntry<?>> visitor) throws IOException, UnsupportedFileTypeException {
        getFileTreeWalker(file).uncompress(visitor);
        return file;
    }

    /**
     * @param file    待解压的文件
     * @param visitor 压缩文件访问器，通过访问器遍历压缩文件类的所有文件和目录
     * @return 解压后的目录
     */
    public static File uncompress(File file, FileVisitor<FileEntry<?>> visitor) throws IOException, UnsupportedFileTypeException {
        return uncompress(file, Integer.MAX_VALUE, visitor);
    }

    /**
     * @param file      待解压的文件
     * @param targetDir 解压到的目录
     * @param showLog   是否显示解压日志
     * @return 解压后的目录
     */
    public static File uncompress(File file, File targetDir, boolean showLog) throws IOException, UnsupportedFileTypeException {
        if (!targetDir.exists()) {
            throw new FileNotFoundException("目录不存在：" + targetDir);
        }
        if (!targetDir.isDirectory()) {
            throw new FileNotFoundException("targetDir必须是一个目录");
        }
        return uncompress(file, Integer.MAX_VALUE, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(FileEntry<?> fileEntry, BasicFileAttributes attrs) throws IOException {
                if (showLog) {
                    logger.info("解压：" + fileEntry.path);
                }
                File file = new File(targetDir, fileEntry.getPath());
                File parentFile = file.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }
                fileEntry.transferTo(file);
                return super.visitFile(fileEntry, attrs);
            }
        });
    }

    /**
     * @param file      待解压的文件
     * @param targetDir 解压到的目录
     * @param showLog   是否显示解压日志
     * @return 解压后的目录
     */
    public static File uncompress(File file, String targetDir, boolean showLog) throws IOException, UnsupportedFileTypeException {
        return uncompress(file, new File(targetDir), showLog);
    }

    private static FileTreeWalker<?> getFileTreeWalker(File file) throws UnsupportedFileTypeException {
        String name = file.getName().toLowerCase();
        if (name.endsWith("zip") || name.endsWith("jar")) {
            return new SevenZipBindingFileTreeWalker(file, ArchiveFormat.ZIP);
        } else if (name.endsWith("tar")) {
            return new SevenZipBindingFileTreeWalker(file, ArchiveFormat.TAR);
        } else if (name.endsWith("tar.gz")) {
            return new TarGzFileTreeWalker(file);
        } else if (name.endsWith("rar")) {
            return new SevenZipBindingFileTreeWalker(file, ArchiveFormat.RAR);
        } else if (name.endsWith("7z")) {
            return new SevenZipBindingFileTreeWalker(file, ArchiveFormat.SEVEN_ZIP);
        } else {
            return new SevenZipBindingFileTreeWalker(file, name.substring(name.lastIndexOf(".") + 1));
        }
//        throw new UnsupportedFileTypeException("不支持的压缩文件类型:" + file.getName());
    }

    private static final String[] SUPPORTED_TYPES = new String[]{"zip", "jar", "tar", "tar.gz", "rar", "7z"};

    public static boolean isSupported(File file) {
        String name = file.getName().toLowerCase();
        for (String supportedType : SUPPORTED_TYPES) {
            if (name.endsWith(supportedType)) {
                return true;
            }
        }
        return false;
    }
}
