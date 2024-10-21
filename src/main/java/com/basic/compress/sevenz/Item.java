package com.basic.compress.sevenz;

import net.sf.sevenzipjbinding.ISequentialInStream;

import java.io.File;

/**
 * @author Gersy
 * @date 2024/1/10
 * @since 1.0
 */
public class Item {
    private String path;
    private ISequentialInStream inStream;

    private File file;

    public Item() {
    }

    public Item(String path, File file) {
        this.path = path;
        this.file = file;
    }

    public Item(String path, ISequentialInStream inStream) {
        this.path = path;
        this.inStream = inStream;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ISequentialInStream getInStream() {
        return inStream;
    }

    public void setInStream(ISequentialInStream inStream) {
        this.inStream = inStream;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}
