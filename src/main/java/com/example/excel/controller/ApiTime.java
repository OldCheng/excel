package com.example.excel.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.io.*;
import java.util.*;
import java.util.stream.Collectors;


import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;


@RestController
public class ApiTime {

    private final String CLIENT = "CLIENT";
    private final String SERVER = "SERVER";
    private final String SELECT = "select";
    private final String INSERT = "insert";
    private final String UPDATE = "update";
    private final String DELETE = "delete";

    @GetMapping("/getTime")
    public String getTime(@RequestParam("filePath") String filePath, @RequestParam("spiltStr") String spiltStr) {
        //String spiltStr = "b2b00000/api";
        if (StringUtils.isBlank(filePath) || StringUtils.isBlank(spiltStr)) {
            return "filePath或者spiltStr不能为空！";
        }
        File file = new File(filePath);
        //int read = output.read();
        if (!file.isFile() || !file.exists()) {
            return "文件不存在，请填写完整的txt文件路径！";
        }
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            fileInputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer sb = new StringBuffer();
            String text = null;
            while ((text = bufferedReader.readLine()) != null) {
                sb.append(text);
            }
            List<JSONObject> list = getList(sb.toString().replace(",TestCases.", ""), spiltStr);
            createExcel(list, filePath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
                if (inputStreamReader != null) inputStreamReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "success!";
    }

    private List<JSONObject> getList(String str, String spiltStr) throws Exception {
        List<String> list = Arrays.asList(str.split("######"));
        System.out.println("总共的数据有多少===" + list.size());
        List<String> collect = list.stream().filter(s -> s.contains("b2b00000/api")).collect(Collectors.toList());
        List<String> rubbishList = list.stream().filter(s -> !s.contains("b2b00000/api")).collect(Collectors.toList());
        System.out.println("为修改标识数据：：：" + rubbishList);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (String string : collect) {
            JSONObject jsonObject = new JSONObject();
            String[] split = string.split("---");
            String[] nameSplit = split[0].split(spiltStr);
            jsonObject.put("time", split[1]);
            jsonObject.put("name", nameSplit[1]);
            jsonObject.put("fileName", nameSplit[0]);
            jsonObjectList.add(jsonObject);
        }
        return jsonObjectList;
    }

    private void createExcel(List<JSONObject> list, String filePath) throws Exception {
        String subFilePath = filePath.substring(0, filePath.lastIndexOf("/") + 1);

        HSSFWorkbook wb = null;
        FileOutputStream output = null;
        try {
            wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("sheet0");
            for (int i = 0; i < list.size(); i++) {
                JSONObject jsonObject = list.get(i);
                HSSFRow row = sheet.createRow(i);
                for (int k = 1; k < 7; k++) {
                    HSSFCell cell = row.createCell(k);
                    String cellStr = "";
                    if (k == 1) {
                        cellStr = jsonObject.getString("fileName");
                    } else if (k == 2) {
                        cellStr = jsonObject.getString("method");
                    } else if (k == 3) {
                        cellStr = jsonObject.getString("name");
                    } else if (k == 4) {
                        cellStr = jsonObject.getString("time");
                    } else if (k == 5) {
                        cellStr = jsonObject.getString("status");
                    } else if (k == 6) {
                        cellStr = jsonObject.getString("errorMessage");
                    }
                    cell.setCellValue(cellStr);
                }
            }
            Date date = new Date();
            output = new FileOutputStream(subFilePath + date + "workbook.xls");
            wb.write(output);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            wb.close();
            output.close();
        }

    }

    @GetMapping("/getTime2")
    public String getTime2(@RequestParam("filePath") String filePath, @RequestParam("spiltStr") String spiltStr) {
        //String spiltStr = "b2b00000/api";
        if (StringUtils.isBlank(filePath) || StringUtils.isBlank(spiltStr)) {
            return "filePath或者spiltStr不能为空！";
        }
        File file = new File(filePath);
        //int read = output.read();
        if (!file.isFile() || !file.exists()) {
            return "文件不存在，请填写完整的txt文件路径！";
        }
        FileInputStream fileInputStream = null;
        InputStreamReader inputStreamReader = null;
        try {
            fileInputStream = new FileInputStream(file);
            inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuffer sb = new StringBuffer();
            String text = null;
            while ((text = bufferedReader.readLine()) != null) {
                sb.append(text);
            }
            JSONArray jsonArray = JSONArray.parseArray(sb.toString());
            List<JSONObject> list = getList(jsonArray, spiltStr);
            createExcel(list, filePath);
            //String substring = filePath.substring(0,filePath.lastIndexOf("/") + 1);
            //createExcel(list, substring);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) fileInputStream.close();
                if (inputStreamReader != null) inputStreamReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return "success!";
    }

    private List<JSONObject> getList(JSONArray jsonArray, String spiltStr) throws Exception {
        String builtIn = "BuiltIn";
        String errorMessageSpilt = "Expected status:";
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (int i = 1; i < jsonArray.size(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            if (jsonObject == null) {
                continue;
            }
            JSONArray jsonChild = jsonObject.getJSONArray("children");
            if (CollectionUtils.isEmpty(jsonChild)) {
                continue;
            }

            String fileName = jsonObject.getString("name");
            for (int j = 0; j < jsonChild.size(); j++) {
                JSONObject jsonData = new JSONObject();
                JSONObject childrenJson = jsonChild.getJSONObject(j);
                String name = childrenJson.getString("name");
                jsonData.put("fileName", fileName);
                if (name.contains(spiltStr)) {
                    String[] names = childrenJson.getString("name").split(spiltStr);
                    name = names[1];
                    jsonData.put("method", names[0]);
                }
                //String name = names[1];
                jsonData.put("name", name);
                jsonData.put("time", childrenJson.getString("time"));
                if (StringUtils.isNotBlank(name) && name.contains(builtIn)) {
                    jsonData.put("name", name.substring(0, name.indexOf(builtIn)));
                    jsonData.put("time", "");
                    JSONArray errorMessageArray = childrenJson.getJSONArray("list2");
                    if (!CollectionUtils.isEmpty(errorMessageArray)) {
                        String errorMessage = errorMessageArray.getJSONObject(0).getString("failMessage");
                        jsonData.put("errorMessage", errorMessage);
                        if (errorMessage.contains(errorMessageSpilt)) {
                            String[] errorMessageSpilts = errorMessage.split(errorMessageSpilt);
                            jsonData.put("errorMessage", errorMessageSpilts[0]);
                            jsonData.put("status", errorMessageSpilts[1]);
                        }
                    }
                }
                jsonObjectList.add(jsonData);
            }
        }
        return jsonObjectList;
    }


    @GetMapping("/getZipkinList")
    public JSONArray getZipkinList(@RequestParam("sendUrl") String sendUrl) {
        JSONArray arrayNew = new JSONArray();
        HttpGet httpGet = new HttpGet(sendUrl);
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        try {
            // 发起请求
            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
            // 请求结束，返回结果。并解析json。
            String resData = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            if (StringUtils.isBlank(resData)) {
                return arrayNew;
            }
            JSONArray jsonArray = JSONArray.parseArray(resData);
            if (CollectionUtils.isEmpty(jsonArray)) {
                return arrayNew;
            }
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONArray array = jsonArray.getJSONArray(i);
                if(array.size()==1){
                    JSONObject jsonObject = array.getJSONObject(0);
                    String kind = jsonObject.getString("kind");//
                    if(CLIENT.equals(kind)){
                        continue;
                    }
                }
                JSONArray arrayNewChilderen = new JSONArray();
                for (int j = 0; j < array.size(); j++) {
                    JSONObject jsonObject = array.getJSONObject(j);
                    String kind = jsonObject.getString("kind");//
                    String name = jsonObject.getString("name");//
                    if (CLIENT.equals(kind) && !(SELECT.equals(name) || INSERT.equals(name) || UPDATE.equals(name) || DELETE.equals(name))) {
                        continue;
                    }
                    arrayNewChilderen.add(jsonObject);
                }
                arrayNew.add(arrayNewChilderen);
            }
            return arrayNew;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != closeableHttpClient) {
                try {
                    closeableHttpClient.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return arrayNew;
    }

    @GetMapping("/getZipkin")
    public JSONArray getZipkin(@RequestParam("sendUrl") String sendUrl, @RequestParam("maxCount") Integer maxCount) throws Exception {
        JSONArray result = new JSONArray();
        JSONArray zipkinList = getZipkinList(sendUrl);
        for (int i = 0; i < zipkinList.size(); i++) {
            JSONArray array = zipkinList.getJSONArray(i);
            JSONObject jsonObjectChild = new JSONObject();
            int updateCount = 0;
            int insertCount = 0;
            int deleteCount = 0;
            int selectCount = 0;
            for (int j = 0; j < array.size(); j++) {
                JSONObject jsonObject = array.getJSONObject(j);
                String kind = jsonObject.getString("kind");//
                String name = jsonObject.getString("name");//
                if (SERVER.equals(kind)) {
                    jsonObjectChild.put("name", name);
                } else if (CLIENT.equals(kind)) {
                    switch (name) {
                        case SELECT:
                            selectCount++;
                            break;
                        case INSERT:
                            insertCount++;
                            break;
                        case UPDATE:
                            updateCount++;
                            break;
                        case DELETE:
                            deleteCount++;
                            break;
                    }

                }
                jsonObjectChild.put("updateCount", updateCount);
                jsonObjectChild.put("insertCount", insertCount);
                jsonObjectChild.put("deleteCount", deleteCount);
                jsonObjectChild.put("selectCount", selectCount);
                int totalCount = selectCount + deleteCount + insertCount + updateCount;
                jsonObjectChild.put("totalCount", totalCount);
                if(totalCount == 0){
                    jsonObjectChild.put("tags",jsonObject.getString("tags"));
                }
            }
            result.add(jsonObjectChild);

        }
        createExcel(result);
        return result;
    }

    private void createExcel(JSONArray result) throws Exception {
        String subFilePath = "/Users/user/Desktop/zipkin/api/";
        HSSFWorkbook wb = null;
        FileOutputStream output = null;
        try {
            wb = new HSSFWorkbook();
            HSSFSheet sheet = wb.createSheet("sheet0");
            HSSFRow rowFirst = sheet.createRow(0);
            for (int k = 0; k < 7; k++) {
                HSSFCell cell = rowFirst.createCell(k);
                String cellStr = "";
                if(k==0){
                    cellStr = "api";
                }else if(k == 1){
                    cellStr = "total";
                }else if(k == 2){
                    cellStr = SELECT;
                }else if(k == 3){
                    cellStr = INSERT;
                }else if(k == 4){
                    cellStr = UPDATE;
                }else if(k == 5){
                    cellStr = DELETE;
                }else if (k == 6){
                    cellStr = "errorMsg";
                }
                cell.setCellValue(cellStr);
            }
            for (int i = 0; i < result.size(); i++) {
                JSONObject jsonObject = result.getJSONObject(i);
                HSSFRow row = sheet.createRow(i+1);
                for (int k = 0; k < 7; k++) {
                    HSSFCell cell = row.createCell(k);
                    String cellStr = "";
                    if(k==0){
                        cellStr = jsonObject.getString("name");
                    }else if(k == 1){
                        cellStr = jsonObject.getString("totalCount");
                    }else if(k == 2){
                        cellStr = jsonObject.getString("selectCount");;
                    }else if(k == 3){
                        cellStr = jsonObject.getString("insertCount");;
                    }else if(k == 4){
                        cellStr = jsonObject.getString("updateCount");;
                    }else if(k == 5){
                        cellStr = jsonObject.getString("deleteCount");
                    }else if (k == 6){
                        cellStr = jsonObject.getString("tags");
                    }
                    cell.setCellValue(cellStr);
                }
            }
            Date date = new Date();
            output = new FileOutputStream(subFilePath + date + "workbook.xls");
            wb.write(output);
            output.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            wb.close();
            output.close();
        }

    }

    @GetMapping("/getSwagger")
    public List getSwaggerst() {
        List<Map<String,String >> list = new ArrayList<>();
        HttpGet httpGet = new HttpGet("https://moh.dev.platform.michaels.com/api/v3/api-docs/OrderApi");
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        try {
            // 发起请求
            HttpResponse httpResponse = closeableHttpClient.execute(httpGet);
            // 请求结束，返回结果。并解析json。
            String resData = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            if (StringUtils.isBlank(resData)) {
                System.out.println("没有数据返回！");
            }
            JSONObject jsonObject = JSONObject.parseObject(resData);
            JSONObject jsonPaths = jsonObject.getJSONObject("paths");
            Set<String> strings = jsonPaths.keySet();
            for (String str:strings){
                Map<String,String> map = new HashMap<>();
                map.put("path",str);
                JSONObject jsonObject1 = jsonPaths.getJSONObject(str);
                Set<String> strings1 = jsonObject1.keySet();
                strings1.forEach(s -> {map.put("method",s);});
                JSONObject jsonMethod = jsonPaths.getJSONObject(map.get("method"));

                JSONObject requestBody = jsonMethod.getJSONObject("requestBody");
                if(requestBody != null){
                    String string = requestBody.getJSONObject("content").getJSONObject("application/json").getJSONObject("schema").getString("$ref");
                }


                list.add(map);
            }
            JSONObject components = jsonObject.getJSONObject("components");
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        String str = "#/components/schemas/ParentOrderVo";
    }

}

