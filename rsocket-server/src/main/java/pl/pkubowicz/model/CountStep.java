package pl.pkubowicz.model;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class CountStep {
    private final int value;
    private final String field1;
    private final String field2;
    private final String field3;
    private final List<Integer> list;

    public CountStep(int value) {
        this.value = value;
        this.field1 = Integer.toBinaryString(Integer.MAX_VALUE - value);
        this.field2 = Integer.toOctalString(Integer.MIN_VALUE + value);
        this.field3 = Integer.toHexString(Integer.MAX_VALUE - value);
        this.list = Stream.of(value, field1, field2, field3).map(Object::hashCode).collect(toList());
    }

    public int getValue() {
        return value;
    }

    public String getField1() {
        return field1;
    }

    public String getField2() {
        return field2;
    }

    public String getField3() {
        return field3;
    }

    public List<Integer> getList() {
        return list;
    }
}
