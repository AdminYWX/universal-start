package com.universal.common.excel;

import java.io.OutputStream;

/**
 * @author laoge
 * 一个简单的Excel导出实现类
 * 基于@ExcelSheet来定义Sheet的导出
 */
public class SimpleExport {

    public static WriteBuilder write(OutputStream outputStream, Class<?> clazz) {
        return new WriteBuilder(outputStream, clazz);
    }


}
