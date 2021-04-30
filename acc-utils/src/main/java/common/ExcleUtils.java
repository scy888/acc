package common;

import entity.User;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: scyang
 * @program: ssm_super
 * @package: com.itheima.commom
 * @date: 2020-04-18 10:02:04
 * @describe: excle表格的导入和导出
 */

public class ExcleUtils {
    @Autowired
    private HttpServletResponse response;

    public static void exportExcle(HttpServletResponse response, String path, String sheetName, String titleName, String[] heards,
                                   String[] fields, List<Map<String, Object>> dataList) throws Exception {

        /** 创建一个工作薄对象 */
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        /** 创建一个工作表格对象 */
        SXSSFSheet sheet = workbook.createSheet(sheetName);
        /** 设置表格的默认宽度15个字节 */
        sheet.setDefaultColumnWidth(15);
        /** 合并小标题的单元格 */
        CellRangeAddress region = new CellRangeAddress(0, 0, 0, dataList.get(0).size() - 1);
        sheet.addMergedRegion(region);
        /** 设置合并单元格的边框 */
        // setBorder(workbook, sheet, region);
        /** 设置小标题样式 */
        CellStyle title_style = createTitleStyle(workbook);
        /** 设置小标题单元格值 */
        SXSSFRow tille_row = sheet.createRow(0);
        /** 设置标题行的高度 */
        tille_row.setHeightInPoints((30));
        SXSSFCell title_cell = tille_row.createCell(0);
        title_cell.setCellStyle(title_style);
        title_cell.setCellValue(titleName);
        /** 设置头行(表格的第二行) */
        createHeardRow(workbook, sheet, heards, 1);
        /** 设置类容行(表格的第三行开始) */
        createContextRow(workbook, sheet, fields, dataList, 2);
        /** 数据的导出 */
        FileOutputStream fos = new FileOutputStream(path);
        workbook.write(fos);
        fos.flush();
        fos.close();
        workbook.close();

        /*ServletOutputStream os = response.getOutputStream();*//** 取得输出流 *//*
        path = new String(path.getBytes("GB2312"), "ISO8859-1");
        response.reset();*//** 清空输出流 *//*
        response.setHeader("Content=disposition", "attachment;fileName=" + path + ".xlsx");*//** 设置输出文件头 *//*
        response.setContentType("application/ms-excel");*//** 定义输出类型 *//*
        workbook.write(os);
        os.flush();
        os.close();*/
    }

    private static void createContextRow(SXSSFWorkbook workbook, SXSSFSheet sheet, String[] fields,
                                         List<Map<String, Object>> dataList, int num) {
        /**
         * @Description: workbook
         * @methodName: createContextRow
         * @Param: [workbook, sheet, fields, dataList, num]
         * @return: void
         * @Author: scyang
         * @Date: 2020/4/18 13:39
         */
        /** 获取类容行 */
        for (Map<String, Object> map : dataList) {
            SXSSFRow contextRow = sheet.createRow(num);
            /** 获取类容行的列 */
            for (int i = 0; i < fields.length; i++) {
                SXSSFCell contextCell = contextRow.createCell(i);
                /** 获取每行列的值 */
                Object obj = map.get(fields[i]);
                obj = obj == null ? "" : obj;
                /** 如果时间是Date类型要处理成字符串类型 */
                if (obj instanceof Date) {
                    Date date = (Date) obj;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒");
                    contextCell.setCellStyle(createContextStyle(workbook));
                    contextCell.setCellValue(sdf.format(date));
                } else {
                    contextCell.setCellStyle(createContextStyle(workbook));
                    contextCell.setCellValue(obj.toString());
                }
            }
            num++;
        }
    }

    private static CellStyle createContextStyle(SXSSFWorkbook workbook) {
        /**
         * @Description: 设置内容行样式
         * @methodName: createContextStyle
         * @Param: [workbook]
         * @return: org.apache.poi.ss.usermodel.CellStyle
         * @Author: scyang
         * @Date: 2020/4/18 13:54
         */
        CellStyle style_ = createStyle(workbook);
        style_.setFont(workbook.createFont());
        return style_;
    }


    private static void createHeardRow(SXSSFWorkbook workbook, SXSSFSheet sheet, String[] heards, int num) {
        /**
         * @Description: 设置头行
         * @methodName: createHeardRow
         * @Param: [workbook, sheet, heards, num]
         * @return: void
         * @Author: scyang
         * @Date: 2020/4/18 13:20
         */

        /** 获取行 */
        SXSSFRow heardRow = sheet.createRow(num);
        /** 设置头行样式 */
        CellStyle style_ = createHeardStyle(workbook);
        /** 创建头行的列 */
        for (int i = 0; i < heards.length; i++) {
            SXSSFCell heardCell = heardRow.createCell(i);
            heardCell.setCellValue(heards[i]);
            heardCell.setCellStyle(style_);
        }
    }

    private static CellStyle createHeardStyle(SXSSFWorkbook workbook) {
        /**
         * @Description: 设置头行样式
         * @methodName: createHeardStyle
         * @Param: [workbook]
         * @return: org.apache.poi.ss.usermodel.CellStyle
         * @Author: scyang
         * @Date: 2020/4/18 13:23
         */
        CellStyle style_ = createStyle(workbook);
        /** 设置头行字体 */
        Font font_ = createHeadFont(workbook);
        style_.setFont(font_);
        return style_;
    }

    private static Font createHeadFont(SXSSFWorkbook workbook) {
        /**
         * @Description: 设置头行字体
         * @methodName: createHeadFont
         * @Param: [workbook]
         * @return: org.apache.poi.ss.usermodel.Font
         * @Author: scyang
         * @Date: 2020/4/18 13:26
         */
        Font font_ = workbook.createFont();
        font_.setFontName("宋体");/** 设置字体 */
        font_.setItalic(false);/** 设置是否为斜体 */
        font_.setFontHeightInPoints((short) 15);/** 设置字体大小 */
        //font_.setFontHeight((short) 300);
        font_.setColor(Font.COLOR_NORMAL);/** 设置字体颜色 */
        // font_.setBoldweight(Font.BOLDWEIGHT_BOLD);/** 字体加粗 */
        return font_;
    }


    private static CellStyle createTitleStyle(SXSSFWorkbook workbook) {
        /** 设置普通样式*/
        CellStyle style_ = createStyle(workbook);
        style_.setFillBackgroundColor(/*HSSFColor.YELLOW.index*/(short) 20);
        /** 设置小标题字体 */
        Font font_ = createTitleFont(workbook);
        style_.setFont(font_);
        return style_;
    }

    private static Font createTitleFont(SXSSFWorkbook workbook) {
        /**
         * @Description: 设置小标题字体
         * @methodName: createTitleFont
         * @Param: [workbook]
         * @return: org.apache.poi.ss.usermodel.Font
         * @Author: scyang
         * @Date: 2020/4/18 13:00
         */
        Font font_ = workbook.createFont();
        font_.setFontName("宋体");/** 设置字体 */
        font_.setItalic(false);/** 设置是否为斜体 */
        font_.setFontHeightInPoints((short) 25);/** 设置字体大小 */
        font_.setFontHeight((short) 400);
        font_.setColor(Font.COLOR_RED);/** 设置字体颜色 */
        // font_.setBoldweight(Font.BOLDWEIGHT_BOLD);/** 字体加粗 */
        return font_;
    }

    private static CellStyle createStyle(SXSSFWorkbook workbook) {
        /**
         * @Description: 设置普通样式
         * @methodName: createStyle
         * @Param: [workbook]
         * @return: org.apache.poi.ss.usermodel.CellStyle
         * @Author: scyang
         * @Date: 2020/4/18 12:55
         */
        CellStyle style_ = workbook.createCellStyle();
        style_.setAlignment(HorizontalAlignment.CENTER);//左右居中
        style_.setVerticalAlignment(VerticalAlignment.CENTER);//上下居中
        style_.setBorderTop(BorderStyle.THIN);//设置上边框
        style_.setBorderBottom(BorderStyle.THIN);//设置下边框
        style_.setBorderLeft(BorderStyle.THIN);//设置左边框
        style_.setBorderRight(BorderStyle.THIN);//设置右边框
        style_.setTopBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        style_.setBottomBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        style_.setLeftBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        style_.setRightBorderColor(HSSFColor.HSSFColorPredefined.BLACK.getIndex());
        style_.setWrapText(true);/** 自动换行 */
        return style_;
    }
    /*private static void setBorder(SXSSFWorkbook workbook, SXSSFSheet sheet, CellRangeAddress region) {
        RegionUtil.setBorderTop(1, region, sheet, workbook);*//** 上边框 *//*
        RegionUtil.setBorderBottom(1, region, sheet, workbook);*//** 下边框 *//*
        RegionUtil.setBorderLeft(1, region, sheet, workbook);*//** 左边框 *//*
        RegionUtil.setBorderRight(1, region, sheet, workbook);*//** 右边框 *//*
    }*/

    /****************************************************************************/

    public static List<Object[]> importExcle(String path, int sheetNum, int contextRowNum) throws Exception {
        List<Object[]> list = new ArrayList<>();
        /** 获取工作薄对象 */
        XSSFWorkbook workbook = new XSSFWorkbook(path);
        /** 获取工作表对象 */
        XSSFSheet sheet = workbook.getSheetAt(sheetNum);
        /** 获取工作表的最后一行 */
        int lastRowNum = sheet.getLastRowNum();
        for (int i = contextRowNum; i <= lastRowNum; i++) {
            /** 获取类容行对象 */
            XSSFRow row = sheet.getRow(i);
            /** 获取类容行的第一列 */
            short firstCellNum = row.getFirstCellNum();
            /** 获取类容行的最后一列 */
            short lastCellNum = row.getLastCellNum();
            Object[] objects = new Object[lastCellNum];
            for (short j = firstCellNum; j < lastCellNum; j++) {
                objects[j] = row.getCell(j).getStringCellValue();
            }
            list.add(objects);
        }
        return list;
    }

    /****************************************************************************/

    @Test
    public void testExclePort() throws Exception {
        List<User> userList = Arrays.asList(
                new User(1L, "张无忌", new Date(2020 - 1900, 4 - 1, 1, 2, 18, 27), 20, "男", "明教", "13456", "13297053048", new BigDecimal("128.369")),
                new User(2L, "赵敏", new Date(2020 - 1900, 4 - 1, 11, 12, 15, 27), 19, "女", "蒙古", "13456", "13297053048", new BigDecimal("128.369")),
                new User(3L, "周芷若", new Date(2020 - 1900, 4 - 1, 6, 18, 18, 27), 18, "女", "峨嵋", "13456", "13297053048", new BigDecimal("128.369")),
                new User(4L, "小昭", new Date(2020 - 1900, 4 - 1, 30, 23, 59, 59), 18, "女", "波斯", "13456", "13297053048", new BigDecimal("128.369")),
                new User(5L, "阿离", new Date(2020 - 1900, 4 - 1, 15, 8, 18, 27), 17, "女", "灵蛇岛", "13456", "13297053048", new BigDecimal("128.369")));

        String[] heards = {"用户id", "姓名", "出生日期", "年龄", "性别", "地址", "密码", "联系方式", "薪资"};
        String[] fields = {"id", "username", "birthday", "age", "sex", "address", "password", "mobile", "money"};
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (User user : userList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", user.getId());
            map.put("username", user.getUsername());
            map.put("birthday", user.getBirthday());
            map.put("age", user.getAge());
            map.put("sex", user.getSex());
            map.put("address", user.getAddress());
            map.put("password", user.getPassword());
            map.put("mobile", user.getMobile());
            map.put("money", user.getMoney());
            mapList.add(map);
        }
        exportExcle(response, "E:\\yitian.xlsx", "倚天屠龙记", "倚天屠龙记主要人物", heards, fields, mapList);

    }

    @Test
    public void testExcleImport() throws Exception {
        List<Object[]> list = importExcle("E:\\yitian.xlsx", 0, 2);
        List<User> userList = new ArrayList<>();
        for (Object[] objects : list) {
            User user = new User();
           // user.setId(new Integer(objects[0].toString()));
            user.setUsername(objects[1].toString());
            user.setBirthday(new SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒").parse(objects[2].toString()));
            user.setAge(Integer.parseInt(objects[3].toString()));
            user.setSex(objects[4].toString());
            user.setAddress((String) objects[5]);
            user.setPassword(objects[6].toString());
            user.setMobile(objects[7].toString());
            user.setMoney(new BigDecimal(objects[8].toString()));
            userList.add(user);
        }
        for (User user : userList) {
            System.out.println(user);
        }
    }
}
