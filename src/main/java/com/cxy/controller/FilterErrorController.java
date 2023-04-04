package com.cxy.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class FilterErrorController {
    @ResponseBody
    @RequestMapping("/filterError/{code}")
//    public Map<String,Object> error(@PathVariable("code")Integer code, @PathVariable("message")String message){
    public Map<String,Object> error(@PathVariable("code")Integer code){
        Map<String,Object> map = new HashMap<>();
        map.put("code",code);
//        map.put("message",message);
        return map;
    }

}
