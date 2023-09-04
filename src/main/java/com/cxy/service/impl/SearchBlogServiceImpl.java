package com.cxy.service.impl;

import com.alibaba.fastjson.JSON;
import com.cxy.config.MallESConfig;
import com.cxy.constant.EsConstant;
import com.cxy.service.SearchBlogService;
import com.cxy.vo.BlogESVo;
import com.cxy.vo.SearchParam;
import com.cxy.vo.SearchResult;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchBlogServiceImpl implements SearchBlogService {
    @Autowired
    RestHighLevelClient client;

    @Override
    public SearchResult search(SearchParam param) {
        SearchResult result = null;
        SearchRequest searchRequest = buildSearchRequest(param);
        try {
            SearchResponse response = client.search(searchRequest, MallESConfig.COMMON_OPTIONS);

            result = buildSearchResult(response, param);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result;
    }


    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        if (!StringUtils.isEmpty(param.getContent())) {
            boolQuery.should(QueryBuilders.matchQuery("content", param.getContent()));
        }
        if (!StringUtils.isEmpty(param.getDescription())) {
            boolQuery.should(QueryBuilders.matchQuery("description", param.getDescription()));
        }
        if (!StringUtils.isEmpty(param.getTitle())) {
            boolQuery.should(QueryBuilders.matchQuery("title", param.getTitle()));
        }
        if (!StringUtils.isEmpty(param.getUsername())) {
            boolQuery.should(QueryBuilders.matchQuery("username", param.getUsername()));
        }


//        // attrs
//        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
//            for (String attrStr : param.getAttrs()) {
//                BoolQueryBuilder nestBoolQuery = QueryBuilders.boolQuery();
//                String[] s = attrStr.split("_");
//                String attrId = s[0]; // id
//                String[] attrValues = s[1].split(":"); // val
//                nestBoolQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
//                nestBoolQuery.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
//                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestBoolQuery, ScoreMode.None);
//                boolQuery.filter(nestedQuery);
//            }
//        }

        // price
//        if (!StringUtils.isEmpty(param.getSkuPrice())) {
//            //1_500/_500/500_
//            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
//            String[] s = param.getSkuPrice().split("_");
//            if (s.length == 2) {
//                rangeQuery.gte(s[0]).lte(s[1]);
//            } else if (s.length == 1) {
//                if (param.getSkuPrice().startsWith("_")) {
//                    rangeQuery.lte(s[0]);
//                }
//                if (param.getSkuPrice().endsWith("_")) {
//                    rangeQuery.gte(s[0]);
//                }
//            }
//            boolQuery.filter(rangeQuery);
//        }

        sourceBuilder.query(boolQuery);

//        // 排序
//        if (!StringUtils.isEmpty(param.getSort())) {
//            String sort = param.getSort();
//            String[] s = sort.split("_");
//            SortOrder order = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
//            sourceBuilder.sort(s[0], order);
//        }

        // 分页
        sourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size(EsConstant.PRODUCT_PAGESIZE);

        String s = sourceBuilder.toString();
        System.out.println("构建的DSL" + s);


        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);
        return searchRequest;
    }


    private SearchResult buildSearchResult(SearchResponse response, SearchParam param) {
        SearchResult result = new SearchResult();
        SearchHits hits = response.getHits();

        List<BlogESVo> esModels = new ArrayList<>();
        if (hits.getHits() != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                BlogESVo esModel = JSON.parseObject(sourceAsString, BlogESVo.class);
                esModels.add(esModel);
            }
        }
        result.setBlogs(esModels);

        // page
        result.setPageNum(param.getPageNum());

        // total
        long total = hits.getTotalHits().value;
        result.setTotal(total);

        // totalPage
        int totalPages = (int) total % EsConstant.PRODUCT_PAGESIZE == 0 ? (int) total / EsConstant.PRODUCT_PAGESIZE : (int) (total / EsConstant.PRODUCT_PAGESIZE + 1);
        result.setTotalPages(totalPages);
        System.out.println("result->>>" + result);
        return result;

    }

}
