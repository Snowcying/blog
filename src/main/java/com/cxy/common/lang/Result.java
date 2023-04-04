package com.cxy.common.lang;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Data
public class Result implements Serializable {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private int code;
    private String msg;
    private Object data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static Result success(Object data){
        return success(200,"操作成功",data);
    }


    public static Result success(int code,String msg,Object data){
        Result r = new Result();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }

    public static Result fail(String msg){
        return fail(400,msg,null);
    }
    public static Result fail(String msg,Object data){
        return fail(400,msg,data);
    }
    public static Result fail(int code,String msg,Object data){
        Result r = new Result();
        r.setCode(code);
        r.setMsg(msg);
        r.setData(data);
        return r;
    }
    public static void failReturnJson(int code, String msg, ServletResponse response) throws IOException {
        Result r=Result.fail(code,msg,null);
        String json = MAPPER.writeValueAsString(r);
        response.getWriter().print(json);
    }
}
