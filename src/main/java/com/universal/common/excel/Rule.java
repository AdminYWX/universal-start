package com.universal.common.excel;

import org.apache.poi.ss.usermodel.IndexedColors;

/**
 * @author laoge
 */
public class Rule {

    /**
     * 操作符
     */
    private byte operator;
    /**
     * 条件
     */
    private String content;
    /**
     * 颜色
     */
    private short indexedColor;

    /**
     * 是否加粗
     */
    private boolean bold;

    public Rule(Operator operator, String content, IndexedColors indexedColor, boolean bold) {
        this.operator = operator.value();
        this.content = content;
        this.bold = bold;
        this.indexedColor = indexedColor.getIndex();
    }

    public Rule(Operator operator, String content, IndexedColors indexedColor) {
        this.operator = operator.value();
        this.content = content;
        this.indexedColor = indexedColor.getIndex();
    }

    public Rule(Operator operator, String content) {
        this.operator = operator.value();
        this.content = content;
        this.indexedColor = IndexedColors.RED.getIndex();
    }

    public byte getOperator() {
        return operator;
    }

    public String getContent() {
        return content;
    }

    public short getIndexedColor() {
        return indexedColor;
    }

    public boolean getBold() {
        return bold;
    }
}
