package com.jd.bluedragon.distribution.client;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.jd.bluedragon.distribution.api.response.WeightResponse;
import com.jd.bluedragon.utils.PropertiesHelper;

public class WeightClient {

    private static final String WAYBILL_WEIGHT_TRACK_ADDRESS = "waybill.weight.address";

    public static WeightResponse weightTrack(String json) {
        RestTemplate template = new RestTemplate();
        String url = PropertiesHelper.newInstance().getValue(WAYBILL_WEIGHT_TRACK_ADDRESS);
        ((SimpleClientHttpRequestFactory) template.getRequestFactory()).setConnectTimeout(1000 * 60);
        HttpHeaders headers = new HttpHeaders();
        MediaType type = MediaType.parseMediaType("application/json; charset=UTF-8");
        headers.setContentType(type);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        HttpEntity<String> formEntity = new HttpEntity<String>(json, headers);
        ResponseEntity<WeightResponse> response = template.postForEntity(url, formEntity, WeightResponse.class);
        if (null != response && null != response.getBody()) {
            return  response.getBody();
        } else {
            return new WeightResponse();
        }
    }

}
