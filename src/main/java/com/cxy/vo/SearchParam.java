package com.cxy.vo;

import lombok.Data;

import java.util.List;

@Data
public class SearchParam {
    private String username;
    private String title;
    private String description;
    private String content;

    private Integer pageNum = 1;

}
