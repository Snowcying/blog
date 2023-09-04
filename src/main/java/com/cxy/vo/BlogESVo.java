package com.cxy.vo;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class BlogESVo {
    private Long id;
    private Long userId;
    private String username;
    private String title;
    private String description;
    private String content;
    private LocalDateTime created;

    private Integer status;
}
