package com.basic.compress.normal;

import com.basic.compress.FileEntry;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitor;

/**
 * 压缩包读取类
 *
 * @author Gersy
 * @date 2023/12/5
 * @since 1.0
 */
public interface CompressionReader {
    void read(File file, int maxDepth, FileVisitor<? super FileEntry> visitor) throws IOException;
}
