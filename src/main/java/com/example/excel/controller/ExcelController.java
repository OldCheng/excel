package com.example.excel.controller;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;

@RestController
@RequestMapping("/excel")
public class ExcelController {

    @GetMapping("/data")
    public static String insert(@RequestParam("count") int count) throws IOException {
        String filePath ="/Volumes/myproject/excel/BulkUploadTemplate.73de0a5e.xlsx";
        File file = new File(filePath);
        FileInputStream in =new FileInputStream(file);
        //读取excel模板
        XSSFWorkbook wb = new XSSFWorkbook(in);
        //读取了模板内所有sheet内容
        XSSFSheet sheet = wb.getSheetAt(4);
        //如果这行没有了，整个公式都不会有自动计算的效果的

        int j = 4;
        // 订单明细列表信息 从第4行开始插入明细
        for (int i = 0; i < count ; i++) {
            XSSFRow row = sheet.createRow((j + i));
            row.createCell(1).setCellValue("fgm_import_test"+(i+1));
            row.createCell(2).setCellValue("art");
            row.createCell(3).setCellValue("10");
            row.createCell(4).setCellValue("30");
            row.createCell(5).setCellValue("take it");
            row.createCell(6).setCellValue("just tag");
            row.createCell(7).setCellValue("Art");
            row.createCell(8).setCellValue("68");
            row.createCell(9).setCellValue("99");
            row.createCell(10).setCellValue("No");
            row.createCell(19).setCellValue("90");
            row.createCell(20).setCellValue("66");
            row.createCell(21).setCellValue("7");
            row.createCell(22).setCellValue("8");
            row.createCell(23).setCellValue("9");
            row.createCell(25).setCellValue("Flat Rate");
            row.createCell(26).setCellValue("UPS");
            row.createCell(27).setCellValue("THREE_DAY_SELECT");
            row.createCell(28).setCellValue("87");
            row.createCell(30).setCellValue("60 Days");
            row.createCell(31).setCellValue("No");
        }


        // 保存文件的路径

        String newFileName = "BulkUploadTemplate."+ count + ".xlsx";

        //修改模板内容导出新模板
        FileOutputStream out = new FileOutputStream(newFileName);
        wb.write(out);

        return "success";
    }

    public static void main(String[] args) throws IOException {
        insert(100000);
    }
}
