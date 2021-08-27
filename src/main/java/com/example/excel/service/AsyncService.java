package com.example.excel.service;

import org.apache.poi.ss.usermodel.Workbook;

import java.util.concurrent.CountDownLatch;

public interface AsyncService {

    String insertImportData(Workbook workbook, Integer startRow, Integer endRow, StudentService studentService, CountDownLatch latch);

}
