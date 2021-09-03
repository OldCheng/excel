package com.example.excel.service.impl;

import com.example.excel.dao.StudentDao;
import com.example.excel.enetity.ImgParam;
import com.example.excel.enetity.Student;
import com.example.excel.service.AsyncService;
import com.example.excel.service.StudentService;
import com.example.excel.utility.ImportTask;
import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfAcroForm;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

@Service
public class StudentServiceImpl implements StudentService {


    public static int dataCount = 0;

    public static synchronized void writer() { //1
        dataCount++;
    }

    public static synchronized int read() { //1
        return dataCount;
    }

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private AsyncService asyncService;

    @Override
    public Student getStudentById(Integer id) {
        return studentDao.getStudentById(id);
    }

    @Override
    public List<Student> getStudentList() {
        return studentDao.getStudentList();
    }

    @Override
    public int add(Student student) {
        return studentDao.add(student);
    }

    @Override
    public int update(Integer id, Student student) {
        return studentDao.update(id, student);
    }

    @Override
    public int delete(Integer id) {
        return studentDao.delete(id);
    }

    @Override
    public String importStudent(MultipartFile multipartFile) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        int rowCount = sheet.getPhysicalNumberOfRows(); //获取总行数
        //遍历每一行
        for (int r = 1; r < rowCount; r++) {
            Student student = new Student();
            Row row = sheet.getRow(r);
            Cell cell0 = row.getCell(0);
            Cell cell1 = row.getCell(1);
            String name = cell0.getStringCellValue();
            String company = cell1.getStringCellValue();
            student.setName(name);
            student.setCompany(company);
            studentDao.add(student);
        }
        return null;
    }

    @Override
    public String importStudentThread(MultipartFile multipartFile) throws IOException, ExecutionException, InterruptedException {
        //设置一个信号量为5的信号量，限制同时运行的线程数量最大为5
//        Semaphore semaphore = new Semaphore(10);

        XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        int rowCount = sheet.getPhysicalNumberOfRows(); //获取总行数
        //第一行是表头，实际行数要减1
        int rows = rowCount - 1;
        //一个线程让他处理200个row,也许可以处理更多吧
        //int threadNum = rows/1000 + 1; //线程数量
        int threadNum = 8 + 1; //线程数量  cpu 核shu：cup-core+1  或两倍的 cup-core
        //设置一个倒计时门闩，用来处理主线程等待蚂蚁线程执行完成工作之后再运行
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);
        //创建一个定长的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
        //System.out.println("开始创建线程,数据总行数:{},线程数量:{}",rows,threadNum);

        int handleCount = threadHandleCount(rowCount);

        List<Future<Integer>> futures = new ArrayList<>();
        int successCount = 0;
        for(int i = 1; i <= threadNum; i++){
            int startRow = (i-1) * handleCount +1;
            int endRow = i * handleCount;
            if(i == threadNum){
                endRow = rows;
            }
//            System.out.println("开始执行线程方法,线程ID:<{}>,线程名称:<{}>",Thread.currentThread().getId(),Thread.currentThread().getName());
            Future<Integer> future = executorService.submit(new ImportTask(workbook, startRow, endRow,this,countDownLatch));
            futures.add(future);
//            System.out.println("结束线程执行方法,返回结果:<{}>,当前线程ID:<{}>,当前线程名称:<{}>",JSON.toJSONString(future),Thread.currentThread().getId(),Thread.currentThread().getName());
        }
        for(Future<Integer> future : futures){
            successCount += future.get();
        }
        countDownLatch.await();
        //countDownLatch.await(60,TimeUnit.SECONDS);
        executorService.shutdown();

        String result = "总行数："+rowCount+"  开起的线程数量："+threadNum+ " 导入成功的条数："+successCount;

        return result;
    }

    @Override
    public void exportPdf(HttpServletResponse response) throws UnsupportedEncodingException {
        String fileName = new Date().getTime()+"aaa.pdf"; // 设置response方式,使执行此controller时候自动出现下载页面,而非直接使用excel打开
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","filename=" + new String(fileName.getBytes(), "iso8859-1"));
//        response.setHeader("Content-Disposition","attachment;filename=" + new String(fileName.getBytes(), "iso8859-1"));
        BaseFont bf;
        Font font = null;
        Font font2 = null;
        try {

            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
                    BaseFont.NOT_EMBEDDED);//创建字体
            font = new Font(bf, 12);//使用字体
            font2 = new Font(bf, 12, Font.BOLD);//使用字体
        } catch (Exception e) {
            e.printStackTrace();
        }
        Document document = new Document();
        try {
//            PdfWriter.getInstance(document, new FileOutputStream("aaa.pdf"));
            FileOutputStream fileOutputStream = new FileOutputStream("aaa.pdf");
            PdfWriter instance = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();
            Paragraph elements = new Paragraph("常州武进1区飞行报告", font2);
            elements.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(elements);
            Image png = Image.getInstance("/Volumes/myproject/excel/pdf.jpeg");
//            Image png = Image.getInstance("https://imgproxy.dev.platform.michaels.com/XXTpt9MjeIKtJwhEo-W6WCFk1c9-K_Ll4qb5g7o5vco/aHR0cHM6Ly9zdG9yYWdlLmdvb2dsZWFwaXMuY29tL2Ntcy1mZ20tZGV2MDAvNTM2MjgzMzM2NDIyNTc2MTI4MA.png");
            png.setAlignment(Image.ALIGN_CENTER);
            document.add(png);
            document.add(new Paragraph("任务编号：20190701        开始日期：20190701", font));
            document.add(new Paragraph("任务名称：常州武进1区     结束日期：20190701", font));
            document.add(new Paragraph("平均飞行高度：100m        平均飞行速度：100km/h", font));
            document.add(new Paragraph("任务面积：1000㎡      结束日期：20190701", font));
            document.add(new Paragraph("飞行总时长：1000㎡", font));
            document.addCreationDate();
            document.close();
        } catch (Exception e) {
            System.out.println("file create exception");
        }
    }

    @Override
    public void imgExportPdf(ImgParam imgParam, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition","filename=" + new String("cccasd.pdf".getBytes(), "iso8859-1"));

        String replace = imgParam.getImgCode().replace("data:image/jpeg;base64,", "");
        // 解码
        byte[] b = Base64.getDecoder().decode(replace);
        BaseFont bf;
        Font font = null;
        Font font2 = null;
        try {

            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
                    BaseFont.NOT_EMBEDDED);//创建字体
            font = new Font(bf, 12);//使用字体
            font2 = new Font(bf, 12, Font.BOLD);//使用字体
        } catch (Exception e) {
            e.printStackTrace();
        }
        Document document = new Document();
        try {
//            PdfWriter.getInstance(document, new FileOutputStream("aaa.pdf"));
            PdfWriter instance = PdfWriter.getInstance(document, response.getOutputStream());
            document.open();
            Paragraph elements = new Paragraph("常州武进1区飞行报告", font2);
            elements.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(elements);
//            Image png = Image.getInstance("/Volumes/myproject/excel/pdf.jpeg");
//            Image png = Image.getInstance("https://imgproxy.dev.platform.michaels.com/XXTpt9MjeIKtJwhEo-W6WCFk1c9-K_Ll4qb5g7o5vco/aHR0cHM6Ly9zdG9yYWdlLmdvb2dsZWFwaXMuY29tL2Ntcy1mZ20tZGV2MDAvNTM2MjgzMzM2NDIyNTc2MTI4MA.png");
            Image png = Image.getInstance(b);
            png.setAlignment(Image.ALIGN_CENTER);
            document.add(png);
            document.add(new Paragraph("任务编号：20190701        开始日期：20190701", font));
//            document.add(new Paragraph("任务名称：常州武进1区     结束日期：20190701", font));
//            document.add(new Paragraph("平均飞行高度：100m        平均飞行速度：100km/h", font));
//            document.add(new Paragraph("任务面积：1000㎡      结束日期：20190701", font));
//            document.add(new Paragraph("飞行总时长：1000㎡", font));
            document.addCreationDate();
            document.close();
        } catch (Exception e) {
            System.out.println("file create exception");
        }


    }


    public int threadHandleCount(int count){
        int handleCount = count / 8 + 1;
        return handleCount;
    }

    @Override
    public String importStudentThreadAsync(MultipartFile multipartFile) throws Exception{
        dataCount = 0;
        System.out.println(Thread.currentThread().getName()+"----------------"+Thread.currentThread().getId());
        XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());

        Sheet sheet = workbook.getSheetAt(0);
        int rowCount = sheet.getPhysicalNumberOfRows(); //获取总行数
        //第一行是表头，实际行数要减1
        int rows = rowCount - 1;
        //一个线程让他处理200个row,也许可以处理更多吧
        //int threadNum = rows/1000 + 1; //线程数量
        int threadNum = 15 + 1; //线程数量  cpu 核shu：cup-core+1  或两倍的 cup-core
        //设置一个倒计时门闩，用来处理主线程等待蚂蚁线程执行完成工作之后再运行
        CountDownLatch countDownLatch = new CountDownLatch(threadNum);

        int handleCount = threadHandleCount(rowCount);

        int successCount = 0;
        for(int i = 1; i <= threadNum; i++){
            int startRow = (i-1) * handleCount +1;
            int endRow = i * handleCount;
            if(i == threadNum){
                endRow = rows;
            }
            //使用spring管理的线程池
            asyncService.insertImportData(workbook, startRow, endRow,this,countDownLatch);
        }
        countDownLatch.await();
        //countDownLatch.await(60,TimeUnit.SECONDS);
        String result = "总行数："+rows+"  开起的线程数量："+threadNum+ " 导入成功的条数："+read();
        return result;
    }

}
