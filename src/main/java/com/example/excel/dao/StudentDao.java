package com.example.excel.dao;

import com.example.excel.enetity.Student;

import java.util.List;

public interface StudentDao {

    Student getStudentById(Integer id);

    public List<Student> getStudentList();

    public int add(Student student);

    public int update(Integer id, Student student);

    public int delete(Integer id);
}
