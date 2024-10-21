package com.basic.compress;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Gersy
 * @date 2023/12/5
 * @since 1.0
 */
public class FileEntry<T> {
    private InputStream inputStream;
    protected T entry;
    protected String path;

    public FileEntry() {
    }

    public FileEntry(String path) {
        this.path = path;
    }

    public FileEntry(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public FileEntry(InputStream inputStream, T entry, String path) {
        this.inputStream = inputStream;
        this.entry = entry;
        this.path = path;
    }

    public void transferTo(File file) throws IOException {
        if (inputStream == null) {
            throw new IllegalStateException("无法转存文件,entry读取失败,没有获取输入流");
        }
        try (FileOutputStream output = new FileOutputStream(file)) {
            IOUtils.copy(inputStream, output);
        }
    }

    public void transferTo(File file, long lastModified) throws IOException {
        transferTo(file);
        boolean b = file.setLastModified(lastModified);
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public String getPath() {
        return path;
    }

    public T getEntry() {
        return entry;
    }

}
