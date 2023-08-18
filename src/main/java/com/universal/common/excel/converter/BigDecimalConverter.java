package com.universal.common.excel.converter;

import com.universal.common.excel.AbstractConverter;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * 默认的BigDecimal类型转换器
 *
 * @author laoge
 */
@Component
public class BigDecimalConverter extends AbstractConverter<BigDecimal> {
    /**
     * 对用户写入Cell的值做针对性转换
     *
     * @param value 处理的数据
     * @return 处理后的标准数据
     * @throws Exception 异常
     */
    @Override
    public String convertToExcelData(BigDecimal value) throws Exception {
        return value.stripTrailingZeros().toPlainString();
    }

    /**
     * 获取ConverterLoader注册的唯一标识
     *
     * @return BigDecimal.class
     */
    @Override
    public Class<BigDecimal> toKey() {
        return BigDecimal.class;
    }

    public BigDecimalConverter() {
    }
}
