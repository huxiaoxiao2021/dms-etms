package com.jd.bluedragon.distribution.admin.service.impl;

import com.jd.bluedragon.distribution.admin.service.RemoteAccessService;
import com.jd.bluedragon.distribution.api.domain.YtWaybillSync;
import com.jd.bluedragon.distribution.api.request.RemoteAccessRequest;
import com.jd.bluedragon.distribution.api.response.RemoteAccessResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by yangbo7 on 2016/3/28.
 */
@Service("remoteAccessService")
public class RemoteAccessServiceImpl implements RemoteAccessService {

    private static final Logger logger = Logger.getLogger(RemoteAccessServiceImpl.class);

    @Override
    public RemoteAccessResponse<List<YtWaybillSync>> findListByWaybillCode(RemoteAccessRequest request) {
        RestTemplate template = new RestTemplate();
        String url = resolveUrl(request);
        ((SimpleClientHttpRequestFactory) template.getRequestFactory()).setConnectTimeout(1000 * 60);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        return template.getForObject(url, RemoteAccessResponse.class);
    }

    private String resolveUrl(RemoteAccessRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getLocalDmsUrl());
        sb.append("?");
        sb.append("waybillOrPackageCode=");
        sb.append(request.getWaybillOrPackageCode());
        sb.append("&");
        sb.append("machineCode=");
        sb.append(request.getMachineCode());
        return sb.toString();
    }

}
