package com.jd.bluedragon.distribution.saf;

import java.awt.image.BufferedImage;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.dms.CancelWaybillJsfManager;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.command.JdCommandService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jsf.domain.BlockResponse;
import com.jd.bluedragon.distribution.print.domain.PrintPackage;
import com.jd.bluedragon.distribution.print.domain.PrintPackageImage;
import com.jd.bluedragon.distribution.print.domain.TemplateGroupEnum;
import com.jd.bluedragon.distribution.print.request.PackagePrintRequest;
import com.jd.bluedragon.distribution.print.request.RePrintRecordRequest;
import com.jd.bluedragon.distribution.print.service.PackagePrintService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.fastjson.JSONObject;
import com.jd.ql.dms.print.engine.TemplateEngine;
import com.jd.ql.dms.print.engine.TemplateFactory;
import com.jd.ql.dms.print.engine.toolkit.JPGBase64Encoder;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

/**
 * B网营业厅打印JSF接口
 * @author shipeilin
 * @Description: 类描述信息
 * @date 2019年01月24日 13时:43分
 */
public class PackagePrintServiceImpl implements PackagePrintService {
    private static final String TRANSFER_INTERCEPT_MESSAGE = "该运单已转网，请操作【包裹补打】更换面单!";
    @Autowired
    @Qualifier("jsonCommandService")
    private JdCommandService jdCommandService;
    
    @Autowired
    @Qualifier("cancelWaybillJsfManager")
    private CancelWaybillJsfManager cancelWaybillJsfManager;
    
    @Autowired
    private TemplateFactory templateFactory;

    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 打印JSF接口token校验开关
     */
    private static final String PRINT_SWITCH = "print.switch";

    /**
     * 打印JSF接口系统来源前缀
     */
    private static final String PRINT_PREFIX = "PRINT_SOURCE_";
    
    private static final Integer FEATURE_TYPE_C2B = 7;
    private static final Integer FEATURE_TYPE_B2C = 8;
    /**
     * 打印记录查询
     */
    private static final Integer BUSINESS_TYPE_1004 = 1004;
    /**
     * 补打打印记录查询
     */
    private static final Integer OPERATE_TYPE_100401 = 100401;
    /**
     * 
     */
    private static final Map<TemplateGroupEnum,Integer> FEATURE_TYPES_MAP = new HashMap<>();

    private static Logger logger = Logger.getLogger(PackagePrintServiceImpl.class);
    static{
    	FEATURE_TYPES_MAP.put(TemplateGroupEnum.TEMPLATE_GROUP_B, FEATURE_TYPE_C2B);
    	FEATURE_TYPES_MAP.put(TemplateGroupEnum.TEMPLATE_GROUP_C, FEATURE_TYPE_B2C);
    }
    @JProfiler(jKey = "dmsWeb.jsf.server.PackagePrintServiceImpl.getPrintInfo",jAppName=Constants.UMP_APP_NAME_DMSWEB,
    		mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdResult<Map<String, Object>> getPrintInfo(JdCommand<String> printRequest) {
        logger.info("查询包裹信息参数：" + JsonHelper.toJson(printRequest));
        JdResult<Map<String, Object>> result = new JdResult<Map<String, Object>>();
        result.toSuccess();
        if(!checkToken(printRequest.getSystemCode(), printRequest.getSecretKey())){
            result.toFail("系统访问密钥校验失败，请维护并使用正确的秘钥！");
            return result;
        }
        String commandResult = jdCommandService.execute(JsonHelper.toJson(printRequest));
        logger.info("查询包裹信息结果：" + commandResult);
        JdResult jdResult = JsonHelper.fromJson(commandResult, JdResult.class);
        String data = JSONObject.parseObject(commandResult).getString("data");
        Map map = JsonHelper.json2MapNormal(data);

        result.setCode(jdResult.getCode());
        result.setMessage(jdResult.getMessage());
        result.setMessageCode(jdResult.getMessageCode());

        PackagePrintRequest packagePrintRequest = JsonHelper.fromJson(printRequest.getData(), PackagePrintRequest.class);
        if(WaybillUtil.isPackageCode(packagePrintRequest.getBarCode())){
            filterData(map, packagePrintRequest.getBarCode());
        }
        result.setData(map);
        return result;
    }
    @JProfiler(jKey = "dmsWeb.jsf.server.PackagePrintServiceImpl.generateImage",jAppName=Constants.UMP_APP_NAME_DMSWEB,
    		mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdResult<List<PrintPackageImage>> generateImage(JdCommand<String> printRequest) {
        logger.info("获取图片列表参数：" + JsonHelper.toJson(printRequest));
        JdResult<List<PrintPackageImage>> result = new JdResult<List<PrintPackageImage>>();
        result.toSuccess();
        JdResult<Map<String, Object>> data = getPrintInfo(printRequest);
        logger.info("获取图片列表之打印信息查询结果：" + JsonHelper.toJson(data));
        if(!data.isSucceed()){
            result.setCode(data.getCode());
            result.setMessage(data.getMessage());
            result.setMessageCode(data.getMessageCode());
            return result;
        }
        if(data.getData() == null || data.getData().isEmpty()){
            result.toError("查不到包裹信息!");
            return result;
        }

        JPGBase64Encoder encoder = new JPGBase64Encoder();
        List<Map<String, String>> printData = convertPrintMap(data.getData());
        String templateName = printData.get(0).get("templateName");
        Integer templateVersion = 0;
        String version = printData.get(0).get("templateVersion");
        if(StringUtils.isNotEmpty(version)){
            templateVersion = Integer.valueOf(version);
        }
        result.setData(new ArrayList<PrintPackageImage>(printData.size()));
        for(Map<String, String> map : printData){
            TemplateEngine engine = null;
            PackagePrintRequest request = JsonHelper.fromJson(printRequest.getData(), PackagePrintRequest.class);
            try{
                engine = templateFactory.buildEngine(templateName, templateVersion);
                engine.SetParameters(map);
                long startTime = System.currentTimeMillis();
                BufferedImage image = engine.GenerateImage(false, request.getDpiX(), request.getDpiY());
                logger.info(MessageFormat.format("生成标签时间为{0}ms", System.currentTimeMillis() - startTime));
                PrintPackageImage packageImage = new PrintPackageImage();
                packageImage.setImageBase64(encoder.encode(image, request.getDpiX(), request.getDpiY()));
                packageImage.setPackageCode(map.get("packageCode"));
                result.getData().add(packageImage);
            }catch (Throwable e){
                logger.error("打印服务异常，参数：" + JsonHelper.toJson(map), e);
                result.toError("打印服务异常:" + e.getMessage());
                break;
            }
        }
        return result;
    }


    /**
     * 信息转换为字符串
     * @param data
     * @return
     */
    private List<Map<String, String>> convertPrintMap(Map<String, Object> data){

        List<PrintPackage> printPackages = Arrays.asList(JsonHelper.jsonToArray(JsonHelper.toJson(data.get("packList")), PrintPackage[].class));

        List<Map<String, String>> printData = new ArrayList<Map<String, String>>(printPackages.size());

        for(PrintPackage printPackage: printPackages){
            Map<String, String> map = new HashMap<String, String>();
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (!"packList".equals(entry.getKey()) && entry.getValue() != null) {
                    map.put(entry.getKey(), entry.getValue().toString());
                }

                map.put("packageCode", printPackage.getPackageCode());
                map.put("packageIndex", printPackage.getPackageIndex());
                map.put("packageWeight", printPackage.getPackageWeight());
                map.put("packageSuffix", printPackage.getPackageSuffix());
                if(printPackage.getWeight() != null ){
                    map.put("weight", printPackage.getWeight().toString());
                }
            }
            printData.add(map);

        }
        return printData;
    }
    /**
     * 仅保留需要的包裹号信息
     * @param data
     * @return
     */
    private void filterData(Map<String, Object> data, String packageCode){

        List<PrintPackage> printPackages = Arrays.asList(JsonHelper.jsonToArray(JsonHelper.toJson(data.get("packList")), PrintPackage[].class));

        for(PrintPackage printPackage: printPackages){
            if(packageCode.equals(printPackage.getPackageCode())){
                List<PrintPackage> temp = new ArrayList<PrintPackage>();
                temp.add(printPackage);
                data.put("packList", temp);
                break;
            }
        }
    }

    /**
     * 校验秘钥
     * @param source
     * @param secretKey
     * @return
     */
    private boolean checkToken(String source, String secretKey){

        SysConfig printSwitch = sysConfigService.findConfigContentByConfigName(PRINT_SWITCH);
        //未开启时不校验
        if(printSwitch != null && Constants.STRING_FLG_TRUE.equals(printSwitch.getConfigContent())){
            //校验source和secretKey是否一致
            SysConfig content = sysConfigService.findConfigContentByConfigName(PRINT_PREFIX + source.toUpperCase());
            if(content != null && StringUtils.isNotEmpty(secretKey) && secretKey.equals(content.getConfigContent())){
                return true;
            }else{
                return false;
            }
        }
        return true;
    }
    /**
     * 1、验证是否有访问jsf的权限（验证系统编码和密钥）
     * 2、参数校验（waybillCode、templateGroupCode不能为空，templateGroupCode值为TemplateGroupEnum允许的编码）
     * 3、调用ver系统jsf
     */
    @JProfiler(jKey = "dmsWeb.jsf.server.PackagePrintServiceImpl.hasReprintAll",jAppName=Constants.UMP_APP_NAME_DMSWEB,
    		mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public JdResult<Boolean> hasReprintAll(JdCommand<RePrintRecordRequest> rePrintRecordRequest) {
		JdResult<Boolean> jdResult = new JdResult<Boolean>();
		jdResult.setData(Boolean.TRUE);
		jdResult.toSuccess();
		if(rePrintRecordRequest == null || rePrintRecordRequest.getData() == null){
			jdResult.toFail("请求参数不能为空！");
			return jdResult;
		}
		if(rePrintRecordRequest != null){
			rePrintRecordRequest.setBusinessType(BUSINESS_TYPE_1004);
			rePrintRecordRequest.setOperateType(OPERATE_TYPE_100401);
		}
		if(!checkToken(rePrintRecordRequest.getSystemCode(), rePrintRecordRequest.getSecretKey())){
			jdResult.toFail("系统访问密钥校验失败，请使用正确的秘钥！");
            return jdResult;
        }else{
        	String templateGroupCode = rePrintRecordRequest.getData().getTemplateGroupCode();
        	TemplateGroupEnum templateGroupEnum = TemplateGroupEnum.toEnum(templateGroupCode);
        	if(templateGroupEnum == null){
        		jdResult.toFail("请求参数templateGroupCode无效！");
        		return jdResult;
        	}
        	Integer featureType = FEATURE_TYPES_MAP.get(templateGroupEnum);
        	if(featureType == null){
        		jdResult.toSuccess("非B网和C网面单不拦截！");
        		return jdResult; 
        	}
        	String waybillCode = rePrintRecordRequest.getData().getWaybillCode();
        	String packageCode = rePrintRecordRequest.getData().getPackageCode();
        	BlockResponse blockResponse = null;
        	if(StringHelper.isNotEmpty(packageCode)){
        		blockResponse = cancelWaybillJsfManager.checkPackageBlock(packageCode, featureType);
        	}else if(StringHelper.isNotEmpty(waybillCode)){
        		blockResponse =cancelWaybillJsfManager.checkWaybillBlock(waybillCode, featureType);
        	}else{
        		jdResult.toFail("请求参数需要提供packageCode或waybillCode！");
        		return jdResult;
        	}
            if (blockResponse != null && Boolean.FALSE.equals(blockResponse.getResult())) {
            	jdResult.setData(Boolean.FALSE);
				jdResult.toSuccess(TRANSFER_INTERCEPT_MESSAGE);
            }else if(blockResponse != null){
            	jdResult.setData(Boolean.TRUE);
            	jdResult.toSuccess();            	
            }else{
            	jdResult.toFail("调用拦截接口失败！");
            }
        	return jdResult;
        }
	}
    @JProfiler(jKey = "dmsWeb.jsf.server.PackagePrintServiceImpl.hasReprinted",jAppName=Constants.UMP_APP_NAME_DMSWEB,
    		mState = {JProEnum.TP, JProEnum.FunctionError})
	@Override
	public JdResult<Boolean> hasReprinted(JdCommand<String> rePrintRecordRequest) {
    	JdResult<Boolean> jdResult = new JdResult<Boolean>();
		jdResult.setData(Boolean.TRUE);
		jdResult.toSuccess();
		if(rePrintRecordRequest == null || rePrintRecordRequest.getData() == null){
			jdResult.toFail("请求参数不能为空！");
			return jdResult;
		}
		RePrintRecordRequest requestData = JsonHelper.fromJson(rePrintRecordRequest.getData(), RePrintRecordRequest.class);
		if(requestData != null){
			JdCommand<RePrintRecordRequest> request = new JdCommand<RePrintRecordRequest>();
			request.setSystemCode(rePrintRecordRequest.getSystemCode());
			request.setSecretKey(rePrintRecordRequest.getSecretKey());
			request.setBusinessType(rePrintRecordRequest.getBusinessType());
			request.setOperateType(rePrintRecordRequest.getOperateType());
			request.setData(requestData);
			return this.hasReprintAll(request);
		}else{
			jdResult.toFail("请求参数中data值无效！");
			return jdResult;
		}
	}

}
