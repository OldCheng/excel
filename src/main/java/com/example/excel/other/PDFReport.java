package com.example.excel.other;

import com.itextpdf.text.Document;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;

public class PDFReport {

    private final static String REPORT_PATH = "C:/air-navi-monitor/report";

    private static void exportReport() {
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
            PdfWriter.getInstance(document, new FileOutputStream("aaa.pdf"));

            document.open();
            Paragraph elements = new Paragraph("常州武进1区飞行报告", font2);
            elements.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(elements);
//            Image png = Image.getInstance("/Volumes/myproject/excel/pdfff.jpeg");
            Image png = Image.getInstance("https://imgproxy.dev.platform.michaels.com/XXTpt9MjeIKtJwhEo-W6WCFk1c9-K_Ll4qb5g7o5vco/aHR0cHM6Ly9zdG9yYWdlLmdvb2dsZWFwaXMuY29tL2Ntcy1mZ20tZGV2MDAvNTM2MjgzMzM2NDIyNTc2MTI4MA.png");
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

    /**
     * 生成pdf文件
     *
     * @param //missionReport
     * @return
     */
//    public static String exportReport(MissionReportTb missionReport) throws AirNaviException {
//        String pdfPath = null;
//        String imgPath = Shape2Image.getImgPath(missionReport.getMissionID());
////        String imgPath = "E:\\test.png";
//        String finalReportStr = missionReport.getMissionReport();
//        MissionReport finalReport = JSONObject.parseObject(finalReportStr, MissionReport.class);
//        BaseFont bf;
//        Font font = null;
//        Font font2 = null;
//        try {
//            bf = BaseFont.createFont("STSong-Light", "UniGB-UCS2-H",
//                    BaseFont.NOT_EMBEDDED);//创建字体
//            font = new Font(bf, 12);//使用字体
//            font2 = new Font(bf, 12, Font.BOLD);//使用字体
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Document document = new Document();
//        try {
//            File dir = new File(REPORT_PATH);
//            if (!dir.exists()) {
//                dir.mkdirs();
//            }
//            File file = new File(REPORT_PATH + File.separator + missionReport.getMissionID() + ".pdf");
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            PdfWriter.getInstance(document, new FileOutputStream(REPORT_PATH + File.separator + missionReport.getMissionID() + ".pdf"));
//            document.open();
//            Paragraph elements = new Paragraph(missionReport.getMissionName() + "飞行报告", font2);
//            elements.setAlignment(Paragraph.ALIGN_CENTER);
//            document.add(elements);
//            Image png = Image.getInstance(imgPath);
////            https://blog.csdn.net/lingbo89/article/details/76177825
//            float documentWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
//            float documentHeight = documentWidth / 580 * 320;//重新设置宽高
//            png.scaleAbsolute(documentWidth, documentHeight);//重新设置宽高
//            png.scalePercent(50);
//            // 根据域的大小缩放图片
////            image.scaleToFit(signRect.getWidth(), signRect.getHeight());
//            png.setAlignment(Image.ALIGN_CENTER);
//            document.add(png);
//            document.add(new Paragraph("任务编号：" + missionReport.getMissionCode() + ",开始日期：" + finalReport.getStartTime(), font));
//            document.add(new Paragraph("任务名称：" + missionReport.getMissionName() + ",结束日期：" + finalReport.getEndTime(), font));
//            document.add(new Paragraph("平均飞行高度：" + finalReport.getAvgFlightHeight() + "m" + ",平均飞行速度：" + finalReport.getAvgFlightSpeed() + "km/h", font));
//            document.add(new Paragraph("任务面积：" + finalReport.getMissionArea() + "㎡" + ",飞行总时长：" + finalReport.getFlightDuration() + "min", font));
//            document.addCreationDate();
//            document.close();
//            pdfPath = file.getAbsolutePath();
//        } catch (Exception e) {
//            e.printStackTrace();
//            log.error(e.getMessage());
//            System.out.println("file create exception");
//            throw new AirNaviException("生成PDF失败：" + e.getMessage());
//        }
//
//        return pdfPath;
//    }

    public static void main(String[] args)  {
//        String report = "{\"detailMissionReport\":[{\"avgFlightHeight\":119.7,\"avgFlightSpeed\":71.1,\"endPoint\":\"113.27484,22.86843\",\"endTime\":\"2019-09-17 17:47:07\",\"flightDuration\":9,\"reportID\":1,\"startPoint\":\"113.31429,22.78240\",\"startTime\":\"2019-09-17 17:38:03\",\"statisticsTimes\":505}],\"missionReport\":{\"avgFlightHeight\":119.7,\"avgFlightSpeed\":71.1,\"endPoint\":\"113.31429,22.78240\",\"endTime\":\"2019-09-17 17:47:07\",\"flightDuration\":9,\"reportID\":1,\"startPoint\":\"113.31429,22.78240\",\"startTime\":\"2019-09-17 17:38:03\",\"statisticsTimes\":0},\"missionArea\":0.0,\"missionCode\":\"M001\",\"missionID\":\"888813ddef6646cd9bfaba5abb748a43\",\"missionName\":\"德胜航点M008\",\"missionStatus\":1,\"missionType\":0,\"plannedFlightTime\":\"20190909\"}";
//        MissionReportTb missionReportTb = JSONObject.parseObject(report, MissionReportTb.class);
//        exportReport(missionReportTb);
        exportReport();
    }

}
