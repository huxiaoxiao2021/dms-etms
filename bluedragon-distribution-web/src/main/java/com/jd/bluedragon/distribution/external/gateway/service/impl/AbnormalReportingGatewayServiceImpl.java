package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.domain.SiteEntity;
import com.jd.bluedragon.common.dto.abnormal.AbnormalReasonSourceEnum;
import com.jd.bluedragon.common.dto.abnormal.DmsAbnormalReasonDto;
import com.jd.bluedragon.common.dto.abnormal.request.AbnormalReportingRequest;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.core.base.IAbnPdaAPIManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.api.request.QualityControlRequest;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.bluedragon.distribution.qualityControl.QcVersionFlagEnum;
import com.jd.bluedragon.distribution.qualityControl.service.QualityControlService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.external.gateway.service.AbnormalReportingGatewayService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.AbnormalReasonDto;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.PdaResult;
import com.jd.wl.data.qc.abnormal.jsf.jar.abnormal.dto.WpAbnormalRecordPda;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

public class AbnormalReportingGatewayServiceImpl implements AbnormalReportingGatewayService {

    @Value("${qc.abnormal.reason.type:920002312}")
    private int qcAbnormalReasonType;

    @Value("${non.qc.abnormal.reason.type:920002313}")
    private int nonQcAbnormalReasonType;

    private Map<String, AbnormalReasonDto> abnormalReasonDtoMap;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

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

    @Value("${jss.pda.image.bucket}")
    private String bucket;

    private static final int PACKAGE_CODE_TYPE = 1;

    private static final int WAYBILL_CODE_TYPE = 2;

    @Override
    public JdCResponse<List<DmsAbnormalReasonDto>> getAllAbnormalReason(String userErp) {

        JdCResponse<List<DmsAbnormalReasonDto>> jdCResponse = new JdCResponse<>(JdCResponse.CODE_SUCCESS, JdCResponse.MESSAGE_SUCCESS);
        //获取质控的所有异常原因信息
        this.abnormalReasonDtoMap = iAbnPdaAPIManager.selectAbnReasonByErp(userErp);

        if (abnormalReasonDtoMap == null || abnormalReasonDtoMap.size() == 0) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("ERP：" + userErp + "没有在质控系统维护信息，无法获取异常原因，请异步质控系统维护ERP信息！");
            return jdCResponse;
        }
        List<BaseDataDict> qcDateDictList = baseService.getBaseDictionaryTree(this.qcAbnormalReasonType);
        List<BaseDataDict> nonQcDateDictList = baseService.getBaseDictionaryTree(this.nonQcAbnormalReasonType);

        List<DmsAbnormalReasonDto> multiAbnormalReasonList = genDmsAbnormalReasonList(qcDateDictList, AbnormalReasonSourceEnum.QUALITY_CONTROL_SYSTEM);
        List<DmsAbnormalReasonDto> nonQcAbnormalReasonList = genDmsAbnormalReasonList(nonQcDateDictList, AbnormalReasonSourceEnum.BASIC_SYSTEM);
        multiAbnormalReasonList.addAll(nonQcAbnormalReasonList);

        jdCResponse.setData(multiAbnormalReasonList);

        return jdCResponse;
    }

    @Override
    public byte[] uploadExceptionImage(HttpServletRequest request, HttpServletResponse response) {
//        String result="";
//        response.setContentType("application/zip");
//        try{
//            JdCResponse<String> jdCResponse = new JdCResponse<>(JdCResponse.CODE_SUCCESS, JdCResponse.MESSAGE_SUCCESS);
//
//            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
//            String uuid = request.getHeader("uuid");
//            logger.info("uploadExceptionImage上传uuid:" + uuid);
//            InputStream inStream = request.getInputStream();
//            byte[] buff = new byte[100];
//            int rc = 0;
//            while ((rc = inStream.read(buff, 0, 100)) > 0) {
//                swapStream.write(buff, 0, rc);
//            }
//            byte[] in2b = swapStream.toByteArray();
//            inStream.close();
//            swapStream.close();
//            String url = jssService.uploadImage(bucket, in2b);
//            if (StringUtils.isNotBlank(url)) {
//
////                if(StringUtils.isNotEmpty(uuid)){
////                    jimClientProxy.setEx(uuid, url, 10, TimeUnit.MINUTES);
////                }
//                jdCResponse.setData(url);
//            } else {
//                jdCResponse.setCode(JdCResponse.CODE_FAIL);
//                jdCResponse.setMessage("上传失败，请重新上传！");
//            }
//            result = JSON.toJSONString(jdCResponse);
//        }catch(Exception e){
//            logger.error("uploadExceptionImage error",e);
//        }
//        logger.info("uploadExceptionImage:result[" + result + "]");
        return null;
    }

    @Override
    public JdCResponse<List<SiteEntity>> getDutyDepartment(String barCode) {

        JdCResponse<List<SiteEntity>> jdCResponse = new JdCResponse<>(JdCResponse.CODE_SUCCESS, JdCResponse.MESSAGE_SUCCESS);
        //判断barCode是不是运单或者包裹号
        if (StringHelper.isEmpty(barCode)) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("扫描条码不能为空！");
            return jdCResponse;
        } else if (! WaybillUtil.isWaybillCode(barCode) && ! WaybillUtil.isPackageCode(barCode)) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("扫描条码必须是包裹号或运单号！");
            return jdCResponse;
        }

        //协商再投拦截
        if (this.isNeedRedeliveryIntercept(barCode)) {
            jdCResponse.setCode(JdCResponse.CODE_ERROR);
            jdCResponse.setMessage("此条码为【发起协商再投未处理】状态，需商家审核完成才能提交异常！");
            return jdCResponse;
        }

        BaseEntity<List<PackageState>> resultDTO = waybillTraceManager.getAllOperations(barCode);
        Map<Integer, SiteEntity> siteEntityMap = new HashMap<>();
        if (resultDTO != null && resultDTO.getData() != null) {
            List<PackageState> packageStateList = resultDTO.getData();
            if (packageStateList != null && ! packageStateList.isEmpty()) {
                for (PackageState packageState : packageStateList) {
                    Integer operateSiteId = packageState.getOperatorSiteId();
                    String operateSiteName = packageState.getOperatorSite();
                    if (operateSiteId != null && StringHelper.isNotEmpty(operateSiteName)) {
                        SiteEntity siteEntity = new SiteEntity();
                        siteEntity.setCode(operateSiteId);
                        siteEntity.setName(operateSiteName);
                        siteEntityMap.put(operateSiteId, siteEntity);
                    }

                }
            } else {
                logger.warn("条码【" + barCode + "】的无全程跟踪操作，无法获取处理部门！");
            }
        } else {
            logger.warn("查询条码【" + barCode + "】的全程跟踪记录为空，无法获取处理部门！");
        }

        jdCResponse.setData(new ArrayList<>(siteEntityMap.values()));
        return jdCResponse;
    }

    @Override
    public JdCResponse<String> saveAbnormalReportingInfo(AbnormalReportingRequest abnormalReportingRequest) {

        JdCResponse<String> jdCResponse = new JdCResponse<>(JdCResponse.CODE_SUCCESS, JdCResponse.MESSAGE_SUCCESS);
        DmsAbnormalReasonDto dmsAbnormalReasonDto = abnormalReportingRequest.getDmsAbnormalReasonDto();
        //判断是不是质控
        Integer sourceType = dmsAbnormalReasonDto.getSourceType();
        if (sourceType == AbnormalReasonSourceEnum.QUALITY_CONTROL_SYSTEM.getType()) {
            WpAbnormalRecordPda wpAbnormalRecordPda = this.convert2WpAbnormalRecordPda(abnormalReportingRequest);
            PdaResult pdaResult = iAbnPdaAPIManager.report(wpAbnormalRecordPda);

            if (pdaResult == null) {
                jdCResponse.setCode(JdCResponse.CODE_ERROR);
                jdCResponse.setMessage("上报质控系统失败，请稍后重试！");
                return jdCResponse;
            }

            //返回 5-全部成功 4-重复提交 3-部分成功 2-信息不全
            if (pdaResult.getCode() == 5) {
                //生成异常处理的异步任务，与老质控逻辑保持一致
                this.genQcTask(abnormalReportingRequest, abnormalReportingRequest.getBarCodes(), QcVersionFlagEnum.NEW_QUALITY_CONTROL_SYSTEM.getType());
            } else if (pdaResult.getCode() == 4) {
                jdCResponse.setMessage("上传信息已提交质控系统！");
                //已报备，不发MQ
            } else if (pdaResult.getCode() == 3) {
                //部分成功
                //剔除失败列表
                String failBarCodes = pdaResult.getMsg();
                String[] failBarCodeList = failBarCodes.split(",");
                Set<String> barCodeSet = new HashSet<>(abnormalReportingRequest.getBarCodes());
                for (String failBarCode : failBarCodeList) {
                    barCodeSet.remove(failBarCode);
                }
                this.genQcTask(abnormalReportingRequest, new ArrayList<>(barCodeSet), QcVersionFlagEnum.NEW_QUALITY_CONTROL_SYSTEM.getType());
                jdCResponse.setCode(JdCResponse.CODE_ERROR);
                jdCResponse.setMessage("部分信息提交质控系统成功！未成功条码：" + failBarCodes);
            } else if (pdaResult.getCode() == 2) {
                jdCResponse.setCode(JdCResponse.CODE_ERROR);
                jdCResponse.setMessage("上报信息不全，请检查必填信息！");
            } else if (pdaResult.getCode() == 1) {
                jdCResponse.setCode(JdCResponse.CODE_ERROR);
                jdCResponse.setMessage("信息不全，上报人员未配置，请联系IT运营人员核实质控系统权限！");
            } else if (pdaResult.getCode() == 0) {
                jdCResponse.setCode(JdCResponse.CODE_ERROR);
                jdCResponse.setMessage("质控系统接口异常，请稍后再试！");
                logger.error("质控系统接口异常：" + pdaResult.getMsg());
            } else {
                jdCResponse.setCode(JdCResponse.CODE_ERROR);
                jdCResponse.setMessage("信息提交失败，请联系IT运营人员核实质控系统权限！");
            }
        } else {
            //组装信息走老的异常提交流程
            this.genQcTask(abnormalReportingRequest, abnormalReportingRequest.getBarCodes(), QcVersionFlagEnum.OLD_QUALITY_CONTROL_SYSTEM.getType());
        }

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
                AbnormalReasonDto abnormalReasonDto = abnormalReasonDtoMap.get(key);
                if (abnormalReasonDto == null) {
                    logger.warn("编号：【" + baseDataDict.getTypeCode() + "】的原因不存在于质控系统中");
                    continue;
                }
                dmsAbnormalReasonDto = convertDmsAbnormalReasonDto(abnormalReasonDto);
            } else {
                dmsAbnormalReasonDto = convertDmsAbnormalReasonDto(baseDataDict);

            }

            dmsAbnormalReasonDto.setReasonId(baseDataDict.getId());
            dmsAbnormalReasonDto.setReasonCode(baseDataDict.getTypeCode().longValue());
            dmsAbnormalReasonDto.setSourceType(source.getType());

            if (level == 2) {
                String parentKey = level + "-" + baseDataDict.getTypeCode();
                dmsAbnormalReasonDtoMap.put(parentKey, dmsAbnormalReasonDto);
            } else if (level == 3) {
                String parentKey = (level - 1) + "-" + baseDataDict.getParentId();
                DmsAbnormalReasonDto parentDmsAbnormalReasonDto = dmsAbnormalReasonDtoMap.get(parentKey);
                if (parentDmsAbnormalReasonDto == null) {
                    logger.warn("编号：【" + dmsAbnormalReasonDto.getReasonId() + "】的父节点【" + parentKey + "】原因不存在！");
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
    private DmsAbnormalReasonDto convertDmsAbnormalReasonDto(AbnormalReasonDto abnormalReasonDto) {
        DmsAbnormalReasonDto dmsAbnormalReasonDto = new DmsAbnormalReasonDto();
        dmsAbnormalReasonDto.setLevel(Integer.parseInt(abnormalReasonDto.getAbnormalLevel()));
        dmsAbnormalReasonDto.setReasonName(abnormalReasonDto.getAbnormalName());
        dmsAbnormalReasonDto.setIsOutCallType(abnormalReasonDto.getOutCall() == null ? 0 : Integer.parseInt(abnormalReasonDto.getOutCall()));
        dmsAbnormalReasonDto.setIsUploadImgType(abnormalReasonDto.getUploadImg() == null ? 0 : Integer.parseInt(abnormalReasonDto.getUploadImg()));
        dmsAbnormalReasonDto.setIsDeviceCodeType(abnormalReasonDto.getDeviceCode() == null ? 0 : Integer.parseInt(abnormalReasonDto.getDeviceCode()));
        dmsAbnormalReasonDto.setRemark(abnormalReasonDto.getRemark());
        dmsAbnormalReasonDto.setChildReasonList(new ArrayList<DmsAbnormalReasonDto>());
        return dmsAbnormalReasonDto;
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
                qualityControlRequest.setQcType(PACKAGE_CODE_TYPE);
            } else if (WaybillUtil.isPackageCode(barCode)) {
                qualityControlRequest.setQcType(WAYBILL_CODE_TYPE);
            } else {
                logger.warn("【" + barCode + "】既不是包裹号也不是运单号！");
                continue;
            }

            try {
                this.qualityControlService.convertThenAddTask(qualityControlRequest);
            } catch (Exception e) {
                logger.error("异常配送接口插入质控任务表失败，请求："  + JsonHelper.toJson(qualityControlRequest), e);
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
        qualityControlRequest.setTrackContent("订单扫描异常【" + dmsAbnormalReasonDto.getReasonName() +"】");
        return qualityControlRequest;
    }

    /*
    * 协商再投校验
    *
    * */
    private boolean isNeedRedeliveryIntercept(String barCode){
        try{
            String waybillCode = WaybillUtil.getWaybillCode(barCode);
            Integer busID = waybillQueryManager.getBusiId(waybillCode);
            if (busID != null){
                return qualityControlService.getRedeliveryState(waybillCode, busID) == 0;
            }
        } catch (Exception ex) {
            logger.error("调用协商再投状态验证接口失败，原因 " + ex);
        }
        return true;
    }


    private WpAbnormalRecordPda convert2WpAbnormalRecordPda(AbnormalReportingRequest abnormalReportingRequest) {
        WpAbnormalRecordPda wpAbnormalRecordPda = new WpAbnormalRecordPda();
        DmsAbnormalReasonDto dmsAbnormalReasonDto = abnormalReportingRequest.getDmsAbnormalReasonDto();
        wpAbnormalRecordPda.setOrders(StringUtils.join(abnormalReportingRequest.getBarCodes().toArray(), ','));
        wpAbnormalRecordPda.setAbnormalSecondId(dmsAbnormalReasonDto.getParentCode());
        wpAbnormalRecordPda.setAbnormalSecondName(dmsAbnormalReasonDto.getParentName());
        wpAbnormalRecordPda.setAbnormalThirdId(dmsAbnormalReasonDto.getReasonCode());
        wpAbnormalRecordPda.setAbnormalThirdName(dmsAbnormalReasonDto.getReasonName());
        wpAbnormalRecordPda.setDealDept(abnormalReportingRequest.getDealDeptCode().toString());
        wpAbnormalRecordPda.setDealDeptName(abnormalReportingRequest.getDealDeptName());
        wpAbnormalRecordPda.setOutCallStatus(dmsAbnormalReasonDto.getIsOutCallType().toString());
        wpAbnormalRecordPda.setRemark(abnormalReportingRequest.getRemark());
        wpAbnormalRecordPda.setCreateDept(abnormalReportingRequest.getSiteCode().toString());
        wpAbnormalRecordPda.setCreateDeptName(abnormalReportingRequest.getSiteName());
        wpAbnormalRecordPda.setCreateTimeStr(abnormalReportingRequest.getOperateTime().toString());
        wpAbnormalRecordPda.setCreateUser(abnormalReportingRequest.getUserErp());
        wpAbnormalRecordPda.setProofUrls(StringUtils.join(abnormalReportingRequest.getImgUrls().toArray(), ','));
        wpAbnormalRecordPda.setStoreType("0");

        return wpAbnormalRecordPda;
    }
}
