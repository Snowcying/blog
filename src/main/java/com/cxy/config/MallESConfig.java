package com.cxy.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//@SpringBootConfiguration
@Configuration
public class MallESConfig {
    public static final RequestOptions COMMON_OPTIONS;

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = builder.build();
    }

    @Bean
    public RestHighLevelClient esRestClint() {
        RestClientBuilder builder = RestClient.builder(
                new HttpHost("192.168.56.10", 9200, "http")
        );
        RestHighLevelClient client = new RestHighLevelClient(builder);
        return client;
    }
}
