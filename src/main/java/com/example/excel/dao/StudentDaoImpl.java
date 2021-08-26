package com.example.excel.dao;

import com.example.excel.enetity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public class StudentDaoImpl implements StudentDao{

    @Autowired
    private JdbcTemplate jdbcTemplate;  //这个是系统自带的

    @Override
    public Student getStudentById(Integer id) {
        List<Student> list = jdbcTemplate.query("select * from student where id = ?", new Object[]{id}, new BeanPropertyRowMapper(Student.class));
        if(list!=null && list.size()>0){
            return list.get(0);
        }else{
            return null;
        }
    }

    @Override
    public List<Student> getStudentList() {
        List<Student> list = jdbcTemplate.query("select * from student", new Object[]{}, new BeanPropertyRowMapper(Student.class));
        if(list!=null && list.size()>0){
            return list;
        }else{
            return null;
        }
    }

    @Override
    public int add(Student student) {
        return jdbcTemplate.update("insert into student(name, company, date ) values(?, ?, ?)",
                student.getName(), student.getCompany(), new Date());
    }

    @Override
    public int update(Integer id, Student student) {
        return jdbcTemplate.update("UPDATE student SET name = ? , company = ? WHERE id=?",
                student.getName(),student.getCompany(), id);
    }

    @Override
    public int delete(Integer id) {
        return jdbcTemplate.update("DELETE from student where id = ? ",id);
    }
}
