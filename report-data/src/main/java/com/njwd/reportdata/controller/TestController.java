package com.njwd.reportdata.controller;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.metadata.Table;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.njwd.support.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *@description:
 *@author: fancl
 *@create: 2020-02-25 
 */
@RestController
@RequestMapping("demo")
public class TestController {
    @RequestMapping("export1")
    public void export1(HttpServletResponse response){

        // 文件输出位置
        String outPath = "d:\\test.xlsx";

        try {
            ServletOutputStream out = response.getOutputStream();
            // 所有行的集合
            List<List<Object>> list = new ArrayList<>();

            for (int i = 1; i <= 10; i++) {
                // 第 n 行的数据
                List<Object> row = new ArrayList<>();
                row.add("第" + i + "单元格");
                row.add("第" + i + "单元格");
                list.add(row);
            }

            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX, true);
            // 表单
            Sheet sheet = new Sheet(1,0);
            sheet.setSheetName("第一个Sheet");
            // 创建一个表格
            Table table = new Table(1);
            // 动态添加 表头 headList --> 所有表头行集合
            List<List<String>> headList = new ArrayList<>();
            // 第 n 行 的表头
            List<String> headTitle0 = new ArrayList<>();
            List<String> headTitle1 = new ArrayList<>();
            List<String> headTitle2 = new ArrayList<>();
            headTitle0.add("最顶部-1");
            headTitle0.add("标题1");
            headTitle1.add("最顶部-1");
            headTitle1.add("标题2");
            headTitle2.add("最顶部-1");
            headTitle2.add("标题3");

            headList.add(headTitle0);
            headList.add(headTitle1);
            headList.add(headTitle2);
            table.setHead(headList);
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("utf-8");
            //解决下载附件中文名称变成下划线
            response.addHeader("Content-Disposition", "attachment;filename=" +
                    String.valueOf(System.currentTimeMillis())+ ".xlsx");

            writer.write1(list,sheet,table);
            // 记得 释放资源
            writer.finish();
            System.out.println("ok");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //两层表头
    @RequestMapping("export2")
    public void export2(HttpServletResponse response){

        try {
            ServletOutputStream out = response.getOutputStream();
            // 所有行的集合
            List<List<Object>> list = new ArrayList<>();

            for (int i = 1; i <= 10; i++) {
                // 第 n 行的数据
                List<Object> row = new ArrayList<>();
                row.add("第" + i + "单元格");
                row.add("第" + i + "单元格");
                list.add(row);
            }

            ExcelWriter writer = new ExcelWriter(out, ExcelTypeEnum.XLSX, true);
            // 表单
            Sheet sheet = new Sheet(1,0);
            sheet.setSheetName("第一个Sheet");
            // 创建一个表格
            Table table = new Table(1);
            // 动态添加 表头 headList --> 所有表头行集合
            List<List<String>> headList = new ArrayList<>();
            // 第 n 行 的表头
            List<String> headTitle0 = new ArrayList<>();
            List<String> headTitle1 = new ArrayList<>();
            List<String> headTitle2 = new ArrayList<>();
            headTitle0.add("左边的");
            headTitle0.add("左边的");
            headTitle1.add("最顶部");
            headTitle1.add("标题1");
            headTitle2.add("最顶部");
            headTitle2.add("标题2");

            headList.add(headTitle0);
            headList.add(headTitle1);
            headList.add(headTitle2);
            table.setHead(headList);
            sheet.setHead(headList);
            response.setContentType("multipart/form-data");
            response.setCharacterEncoding("utf-8");
            //解决下载附件中文名称变成下划线
            response.addHeader("Content-Disposition", "attachment;filename=" +
                    String.valueOf(System.currentTimeMillis())+ ".xlsx");

            writer.write1(list,sheet);
            // 记得 释放资源
            writer.finish();
            System.out.println("ok");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
