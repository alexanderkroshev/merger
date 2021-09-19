package com.alexkroshev;

public class IntegerData extends Data {

    private final Integer value;

    public IntegerData(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return value;
    }

    @Override
    String stringify() {
            return value.toString();
    }

    @Override
    public int compareTo(Data o) {
        if (o instanceof IntegerData)
            return this.value - ((IntegerData) o).getValue();
        else
            throw new RuntimeException("value is not INTEGER");
    }

}
