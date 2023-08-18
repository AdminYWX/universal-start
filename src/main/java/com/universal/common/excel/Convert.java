package com.universal.common.excel;

/**
 * @author laoge
 */
interface Convert<T> {
    /**
     * 对用户写入Cell的值做针对性转换
     *
     * @param value 处理的数据
     * @return 处理后的标准数据
     * @throws Exception 异常
     */
    Object convertToExcelData(T value) throws Exception;

    /**
     * 获取ConverterLoader注册的唯一标识
     *
     * @return 转换器针对的类型Class
     */
    Class<T> toKey();

    /**
     * 为转换器生成一个自己的唯一的个性化标识
     * 当存在多个同类型字段时，但是针对不同字段需要不同转换逻辑
     * 为了避免同类型转换器的key在ConverterLoader被覆盖，这时候就需要一个个性化名称保证唯一性
     * 此方法主要用于开发人员自定义的转换器
     *
     * @return 自定义的个性化名称
     */
    String individuation();
}
