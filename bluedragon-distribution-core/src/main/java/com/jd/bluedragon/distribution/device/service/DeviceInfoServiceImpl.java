package com.jd.bluedragon.distribution.device.service;

import com.jd.bd.dms.automatic.sdk.common.dto.BaseDmsAutoJsfResponse;
import com.jd.bd.dms.automatic.sdk.modules.device.DeviceConfigInfoJsfService;
import com.jd.bd.dms.automatic.sdk.modules.device.dto.DeviceConfigDto;
import com.jd.bd.dms.automatic.sdk.modules.device.dto.DeviceConfigQuery;
import com.jd.bd.dms.automatic.sdk.modules.device.dto.DeviceConfigSimpleDto;
import com.jd.bd.dms.automatic.sdk.modules.device.dto.DeviceTypeDto;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.device.response.DeviceInfoDto;
import com.jd.bluedragon.common.dto.device.response.DeviceTypeWithInfoDto;
import com.jd.bluedragon.distribution.api.request.DeviceInfoRequest;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.security.tde.util.Base64;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Key;
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
    @Value("${PC_EID_AES_KEY:''}")
    private String aesKey;

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

        Map<String, Object> eidParamMap = getEidParamMap(request);
        String jsonParam = JsonHelper.toJsonMs(eidParamMap);
        logger.info("开始调用设备指纹接口:url:{},req:{}", eidUrl, jsonParam);
        try {
            HttpClient httpClient = new HttpClient();
            PostMethod method = new PostMethod(eidUrl);
            method.addRequestHeader("Content-type", REST_CONTENT_TYPE);
            method.addRequestHeader("Accept", REST_CONTENT_TYPE);
            String encryptParam = encrypt(jsonParam, aesKey.getBytes());
            logger.info("调用设备指纹接口加密后的参数:{}", encryptParam);
            method.setRequestEntity(new StringRequestEntity(encryptParam,
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
            Map dataMap = (Map)response.get("data");
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

    private String encrypt(final String text, final byte[] privateKey){
        byte[] b = new byte[0];
        try {
            //初始化向量参数，AES 为16bytes. DES 为8bytes.
            String iv = "0102030405060708";
            //两个参数，第一个为私钥字节数组， 第二个为加密方式 AES或者DES
            Key keySpec = new SecretKeySpec(privateKey, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes());
            //实例化加密类，参数为加密方式，要写全
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            //初始化，此方法可以采用三种方式，按服务器要求来添加。
            // （1）无第三个参数（2）第三个参数为SecureRandom random = new SecureRandom();中random对象，随机数。(AES不可采用这种方法)（3）采用此代码中的IVParameterSpec
            b = cipher.doFinal(text.getBytes(Charset.defaultCharset()));
        } catch (Exception e) {
            logger.error("加密出错:", e);
        }
        return Base64.encodeToString(b);
    }

    private Map<String, Object> getEidParamMap(DeviceInfoRequest request) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(EidParamKey.timeStamp, System.currentTimeMillis());
        map.put(EidParamKey.platform, request.getPlatform());
        map.put(EidParamKey.fpVersion, eidVersion);
        map.put(EidParamKey.systemVersion, request.getSystemVersion());
        map.put(EidParamKey.packageName, request.getPackageName());
        map.put(EidParamKey.appName, request.getAppName());
        map.put(EidParamKey.appVersion, request.getAppVersion());
        map.put(EidParamKey.mac, request.getMac());
        map.put(EidParamKey.diskDriveSerial, request.getDiskDriveSerial());
        map.put(EidParamKey.BIOSSerial, request.getBiosSerial());
        map.put(EidParamKey.boardUUID, request.getBoardUuid());
        map.put(EidParamKey.machineGUID, request.getMachineGuid());
        map.put(EidParamKey.CPUID, request.getCpuId());
        return map;
    }

    private interface EidParamKey {
        /**
         * 时间戳
         */
         String timeStamp = "timeStamp";
        /**
         * 平台（windows/macOS）
         */
         String platform = "platform";
        /**
         * 指纹程序版本号（v1.0.0）
         */
         String fpVersion = "fpVersion";
        /**
         * 系统版本号
         */
         String systemVersion = "systemVersion";
        /**
         * 应用程序包名
         */
         String packageName = "packageName";
        /**
         * 程序名
         */
         String appName = "appName";
        /**
         * 程序版本号
         */
         String appVersion = "appVersion";
        /**
         * 网卡mac地址，格式：[{"address":"FC-AA-14-50-F6-B0","type":"MIB_IF_TYPE_ETHERNET"}]
         */
         String mac = "mac";
        /**
         * 硬盘序列号
         */
         String diskDriveSerial = "diskDriveSerial";
        /**
         * BIOS序列号
         */
         String BIOSSerial = "BIOSSerial";
        /**
         * 主板UUID
         */
         String boardUUID = "boardUUID";
        /**
         * machineGUID: 注册表中 windows系统的GUID
         */
         String machineGUID = "machineGUID";
        /**
         * 处理器ID
         */
         String CPUID = "CPUID";

    }

	@Override
	public JdCResponse<List<DeviceTypeWithInfoDto>> queryDeviceTypeWithInfoList(DeviceInfoDto deviceInfoRequest){
		JdCResponse<List<DeviceTypeWithInfoDto>> result = new JdCResponse<List<DeviceTypeWithInfoDto>>();
		result.toSucceed();
		List<DeviceTypeWithInfoDto> listData = new ArrayList<>();
		result.setData(listData);
		DeviceConfigQuery query = new DeviceConfigQuery();
		query.setSiteCode(deviceInfoRequest.getSiteCode());
		query.setMachineCode(deviceInfoRequest.getMachineCode());
		query.setDeviceTypeCode(deviceInfoRequest.getDeviceTypeCode());
        BaseDmsAutoJsfResponse<List<DeviceConfigSimpleDto>> response = deviceConfigInfoJsfService.queryDeviceConfigListForPdaSelect(query);
        if (null == response || BaseDmsAutoJsfResponse.SUCCESS_CODE != response.getStatusCode()) {
            logger.warn("查询设备信息失败，请求参数：{}，返回值：{}",JsonHelper.toJson(deviceInfoRequest), JsonHelper.toJson(response));
        }
		List<DeviceConfigSimpleDto> responseList = response.getData();
        if(CollectionUtils.isEmpty(responseList)) {
        	return result;
        }
        Map<String,DeviceTypeWithInfoDto> typeMap = new HashMap<>();
        
        for (DeviceConfigSimpleDto deviceConfigDto : responseList) {
            DeviceInfoDto deviceInfoDto = new DeviceInfoDto();
            DeviceTypeWithInfoDto typeDto = typeMap.get(deviceConfigDto.getTypeCode());
            if(typeDto == null){
            	typeDto = new DeviceTypeWithInfoDto();
            	typeDto.setDeviceTypeCode(deviceConfigDto.getTypeCode());
            	typeDto.setDeviceTypeName(deviceConfigDto.getTypeName());
            	typeDto.setDeviceList(new ArrayList<DeviceInfoDto>());
            	typeMap.put(deviceConfigDto.getTypeCode(), typeDto);
            	listData.add(typeDto);
            }
            deviceInfoDto.setMachineCode(deviceConfigDto.getMachineCode());
            deviceInfoDto.setDeviceTypeCode(deviceConfigDto.getTypeCode());
            deviceInfoDto.setDeviceTypeName(deviceConfigDto.getTypeName());
            typeDto.getDeviceList().add(deviceInfoDto);
        }
		return result;
	}

	@Override
	public JdResult<DeviceInfoDto> queryDeviceConfigByMachineCode(String machineCode,Integer siteCode) {
        JdResult<DeviceInfoDto> result = new JdResult<>();
        result.toSuccess();
        String siteCodeStr = "";
        if(siteCode != null) {
        	siteCodeStr = siteCode.toString();
        }
        DeviceConfigDto deviceConfig = deviceConfigInfoJsfService.findDeviceConfigByMachineCode(machineCode, siteCodeStr);
        if(deviceConfig == null) {
        	result.toFail("场地中不存在设备编码！");
        	return result;
        }
    	DeviceInfoDto data = new DeviceInfoDto();
    	data.setDeviceTypeCode(deviceConfig.getTypeCode());
    	data.setDeviceTypeName(deviceConfig.getTypeName());
    	data.setMachineCode(machineCode);
    	result.setData(data);
        return result;
	}
}
