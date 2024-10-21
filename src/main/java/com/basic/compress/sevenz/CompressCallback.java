package com.basic.compress.sevenz;

import net.sf.sevenzipjbinding.IOutCreateCallback;
import net.sf.sevenzipjbinding.IOutItemAllFormats;
import net.sf.sevenzipjbinding.ISequentialInStream;
import net.sf.sevenzipjbinding.SevenZipException;
import net.sf.sevenzipjbinding.impl.OutItemFactory;
import net.sf.sevenzipjbinding.impl.RandomAccessFileInStream;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.List;

/**
 * @author Gersy
 * @date 2024/1/10
 * @since 1.0
 */
public class CompressCallback implements IOutCreateCallback<IOutItemAllFormats> {

    private final List<Item> items;
    private int currentIndex;

    public CompressCallback(List<Item> items) {
        this.items = items;
    }

    @Override
    public void setOperationResult(boolean operationResultOk) throws SevenZipException {
//        Item item = items.get(currentIndex++);
//        ISequentialInStream itemInStream = item.getInStream();
//        if (itemInStream != null) {
////            IOUtils.closeQuietly(itemInStream);
//        }
//        if (!operationResultOk) {
//            throw new SevenZipException("文件压缩失败:" + item.getPath());
//        }
    }

    @Override
    public IOutItemAllFormats getItemInformation(int index, OutItemFactory<IOutItemAllFormats> outItemFactory) throws SevenZipException {
        IOutItemAllFormats outItem = outItemFactory.createOutItem();
        outItem.setPropertyPath(items.get(index).getPath());
        return outItem;
    }

    @Override
    public ISequentialInStream getStream(int index) throws SevenZipException {
        try {
            Item item = items.get(index);
            RandomAccessFileInStream fileInStream = new RandomAccessFileInStream(new RandomAccessFile(item.getFile(), "r"));
            item.setInStream(fileInStream);
            return fileInStream;
        } catch (FileNotFoundException e) {
            throw new SevenZipException(e);
        }
    }

    @Override
    public void setTotal(long total) throws SevenZipException {
    }

    @Override
    public void setCompleted(long complete) throws SevenZipException {
    }
}
