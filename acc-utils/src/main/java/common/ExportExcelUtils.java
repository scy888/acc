package common;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author: scyang
 * @program: ssm_super
 * @package: com.itheima.commom
 * @date: 2019-12-01 10:31:44
 * @describe: excel表格导出
 */
public class ExportExcelUtils {
    public static void exportExcel(HttpServletResponse response, String fileName, String[] headers, List<Object> dataList, String[] exportFiles) throws IOException {
        /**
         * @Description:
         * @methodName: exportExcel
         * @Param: [response, fileName, headers, dataList, exportFiles]
         * @return: void
         * @Author: scyang
         * @Date: 2019/12/1 10:35
         */

        /** 声明一个工作薄 */

        SXSSFWorkbook workbook;
        /** 工作表 */
        SXSSFSheet sheet;
        /** 设置类容样式 */
        CellStyle style_ = null;
        workbook = new SXSSFWorkbook();
        /** 生成一个表格 */
        sheet = workbook.createSheet();
        /** 设置表格默认宽度15个字节 */
        sheet.setDefaultColumnWidth((short) 15);
        /** 设置小标题行 */
        setlitterTitleRow(headers, sheet, workbook);
        int index = 1;
        /** 设置类容样式 */
        style_=setContextStyle(workbook);
        /** 遍历数据集合产生数据行 */
        Iterator<Object> iterator = dataList.iterator();
        while (iterator.hasNext()) {
            index++;
            SXSSFRow row = sheet.createRow(index);
            Object t = iterator.next();
            /** 当时map类型时,用map方式取值 */
            if (t instanceof Map) {
                /** 当导出为指定列时,按指定列取值 */
                for (int i = 0; i < exportFiles.length; i++) {
                    String exportFile = exportFiles[i];
                    Object value = ((Map) t).get(exportFile);
                    setCellValue(sheet, index, row, i, style_, value);
                }
            }
        }
        ServletOutputStream os = response.getOutputStream();/** 取得输出流 */
        fileName = new String(fileName.getBytes("GB2312"), "ISO8859-1");
        response.reset();/** 清空输出流 */
        response.setHeader("Content=disposition", "attachment;fileName=" + fileName + ".xlsx");/** 设置输出文件头 */
        response.setContentType("application/ms-excel");/** 定义输出类型 */
        workbook.write(os);
        os.flush();
        os.close();
    }

    private static CellStyle setContextStyle(SXSSFWorkbook workbook) {
        /** 生成并设置另一个样式 */
        CellStyle style_ = workbook.createCellStyle();
        /*style_.setBorderBottom(CellStyle.BORDER_THIN);
        style_.setBorderLeft(CellStyle.BORDER_THIN);
        style_.setBorderRight(CellStyle.BORDER_THIN);
        style_.setBorderTop(CellStyle.BORDER_THIN);
        style_.setAlignment(CellStyle.ALIGN_CENTER);
        style_.setVerticalAlignment(CellStyle.VERTICAL_CENTER);*/
        /** 生成另一个字体 */
        Font font_ = workbook.createFont();
        style_.setFont(font_);
        return style_;
    }

    private static void setCellValue(SXSSFSheet sheet, int index, SXSSFRow row, int i, CellStyle style_, Object value) {
        /** 设置单元格的值
         * @Description:
         * @methodName: setCellValue
         * @Param: [sheet, index, row, i, style_, value]
         * @return: void
         * @Author: scyang
         * @Date: 2019/12/1 15:30
         */
        SXSSFCell cell = row.createCell(i);
        cell.setCellStyle(style_);
        /** 判断值的类型后进行强制类型转换 */
        String textValue=null;
        if (value instanceof Date){
            Date data=(Date) value;
            SimpleDateFormat sdf=new SimpleDateFormat("yyy-MM-dd");
            sdf.format(data);
        }
        else {
            /** 其他数据类型都当字符窜简单处理 */
            textValue=value==null ? "":value.toString();
        }
        /** 如果不是图片数据,则利用正则表达式判断textValue是否全部有数字组成 */
        if (textValue!=null){
            cell.setCellValue(textValue);
        }
    }



    private static void setlitterTitleRow(String[] headers, SXSSFSheet sheet, SXSSFWorkbook workbook) {
        /**
         * @Description: 设置小标题
         * @methodName: setlitterTitleRow
         * @Param: [headers, sheet, workbook]
         * @return: void
         * @Author: scyang
         * @Date: 2019/12/1 15:31
         */
        /** 设置字体 */
        Font font = setFontStyle(workbook);
        /** 设置小标题样式 */
        CellStyle style = setTitleStyle(workbook, font);
        SXSSFRow row = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            SXSSFCell cell = row.createCell(i);
            cell.setCellStyle(style);
            cell.setCellValue(headers[i]);
        }
    }
    private static Font setFontStyle(SXSSFWorkbook workbook) {
        /**
         * @Description: 设置单元格字体
         * @methodName: setFontStyle
         * @Param: [workbook]
         * @return: org.apache.poi.ss.usermodel.Font
         * @Author: scyang
         * @Date: 2019/12/1 15:43
         */
        /** 生成一个字体 */
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short)12);
        font.setFontName("宋体");
       /* font.setBoldweight(Font.BOLDWEIGHT_BOLD);*/
        return font;
    }

    private static CellStyle setTitleStyle(SXSSFWorkbook workbook, Font font) {
        /**
         * @Description: 设置样式
         * @methodName: setTitleStyle
         * @Param: [workbook, font]
         * @return: org.apache.poi.ss.usermodel.CellStyle
         * @Author: scyang
         * @Date: 2019/12/1 15:48
         */
        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);/** 自动换行 */

        /*style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);*/
        /** 把字体应用到当前样式 */
        style.setFont(font);
        return style;
    }
}