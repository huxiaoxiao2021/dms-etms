package com.jd.bluedragon.distribution.web.memory;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdRequest;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NetHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.framework.utils.cache.memory.MemoryCache;
import com.jd.uim.annotation.Authorization;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yangbo7 on 2016/6/12.
 * 查询内存中的缓存数据,例如com.jd.etms.framework.utils.cache.memory.MemoryCache的cache集合
 */
@Controller
@RequestMapping("/admin/memory")
public class MemoryController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final static String PREFIX = "http://";
    private final static String SUFFIX_COUNT = ":1601/admin/memory/count";
    private final static String SUFFIX_VALUE = ":1601/admin/memory/value";

    @Authorization(Constants.DMS_WEB_DEVELOP_MEMORY_CACHE_R)
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index() {
        return "admin/memory-monitor/memory-monitor-index";
    }

    @Authorization(Constants.DMS_WEB_DEVELOP_MEMORY_CACHE_R)
    @RequestMapping(value = "/value", method = RequestMethod.POST)
    @ResponseBody
    public MemoryCacheResponse getValueByKey(@RequestBody MemoryCacheRequest request) {
        MemoryCacheResponse<List<MemoryCacheDto>> response = new MemoryCacheResponse<List<MemoryCacheDto>>();
        if (StringUtils.isBlank(request.getKey())) {
            response.setCode(JdResponse.CODE_PARAM_ERROR);
            response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            return response;
        }
        response.setCode(JdResponse.CODE_OK);
        response.setData(findAllValue(request));
        return response;
    }

    @Authorization(Constants.DMS_WEB_DEVELOP_MEMORY_CACHE_R)
    @RequestMapping(value = "/count", method = RequestMethod.POST)
    @ResponseBody
    public MemoryCacheResponse getCountByIp(@RequestBody MemoryCacheRequest request) {
        MemoryCacheResponse<List<MemoryCacheDto>> response = new MemoryCacheResponse<List<MemoryCacheDto>>();
        response.setCode(JdResponse.CODE_OK);
        response.setData(findAllCount(request));
        return response;
    }

    private List<MemoryCacheDto> findAllValue(MemoryCacheRequest request) {
        List<MemoryCacheDto> itemList = new ArrayList<MemoryCacheDto>();
        List<String> ipList = StringHelper.parseList(request.getIps(), ",");
        List<String> localIpList = NetHelper.getAllLocalIP();
        itemList.add(localValueMemoryDot(localIpList, request.getKey()));
        for (String ip : ipList) {
            if (!localIpList.contains(ip)) {
                MemoryCacheDto item = getRemoteValueMemoryDto(ip, request.getKey());
                if (item != null) {
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    private List<MemoryCacheDto> findAllCount(MemoryCacheRequest request) {
        List<MemoryCacheDto> itemList = new ArrayList<MemoryCacheDto>();
        List<String> ipList = StringHelper.parseList(request.getIps(), ",");
        List<String> localIpList = NetHelper.getAllLocalIP();
        itemList.add(localCountMemoryDot(localIpList));
        for (String ip : ipList) {
            if (!localIpList.contains(ip)) {
                MemoryCacheDto item = getRemoteCountMemoryDto(ip);
                if (item != null) {
                    itemList.add(item);
                }
            }
        }
        return itemList;
    }

    private MemoryCacheDto localCountMemoryDot(List<String> localIpList) {
        MemoryCacheDto item = new MemoryCacheDto();
        item.setIp(localIpList.size() > 0 ? localIpList.get(0) : "127.0.0.1");
        item.setValue(MemoryCache.info());
        return item;
    }

    private MemoryCacheDto localValueMemoryDot(List<String> localIpList, String key) {
        MemoryCacheDto item = new MemoryCacheDto();
        item.setIp(localIpList.size() > 0 ? localIpList.get(0) : "127.0.0.1");
        try {
            item.setValue(MemoryCache.getValue(key, 20 * 60 * 1000, null));
        } catch (Exception e) {
            item.setValue("4.value is null");
        }
        return item;
    }

    /**
     * @param ip
     * @return
     */
    private MemoryCacheDto getRemoteCountMemoryDto(String ip) {
        String url = PREFIX + ip + SUFFIX_COUNT;
        return getRemoteMemoryDto(url, ip, null);
    }

    private MemoryCacheDto getRemoteValueMemoryDto(String ip, String key) {
        String url = PREFIX + ip + SUFFIX_VALUE;
        return getRemoteMemoryDto(url, ip, key);
    }

    private MemoryCacheDto getRemoteMemoryDto(String url, String ip, String key) {
        try {
            RestTemplate template = new RestTemplate();
            ((SimpleClientHttpRequestFactory) template.getRequestFactory()).setConnectTimeout(1000);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
            headers.add("Accept", MediaType.APPLICATION_JSON.toString());
            HttpEntity<String> formEntity = new HttpEntity<String>(JsonHelper.toJson(new MemoryCacheRequest(ip, key)), headers);
            URL u = new URL(url);
            boolean safe = jdSsrfCheck(u);
            if(!safe){
                return new MemoryCacheDto(ip, "This request is not safe!");
            }
            ResponseEntity<MemoryCacheResponse> response = template.postForEntity(url, formEntity, MemoryCacheResponse.class);

            if (null == response || null == response.getBody()) {
                return new MemoryCacheDto(ip, "1.value is null");
            }
            List list = (ArrayList) response.getBody().getData();
            if (list == null || list.size() < 1) {
                return new MemoryCacheDto(ip, "2.value is null");
            }
            LinkedHashMap<String, String> resultMap = (LinkedHashMap<String, String>) list.get(0);
            MemoryCacheDto<String> dto = new MemoryCacheDto<String>();
            for (Map.Entry<String, String> entry : resultMap.entrySet()) {
                if (entry != null && "ip".equals(entry.getKey())) {
                    dto.setIp(entry.getValue());
                } else if (entry != null && "value".equals(entry.getKey())) {
                    dto.setValue(entry.getValue());
                }
            }
            return dto;
        } catch (Exception e) {
            log.error("获取远程内存缓存信息失败, url:{}", url, e);
        }
        return new MemoryCacheDto(ip, "3.value is null");
    }

    public List<String> getIpList(MemoryCacheRequest request) {
        List<String> ipList = new ArrayList<String>();
        if (StringUtils.isNotBlank(request.getIps())) {
            ipList = StringHelper.parseList(request.getIps(), ",");
        }
        String localIp = "";
        if (!ipList.contains(localIp)) {
            ipList.add(localIp);
        }
        return ipList;
    }


    //-----------domain-------------


    private static class MemoryCacheRequest extends JdRequest {

        private String ips;

        public MemoryCacheRequest() {
        }

        public MemoryCacheRequest(String ips) {
            this.ips = ips;
        }

        public MemoryCacheRequest(String ips, String key) {
            this.ips = ips;
            this.setKey(key);
        }

        public String getIps() {
            return ips;
        }

        public void setIps(String ips) {
            this.ips = ips;
        }

    }

    private static class MemoryCacheResponse<T> extends JdResponse {

        T data;

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

    private static class MemoryCacheDto<T> {

        private String ip;
        private T value;

        public MemoryCacheDto() {
        }

        public MemoryCacheDto(String ip, T value) {
            this.ip = ip;
            this.value = value;
        }

        public String getIp() {
            return ip;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public T getValue() {
            return value;
        }

        public void setValue(T value) {
            this.value = value;
        }

    }

    /**
     * web漏洞修复
     * @param urlObj
     * @return
     */
    public boolean jdSsrfCheck(URL urlObj){
        //定义请求协议白名单列表
        String[] allowProtocols = new String[]{"http", "https"};
        //定义请求域名白名单列表
        String[] allowDomains = new String[]{"dms.etms.jd.com","dms.etms.jdl.com"};
        //定义请求端口白名单列表
        int[] allowPorts = new int[]{80, 443};
        boolean ssrfCheck = false, protocolCheck = false, domianCheck = false;

        // 首先进行协议校验，若协议校验不通过，SSRF校验不通过
        String protocol = urlObj.getProtocol();
        for(String item : allowProtocols){
            if(protocol.equals(item)){
                protocolCheck = true;
                break;
            }
        }
        // 协议校验通过后，再进行域名校验，反之不进行域名校验，SSRF校验不通过
        if(protocolCheck){
            String host = urlObj.getHost();
            for(String domain: allowDomains){
                if(domain.equals(host)){
                    domianCheck = true;
                    break;
                }
            }
        }
        //域名校验通过后，再进行端口校验，反之不进行端口校验，SSRF校验不通过
        if(domianCheck){
            int port = urlObj.getPort();
            if(port == -1) {
                port = 80;
            }
            for (Integer item : allowPorts) {
                if (item == port) {
                    ssrfCheck = true;
                    break;
                }
            }
        }
        if(ssrfCheck){
            return true;
        }else{
            return false;
        }
    }
}
