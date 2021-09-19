package com.alexkroshev;

public class StringData extends Data {

    private final String value;

    public String getValue() {
        return value;
    }

    public StringData(String value) {
        this.value = value;
    }

    @Override
    String stringify() {
        return value;
    }

    @Override
    public int compareTo(Data o) {
        if (o instanceof StringData)
            return this.value.compareTo(((StringData) o).getValue());
        else
            throw new RuntimeException("value is not STRING");
    }
}

