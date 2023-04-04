package com.jd.bluedragon.utils.easydata;

import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.easydata.EasyDataQueryException;
import com.jd.fds.lib.dto.server.ApiQueryCacheableRequest;
import com.jd.fds.lib.dto.server.ApiQueryRequest;
import com.jd.fds.lib.dto.server.FdsPage;
import com.jd.fds.lib.dto.server.FdsServerResult;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 注意：分页接口返回的数据再结果对象的content
 */
@Slf4j
@Component
public class EasyDataClientUtil {

    private static final String UMP_KEY_PREFIX = "dmsWeb.client";
    
    private static String EZD_API_PATH = "http://public.fds.jd.com/query/api-page";

    // EasyData非分页查询API
    private static String EZD_NO_PAGE_API_PATH = "http://public.fds.jd.com/query/api-list";

    // 租户对应分页 API MAP
    private static final CaseInsensitiveMap<String, String> TENANT_PAGE_MAP = new CaseInsensitiveMap<>();

    // 租户对应不分页 API MAP
    private static final CaseInsensitiveMap<String, String> TENANT_NO_PAGE_MAP = new CaseInsensitiveMap<>();

    static  {
        // 分页API租户路径
        TENANT_PAGE_MAP.put("LEO","http://leo.fds.jd.com/query/api-page");
        // 不分页API租户路径
        TENANT_NO_PAGE_MAP.put("LEO","http://leo.fds.jd.com/query/api-list");
    }

    @Autowired
    @Setter
    RestTemplate restClient;

    @JProfiler(jKey = UMP_KEY_PREFIX + ".EasyDataClientUtil.query", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public FdsPage query(String apiName, Map<String, Object> requestParam,
                         String apiGroupName, String appToken,
                         Integer pageSize, Integer pageNumber) throws RuntimeException {

        ApiQueryRequest queryRequest = new ApiQueryRequest();
        queryRequest.setApiGroupName(apiGroupName);
        queryRequest.setApiName(apiName);
        queryRequest.setAppToken(appToken);
        queryRequest.setRequestId(UUID.randomUUID().toString());
        queryRequest.setDebug(Boolean.FALSE);
        queryRequest.setPageSize(pageSize); //limit 500
        queryRequest.setPageNumber(pageNumber);

        // 模板参数
        queryRequest.setStringSubs(requestParam);

        log.info(apiName + "DtsMapEasyDataByHttp 查询 Request:" + JSONObject.toJSONString(queryRequest));

        ResponseEntity entity = restClient.postForEntity(EZD_API_PATH, queryRequest, Object.class);
        if (!entity.getStatusCode().equals(HttpStatus.OK)) {
            log.error("调用大数据平台查询服务失败. req:{}, http-resp:{}", JSON.toJSONString(requestParam), JSON.toJSONString(entity));
            throw new EasyDataQueryException("调用EasyData服务失败,statusCode:" + entity.getStatusCode());
        }

        FdsPage response = JSON.parseObject(JSON.toJSONString(entity.getBody()), FdsPage.class);
        if (response == null || !response.getStatus().equals(HttpStatus.OK.value())) {
            log.error("EasyData 查询 失败. req:{}, http-resp:{}", JSON.toJSONString(requestParam), JSON.toJSONString(entity));
            throw new EasyDataQueryException("调用EasyData服务失败" + response.getMessage());
        }
        return response;
    }

    /**
     * 非分页查询
     *
     * @param apiName
     * @param requestParam
     * @param apiGroupName
     * @param appToken
     * @return
     * @throws RuntimeException
     */
    @JProfiler(jKey = UMP_KEY_PREFIX + ".EasyDataClientUtil.queryNoPage", jAppName = UMP_KEY_PREFIX, mState = {JProEnum.TP})
    public FdsServerResult queryNoPage(String apiName, Map<String, Object> requestParam,
                                       String apiGroupName, String appToken) throws RuntimeException {

        ApiQueryRequest queryRequest = new ApiQueryRequest();
        queryRequest.setApiGroupName(apiGroupName);
        queryRequest.setApiName(apiName);
        queryRequest.setAppToken(appToken);
        queryRequest.setRequestId(UUID.randomUUID().toString());
        queryRequest.setDebug(Boolean.FALSE);
        // 模板参数
        queryRequest.setStringSubs(requestParam);

        //log.info(apiName + "DtsMapEasyDataByHttp 查询 Request:" + JSONObject.toJSONString(queryRequest));

        ResponseEntity entity = restClient.postForEntity(EZD_NO_PAGE_API_PATH, queryRequest, Object.class);
        if (!entity.getStatusCode().equals(HttpStatus.OK)) {
            log.error("调用大数据平台查询服务失败. req:{}, http-resp:{}", JSON.toJSONString(requestParam), JSON.toJSONString(entity));
            throw new EasyDataQueryException("调用EasyData服务失败,statusCode:" + entity.getStatusCode());
        }

        FdsServerResult response = JSON.parseObject(JSON.toJSONString(entity.getBody()), FdsPage.class);
        if (response == null || !response.getStatus().equals(HttpStatus.OK.value())) {
            log.error("EasyData 查询 失败. req:{}, http-resp:{}", JSON.toJSONString(requestParam), JSON.toJSONString(entity));
            throw new EasyDataQueryException("调用EasyData服务失败" + response.getMessage());
        }

        log.info("调用大数据平台成功. req:{}, http-resp:{}", JSON.toJSONString(requestParam), JSON.toJSONString(entity));
        return response;
    }


    /**
     * 分页查询带租户信息
     * @param apiName
     * @param requestParam
     * @param apiGroupName
     * @param appToken
     * @param pageSize
     * @param pageNumber
     * @return
     * @throws RuntimeException
     */
    @JProfiler(jKey = UMP_KEY_PREFIX + ".EasyDataClientUtil.query(withTenant)", jAppName = UMP_KEY_PREFIX, mState = {JProEnum.TP})
    public FdsPage query(String apiName, Map<String, Object> requestParam,
                         String apiGroupName, String appToken,
                         Integer pageSize, Integer pageNumber, String tenant) throws RuntimeException {

        ApiQueryRequest queryRequest = new ApiQueryRequest();
        queryRequest.setApiGroupName(apiGroupName);
        queryRequest.setApiName(apiName);
        queryRequest.setAppToken(appToken);
        queryRequest.setRequestId(UUID.randomUUID().toString());
        queryRequest.setDebug(Boolean.FALSE);
        queryRequest.setPageSize(pageSize); // limit 500
        queryRequest.setPageNumber(pageNumber);

        // 模板参数
        queryRequest.setStringSubs(requestParam);

        log.info(apiName + "DtsMapEasyDataByHttp 查询 Request:" + JSONObject.toJSONString(queryRequest));

        // 默认路径
        String path = EZD_API_PATH;
        // 如果传的租户为空值则为默认
        if (StringUtils.isNotEmpty(tenant)) {
            // 如果Map集合里包含租户则取相应路径，反之默认
            if (TENANT_PAGE_MAP.containsKey(tenant)){
                path = TENANT_PAGE_MAP.get(tenant);
            }
        }

        ResponseEntity<Object> entity = restClient.postForEntity(path, queryRequest, Object.class);
        if (!entity.getStatusCode().equals(HttpStatus.OK)) {
            log.error("调用大数据平台查询服务失败. 发送路径:{}, req:{}, http-resp:{}", path, JSON.toJSONString(requestParam), JSON.toJSONString(entity));
            throw new EasyDataQueryException("调用EasyData服务失败,statusCode:" + entity.getStatusCode());
        }

        FdsPage response = JSON.parseObject(JSON.toJSONString(entity.getBody()), FdsPage.class);
        if (response == null || !response.getStatus().equals(HttpStatus.OK.value())) {
            log.error("EasyData 查询失败. 发送路径:{}, req:{}, http-resp:{}", path, JSON.toJSONString(requestParam), JSON.toJSONString(entity));
            throw new EasyDataQueryException("调用EasyData服务失败" + response.getMessage());
        }
        return response;
    }


    /**
     * 不分页查询带租户信息
     * @param apiName
     * @param requestParam
     * @param apiGroupName
     * @param appToken
     * @param tenant
     * @return
     * @throws RuntimeException
     */
    @JProfiler(jKey = UMP_KEY_PREFIX + ".EasyDataClientUtil.queryNoPage(withTenant)", jAppName = UMP_KEY_PREFIX, mState = {JProEnum.TP})
    public FdsServerResult queryNoPage(String apiName, Map<String, Object> requestParam,
                                       String apiGroupName, String appToken, String tenant) throws RuntimeException {

        ApiQueryRequest queryRequest = new ApiQueryRequest();
        queryRequest.setApiGroupName(apiGroupName);
        queryRequest.setApiName(apiName);
        queryRequest.setAppToken(appToken);
        queryRequest.setRequestId(UUID.randomUUID().toString());
        queryRequest.setDebug(Boolean.FALSE);
        // 模板参数
        queryRequest.setStringSubs(requestParam);

        // 默认路径
        String path = EZD_NO_PAGE_API_PATH;
        // 如果传的租户为空值则为默认
        if (StringUtils.isNotEmpty(tenant)) {
            // 如果Map集合里包含租户则取相应路径，反之默认
            if (TENANT_NO_PAGE_MAP.containsKey(tenant)){
                path = TENANT_NO_PAGE_MAP.get(tenant);
            }
        }
        ResponseEntity<Object> entity = restClient.postForEntity(path, queryRequest, Object.class);
        if (!entity.getStatusCode().equals(HttpStatus.OK)) {
            log.error("调用大数据平台查询服务失败. 发送路径:{}, req:{}, http-resp:{}", path, JSON.toJSONString(requestParam), JSON.toJSONString(entity));
            throw new EasyDataQueryException("调用EasyData服务失败,statusCode:" + entity.getStatusCode());
        }

        FdsServerResult response = JSON.parseObject(JSON.toJSONString(entity.getBody()), FdsPage.class);
        if (response == null || !response.getStatus().equals(HttpStatus.OK.value())) {
            log.error("EasyData 查询失败. 发送路径:{}, req:{}, http-resp:{}", path, JSON.toJSONString(requestParam), JSON.toJSONString(entity));
            throw new EasyDataQueryException("调用EasyData服务失败" + response.getMessage());
        }

        log.info("调用大数据平台成功. req:{}, http-resp:{}", JSON.toJSONString(requestParam), JSON.toJSONString(entity));
        return response;
    }

    /**
     * 非分页查询
     *
     * @param apiName
     * @param requestParam
     * @param apiGroupName
     * @param appToken
     * @return
     * @throws RuntimeException
     */
    @JProfiler(jKey = UMP_KEY_PREFIX + ".EasyDataClientUtil.queryNoPageUseParams", jAppName = UMP_KEY_PREFIX, mState = {JProEnum.TP})
    public FdsServerResult queryNoPageUseParams(String apiName, Map<String, Object> requestParam,
                                       String apiGroupName, String appToken) throws RuntimeException {

        ApiQueryCacheableRequest queryRequest = new ApiQueryCacheableRequest();
        queryRequest.setApiGroupName(apiGroupName);
        queryRequest.setApiName(apiName);
        queryRequest.setAppToken(appToken);
        queryRequest.setRequestId(UUID.randomUUID().toString());
        queryRequest.setDebug(Boolean.FALSE);
        queryRequest.setParams(requestParam);

        //log.info(apiName + "DtsMapEasyDataByHttp 查询 Request:" + JSONObject.toJSONString(queryRequest));

        ResponseEntity entity = restClient.postForEntity(EZD_NO_PAGE_API_PATH, queryRequest, Object.class);
        if (!entity.getStatusCode().equals(HttpStatus.OK)) {
            log.error("调用大数据平台查询服务失败. req:{}, http-resp:{}", JSON.toJSONString(requestParam), JSON.toJSONString(entity));
            throw new EasyDataQueryException("调用EasyData服务失败,statusCode:" + entity.getStatusCode());
        }

        FdsServerResult response = JSON.parseObject(JSON.toJSONString(entity.getBody()), FdsPage.class);
        if (response == null || !response.getStatus().equals(HttpStatus.OK.value())) {
            log.error("EasyData 查询 失败. req:{}, http-resp:{}", JSON.toJSONString(requestParam), JSON.toJSONString(entity));
            throw new EasyDataQueryException("调用EasyData服务失败" + response.getMessage());
        }

        log.info("调用大数据平台成功. req:{}, http-resp:{}", JSON.toJSONString(requestParam), JSON.toJSONString(entity));
        return response;
    }

    @JProfiler(jKey = UMP_KEY_PREFIX + ".EasyDataClientUtil.queryNoPageUseStringSubs", jAppName = UMP_KEY_PREFIX, mState = {JProEnum.TP})
    public FdsServerResult queryNoPageUseStringSubs(String apiName, Map<String, Object> requestParam,
                                                String apiGroupName, String appToken) throws RuntimeException {

        ApiQueryCacheableRequest queryRequest = new ApiQueryCacheableRequest();
        queryRequest.setApiGroupName(apiGroupName);
        queryRequest.setApiName(apiName);
        queryRequest.setAppToken(appToken);
        queryRequest.setRequestId(UUID.randomUUID().toString());
        queryRequest.setDebug(Boolean.FALSE);
        queryRequest.setStringSubs(requestParam);

        //log.info(apiName + "DtsMapEasyDataByHttp 查询 Request:" + JSONObject.toJSONString(queryRequest));

        ResponseEntity entity = restClient.postForEntity(EZD_NO_PAGE_API_PATH, queryRequest, Object.class);
        if (!entity.getStatusCode().equals(HttpStatus.OK)) {
            log.error("调用大数据平台查询服务失败. req:{}, http-resp:{}", JSON.toJSONString(requestParam), JSON.toJSONString(entity));
            throw new EasyDataQueryException("调用EasyData服务失败,statusCode:" + entity.getStatusCode());
        }

        FdsServerResult response = JSON.parseObject(JSON.toJSONString(entity.getBody()), FdsPage.class);
        if (response == null || !response.getStatus().equals(HttpStatus.OK.value())) {
            log.error("EasyData 查询 失败. req:{}, http-resp:{}", JSON.toJSONString(requestParam), JSON.toJSONString(entity));
            throw new EasyDataQueryException("调用EasyData服务失败" + response.getMessage());
        }

        log.info("调用大数据平台成功. req:{}, http-resp:{}", JSON.toJSONString(requestParam), JSON.toJSONString(entity));
        return response;
    }
}
