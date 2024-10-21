package com.basic.compress.sevenz;

import com.basic.compress.FileEntry;

import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.RandomAccessFileOutStream;
import net.sf.sevenzipjbinding.simple.ISimpleInArchiveItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * @author Gersy
 * @date 2024/1/9
 * @since 1.0
 */
public class SevenZipFileEntry extends FileEntry<ISimpleInArchiveItem> {

    private final String password;

    public SevenZipFileEntry(ISimpleInArchiveItem entry, String path) {
        this(entry, path, null);
    }

    public SevenZipFileEntry(ISimpleInArchiveItem entry, String path, String password) {
        this.password = password;
        this.entry = entry;
        this.path = path;
    }

    public void transferTo(File file) throws FileNotFoundException, SevenZipException {
        RandomAccessFileOutStream fileOutStream = null;
        try {
            fileOutStream = new RandomAccessFileOutStream(new RandomAccessFile(file, "rw"));
            if (password != null) {
                entry.extractSlow(fileOutStream, password);
            } else {
                entry.extractSlow(fileOutStream);
            }
        } finally {
            if (fileOutStream != null) {
                try {
                    fileOutStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public InputStream getInputStream() {
        throw new UnsupportedOperationException();
    }
}
