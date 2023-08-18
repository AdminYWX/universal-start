package com.universal.common.excel;

import java.util.List;

/**
 * @author laoge
 */
interface CellRule {
    /**
     * 获取CellRules设置的所有规则
     *
     * @return 规则集合
     */
    List<Rule> getRule();
}
