package com.example.excel.controller;

import java.util.Arrays;
import java.util.List;

public class Locust {

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(10,50,100,200,300,400,500);
        String name = "SCH_3.recentTrending.py";
        String nameCount = name.substring(4,5);
        for (Integer integer:list){
            getTest(name,integer,nameCount);
        }
    }
    private static void getTest(String name,Integer count,String nameCount){
        String str = "locust -f sch/TestCases/" + name +" --headless -u "+count+" -r "+count+" --run-time 120 --html ~/per/sch/sch_"+nameCount+"_"+count+".html";
        System.out.println(str);
    }
}
