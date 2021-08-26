package com.example.excel.controller;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;

@RestController
public class ThreadExportController {

    @GetMapping(value = "/test/data")
    public String test(){
        return "hello word!";
    }

    @GetMapping(value = "/create/data")
    public String data(@RequestParam("count") int count) throws IOException {
        long begin = System.currentTimeMillis();

        String filePath ="/Volumes/myproject/demo13/students.xlsx";
        File file = new File(filePath);
        FileInputStream in =new FileInputStream(file);
        //读取excel模板
        XSSFWorkbook wb = new XSSFWorkbook(in);
        //读取了模板内所有sheet内容
        XSSFSheet sheet = wb.getSheetAt(0);
        //如果这行没有了，整个公式都不会有自动计算的效果的

        int j = 1;
        // 订单明细列表信息 从第4行开始插入明细
        for (int i = 0; i < count ; i++) {
            XSSFRow row = sheet.createRow((j + i));
            row.createCell(0).setCellValue("jax"+(i+1));
            row.createCell(1).setCellValue("bat"+(i+1));
        }

        // 保存文件的路径

        String newFileName = "students"+ count + ".xlsx";
        //修改模板内容导出新模板
        FileOutputStream out = new FileOutputStream(newFileName);
        wb.write(out);

        long end = System.currentTimeMillis();
        long time;
        String str = "";
        if(count >= 0 && count < 10000){
            time = (end - begin) ;
            str = time + " 毫秒";
        }else if (count > 1000000) {
            time = (end - begin) / (1000 * 60) ;
            str = time + " 分钟";
        }else{
            time = (end - begin) / (1000 ) ;
            str = time + " 秒";
        }
        return "success：" + str;
    }
}
