package com.jd.bluedragon.distribution.saf;

import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jsf.dms.CancelWaybillJsfManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.PopPrintRequest;
import com.jd.bluedragon.distribution.api.request.ReassignWaybillRequest;
import com.jd.bluedragon.distribution.api.request.ReversePrintRequest;
import com.jd.bluedragon.distribution.api.response.DmsWaybillInfoResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.domain.JsfVerifyConfig;
import com.jd.bluedragon.distribution.base.domain.SysConfig;
import com.jd.bluedragon.distribution.base.service.SysConfigService;
import com.jd.bluedragon.distribution.client.domain.ClientOperateRequest;
import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.command.JdCommandService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.jsf.domain.BlockResponse;
import com.jd.bluedragon.distribution.print.domain.PrintPackage;
import com.jd.bluedragon.distribution.print.domain.PrintPackageImage;
import com.jd.bluedragon.distribution.print.domain.TemplateGroupEnum;
import com.jd.bluedragon.distribution.print.request.PackagePrintRequest;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import com.jd.bluedragon.distribution.print.request.RePrintRecordRequest;
import com.jd.bluedragon.distribution.print.request.SiteTerminalPrintCompleteRequest;
import com.jd.bluedragon.distribution.print.service.PackagePrintInternalService;
import com.jd.bluedragon.distribution.print.service.PackagePrintService;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintMessages;
import com.jd.bluedragon.distribution.reassignWaybill.service.ReassignWaybillService;
import com.jd.bluedragon.distribution.rest.packageMake.PackageResource;
import com.jd.bluedragon.distribution.rest.reverse.ReversePrintResource;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.fastjson.JSON;
import com.jd.jsf.gd.util.RpcContext;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.print.engine.TemplateEngine;
import com.jd.ql.dms.print.engine.TemplateFactory;
import com.jd.ql.dms.print.engine.toolkit.JPGBase64Encoder;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.awt.image.BufferedImage;
import java.util.*;

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
    
    @Autowired
    ReversePrintResource reversePrintResource;
	@Autowired
	ReassignWaybillService reassignWaybillService;
	@Autowired
	private WaybillService waybillService;
	@Autowired
	private PackageResource packageResource;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    @Qualifier("packagePrintInternalService")
    private PackagePrintInternalService packagePrintInternalService;

    @Autowired
    private UccPropertyConfiguration uccConfig;

    @Autowired
    private TaskService taskService;

    /**
     * 打印JSF接口token校验开关
     */
    private static final String PRINT_SWITCH = "dms.jsf.packagePrint.verifyConfig.switch";

    /**
     * 打印JSF接口系统来源前缀
     */
    private static final String PRINT_PREFIX = "dms.jsf.packagePrint.verifyConfig.";
    
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

    private static Logger log = LoggerFactory.getLogger(PackagePrintServiceImpl.class);
    static{
    	FEATURE_TYPES_MAP.put(TemplateGroupEnum.TEMPLATE_GROUP_B, FEATURE_TYPE_C2B);
    	FEATURE_TYPES_MAP.put(TemplateGroupEnum.TEMPLATE_GROUP_C, FEATURE_TYPE_B2C);
    }
    @JProfiler(jKey = "dmsWeb.jsf.server.PackagePrintServiceImpl.getPrintInfo",jAppName=Constants.UMP_APP_NAME_DMSWEB,
    		mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public JdResult<Map<String, Object>> getPrintInfo(JdCommand<String> printRequest) {
		RpcContext context = RpcContext.getContext();
		log.info("RpcContext  www {}", JSON.toJSONString(context));
    	if(log.isInfoEnabled()){
			log.info("查询包裹信息参数：{}", JsonHelper.toJson(printRequest));
		}
		JdResult<Map<String, Object>> result = new JdResult<Map<String, Object>>();
        result.toSuccess();
        if(printRequest == null){
            result.toFail("传入的参数不能为空！");
            return result;
        }
        if(!this.checkVerify(printRequest)){
            result.toFail("权限验证失败！请检查传入参数systemCode、secretKey、businessType、operateType的值！");
            return result;
        }
        String commandResult = jdCommandService.execute(JsonHelper.toJson(printRequest));
        JdResult jdResult = JsonHelper.fromJson(commandResult, JdResult.class);
        if(jdResult == null){
            result.toFail("获取打印数据为空！");
            return result;
        }
        //调用不成功，返回相应的信息
        if(!jdResult.isSucceed()){
            result.setCode(jdResult.getCode());
            result.setMessageCode(jdResult.getMessageCode());
            result.setMessage(jdResult.getMessage());
            return result;
        }
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
		if(log.isInfoEnabled()){
			log.info("获取图片列表参数：{}", JsonHelper.toJson(printRequest));
		}
		JdResult<List<PrintPackageImage>> result = new JdResult<List<PrintPackageImage>>();
        result.toSuccess();
        JdResult<Map<String, Object>> data = getPrintInfo(printRequest);
		if(log.isInfoEnabled()){
			log.info("获取图片列表之打印信息查询结果：{}", JsonHelper.toJson(data));
		}
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
                BufferedImage image = engine.GenerateImage(false, request.getDpiX(), request.getDpiY());
                PrintPackageImage packageImage = new PrintPackageImage();
                packageImage.setImageBase64(encoder.encode(image, request.getDpiX(), request.getDpiY()));
                packageImage.setPackageCode(map.get("packageCode"));
                result.getData().add(packageImage);
            }catch (Throwable e){
                log.error("打印服务异常，参数：{}", JsonHelper.toJson(map), e);
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
    	//增加null判断
    	Object packList = data.get("packList");
    	if(packList != null){
	        List<PrintPackage> printPackages = Arrays.asList(JsonHelper.jsonToArray(JsonHelper.toJson(packList), PrintPackage[].class));
	        if(printPackages != null ){
		        for(PrintPackage printPackage: printPackages){
		            if(packageCode.equals(printPackage.getPackageCode())){
		                List<PrintPackage> temp = new ArrayList<PrintPackage>();
		                temp.add(printPackage);
		                data.put("packList", temp);
		                break;
		            }
		        }
	        }
    	}
    }
    /**
     * 校验基本传入参数和权限
     * @param printRequest
     * @return
     */
    private <T> JdResult<T> checkParams(JdCommand<?> request){
    	JdResult<T> jdResult = new JdResult<T>();
    	jdResult.toSuccess();
		if(request == null || request.getData() == null){
			jdResult.toFail("请求参数不能为空！");
			return jdResult;
		}
        if(!this.checkVerify(request)){
        	jdResult.toFail("权限验证失败！请检查传入参数systemCode、secretKey、businessType、operateType的值！");
            return jdResult;
        }
    	return jdResult;
    }
    /**
     * 权限验证
     * @param printRequest
     * @return
     */
    private boolean checkVerify(JdCommand<?> printRequest){
    	//是否开启验证
    	if(sysConfigService.getConfigByName(PRINT_SWITCH)){
    		if(StringHelper.isEmpty(printRequest.getSystemCode())
    				||StringHelper.isEmpty(printRequest.getSecretKey())
    				||printRequest.getBusinessType() == null
    				||printRequest.getOperateType() == null){
    			return false;
    		}
            //验证密钥和操作码
    		String configKey = PRINT_PREFIX + printRequest.getSystemCode();
            SysConfig sysConfig = sysConfigService.findConfigContentByConfigName(configKey);
            if(sysConfig != null && StringHelper.isNotEmpty(sysConfig.getConfigContent())){
            	JsfVerifyConfig jsfVerifyConfig = JsonHelper.fromJson(sysConfig.getConfigContent(), JsfVerifyConfig.class);
                if(jsfVerifyConfig != null){
                	if(!printRequest.getSecretKey().equals(jsfVerifyConfig.getSecretKey())){
                		log.warn("{}密钥验证失败！",printRequest.getSystemCode());
                		return false;
                	}
                	//是否允许所有访问
                	if(Boolean.TRUE.equals(jsfVerifyConfig.getAllowAll())){
                		return true;
                	}else{
                		return jsfVerifyConfig.getBusinessTypes() != null 
                				&& jsfVerifyConfig.getBusinessTypes().contains(printRequest.getBusinessType())
                				&& jsfVerifyConfig.getOperateTypes() != null 
                				&& jsfVerifyConfig.getOperateTypes().contains(printRequest.getOperateType());
                	}
                }else{
                	log.warn("jsf未获取到权限配置信息！key={}", configKey);
                }
            	return false;
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
		if(!checkVerify(rePrintRecordRequest)){
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

	@Override
    @JProfiler(jKey = "dmsWeb.jsf.server.PackagePrintServiceImpl.reversePrintAfter",jAppName=Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
	public JdResult<Boolean> reversePrintAfter(JdCommand<String> reversePrintAfterRequest) {
    	JdResult<Boolean> jdResult = new JdResult<Boolean>();
		jdResult.setData(Boolean.FALSE);
		jdResult.toFail();
		if(reversePrintAfterRequest == null || reversePrintAfterRequest.getData() == null){
			jdResult.toFail("请求参数不能为空！");
			return jdResult;
		}
        if(!this.checkVerify(reversePrintAfterRequest)){
        	jdResult.toFail("权限验证失败！请检查传入参数systemCode、secretKey、businessType、operateType的值！");
            return jdResult;
        }
		ReversePrintRequest requestData = JsonHelper.fromJson(reversePrintAfterRequest.getData(), ReversePrintRequest.class);
		if(requestData != null){
			InvokeResult<Boolean> result = reversePrintResource.reversePrintAfter(requestData);
			if(result != null && InvokeResult.RESULT_SUCCESS_CODE == result.getCode()){
				jdResult.toSuccess(result.getMessage());
				jdResult.setData(result.getData());
			}else if(result != null){
				jdResult.toFail(result.getCode(),result.getMessage());
			}
			return jdResult;
		}else{
			jdResult.toFail("请求参数中data值无效！");
			return jdResult;
		}
	}

	@Override
    @JProfiler(jKey = "dmsWeb.jsf.server.PackagePrintServiceImpl.getWaybillInfoForBackSchedule",jAppName=Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
	public JdResult<String> getWaybillInfoForBackSchedule(JdCommand<String> packageCodeRequest) {
    	JdResult<String> jdResult = this.checkParams(packageCodeRequest);
		if(!jdResult.isSucceed()){
			return jdResult;
		}
		DmsWaybillInfoResponse dmsWaybillInfoResponse = waybillService.getDmsWaybillInfoResponse(packageCodeRequest.getData());
		if(dmsWaybillInfoResponse != null && JdResponse.CODE_OK.equals(dmsWaybillInfoResponse.getCode())){
			jdResult.toSuccess();
			jdResult.setData(JsonHelper.toJson(dmsWaybillInfoResponse));
		}else if(dmsWaybillInfoResponse != null){
			jdResult.toFail(dmsWaybillInfoResponse.getCode(),dmsWaybillInfoResponse.getMessage());
		}else{
			jdResult.toFail("获取运单数据为空！");
		}
		return jdResult;
	}
	/**
	 * 现场预分拣回调处理
	 */
	@Override
    @JProfiler(jKey = "dmsWeb.jsf.server.PackagePrintServiceImpl.backScheduleAfter",jAppName=Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
	public JdResult<Boolean> backScheduleAfter(JdCommand<String> reassignWaybillRequest) {
    	JdResult<Boolean> jdResult = this.checkParams(reassignWaybillRequest);
		if(!jdResult.isSucceed()){
			return jdResult;
		}
		jdResult.setData(Boolean.FALSE);
        ReassignWaybillRequest requestData = JsonHelper.fromJson(reassignWaybillRequest.getData(), ReassignWaybillRequest.class);
		if(requestData != null){
			return reassignWaybillService.backScheduleAfter(requestData);
		}else{
			jdResult.toFail("请求参数中data值无效！");
			return jdResult;
		}
	}

	/**
	 * 包裹补打回调处理
	 */
    @Override
    @JProfiler(jKey = "dmsWeb.jsf.server.PackagePrintServiceImpl.reprintAfter",jAppName=Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
	public JdResult<Boolean> reprintAfter(JdCommand<String> reprintAfterRequest) {
    	JdResult<Boolean> jdResult = this.checkParams(reprintAfterRequest);
		if(!jdResult.isSucceed()){
			return jdResult;
		}
		jdResult.setData(Boolean.FALSE);
		ClientOperateRequest requestData = JsonHelper.fromJson(reprintAfterRequest.getData(), ClientOperateRequest.class);
		if(requestData != null){
			JdResponse jdResponse = packageResource.packReprintAfter(requestData);
			if(jdResponse != null && JdResponse.CODE_OK.equals(jdResponse.getCode())){
				jdResult.toSuccess(jdResponse.getMessage());
				jdResult.setData(Boolean.TRUE);
			}else if(jdResponse != null){
				jdResult.toFail(jdResponse.getCode(),jdResponse.getMessage());
			}else{
				jdResult.toFail("包裹补打回调处理失败！");
			}
			return jdResult;
		}else{
			jdResult.toFail("请求参数中data值无效！");
			return jdResult;
		}
	}

    /**
     * 打印完成回调接口
     *
     * @param printRequest
     * @return
     */
    @Override
    @JProfiler(jKey = "dmsWeb.jsf.server.PackagePrintServiceImpl.packagePrintComplete", jAppName=Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResult<Boolean> packagePrintComplete(JdCommand<SiteTerminalPrintCompleteRequest> printRequest) {
        JdResult<Boolean> jdResult = this.checkParams(printRequest);
        if (!jdResult.isSucceed()) {
            return jdResult;
        }

        try {
            SiteTerminalPrintCompleteRequest printData = printRequest.getData();

            Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(WaybillUtil.getWaybillCode(printData.getBarCode()));
            if (waybill == null) {
                log.warn("根据运单号{}未获取到运单信息!", WaybillUtil.getWaybillCode(printData.getBarCode()));
                jdResult.toFail(WaybillPrintMessages.FAIL_MESSAGE_WAYBILL_NULL.getMsgCode(),
                        WaybillPrintMessages.FAIL_MESSAGE_WAYBILL_NULL.formatMsg());
                return jdResult;
            }

            JdCommand<PrintCompleteRequest> jdCommand = convertPrintRequest(printRequest, waybill);

            // 大运单打印回调异步处理
            if (judgeAsyncDealPrintLogic(waybill)) {

                addPrintAsyncTask(jdCommand);

                return jdResult;
            }

            jdResult = packagePrintInternalService.printComplete(jdCommand);
        }
        catch (Exception e) {
            log.error("处理打印回调异常. request:{}", JsonHelper.toJson(printRequest), e);
            jdResult.toFail("处理打印回调异常");
        }

        return jdResult;
    }

    /**
     *
     * @param printRequest
     * @param waybill
     * @return
     */
    private JdCommand<PrintCompleteRequest> convertPrintRequest(JdCommand<SiteTerminalPrintCompleteRequest> printRequest, Waybill waybill) {
        JdCommand<PrintCompleteRequest> jdCommand = new JdCommand<>();
        jdCommand.setSystemCode(printRequest.getSystemCode());
        jdCommand.setSecretKey(printRequest.getSecretKey());
        jdCommand.setProgramType(printRequest.getProgramType());
        jdCommand.setVersionCode(printRequest.getVersionCode());
        jdCommand.setBusinessType(printRequest.getBusinessType());
        jdCommand.setOperateType(printRequest.getOperateType());

        PrintCompleteRequest request = new PrintCompleteRequest();
        jdCommand.setData(request);

        // 非分拣中心首次打印
        request.setSortingFirstPrint(0);

        SiteTerminalPrintCompleteRequest printData = printRequest.getData();

        request.setWaybillCode(WaybillUtil.getWaybillCode(printData.getBarCode()));
        request.setPackageBarcode(printData.getBarCode());

        if (StringUtils.isBlank(printData.getWaybillSign())) {
            request.setWaybillSign(waybill.getWaybillSign());
        }
        else {
            request.setWaybillSign(printData.getWaybillSign());
        }

        BaseStaffSiteOrgDto baseStaff = baseMajorManager.getBaseStaffByErpNoCache(printData.getOperatorErp());
        if (baseStaff != null) {
            request.setOperatorCode(baseStaff.getStaffNo());
        }

        request.setOperatorErp(printData.getOperatorErp());
        request.setOperatorName(printData.getOperatorName());
        request.setOperateSiteCode(printData.getSiteCode());

        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(printData.getSiteCode());
        if (baseSite != null) {
            request.setOperateSiteName(baseSite.getSiteName());
        }

        request.setOperateType(PopPrintRequest.PRINT_PACK_TYPE);
        if (printData.getOpeTime() == null || printData.getOpeTime() <= 0) {
            request.setOperateTime(DateHelper.formatDate(new Date(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
        }
        else {
            request.setOperateTime(DateHelper.formatDate(new Date(printData.getOpeTime()), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
        }

        request.setQuantity(waybill.getGoodNumber());
        request.setPopSupId(waybill.getConsignerId());
        request.setPopSupName(waybill.getConsigner());
        request.setWaybillType(waybill.getWaybillType());
        request.setBusiId(waybill.getBusiId());
        request.setBusiName(waybill.getBusiName());

        request.setInterfaceType(jdCommand.getOperateType());

        if (null != printData.getFirstTimePrint()) {
            request.setFirstTimePrint(printData.getFirstTimePrint());
        }

//        request.setPopReceiveType();
//        request.setBusinessType();
//        request.setCrossCode();
//        request.setThirdWaybillCode();
//        request.setQueueNo();
//        request.setBoxCode();
//        request.setDriverCode();
//        request.setDriverName();
//        request.setCategoryName();
//        request.setTemplateGroupCode();
//        request.setTemplateName();
//        request.setTemplateVersion();
        return jdCommand;
    }

    /**
     * 插入大运单异步处理任务
     * @param request
     */
    private void addPrintAsyncTask(JdCommand<PrintCompleteRequest> request) {
        Task task = new Task();
        task.setType(Task.TASK_TYPE_PRINT_CALLBACK_BIG_WAYBILL);
        task.setTableName(Task.getTableName(task.getType()));
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setCreateSiteCode(request.getData().getOperateSiteCode());
        task.setKeyword1(request.getData().getWaybillCode());
        task.setKeyword2(request.getData().getPackageBarcode());
        task.setOwnSign(BusinessHelper.getOwnSign());
        task.setBody(JsonHelper.toJson(request));

        String fingerprint = task.getCreateSiteCode() +
                Constants.UNDER_LINE + task.getKeyword1() +
                Constants.UNDER_LINE + DateHelper.formatDateTimeMs(task.getOperateTime());
        task.setFingerprint(Md5Helper.encode(fingerprint));

        taskService.add(task);

        if (log.isInfoEnabled()) {
            log.info("add print callback task. [{}]", JsonHelper.toJson(task));
        }
    }

    /**
     * 判断打印回调是否异步。运单包裹数超过上限
     * @param waybill
     * @return
     */
    private boolean judgeAsyncDealPrintLogic(Waybill waybill) {
        if (waybill.getGoodNumber() != null) {

            return waybill.getGoodNumber() >= getSyncDealPackageCount();
        }

        return false;
    }

    private int getSyncDealPackageCount() {
        return 0 == uccConfig.getPrintCompleteCallbackAsyncPackageNum() ? 500 : uccConfig.getPrintCompleteCallbackAsyncPackageNum();
    }

}
