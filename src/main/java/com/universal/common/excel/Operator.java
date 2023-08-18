package com.universal.common.excel;

/**
 * @author laoge
 */
public enum Operator {
    NO_COMPARISON((byte) 0),
    BETWEEN((byte) 1),
    NOT_BETWEEN((byte) 2),
    EQUAL((byte) 3),
    NOT_EQUAL((byte) 4),
    GT((byte) 5),
    LT((byte) 6),
    GE((byte) 7),
    LE((byte) 8);

    private final byte value;

    public byte value() {
        return this.value;
    }

    Operator(byte value) {
        this.value = value;
    }
}
