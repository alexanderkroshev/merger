package com.alexkroshev;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public class Merge {

    private final Ordering ordering;
    private final DataIterator[] dataIterators;
    private final BufferedWriter writer;
    private final BufferedWriter errorWriter;

    public Merge(DataIterator[] dataIterators, BufferedWriter writer, BufferedWriter errorWriter) {
        this.dataIterators = dataIterators;
        this.writer = writer;
        this.errorWriter = errorWriter;
        ordering = dataIterators[0].getOrdering();
    }


    //нужен если вдруг Data = IntegerData, при этом прочитанная строка из файла не распарсилась
    public void safeNext(DataIterator iterator, ArrayList<DataIteratorWrapper> arrayList) throws IOException {
        Data data;
        while (iterator.hasNext()) {
            Integer lineNum =null;
            try {
                lineNum = iterator.getNumberOfLine();
                data = iterator.next();
                DataIteratorWrapper dataIteratorWrapper = new DataIteratorWrapper(data, iterator);
                arrayList.add(dataIteratorWrapper);
                break;
            } catch (NumberFormatException e) {
                errorWriter.write("problem with parsing of integer in file "
                        + iterator.getFileName() + ", line = " + lineNum + "\r\n");
                errorWriter.flush();
            }
        }
    }

    public void merge() throws IOException {
        ArrayList<DataIteratorWrapper> listWithData = new ArrayList<>();
        int index = 0;
        while (index < dataIterators.length) {
            if (dataIterators[index].hasNext())
                safeNext(dataIterators[index], listWithData);
            index++;
        }

        if (listWithData.size() == 0)
            return;

        Data tmpData = null;
        if (ordering.equals(Ordering.ASC))
            tmpData = minFromList(listWithData).getData();
        else if (ordering.equals(Ordering.DESC))
            tmpData = maxFromList(listWithData).getData();


        while (!listWithData.isEmpty()) {
            DataIteratorWrapper dataWrapper = null;
            if (ordering.equals(Ordering.ASC)) {
                dataWrapper = minFromList(listWithData);
                if (compare(tmpData, dataWrapper.getData()) > 0) {
                    listWithData.remove(dataWrapper);
                    safeNext(dataWrapper.getIterator(), listWithData);
                    errorWriter.write("fail of ordering, " + " file path: " + dataWrapper.getFileName() +
                            ", line = " + (dataWrapper.getIterator().getNumberOfLine()-1) + "\r\n");
                    continue;
                }
            } else if (ordering.equals(Ordering.DESC)) {
                dataWrapper = maxFromList(listWithData);
                if (compare(tmpData, dataWrapper.getData()) < 0) {
                    listWithData.remove(dataWrapper);
                    safeNext(dataWrapper.getIterator(), listWithData);
                    errorWriter.write("fail of ordering, " + " file path: " + dataWrapper.getFileName() +
                            ", line = " + (dataWrapper.getIterator().getNumberOfLine()-1) + "\r\n");
                    continue;
                }
            }

            Iterator<Data> dataIterator = dataWrapper.getIterator();
            writer.write(dataWrapper.getData().stringify() + "\r\n");
            writer.flush();
            tmpData = dataWrapper.getData();
            listWithData.remove(dataWrapper);
            if (dataIterator.hasNext()) {
                safeNext((DataIterator) dataIterator, listWithData);
            }
        }
        writer.close();
        errorWriter.close();
    }

    //нужен, чтобы для выявляния неотсортированных значений на входе
    public Integer compare(Data data1, Data data2) {
        int i = 0;
        if (data1 instanceof IntegerData && data2 instanceof IntegerData)
            i = ((IntegerData) data1).getValue() - ((IntegerData) data2).getValue();
        else if (data1 instanceof StringData && data2 instanceof StringData)
            i = data1.compareTo(data2);
        return i;
    }

    private static DataIteratorWrapper maxFromList(ArrayList<DataIteratorWrapper> arrayList) {
        Collections.sort(arrayList);
        return arrayList.get(arrayList.size() - 1);
    }

    private static DataIteratorWrapper minFromList(ArrayList<DataIteratorWrapper> arrayList) {
        Collections.sort(arrayList);
        return arrayList.get(0);
    }
}
