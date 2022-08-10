package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.abnormal.AbnormalReasonSourceEnum;
import com.jd.bluedragon.common.dto.abnormal.Dept;
import com.jd.bluedragon.common.dto.abnormal.DeptType;
import com.jd.bluedragon.common.dto.abnormal.DmsAbnormalReasonDto;
import com.jd.bluedragon.common.dto.abnormal.DutyDepartmentInfo;
import com.jd.bluedragon.common.dto.abnormal.DutyDepartmentTypeEnum;
import com.jd.bluedragon.common.dto.abnormal.SpecialScene;
import com.jd.bluedragon.common.dto.abnormal.TraceDept;
import com.jd.bluedragon.common.dto.abnormal.request.AbnormalReportingRequest;
import com.jd.bluedragon.common.dto.abnormal.request.DeptQueryRequest;
import com.jd.bluedragon.common.dto.abnormal.request.TraceDeptQueryRequest;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.core.base.DeptServiceQcManager;
import com.jd.bluedragon.core.base.IAbnPdaAPIManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.distribution.base.domain.JdCancelWaybillResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.bluedragon.distribution.qualityControl.QcVersionFlagEnum;
import com.jd.bluedragon.distribution.qualityControl.service.QualityControlService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.AreaData;
import com.jd.bluedragon.dms.utils.AreaEnum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.common.dto.abnormal.response.SiteDto;
import com.jd.bluedragon.external.gateway.service.AbnormalReportingGatewayService;
import com.jd.bluedragon.utils.IntegerHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ql.dms.report.SiteQueryService;
import com.jd.ql.dms.report.domain.BasicSite;
import com.jd.ql.dms.report.domain.SiteQueryCondition;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.CodeTypeEnum;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.ExceptionReason;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.ReportRecord;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import javax.annotation.Resource;

public class AbnormalReportingGatewayServiceImpl implements AbnormalReportingGatewayService {

    @Value("${qc.abnormal.reason.type:920002312}")
    private int qcAbnormalReasonType;

    @Value("${non.qc.abnormal.reason.type:920002313}")
    private int nonQcAbnormalReasonType;

    private Map<String, ExceptionReason> abnormalReasonDtoMap;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IAbnPdaAPIManager iAbnPdaAPIManager;

    @Autowired
    private BaseService baseService;

    @Autowired
    private JssService jssService;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private QualityControlService qualityControlService;

    @Autowired
    private SiteQueryService siteQueryService;
    
    @Autowired
    private DeptServiceQcManager deptServiceQcManager;

    @Value("${jss.pda.image.bucket}")
    private String bucket;

    @Autowired
    private WaybillService waybillService;
    
    private static final int PACKAGE_CODE_TYPE = 1;

    private static final int WAYBILL_CODE_TYPE = 2;
    
    @Resource(name = "checkPrintInterceptReasonIdSet")
    private Set<Long> checkPrintInterceptReasonIdSet;

    @Override
    public JdCResponse<List<DmsAbnormalReasonDto>> getAllAbnormalReason(String userErp) {

        JdCResponse<List<DmsAbnormalReasonDto>> jdCResponse = new JdCResponse<>(JdCResponse.CODE_SUCCESS, JdCResponse.MESSAGE_SUCCESS);
        //获取质控的所有异常原因信息
        this.abnormalReasonDtoMap = iAbnPdaAPIManager.selectAbnReasonByErp();

        if (abnormalReasonDtoMap == null || abnormalReasonDtoMap.size() == 0) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("没有查询到质控系统异常原因数据");
            return jdCResponse;
        }
        List<BaseDataDict> qcDateDictList = baseService.getBaseDictionaryTree(this.qcAbnormalReasonType);
        List<BaseDataDict> nonQcDateDictList = baseService.getBaseDictionaryTree(this.nonQcAbnormalReasonType);

        List<DmsAbnormalReasonDto> multiAbnormalReasonList = genDmsAbnormalReasonList(qcDateDictList, AbnormalReasonSourceEnum.QUALITY_CONTROL_SYSTEM);
        List<DmsAbnormalReasonDto> nonQcAbnormalReasonList = genDmsAbnormalReasonList(nonQcDateDictList, AbnormalReasonSourceEnum.BASIC_SYSTEM);
        multiAbnormalReasonList.addAll(nonQcAbnormalReasonList);

        jdCResponse.setData(multiAbnormalReasonList);
        if(log.isDebugEnabled()) {
        	log.debug("异常上报-获取异常原因getAllAbnormalReason:请求userErp={}，返回结果：{}", userErp,JsonHelper.toJson(jdCResponse));
        }
        return jdCResponse;
    }

    @Override
    public String uploadExceptionMedia(InputStream inStream, String originalFileName) {
        String extName = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);

        String url = null;
        ByteArrayOutputStream swapStream = null;
        try {
            swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];
            int rc;
            while ((rc = inStream.read(buff, 0, 1024)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            byte[] in2b = swapStream.toByteArray();
            inStream.close();
            swapStream.close();
            url = jssService.uploadFile(bucket, in2b, extName);
            log.info("[新-异常提报-图片上传]uploadExceptionImage上传成功 : url[{}]", url);
        } catch (Exception e) {
            log.error("[新-异常提报-图片上传]uploadExceptionImage图片上传时发生异常", e);
        } finally {
            if (inStream != null) {
                try {
                    inStream.close();
                } catch (IOException e) {
                    log.error("[新-异常提报-图片上传]uploadExceptionImage 输入流关闭异常", e);
                }
            }
            if (swapStream != null) {
                try {
                    swapStream.close();
                } catch (IOException e) {
                    log.error("[新-异常提报-图片上传]uploadExceptionImage 输出流关闭异常", e);
                }
            }
        }
        return url;
    }

    @Override
    public JdCResponse<List<DutyDepartmentInfo>> getDutyDepartment(String barCode, Integer siteCode, String siteName) {

        JdCResponse<List<DutyDepartmentInfo>> jdCResponse = new JdCResponse<>(JdCResponse.CODE_SUCCESS, JdCResponse.MESSAGE_SUCCESS);

        if(null==siteCode || StringUtils.isBlank(siteName)){
            jdCResponse.toFail("操作人场地信息都不能为空");
            return jdCResponse;
        }
        //判断barCode是不是运单或者包裹号
        if (StringHelper.isEmpty(barCode)) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("扫描条码不能为空！");
            return jdCResponse;
        } else if (!WaybillUtil.isPackageCode(barCode)) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("扫描条码必须是包裹号！");
            return jdCResponse;
        }

        //协商再投拦截
        if (this.isNeedRedeliveryIntercept(barCode)) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("此条码为【发起协商再投未处理】状态，需商家审核完成才能提交异常！");
            return jdCResponse;
        }

        BaseEntity<List<PackageState>> resultDTO = waybillTraceManager.getAllOperations(barCode);
        Set<Integer> set = new HashSet<>();
        List<DutyDepartmentInfo> dutyDepartmentInfos = new ArrayList<>();
        if (resultDTO != null && resultDTO.getData() != null) {
            List<PackageState> packageStateList = resultDTO.getData();
            if (packageStateList != null && !packageStateList.isEmpty()) {
                for (PackageState packageState : packageStateList) {
                    Integer operateSiteId = packageState.getOperatorSiteId();
                    String operateSiteName = packageState.getOperatorSite();
                    if (operateSiteId != null && operateSiteId > 0 && StringHelper.isNotEmpty(operateSiteName)) {
                        //此处需要保留原有的全程跟踪顺序，所以不能用map，再获取信息即map的value集合
                        if (!set.contains(operateSiteId)) {
                            dutyDepartmentInfos.add(new DutyDepartmentInfo(operateSiteId.toString(), operateSiteName, DutyDepartmentTypeEnum.DISTRIBUTION_SITE.getType()));
                        }
                        set.add(operateSiteId);
                    }
                }
            } else {
                log.warn("条码【{}】的无全程跟踪操作，无法获取处理部门！", barCode);
            }
        } else {
            log.warn("查询条码【{}】的全程跟踪记录为空，无法获取处理部门！", barCode);
        }
        //添加当前操作单位信息
        if (!set.contains(siteCode)) {
            dutyDepartmentInfos.add(new DutyDepartmentInfo(siteCode.toString(), siteName, DutyDepartmentTypeEnum.DISTRIBUTION_SITE.getType()));
        }

        //质控暂时不支持，代码注释掉
//        String waybillCode = WaybillUtil.getWaybillCode(barCode);
//        //获取库房信息
//        BaseEntity<BigPackageStateDto> baseEntity = waybillTraceManager.getPkStateByCodeAndChoice(waybillCode, false, false, true, false);
//
//        if (baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getStoreInfoDto() != null) {
//            StoreInfoDto storeInfoDto = baseEntity.getData().getStoreInfoDto();
//            if (storeInfoDto != null) {
//                log.info("异常处理获取条码：{}的StoreInfoDto信息：{}", barCode, JsonHelper.toJson(storeInfoDto));
//                String storeCode = storeInfoDto.getCky2() + "|" + storeInfoDto.getStoreId();
//                dutyDepartmentInfos.add(new DutyDepartmentInfo(storeCode, storeInfoDto.getStoreName(), DutyDepartmentTypeEnum.WAREHOUSE.getType()));
//            }
//        } else {
//            log.warn("条码【{}】的无库房信息！", barCode);
//        }

        jdCResponse.setData(dutyDepartmentInfos);
        return jdCResponse;
    }

    @Override
    public JdCResponse<String> saveAbnormalReportingInfo(AbnormalReportingRequest abnormalReportingRequest) {
        log.info("AbnormalReportingRequest：{}", JsonHelper.toJson(abnormalReportingRequest));
        JdCResponse<String> jdCResponse = new JdCResponse<>(JdCResponse.CODE_SUCCESS, JdCResponse.MESSAGE_SUCCESS);

        if(abnormalReportingRequest == null){
            jdCResponse.toFail("入参不能为空");
            return jdCResponse;
        }

        if (null==abnormalReportingRequest.getUserCode() || StringUtils.isBlank(abnormalReportingRequest.getUserName()) || null==abnormalReportingRequest.getSiteCode()|| StringUtils.isBlank(abnormalReportingRequest.getSiteName())){
            jdCResponse.toFail("操作人信息和场地信息都不能为空");
            return jdCResponse;
        }

        DmsAbnormalReasonDto dmsAbnormalReasonDto = abnormalReportingRequest.getDmsAbnormalReasonDto();
        //判断是不是质控
        Integer sourceType = dmsAbnormalReasonDto.getSourceType();
        if (sourceType == AbnormalReasonSourceEnum.QUALITY_CONTROL_SYSTEM.getType()) {
            List<ReportRecord> wpAbnormalRecordPda = this.convert2ReportRecord(abnormalReportingRequest);
            log.info("WpAbnormalRecordPda参数：{}", JsonHelper.toJson(wpAbnormalRecordPda));
            if(wpAbnormalRecordPda==null || wpAbnormalRecordPda.size()<=0){
                jdCResponse.setCode(JdCResponse.CODE_ERROR);
                jdCResponse.setMessage("上报质控系统失败，提报的原因不在质控系统中");
                return jdCResponse;
            }

            JdCResponse<List<String>> pdaResult = iAbnPdaAPIManager.report(wpAbnormalRecordPda);
            if (pdaResult == null) {
                jdCResponse.setCode(JdCResponse.CODE_ERROR);
                jdCResponse.setMessage("上报质控系统失败，请稍后重试！");
                return jdCResponse;
            }
            log.info("上报质控系统返回结果，code：{}，message：{}", pdaResult.getCode(), pdaResult.getMessage());
            //返回 5-全部成功 3-部分成功
            if (pdaResult.getCode() == 5) {
                //生成异常处理的异步任务，与老质控逻辑保持一致
                this.genQcTask(abnormalReportingRequest, abnormalReportingRequest.getBarCodes(), QcVersionFlagEnum.NEW_QUALITY_CONTROL_SYSTEM.getType());
            }else if (pdaResult.getCode() == 3) {
                //部分成功
                //剔除失败列表
                Set<String> barCodeSet = new HashSet<>(abnormalReportingRequest.getBarCodes());
                List<String> failBarCodes = pdaResult.getData();
                if(failBarCodes!=null){

                    for (String failBarCode : failBarCodes) {
                        barCodeSet.remove(failBarCode);
                    }
                }
                this.genQcTask(abnormalReportingRequest, new ArrayList<>(barCodeSet), QcVersionFlagEnum.NEW_QUALITY_CONTROL_SYSTEM.getType());
                jdCResponse.setCode(JdCResponse.CODE_ERROR);
                jdCResponse.setMessage("部分信息提交质控系统成功。\r\n" + pdaResult.getMessage());
            } else if (pdaResult.getCode() == 0) {
                jdCResponse.setCode(JdCResponse.CODE_ERROR);
                jdCResponse.setMessage("上报质控系统失败！\r\n" + pdaResult.getMessage());
                log.error("质控系统接口异常：{}", pdaResult.getMessage());
            }
        } else {
            //组装信息走老的异常提交流程
            this.genQcTask(abnormalReportingRequest, abnormalReportingRequest.getBarCodes(), QcVersionFlagEnum.OLD_QUALITY_CONTROL_SYSTEM.getType());
        }

        return jdCResponse;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.AbnormalReportingGatewayService.querySite", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
    public JdCResponse querySite(String orgId, String siteName, String siteCode) {
        JdCResponse<List<SiteDto>> jdCResponse = new JdCResponse<>(JdCResponse.CODE_SUCCESS, JdCResponse.MESSAGE_SUCCESS);

        List<Integer> siteTypeList = Arrays.asList(96, 16);//96配送运输，16第三方

        if ((StringUtils.isEmpty(siteName) && StringUtils.isEmpty(siteCode))) {
            jdCResponse.toFail("参数不全");
            return jdCResponse;
        }

        if(!IntegerHelper.isInIntegerRange(siteCode)){
            jdCResponse.toFail("站点编号输入错误，请确认后在输入！");
            return jdCResponse;
        }

        List<SiteDto> siteDtoList = new ArrayList<>();
        SiteQueryCondition siteQueryCondition = new SiteQueryCondition();

        siteQueryCondition.setSiteTypes(siteTypeList);

        if(StringUtils.isNotBlank(orgId) && StringUtils.isNumeric(orgId)){
            siteQueryCondition.setOrgIds(Arrays.asList(Integer.valueOf(orgId)));
        }
        if (StringUtils.isNotBlank(siteName)) {
            siteQueryCondition.setSiteNameLike(siteName);
        }
        if (StringUtils.isNotBlank(siteCode) && StringUtils.isNumeric(siteCode)) {
            siteQueryCondition.setSiteCodes(Arrays.asList(Integer.valueOf(siteCode)));
        }

        try{
            List<BasicSite> basicSites = siteQueryService.querySiteByConditionFromEs(siteQueryCondition,20);
            if( basicSites!=null && !basicSites.isEmpty()){
                for(BasicSite basicSite : basicSites){
                    SiteDto siteDto = new SiteDto();
                    siteDto.setSite_code(basicSite.getSiteCode());
                    siteDto.setSite_name(basicSite.getSiteName());
                    siteDtoList.add(siteDto);
                }
            }
        }catch (Exception e){
            log.error("AbnormalReportingGatewayServiceImpl#querySite获取站点数据异常{}，{}",JsonHelper.toJson(siteQueryCondition),e.getMessage(),e);
        }

        jdCResponse.setData(siteDtoList);
        return jdCResponse;
    }

    /**
     * 根据不同来源（质控，基础资料）获取原因列表
     */
    private List<DmsAbnormalReasonDto> genDmsAbnormalReasonList(List<BaseDataDict> dateDictList, AbnormalReasonSourceEnum source) {
        Map<String, DmsAbnormalReasonDto> dmsAbnormalReasonDtoMap = new HashMap<>();

        for (BaseDataDict baseDataDict : dateDictList) {
            if (baseDataDict.getIsvalid() == 0) {
                continue;
            }
            Integer level = baseDataDict.getNodeLevel();
            DmsAbnormalReasonDto dmsAbnormalReasonDto;

            if (source.equals(AbnormalReasonSourceEnum.QUALITY_CONTROL_SYSTEM)) {
                String key = level + "-" + baseDataDict.getTypeCode();
                ExceptionReason abnormalReasonDto = abnormalReasonDtoMap.get(key);
                if (abnormalReasonDto == null) {
                    log.warn("编号：【{}】的原因不存在于质控系统中", baseDataDict.getTypeCode());
                    continue;
                }
                dmsAbnormalReasonDto = convertDmsAbnormalReasonDto(abnormalReasonDto);
            } else {
                dmsAbnormalReasonDto = convertDmsAbnormalReasonDto(baseDataDict);

            }

            dmsAbnormalReasonDto.setReasonId(baseDataDict.getId());
            dmsAbnormalReasonDto.setReasonCode(baseDataDict.getTypeCode().longValue());
            dmsAbnormalReasonDto.setSourceType(source.getType());
            //2对应的是原因列表的二级目录，3对应的是三级目录，与2级目录有所属关系
            if (level == 2) {
                String parentKey = level + "-" + baseDataDict.getTypeCode();
                dmsAbnormalReasonDtoMap.put(parentKey, dmsAbnormalReasonDto);
            } else if (level == 3) {
                String parentKey = (level - 1) + "-" + baseDataDict.getParentId();
                DmsAbnormalReasonDto parentDmsAbnormalReasonDto = dmsAbnormalReasonDtoMap.get(parentKey);
                if (parentDmsAbnormalReasonDto == null) {
                    log.warn("编号：【{}】的父节点【{}】原因不存在！", dmsAbnormalReasonDto.getReasonId(), parentKey);
                    continue;
                }
                dmsAbnormalReasonDto.setParentName(parentDmsAbnormalReasonDto.getReasonName());
                dmsAbnormalReasonDto.setParentCode(parentDmsAbnormalReasonDto.getReasonCode());
                dmsAbnormalReasonDto.setParentId(parentDmsAbnormalReasonDto.getReasonId());
                List<DmsAbnormalReasonDto> childList = parentDmsAbnormalReasonDto.getChildReasonList();
                childList.add(dmsAbnormalReasonDto);
            }
        }

        return new ArrayList<>(dmsAbnormalReasonDtoMap.values());
    }

    /**
     * 从质控获取到的原因转化为通用原因
     */
    private DmsAbnormalReasonDto convertDmsAbnormalReasonDto(ExceptionReason abnormalReasonDto) {
        DmsAbnormalReasonDto dmsAbnormalReasonDto = new DmsAbnormalReasonDto();
        dmsAbnormalReasonDto.setLevel(Integer.parseInt(abnormalReasonDto.getAbnormalLevel()));
        dmsAbnormalReasonDto.setReasonName(abnormalReasonDto.getAbnormalName());
        dmsAbnormalReasonDto.setIsOutCallType(0);
        dmsAbnormalReasonDto.setIsUploadImgType(StringUtils.isEmpty(abnormalReasonDto.getUploadImg()) ? 0 : Integer.parseInt(abnormalReasonDto.getUploadImg()));
        dmsAbnormalReasonDto.setIsDeviceCodeType(StringUtils.isEmpty(abnormalReasonDto.getDeviceCode()) ? 0 : Integer.parseInt(abnormalReasonDto.getDeviceCode()));
        if(StringUtils.isNotBlank(abnormalReasonDto.getRemark()) && abnormalReasonDto.getRemark().length()>10){
            dmsAbnormalReasonDto.setRemark(abnormalReasonDto.getRemark().substring(0,10));
        }else {
            dmsAbnormalReasonDto.setRemark(abnormalReasonDto.getRemark());
        }
        dmsAbnormalReasonDto.setChildReasonList(new ArrayList<DmsAbnormalReasonDto>());
        dmsAbnormalReasonDto.setSpecialScenes(convertSpecialSceneList(abnormalReasonDto.getSpecialScenes()));
        return dmsAbnormalReasonDto;
    }
    private List<SpecialScene> convertSpecialSceneList(List<com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.SpecialScene> list) {
    	List<SpecialScene> newList = new ArrayList<SpecialScene>();
    	if(list != null) {
    		for(com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.SpecialScene dept:list) {
    			SpecialScene newData = new SpecialScene();
    			newData.setCode(dept.getCode());
    			newData.setName(dept.getName());
        		newList.add(newData);
    		}
    	}
    	return newList;
    }
    /**
     * 从基础资料获取到的原因转化为通用原因
     */
    private DmsAbnormalReasonDto convertDmsAbnormalReasonDto(BaseDataDict baseDataDict) {
        DmsAbnormalReasonDto dmsAbnormalReasonDto = new DmsAbnormalReasonDto();
        dmsAbnormalReasonDto.setLevel(baseDataDict.getNodeLevel());
        dmsAbnormalReasonDto.setReasonName(baseDataDict.getTypeName());
        dmsAbnormalReasonDto.setRemark(baseDataDict.getMemo());
        dmsAbnormalReasonDto.setChildReasonList(new ArrayList<DmsAbnormalReasonDto>());
        return dmsAbnormalReasonDto;
    }

    /*
     * 生成异常处理任务
     * */
    private void genQcTask(AbnormalReportingRequest abnormalReportingRequest, List<String> successBarCodeList, Integer qcVersionFlag) {

        QualityControlRequest qualityControlRequest = covert2BaseQualityControlRequest(abnormalReportingRequest);
        qualityControlRequest.setQcVersionFlag(qcVersionFlag);

        for (String barCode : successBarCodeList) {
            qualityControlRequest.setQcValue(barCode);
            if (WaybillUtil.isWaybillCode(barCode)) {
                qualityControlRequest.setQcType(WAYBILL_CODE_TYPE);
            } else if (WaybillUtil.isPackageCode(barCode)) {
                qualityControlRequest.setQcType(PACKAGE_CODE_TYPE);
            } else {
                log.warn("【{}】既不是包裹号也不是运单号！", barCode);
                continue;
            }

            try {
                this.qualityControlService.convertThenAddTask(qualityControlRequest);
            } catch (Exception e) {
                log.error("异常配送接口插入质控任务表失败，请求：{}", JsonHelper.toJson(qualityControlRequest), e);
            }
        }
    }

    private QualityControlRequest covert2BaseQualityControlRequest(AbnormalReportingRequest abnormalReportingRequest) {
        DmsAbnormalReasonDto dmsAbnormalReasonDto = abnormalReportingRequest.getDmsAbnormalReasonDto();
        QualityControlRequest qualityControlRequest = new QualityControlRequest();
        qualityControlRequest.setUserID(abnormalReportingRequest.getUserCode());
        qualityControlRequest.setUserName(abnormalReportingRequest.getUserName());
        qualityControlRequest.setUserERP(abnormalReportingRequest.getUserErp());
        qualityControlRequest.setDistCenterID(abnormalReportingRequest.getSiteCode());
        qualityControlRequest.setDistCenterName(abnormalReportingRequest.getSiteName());
        qualityControlRequest.setOperateTime(abnormalReportingRequest.getOperateTime());
        qualityControlRequest.setQcCode(dmsAbnormalReasonDto.getReasonId());
        qualityControlRequest.setQcName(dmsAbnormalReasonDto.getReasonName());
        //对接新质控的分拣退货任务都是false
        qualityControlRequest.setIsSortingReturn(false);
        qualityControlRequest.setTrackContent("订单扫描异常【" + dmsAbnormalReasonDto.getReasonName() + "】");
        return qualityControlRequest;
    }

    /*
     * 协商再投校验
     *
     * */
    private boolean isNeedRedeliveryIntercept(String barCode) {
        try {
            String waybillCode = WaybillUtil.getWaybillCode(barCode);
            Integer busID = waybillQueryManager.getBusiId(waybillCode);
            if (busID != null) {
                return qualityControlService.getRedeliveryState(waybillCode, busID) == 0;
            }
        } catch (Exception ex) {
            log.error("调用协商再投状态验证接口失败，订单号", ex);
        }
        return true;
    }


    private List<ReportRecord> convert2ReportRecord(AbnormalReportingRequest abnormalReportingRequest) {
        List<ReportRecord> res=new ArrayList<>();

        DmsAbnormalReasonDto dmsAbnormalReasonDto = abnormalReportingRequest.getDmsAbnormalReasonDto();
        JdCResponse<ExceptionReason> abnormalFirst=iAbnPdaAPIManager.getAbnormalFirst(dmsAbnormalReasonDto.getParentCode());
        if(!abnormalFirst.getCode().equals(JdCResponse.CODE_SUCCESS)){
            return res;
        }

        for (String barCode: abnormalReportingRequest.getBarCodes()) {
            ReportRecord itme=new ReportRecord();
            itme.setCode(barCode);
            if (WaybillUtil.isWaybillCode(barCode)) {
                itme.setCodeTypeEnum(CodeTypeEnum.WAYBILL);
            } else if (WaybillUtil.isPackageCode(barCode)) {
                itme.setCodeTypeEnum(CodeTypeEnum.PACKAGE);
            }

            itme.setFirstLevelExceptionId(abnormalFirst.getData().getId());
            itme.setFirstLevelExceptionName(abnormalFirst.getData().getAbnormalName());
            itme.setSecondLevelExceptionId(dmsAbnormalReasonDto.getParentCode());
            itme.setSecondLevelExceptionName(dmsAbnormalReasonDto.getParentName());
            itme.setThirdLevelExceptionId(dmsAbnormalReasonDto.getReasonCode());
            itme.setThirdLevelExceptionName(dmsAbnormalReasonDto.getReasonName());

            if (StringHelper.isNotEmpty(abnormalReportingRequest.getDealDeptCode()) && StringHelper.isNotEmpty(abnormalReportingRequest.getDealDeptName())) {
                itme.setDealDept(abnormalReportingRequest.getDealDeptCode());
                itme.setDealDeptName(abnormalReportingRequest.getDealDeptName());
            }

            itme.setRemark(abnormalReportingRequest.getRemark());
            if (abnormalReportingRequest.getImgUrls() != null && abnormalReportingRequest.getImgUrls().size() > 0) {
                itme.setProofUrls(abnormalReportingRequest.getImgUrls());
            }
            itme.setCreateUser(abnormalReportingRequest.getUserErp());
            itme.setCreateDept(abnormalReportingRequest.getSiteCode().toString());
            itme.setCreateDeptName(abnormalReportingRequest.getSiteName());
            itme.setCreateTime(abnormalReportingRequest.getOperateTime());
            itme.setReportSystem("11");
            itme.setSpecialScene(abnormalReportingRequest.getSpecialScene());
            res.add(itme);
        }

        return res;
    }

	@Override
	public JdCResponse<List<AreaData>> getAreaDataList() {
		JdCResponse<List<AreaData>> result = new JdCResponse<List<AreaData>>();
		result.setData(AreaEnum.toAreaDataList());
		result.toSucceed();
		return result;
	}

	@Override
	public JdCResponse<List<DeptType>> getDeptTypes() {
		return deptServiceQcManager.getDeptTypes();
	}

	@Override
	public JdCResponse<List<Dept>> getDept(DeptQueryRequest queryRequest) {
		JdCResponse<List<Dept>> result = new JdCResponse<List<Dept>>();
		if(queryRequest == null
				|| queryRequest.getRegionId() == null
				|| StringHelper.isEmpty(queryRequest.getDeptTypeCode())
				|| StringHelper.isEmpty(queryRequest.getDeptName())) {
			result.toFail("regionId、deptTypeCode、deptName参数不能为空！");
			return result;
		}
		JdCResponse<List<Dept>> rpcResult = deptServiceQcManager.getDept(queryRequest.getRegionId(), queryRequest.getDeptTypeCode());
		if(rpcResult != null && rpcResult.isSucceed()) {
			result.setData(filterByName(rpcResult.getData(),queryRequest.getDeptName()));
			result.toSucceed();
		}else if(rpcResult != null){
			result.setCode(rpcResult.getCode());
			result.setMessage(rpcResult.getMessage());
		}else {
			result.toFail("查询部门数据异常！");
		}
		return result;
	}
	/**
	 * 按名称过滤列表，返回新列表
	 * @param list
	 * @param filterName
	 * @return
	 */
	private List<Dept> filterByName(List<Dept> list,String filterName){
		List<Dept> newList = new ArrayList<Dept>();
		if(list != null) {
			for(Dept dept: list) {
				if(dept.getName() != null 
						&& dept.getName().contains(filterName)) {
					newList.add(dept);
				}
			}
		}
		return newList;
	}

	@Override
	public JdCResponse<List<TraceDept>> getTraceDept(TraceDeptQueryRequest queryRequest) {
        JdCResponse<List<TraceDept>> jdCResponse = new JdCResponse<>(JdCResponse.CODE_SUCCESS, JdCResponse.MESSAGE_SUCCESS);

        if(StringUtils.isBlank(queryRequest.getCurrentDept())){
            jdCResponse.toFail("操作人场地信息不能为空");
            return jdCResponse;
        }
        //判断barCode是不是运单或者包裹号
        if (StringHelper.isEmpty(queryRequest.getCode())) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("扫描条码不能为空！");
            return jdCResponse;
        } else if (!WaybillUtil.isPackageCode(queryRequest.getCode())) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("扫描条码必须是包裹号！");
            return jdCResponse;
        }
        String waybillCode = WaybillUtil.getWaybillCode(queryRequest.getCode());
        Waybill waybillData = waybillQueryManager.getWaybillByWayCode(waybillCode);
        //补打拦截
        if (waybillData != null
        		&& checkPrintInterceptReasonIdSet != null
        		&& queryRequest.getThirdLevelReasonId() != null
        		&& checkPrintInterceptReasonIdSet.contains(queryRequest.getThirdLevelReasonId())
        		&& waybillService.hasPrintIntercept(waybillCode, waybillData.getWaybillSign())) {
            //取消拦截  存在时跳过 不进行补打拦截提示
            JdCancelWaybillResponse jdCancelResponse = waybillService.dealCancelWaybill(waybillCode);
            if (jdCancelResponse == null || jdCancelResponse.getCode() == null || jdCancelResponse.getCode().equals(JdResponse.CODE_OK)) {
                jdCResponse.setCode(JdCResponse.CODE_ERROR);
                jdCResponse.setMessage(HintService.getHint(HintCodeConstants.EX_REPORT_CHECK_CHANGE_ADDRESS));
                return jdCResponse;
            }
        }
        //协商再投拦截
        if (waybillData != null
        		&& waybillData.getBusiId() != null 
        		&& qualityControlService.getRedeliveryState(waybillCode, waybillData.getBusiId()) == 0) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("此条码为【发起协商再投未处理】状态，需商家审核完成才能提交异常！");
            return jdCResponse;
        }
		return deptServiceQcManager.getTraceDept(queryRequest);
	}
}
