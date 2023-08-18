package com.universal.common.excel;

import java.util.List;

/**
 * @author laoge
 */
public abstract class AbstractCellRule implements CellRule {
    /**
     * 获取CellRules设置的所有规则
     *
     * @return 规则集合
     */
    @Override
    public abstract List<Rule> getRule();

}
