package com.example.excel.controller;

import com.example.excel.common.JsonResult;
import com.example.excel.enetity.Student;
import com.example.excel.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping(value = "/test/asd")
    public String test(){
        return "hello word!";
    }

    /**
     * 根据ID查询学生
     * @param id
     * @return
     */
    @RequestMapping(value = "/student/{id}", method = RequestMethod.GET)
    public ResponseEntity<JsonResult> getUserById (@PathVariable(value = "id") Integer id){
        JsonResult r = new JsonResult();
        try {
            Student student = studentService.getStudentById(id);
            r.setResult(student);
            r.setStatus("ok");
        } catch (Exception e) {
            r.setResult(e.getClass().getName() + ":" + e.getMessage());
            r.setStatus("error");
            e.printStackTrace();
        }
        return ResponseEntity.ok(r);
    }

    /**
     * 查询学生列表
     * @return
     */
    @GetMapping(value = "/students")
    public ResponseEntity<JsonResult> getStudentList (){
        JsonResult r = new JsonResult();
        try {
            List<Student> studentList = studentService.getStudentList();
            r.setResult(studentList);
            r.setStatus("ok");
        } catch (Exception e) {
            r.setResult(e.getClass().getName() + ":" + e.getMessage());
            r.setStatus("error");
            e.printStackTrace();
        }
        return ResponseEntity.ok(r);
    }

    /**
     * 添加学生
     * @param student
     * @return
     */
    @PostMapping(value = "/student")
    public ResponseEntity<JsonResult> add (@RequestBody Student student){
        JsonResult r = new JsonResult();
        try {
            int orderId = studentService.add(student);
            if (orderId < 0) {
                r.setResult(orderId);
                r.setStatus("fail");
            } else {
                r.setResult(orderId);
                r.setStatus("ok");
            }
        } catch (Exception e) {
            r.setResult(e.getClass().getName() + ":" + e.getMessage());
            r.setStatus("error");
            e.printStackTrace();
        }
        return ResponseEntity.ok(r);
    }

    /**
     * 根据id删除学生
     * @param id
     * @return
     */
    @DeleteMapping(value = "/student/{id}")
    public ResponseEntity<JsonResult> delete (@PathVariable(value = "id") Integer id){
        JsonResult r = new JsonResult();
        try {
            int ret = studentService.delete(id);
            if (ret < 0) {
                r.setResult(ret);
                r.setStatus("fail");
            } else {
                r.setResult(ret);
                r.setStatus("ok");
            }
        } catch (Exception e) {
            r.setResult(e.getClass().getName() + ":" + e.getMessage());
            r.setStatus("error");

            e.printStackTrace();
        }
        return ResponseEntity.ok(r);
    }

    /**
     * 根据id修改学生信息
     * @param student
     * @return
     */
    @PutMapping(value = "/student/{id}")
    public ResponseEntity<JsonResult> update (@PathVariable("id") Integer id, @RequestBody Student student){
        JsonResult r = new JsonResult();
        try {
            int ret = studentService.update(id, student);
            if (ret < 0) {
                r.setResult(ret);
                r.setStatus("fail");
            } else {
                r.setResult(ret);
                r.setStatus("ok");
            }
        } catch (Exception e) {
            r.setResult(e.getClass().getName() + ":" + e.getMessage());
            r.setStatus("error");

            e.printStackTrace();
        }
        return ResponseEntity.ok(r);
    }

    @PostMapping(path = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String importStudent(
            @RequestParam("file") MultipartFile multipartFile)
            throws IOException {
        long begin = System.currentTimeMillis();
        studentService.importStudent(multipartFile);
        long end = System.currentTimeMillis();
        long time = end - begin;
        return "success：" +time+ " 毫秒"+" -- " + (time/1000) + " 秒";
    }

    @PostMapping(path = "/import/thread", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String importStudentThread(
            @RequestParam("file") MultipartFile multipartFile)
            throws IOException, ExecutionException, InterruptedException {
        long begin = System.currentTimeMillis();
        String result ="";
        try {
            result = studentService.importStudentThread(multipartFile);
        }catch (Exception e){}
        long end = System.currentTimeMillis();
        long time = end - begin;
        return "success："+ result+"  耗时" +time+ " 毫秒"+ " -- " + (time/1000) + " 秒";
    }

    @PostMapping(path = "/import/thread/async", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String importStudentThreadAsync(
            @RequestParam("file") MultipartFile multipartFile)
            throws Exception {
        long begin = System.currentTimeMillis();
        String result ="";
        try {
            result = studentService.importStudentThreadAsync(multipartFile);
        }catch (Exception e){}
        long end = System.currentTimeMillis();
        long time = end - begin;
        return "success："+ result+"  耗时" +time+ " 毫秒"+ " -- " + (time/1000) + " 秒";
    }

}
