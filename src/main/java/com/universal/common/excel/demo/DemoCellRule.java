package com.universal.common.excel.demo;

import com.universal.common.excel.AbstractCellRule;
import com.universal.common.excel.Operator;
import com.universal.common.excel.Rule;
import org.apache.poi.ss.usermodel.IndexedColors;

import java.util.Arrays;
import java.util.List;

public class DemoCellRule extends AbstractCellRule {
    @Override
    public List<Rule> getRule() {
        return Arrays.asList(new Rule(Operator.EQUAL, "\"超限\"", IndexedColors.RED, true),
                new Rule(Operator.EQUAL, "\"安全\"", IndexedColors.GREEN, true));
    }
}
