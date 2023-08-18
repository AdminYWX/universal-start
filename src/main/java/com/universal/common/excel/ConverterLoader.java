package com.universal.common.excel;

import java.util.HashMap;
import java.util.Map;

/**
 * cell值转换器的加载器
 *
 * @author laoge
 */
public class ConverterLoader {
    private static final Map<String, Convert<?>> DEFAULT_WRITE_CONVERTER = new HashMap<>();


    /**
     * 注册一个类型转换器
     *
     * @param convert 转换器
     */
    public static void register(Convert<?> convert) {
        DEFAULT_WRITE_CONVERTER.put(buildKey(convert), convert);
    }

    /**
     * 为转换器生成一个唯一标识的key
     *
     * @param clazz 转换器处理的class类型
     * @return 唯一标识key
     */
    private static String buildKey(Class<?> clazz) {
        return clazz.getName();
    }

    /**
     * 为转换器生成一个唯一标识的key
     * 用于存在覆盖类型时自定义的转换器注册
     *
     * @param convert 转换器
     * @return 唯一标识key
     */
    private static String buildKey(Convert<?> convert) {
        String individuation = convert.individuation();
        if (individuation == null || "".equals(individuation.trim())) {
            return convert.toKey().getName();
        }
        return buildKey(convert.toKey());
    }

    /**
     * 获取类型转换器
     *
     * @param clazz 要处理的类型
     * @return clazz对应的转换器
     */
    public static Convert getConvert(Class<?> clazz) {
        return DEFAULT_WRITE_CONVERTER.get(buildKey(clazz));
    }

    /**
     * 获取自定义的类型转换器
     *
     * @param convert ExcelColumn中指定的自定义转换器
     * @return 转换器
     */
    public static Convert getConvert(Convert<?> convert) {
        return DEFAULT_WRITE_CONVERTER.get(buildKey(convert));
    }

    /**
     * 判断一个值是否存在它的转换器
     *
     * @param value 要转换的值
     * @return true | false
     */
    public static boolean contains(Object value) {
        if (value == null) {
            return false;
        }
        return DEFAULT_WRITE_CONVERTER.containsKey(buildKey(value.getClass()));
    }

}
