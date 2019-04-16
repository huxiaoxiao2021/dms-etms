package com.jd.bluedragon.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.lang.reflect.Type;


/**
 * Created by yangbo7 on 2016/7/5.
 */
public class RestHelper {
    private final static Log logger = LogFactory.getLog(RestHelper.class);
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
            logger.error("rest jsonPostForEntity  fail url:" + url + "result:" + JsonHelper.toJson(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
    public static <T> T formPostForEntity(String url, String content, MultiValueMap<String, Object> request, TypeReference<T> typeReference) {
        try {
            //请求头设置
            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(new MediaType(content));
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            //提交请求
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(request, headers);
            ResponseEntity result = restTemplate.postForEntity(url, entity, Object.class);
            if (result.getStatusCode() == HttpStatus.OK) {
                return JSON.parseObject(JSON.toJSONString(result.getBody()), typeReference);
            }
            logger.error("rest jsonPostForEntity  fail url:" + url + "result:" + JsonHelper.toJson(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 基于json交互的http访问
     *
     * @param url           访问的url
     * @param typeReference 返回对象的泛型类型, 例如 new TypeReference<SortSchemeResponse<Pager<List<SortScheme>>>>(){}
     * @param <T>
     * @return
     */
    public static <T> T jsonGetForEntity(String url, Type typeReference) {
        try {
            HttpHeaders header = new HttpHeaders();
            header.setContentType(MediaType.parseMediaType("application/text; charset=UTF-8"));
            header.add("Accept", MediaType.APPLICATION_JSON.toString());
            ResponseEntity result = restTemplate.getForEntity(url, Object.class);
            if (result.getStatusCode() == HttpStatus.OK) {
                return JsonHelper.fromJsonUseGson(JsonHelper.toJson(result.getBody()), typeReference);
            }
        } catch (Exception e) {
            logger.error("HTTP post has something wrong when access " + url ,e);
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




























