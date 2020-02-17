package pl.pkubowicz.model;

import java.util.List;

@SuppressWarnings({"FieldCanBeLocal", "unused"})
public class CountStep {
    private final int value;
    private final String field1;
    private final String field2;
    private final String field3;
    private final List<Integer> list;

    public CountStep(int value, String field1, String field2, String field3, List<Integer> list) {
        this.value = value;
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
        this.list = list;
    }

    public int getValue() {
        return value;
    }
}
