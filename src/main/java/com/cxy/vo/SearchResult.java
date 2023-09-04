package com.cxy.vo;


import lombok.Data;

import java.util.List;

@Data
public class SearchResult {
    private List<BlogESVo> blogs;
    private Integer pageNum;
    private Long total;
    private Integer totalPages;

}
