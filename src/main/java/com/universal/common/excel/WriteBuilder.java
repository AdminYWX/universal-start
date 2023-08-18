package com.universal.common.excel;

import com.universal.common.utils.ObjectUtil;
import lombok.SneakyThrows;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFConditionalFormattingRule;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFFontFormatting;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

/**
 * @author laoge
 * SimpleExport导出的核心实现类
 */
class WriteBuilder {
    /**
     * excel文件
     */
    private XSSFWorkbook workbook;
    /**
     * excel的表头
     */
    private String[] head;

    /**
     * sheet文件的名称
     */
    private String sheetName;

    /**
     * sheet内容映射的class类
     */
    private Class<?> clazz;
    /**
     * 请求excel的响应流
     * response.getOutputStream()
     */
    private final OutputStream outputStream;


    /**
     * 获取Excel编辑器
     *
     * @param outputStream response.getOutputStream()
     * @param clazz        sheet内容映射的class类
     */
    public WriteBuilder(OutputStream outputStream, Class<?> clazz) {
        this.outputStream = outputStream;
        this.clazz = clazz;
        this.workInit();
    }

    /**
     * 初始化一个XSSFWorkbook
     */
    private void workInit() {
        this.workbook = new XSSFWorkbook();
    }

    /**
     * 设置当前sheet的表头
     *
     * @param head 表头数组
     * @return 当前编辑器
     */
    public WriteBuilder head(String[] head) {
        this.head = head;
        return this;
    }

    /**
     * 为当前导出sheet设置一个自定义的名称
     *
     * @param name sheet的名称
     * @return 当前编辑器
     */
    public WriteBuilder sheet(String name) {
        this.sheetName = name;
        return this;
    }

    /**
     * 对sheet写入数据
     *
     * @param data 要导出的数据
     */
    @SneakyThrows
    public void doWrite(List data) {
        if (data == null || data.isEmpty()) {
            return;
        }
        ExcelSheet excelSheet = clazz.getAnnotation(ExcelSheet.class);
        if (excelSheet == null) {
            throw new RuntimeException(clazz.getName() + " not an export-compliant class , Missing @ExcelSheet.");
        }
        String sheetName = ObjectUtil.notEmpty(this.sheetName) ?
                this.sheetName : ObjectUtil.isEmpty(excelSheet.value()) ?
                String.valueOf(hash(data)) : excelSheet.value();
        XSSFSheet sheet = this.sheet(sheetName, head);
        this.dataWrite(data, sheet);
        workbook.write(outputStream);
        this.close();
    }

    private int hash(Object key) {
        int h;
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }

    /**
     * 数据写入核心实现方法
     *
     * @param data  数据信息
     * @param sheet 操作sheet
     */
    @SneakyThrows
    private void dataWrite(List data, XSSFSheet sheet) {
        int index = 1;
        Field[] fields = clazz.getDeclaredFields();
        for (Object oneRowData : data) {
            XSSFRow row = sheet.createRow(index);
            for (Field field : fields) {
                if (!field.isAnnotationPresent(ExcelColumn.class)) {
                    continue;
                }
                field.setAccessible(true);
                Object fieldValue = field.get(oneRowData);
                boolean fistCell = row.getLastCellNum() == -1;
                XSSFCell cell = row.createCell(fistCell ? row.getLastCellNum() + 1 : row.getLastCellNum());
                ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
                Class<? extends Convert> converter = excelColumn.converter();
                this.cellSetValue(cell, fieldValue, converter);
                XSSFCellStyle columnStyle = this.getColumnStyle(field);
                cell.setCellStyle(columnStyle);
                if (field.isAnnotationPresent(CellRules.class)) {
                    Class<? extends CellRule> ruleClass = field.getAnnotation(CellRules.class).cellRule();
                    Method getRule = ruleClass.getMethod("getRule");
                    List<Rule> cfRules = (List<Rule>) getRule.invoke(ruleClass.newInstance());
                    if (cfRules == null || cfRules.isEmpty()) {
                        throw new IllegalArgumentException("cfRules must not be empty");
                    } else if (cfRules.size() > 3) {
                        throw new IllegalArgumentException("Number of rules must not exceed 3");
                    }
                    for (Rule cfRule : cfRules) {
                        // 创建条件格式规则
                        XSSFConditionalFormattingRule rule = sheet.getSheetConditionalFormatting().createConditionalFormattingRule(cfRule.getOperator(), cfRule.getContent());
                        // 创建填充模式
                        XSSFFontFormatting fill = rule.createFontFormatting();
                        fill.setFontColorIndex(cfRule.getIndexedColor());
                        fill.setFontStyle(false, cfRule.getBold());
                        // 创建条件格式区域
                        CellRangeAddress[] regions = {CellRangeAddress.valueOf(this.getCellAddress(cell))};
                        sheet.getSheetConditionalFormatting().addConditionalFormatting(regions, rule);
                    }
                }
            }
            index += 1;
        }
    }

    /**
     * 对cell值写入前基于class做值转换
     *
     * @param value     cell写入值
     * @param converter 转换器
     * @return 转换后的值
     */
    @SneakyThrows
    private Object valueConverter(Object value, Class<? extends Convert> converter) {
        if (converter != AbstractConverter.class) {
            Convert customizedConvert = ConverterLoader.getConvert(converter);
            return customizedConvert.convertToExcelData(value);
        }
        Convert defaultConvert = ConverterLoader.getConvert(value.getClass());
        if (defaultConvert != null) {
            return defaultConvert.convertToExcelData(value);
        }
        return value;
    }

    /**
     * 获取cell在sheet的横纵坐标点
     *
     * @param cell cell
     * @return cell地址
     */
    private String getCellAddress(XSSFCell cell) {
        CellAddress address = cell.getAddress();
        return address + ":" + address;
    }

    /**
     * 对cell写入数据
     *
     * @param cell      cell
     * @param value     写入值
     * @param converter 转换器
     */
    private void cellSetValue(XSSFCell cell, Object value, Class<? extends Convert> converter) {
        if (value == null) {
            cell.setCellValue("");
            return;
        }
        Object afterConversionValue = this.valueConverter(value, converter);
        if (afterConversionValue instanceof Date) {
            cell.setCellValue((Date) afterConversionValue);
        } else {
            cell.setCellValue(String.valueOf(afterConversionValue));
        }
    }

    /**
     * 为WorkBook创建一个Sheet
     *
     * @param name sheet的名称
     * @param head 表头
     * @return 一个新的Sheet
     */
    private XSSFSheet sheet(String name, String[] head) {
        XSSFSheet sheet = workbook.createSheet(name);
        XSSFRow row0 = sheet.createRow(0);
        XSSFCellStyle headStyle = this.getHeadCellStyle();
        if (head != null && head.length != 0) {
            for (String item : head) {
                boolean fistCell = row0.getLastCellNum() == -1;
                XSSFCell cell = row0.createCell(fistCell ? row0.getLastCellNum() + 1 : row0.getLastCellNum());
                cell.setCellStyle(headStyle);
                cell.setCellValue(item);
            }
        } else {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                if (!field.isAnnotationPresent(ExcelColumn.class)) {
                    continue;
                }
                ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
                String cellValue = ObjectUtil.isEmpty(excelColumn.value()) ? field.getName() : excelColumn.value();
                boolean fistCell = row0.getLastCellNum() == -1;
                XSSFCell cell = row0.createCell(fistCell ? row0.getLastCellNum() + 1 : row0.getLastCellNum());
                cell.setCellValue(cellValue);
                cell.setCellStyle(headStyle);
            }
        }
        return sheet;
    }

    /**
     * @return 默认的表头样式
     */
    private XSSFCellStyle getHeadCellStyle() {
        XSSFCellStyle headStyle = workbook.createCellStyle();
        headStyle.setHidden(true);
        headStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setFontName("微软雅黑");
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.BLACK.getIndex());
        headStyle.setFont(font);
        return headStyle;
    }

    /**
     * 获取指定字段设置的Cell样式
     *
     * @param field 字段
     * @return 字段对应的样式
     */
    private XSSFCellStyle getColumnStyle(Field field) {
        ExcelColumn excelColumn = field.getAnnotation(ExcelColumn.class);
        if (excelColumn == null) {
            throw new RuntimeException(field.getName() + " not an export-compliant field , Missing @ExcelColumn.");
        }
        boolean bold = excelColumn.bold();
        short color = excelColumn.color();
        String fontName = excelColumn.fontName();
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        Class<?> fieldType = field.getType();
        if (fieldType == String.class) {
            XSSFDataFormat dataFormat = workbook.createDataFormat();
            cellStyle.setDataFormat(dataFormat.getFormat("@"));
        } else if (fieldType == Date.class) {
            XSSFCreationHelper creationHelper = workbook.getCreationHelper();
            short dateFormat = creationHelper.createDataFormat().getFormat("yyyy/MM/dd");
            cellStyle.setDataFormat(dateFormat);
        }
        XSSFFont font = workbook.createFont();
        font.setBold(bold);
        font.setFontName(fontName);
        font.setFontHeightInPoints((short) 11);
        font.setColor(color);
        cellStyle.setFont(font);
        return cellStyle;
    }

    @SneakyThrows
    private void close() {
        if (this.outputStream != null) {
            outputStream.close();
        }
        if (workbook != null) {
            workbook.close();
        }
    }

}
