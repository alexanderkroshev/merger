package com.alexkroshev;


public class DataIteratorWrapper implements Comparable<DataIteratorWrapper>{

    private final Data data;
    private final DataIterator iterator;

    public DataIteratorWrapper(Data data, DataIterator dataIterator) {
        this.data = data;
        this.iterator = dataIterator;
    }

    public Data getData() {
        return data;
    }

    public DataIterator getIterator() {
        return iterator;
    }

    public String getFileName(){
        return iterator.getFileName();
    }

    @Override
    public int compareTo(DataIteratorWrapper o) {
        return this.data.compareTo(o.getData());
    }


}



