package com.example.excel.common;

public class JsonResult {

    private String status = null;  //返回的数据
    private Object result = null;  //返回的状态
    public JsonResult status(String status) {    //注意方法名是可以与属性名同名的。
        this.status = status;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
