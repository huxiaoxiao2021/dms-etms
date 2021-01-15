package com.jd.bluedragon.distribution.device.service;

import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.device.DeviceConfigInfoJsfService;
import com.jd.bd.dms.automatic.sdk.modules.device.dto.DeviceConfigDto;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.device.response.DeviceInfoDto;
import com.jd.bluedragon.distribution.api.request.DeviceInfoRequest;
import com.jd.bluedragon.distribution.api.request.EquipmentIdRequest;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.print.domain.JdCloudPrintResponse;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * <p>
 *     查询自动化设备信息的实现类
 *
 * @author wuzuxiang
 * @since 2019/11/27
 **/
@Service
public class DeviceInfoServiceImpl implements DeviceInfoService {

    private static final Logger logger = LoggerFactory.getLogger(DeviceInfoServiceImpl.class);
    private static final String REST_CONTENT_TYPE = "application/json; charset=UTF-8";
    // 设备指纹信息获取 接口地址
    @Value("${PC_EID_GET_URL:''}")
    private String eidUrl;
    @Value("${PC_EID_VERSION:v1.0.0}")
    private String eidVersion;

    @Autowired
    private DeviceConfigInfoJsfService deviceConfigInfoJsfService;


    @Override
    public List<DeviceInfoDto> queryDeviceConfigByTypeAndSiteCode(String siteCode, String deviceType) {
        BaseDmsAutoJsfResponse<List<DeviceConfigDto>> response = deviceConfigInfoJsfService.findDeviceConfigListByCondition(siteCode,deviceType);
        if (null == response || BaseDmsAutoJsfResponse.SUCCESS_CODE != response.getStatusCode()) {
            logger.warn("查询设备信息失败，创建站点：{},设备类型：{}，返回值：{}",siteCode,deviceType, JsonHelper.toJson(response));
            return Collections.emptyList();
        }
        List<DeviceInfoDto> results = new ArrayList<>(response.getData().size());
        for (DeviceConfigDto deviceConfigDto : response.getData()) {
            DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
            deviceInfoDto.setMachineCode(deviceConfigDto.getMachineCode());
            deviceInfoDto.setDeviceTypeCode(deviceConfigDto.getTypeCode());
            deviceInfoDto.setDeviceTypeName(deviceConfigDto.getTypeName());
            deviceInfoDto.setSiteCode(siteCode);
            deviceInfoDto.setIsEnable(deviceConfigDto.getIsEnable());

            results.add(deviceInfoDto);
        }
        return results;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "DeviceInfoServiceImpl.deviceInfoUpload", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.FunctionError,JProEnum.TP})
    public JdResult<String> deviceInfoUpload(DeviceInfoRequest request) {
        JdResult<String> result = new JdResult<>();
        result.toSuccess();

        EquipmentIdRequest eidRequest = new EquipmentIdRequest();
        BeanUtils.copyProperties(request, eidRequest);
        eidRequest.setTimeStamp(System.currentTimeMillis());
        eidRequest.setFpVersion(eidVersion);
        if (logger.isInfoEnabled()) {
            logger.info("开始调用设备指纹接口:url:{},req:{}", eidUrl, JsonHelper.toJson(request));
        }
        try {
            HttpClient httpClient = new HttpClient();
            PostMethod method = new PostMethod(eidUrl);
            method.addRequestHeader("Content-type", REST_CONTENT_TYPE);
            method.addRequestHeader("Accept", REST_CONTENT_TYPE);
            method.setRequestEntity(new StringRequestEntity(JsonHelper.toJson(eidRequest),
                    REST_CONTENT_TYPE,
                    StandardCharsets.UTF_8.name()));
            int statusCode = httpClient.executeMethod(method);
            if (statusCode != HttpStatus.OK.value()) {
                logger.error("调用设备指纹接口失败,statusCode=:{}", statusCode);
                result.toError("调用设备指纹接口失败！请联系分拣小秘");
                return result;
            }
            String body = method.getResponseBodyAsString();
            logger.info("调用设备指纹接口返回值:{}", body);
            Map response = JsonHelper.fromJson(body, Map.class);
            if (response == null || response.get("data") == null) {
                result.toError("调用设备指纹接口未获取到指纹信息！请联系分拣小秘");
                logger.warn("调用设备指纹接口body或data为空!");
                return result;
            }
            Map dataMap = JsonHelper.fromJson(response.get("data").toString(), Map.class);
            if (dataMap != null && dataMap.get("eid") !=null) {
                result.setData(dataMap.get("eid").toString());
            } else {
                result.toError("调用设备指纹接口未获取到指纹信息！请联系分拣小秘");
                logger.warn("调用设备指纹接口data或eid为空!");
                return result;
            }
        } catch (Exception e) {
            logger.error("调用设备指纹接口出错:", e);
            result.toError("调用设备指纹接口出错！请联系分拣小秘");
        }
        return result;
    }
}
