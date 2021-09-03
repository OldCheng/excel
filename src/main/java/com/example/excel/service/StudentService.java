package com.example.excel.service;

import com.example.excel.enetity.ImgParam;
import com.example.excel.enetity.Student;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface StudentService {

    Student getStudentById(Integer id);

    public List<Student> getStudentList();

    public int add(Student student);

    public int update(Integer id, Student student);

    public int delete(Integer id);

    public String importStudent(MultipartFile multipartFile) throws IOException;

    String importStudentThreadAsync(MultipartFile multipartFile) throws Exception;

    String importStudentThread(MultipartFile multipartFile) throws IOException, ExecutionException, InterruptedException;

    void exportPdf(HttpServletResponse response) throws UnsupportedEncodingException;

    void imgExportPdf(ImgParam imgParam, HttpServletResponse response) throws IOException;
}
