package com.alexkroshev;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Iterator;

public class DataIterator implements Iterator<Data> {

    private final Ordering ordering;
    private final String fileName;
    private final DataType dataType;
    private final RandomAccessFile randomAccessFile;
    private Long pointer;
    private int numberOfLine=0;

    public DataIterator(Ordering ordering, DataType dataType, String fileName) throws FileNotFoundException {
        this.ordering = ordering;
        this.dataType = dataType;
        this.fileName = fileName;
        this.pointer = 0L;
        this.numberOfLine = 0;
        this.randomAccessFile = new RandomAccessFile(fileName, "r");
    }

    public Ordering getOrdering() {
        return ordering;
    }

    public String getFileName() {
        return fileName;
    }

    public int getNumberOfLine() {
        return numberOfLine;
    }

    @Override
    public boolean hasNext() {
        boolean hasNext = false;
        try {
            long lengthOfFile = randomAccessFile.length();
            if (ordering.equals(Ordering.ASC)) {
                hasNext = pointer < lengthOfFile;
            } else if (ordering.equals(Ordering.DESC)) {
                hasNext = lengthOfFile - pointer > 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return hasNext;
    }

    @Override
    public Data next() {
        StringBuilder stringBuilder = new StringBuilder();
        Character c;
        try {
            if (ordering.equals(Ordering.ASC)) {
                while (hasNext()) {
                    randomAccessFile.seek(pointer);
                    c = (char) randomAccessFile.readByte();
                    if ((int) c > 32 && (int) c < 127)
                        stringBuilder.append(c);
                    pointer++;
                    if (c == '\n')
                        break;
                }
            }
            if (ordering.equals(Ordering.DESC)) {
                long lengthOfFile = randomAccessFile.length();
                while (hasNext()) {
                    pointer++;
                    randomAccessFile.seek(lengthOfFile - pointer);
                    c = (char) randomAccessFile.readByte();
                    if ((int) c > 32 && (int) c < 127) {
                        stringBuilder.append(c);
                    }
                    if (c == '\n' || lengthOfFile - pointer == 0) {
                        stringBuilder.reverse();
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        Data data;
        if (dataType.equals(DataType.STRING))
            data = new StringData(stringBuilder.toString());
        else
            data = new IntegerData(Integer.parseInt(stringBuilder.toString()));
        numberOfLine++;
        return data;
    }
}
