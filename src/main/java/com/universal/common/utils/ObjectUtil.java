package com.universal.common.utils;


import com.universal.api.exception.CommonException;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

/**
 * @author laoge
 * 封装了一些基于Class的校验操作
 */
public class ObjectUtil {


    /**
     * 检查对象是否不为空
     */
    public static boolean notEmpty(Object object) {
        return !isEmpty(object);
    }

    /**
     * @param object 校验的类
     * @param msg    提醒消息
     *               带有提醒消息的非空校验
     */
    public static void notEmpty(Object object, String msg) {
        if (isEmpty(object)) {
            throw new CommonException(msg);
        }
    }

    /**
     * @param object 校验的类
     * @param msg    提醒消息
     *               带有提醒消息的空校验
     */
    public static void isEmpty(Object object, String msg) {
        if (!isEmpty(object)) {
            throw new CommonException(msg);
        }
    }

    /**
     * 检查对象是否为空
     *
     * @param object 对象
     * @return true || false
     */
    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        } else if (object instanceof String) {
            return ((String) object).trim().length() == 0;
        } else if (object instanceof CharSequence) {
            return ((CharSequence) object).length() == 0;
        } else if (object.getClass().isArray()) {
            return Array.getLength(object) == 0;
        } else if (object instanceof Collection) {
            return ((Collection) object).isEmpty();
        } else {
            return object instanceof Map && ((Map) object).isEmpty();
        }
    }

}
