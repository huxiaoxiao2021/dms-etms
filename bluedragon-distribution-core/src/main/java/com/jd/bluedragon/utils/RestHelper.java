package com.jd.bluedragon.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.distribution.api.response.SortSchemeResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.InputStream;
import java.util.Map;


/**
 * Created by yangbo7 on 2016/7/5.
 */
public class RestHelper {

    private RestHelper() {
    }

    private static RestTemplate restTemplate;

    static {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setReadTimeout(5000);
        requestFactory.setConnectTimeout(5000);
        restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(requestFactory);
        restTemplate.setErrorHandler(new DefaultResponseErrorHandler());
    }


    /**
     * 基于json交互的http访问
     *
     * @param url           访问的url
     * @param request       请求的对象
     * @param typeReference 返回对象的泛型类型, 例如 new TypeReference<SortSchemeResponse<Pager<List<SortScheme>>>>(){}
     * @param <T>
     * @return
     */
    public static <T> T jsonPostForEntity(String url, Object request, TypeReference<T> typeReference) {
        try {
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
            header.add("Accept", MediaType.APPLICATION_JSON.toString());
            HttpEntity<String> formEntity = new HttpEntity<String>(JSON.toJSONString(request), header);
            ResponseEntity result = restTemplate.postForEntity(url, formEntity, Object.class);
            if (result.getStatusCode() == HttpStatus.OK) {
                return JSON.parseObject(JSON.toJSONString(result.getBody()), typeReference);
            }
        } catch (Exception e) {
        }
        return null;
    }


    public static <T> T postForEntity(String url, InputStream inputStream, TypeReference<T> typeReference) {
        try {
//            FileSystemResource resource = new FileSystemResource(new File());
//            ResponseEntity result = restTemplate.postForEntity(url, param, Object.class);
//            if (result.getStatusCode() == HttpStatus.OK) {
//                return JSON.parseObject(JSON.toJSONString(result.getBody()), typeReference);
//            }
        } catch (Exception e) {
            System.out.println("RestHelper.postForEntity" + e.fillInStackTrace());
        }
        return null;

    }
}




























