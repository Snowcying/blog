package com.cxy.service;

import com.cxy.vo.SearchParam;
import com.cxy.vo.SearchResult;

public interface SearchBlogService {
    SearchResult search(SearchParam searchParam);
}
