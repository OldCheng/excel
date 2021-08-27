package com.example.excel.service.impl;

import com.example.excel.enetity.Student;
import com.example.excel.service.AsyncService;
import com.example.excel.service.StudentService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CountDownLatch;

@Service
public class AsyncServiceImpl implements AsyncService {

    @Override
    @Async("asyncServiceExecutor")
    public String insertImportData(Workbook workbook, Integer startRow, Integer endRow, StudentService studentService, CountDownLatch latch) {
        System.out.println(Thread.currentThread().getName()+"**************"+Thread.currentThread().getId());
        Sheet sheet = workbook.getSheetAt(0);
        try {
            for(int i = startRow; i <= endRow; i++){
                Student student = new Student();
                Row row = sheet.getRow(i);
                Cell cell0 = row.getCell(0);
                Cell cell1 = row.getCell(1);
                String name = cell0.getStringCellValue();
                String company = cell1.getStringCellValue();
                student.setName(name);
                student.setCompany(company);
                studentService.add(student);
                StudentServiceImpl.writer();
            }
        }catch (Exception ignored){}
        finally {
            latch.countDown();
        }
        return "success";
    }

}
