package com.example.excel.utility;

import com.example.excel.enetity.Student;
import com.example.excel.service.impl.StudentService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;

public class ImportTask implements Callable<Integer> {


    private Workbook workbook;

    private Integer startRow;

    private Integer endRow;

    private StudentService studentService;

    private Semaphore semaphore;

    private CountDownLatch latch;

    public ImportTask(Workbook workbook, Integer startRow, Integer endRow, StudentService studentService, CountDownLatch latch) {
        this.workbook = workbook;
        this.startRow = startRow;
        this.endRow = endRow;
        this.studentService = studentService;
        this.latch = latch;
    }

    @Override
    public Integer call() throws Exception {
//        System.out.println("线程ID：<{}>开始运行,startRow:{},endRow:{}" + Thread.currentThread().getId()+"  "+startRow+"  "+endRow);
//        semaphore.acquire();
//        System.out.println("消耗了一个信号量，剩余信号量为:{}"+semaphore.availablePermits());

        Sheet sheet = workbook.getSheetAt(0);
        int count = 0;
        for(int i = startRow; i <= endRow; i++){
            Student student = new Student();
            Row row = sheet.getRow(i);
            Cell cell0 = row.getCell(0);
            Cell cell1 = row.getCell(1);
            String name = cell0.getStringCellValue();
            String company = cell1.getStringCellValue();
            student.setName(name);
            student.setCompany(company);
            count += studentService.add(student);
        }
        latch.countDown();
//        semaphore.release();
        return count;
    }
}
