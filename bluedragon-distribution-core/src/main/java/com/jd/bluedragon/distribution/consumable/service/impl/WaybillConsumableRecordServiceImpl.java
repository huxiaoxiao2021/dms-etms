package com.jd.bluedragon.distribution.consumable.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.consumable.request.WaybillConsumablePdaDto;
import com.jd.bluedragon.common.dto.consumable.request.WaybillConsumablePackConfirmReq;
import com.jd.bluedragon.common.dto.consumable.response.WaybillConsumablePackConfirmRes;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.consumable.dao.WaybillConsumableRecordDao;
import com.jd.bluedragon.distribution.consumable.domain.*;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumablePDAService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.util.*;

/**
 *
 * @ClassName: WaybillConsumableRecordServiceImpl
 * @Description: 运单耗材记录表--Service接口实现
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
@Service("waybillConsumableRecordService")
public class WaybillConsumableRecordServiceImpl extends BaseService<WaybillConsumableRecord>
        implements WaybillConsumableRecordService, WaybillConsumablePDAService {

    @Autowired
	@Qualifier("waybillConsumableRecordDao")
	private WaybillConsumableRecordDao waybillConsumableRecordDao;

    @Autowired
    private WaybillConsumableRelationService waybillConsumableRelationService;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    @Qualifier("waybillConsumableProducer")
    private DefaultJMQProducer waybillConsumableProducer;

    @Autowired
    @Qualifier("waybillConsumableReportProducer")
    private DefaultJMQProducer waybillConsumableReportProducer;

    private static int WAYBILL_CONSUMABLE_MESSAGE_TYPE = 2;

    @Autowired
    private BaseMajorManager baseMajorManager;

	@Override
	public Dao<WaybillConsumableRecord> getDao() {
		return this.waybillConsumableRecordDao;
	}

    @Override
    public WaybillConsumableRecord queryOneByWaybillCode(String waybillCode) {
        WaybillConsumableRecordCondition condition = new WaybillConsumableRecordCondition();
        condition.setWaybillCode(waybillCode);
	    return waybillConsumableRecordDao.queryOneByCondition(condition);
    }

    @Override
    public boolean updateByCondition(WaybillConsumableRecord record) {
        if(record == null || (record.getId() == null && StringUtils.isEmpty(record.getWaybillCode()))){
            log.info("Bw网耗材数据更新失败，参数非法：{}" , JsonHelper.toJson(record));
            return false;
        }
	    return waybillConsumableRecordDao.update(record);
    }

    @Override
    public int exportCountByWebCondition(WaybillConsumableRecordCondition condition) {
        return waybillConsumableRecordDao.exportCountByWebCondition(condition);
    }

    @Override
    public List<WaybillConsumableExportDto> exportInfoByWebCondition(WaybillConsumableRecordCondition condition) {
        return waybillConsumableRecordDao.exportInfoByWebCondition(condition);

    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    @JProfiler(jKey = "DMSWEB.WaybillConsumableRecordService.confirmByIds", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public int confirmByIds(List<WaybillConsumableRecord> confirmRecords) {
	    if(confirmRecords == null || confirmRecords.isEmpty()){
            return 0;
        }
        if(confirmRecords.size() > MAX_ROWS){
	        log.warn("批量确认数据超过最大支持量，数据总数：{}" , confirmRecords.size());
            throw new IllegalArgumentException("批量确认最大支持数量：" + MAX_ROWS);
        }

	    //1.更新主表信息
        int result = 0;
        List<Long> ids = new ArrayList<Long>(confirmRecords.size());
        for (WaybillConsumableRecord record : confirmRecords){
            record.setConfirmStatus(WaybillConsumableRecordService.TREATED_STATE);
            if(waybillConsumableRecordDao.update(record)){
                ids.add(record.getId());
                result++;
            }
        }

        //2.发送MQ通知运单
        List<WaybillConsumableRecord> confirmedRecords = waybillConsumableRecordDao.findByIds(ids);
        CallerInfo info = Profiler.registerInfo("DMSWEB.WaybillConsumableRecordService.sendConfirmWaybillConsumableMq", false, true);
        sendConfirmWaybillConsumableMq(confirmedRecords);
        Profiler.registerInfoEnd(info);

        //3.发送MQ数据同步工作台报表
        sendConfirmWaybillConsumableMqToReport(confirmedRecords);

        return result;
    }

    @Override
    public boolean canModify(String waybillCode) {
        WaybillConsumableRecord oldRecord = queryOneByWaybillCode(waybillCode);
        //1.该运单未被确认
        if(oldRecord != null && oldRecord.getId() != null && UNTREATED_STATE.equals(oldRecord.getConfirmStatus())){
            Waybill waybill = waybillCommonService.findByWaybillCode(waybillCode);
            //1.标示为标识可以修改
            if(waybill != null && !BusinessUtil.isWaybillConsumableOnlyConfirm(waybill.getWaybillSign())){
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean isConfirmed(String waybillCode) {
        WaybillConsumableRecord record = queryOneByWaybillCode(waybillCode);
        if (record != null) {
            log.info("运单号{}确认耗材服务结果【0：未确认，1：确认】：{}" ,waybillCode, record.getConfirmStatus());
            return TREATED_STATE.equals(record.getConfirmStatus());
        }
        //added by hanjiaxing3 2019.04.12 业务方确认取不到包装服务任务的，也进行拦截
        else {
//            log.warn("运单号{}需要使用包装耗材服务，但是不存在包装耗材服务任务，需对TOPIC：【bd_pack_sync_waybill】查询归档",waybillCode);
            // modified by wuzuxiang  2022-02-21 快递快运都需要进行包装耗材服务确认，只看包装耗材任务，不看标位
            return false;
        }
        //edited by hanjiaxing3 2019.04.12 业务方确认取不到包装服务任务的，也进行拦截
        //return true;
    }

    /**
     * 发送确认明细MQ通知运单
     * @param confirmedRecords
     */
    private void sendConfirmWaybillConsumableMq(List<WaybillConsumableRecord> confirmedRecords){
        Map<String, WaybillConsumableDto> consumableDtoMap = new HashMap<String, WaybillConsumableDto>();
        for (WaybillConsumableRecord record : confirmedRecords){
            //只发送修改过的数据
            if(TREATED_STATE.equals(record.getModifyStatus())){
                WaybillConsumableDto dto = new WaybillConsumableDto();
                dto.setWaybillCode(record.getWaybillCode());
                dto.setDmsCode(record.getDmsId());
                dto.setMessageType(WAYBILL_CONSUMABLE_MESSAGE_TYPE);
                dto.setOperateUserErp(record.getConfirmUserErp());
                dto.setOperateUserName(record.getConfirmUserName());
                dto.setOperateTime(DateHelper.formatDateTime(record.getConfirmTime()));
                dto.setPackingChargeList(new ArrayList<WaybillConsumableDetailDto>());
                consumableDtoMap.put(record.getWaybillCode(), dto);
            }
        }
        //构建消息体明细
        if(!consumableDtoMap.isEmpty()){
            List<WaybillConsumableDetailInfo> exportDtos = waybillConsumableRelationService.queryByWaybillCodes(new ArrayList<String>(consumableDtoMap.keySet()));
            for(WaybillConsumableDetailInfo dto : exportDtos){
                WaybillConsumableDetailDto detailDto = new WaybillConsumableDetailDto();
                detailDto.setPackingCode(dto.getConsumableCode());
                detailDto.setPackingName(dto.getName());
                detailDto.setPackingType(dto.getType());
                detailDto.setPackingTypeName(dto.getTypeName());
                detailDto.setPackingVolume(dto.getVolume().doubleValue());
                detailDto.setVolumeCoefficient(dto.getVolumeCoefficient().doubleValue());
                detailDto.setPackingSpecification(dto.getSpecification());
                detailDto.setPackingUnit(dto.getUnit());
                detailDto.setPackingNumber(dto.getConfirmQuantity());

                consumableDtoMap.get(dto.getWaybillCode()).getPackingChargeList().add(detailDto);
            }
            //逐个运单发送MQ
            for (WaybillConsumableDto dto : consumableDtoMap.values()){
                try {
                    waybillConsumableProducer.sendOnFailPersistent(dto.getWaybillCode(), JSON.toJSONString(dto));
                }catch (Exception e){
                    log.error("B网包装耗材确认明细发送运单失败：{}" , JSON.toJSONString(dto), e);
                }
            }
        }
    }

    /**
     * 发送确认消息同步到报表
     * @param confirmedRecords
     */
    private void sendConfirmWaybillConsumableMqToReport(List<WaybillConsumableRecord> confirmedRecords){
        if (CollectionUtils.isEmpty(confirmedRecords)) {
            return;
        }
        Map<String, PackingConsumableReportDto> consumableDtoMap = new HashMap<>();
        for (WaybillConsumableRecord record : confirmedRecords){
            PackingConsumableReportDto dto = new PackingConsumableReportDto();
            dto.setWaybillCode(record.getWaybillCode());
            dto.setCollectionErp(record.getReceiveUserErp());
            if (NumberUtils.isParsable(record.getReceiveUserCode())) {
                dto.setCollectionUserId(Integer.valueOf(record.getReceiveUserCode()));
            }
            dto.setConfirmErp(record.getConfirmUserErp());
            dto.setConfirmSiteCode(record.getDmsId());
            dto.setConfirmSiteName(record.getDmsName());
            dto.setConfirmTime(record.getConfirmTime());
            dto.setPackingChargeDes("耗材价格明细：");
            consumableDtoMap.put(record.getWaybillCode(), dto);
        }
        //构建消息体价格明细
        List<WaybillConsumableDetailInfo> exportDtos = waybillConsumableRelationService.queryByWaybillCodes(new ArrayList<>(consumableDtoMap.keySet()));
        for(WaybillConsumableDetailInfo dto : exportDtos) {
            PackingConsumableReportDto item = consumableDtoMap.get(dto.getWaybillCode());
            String packingChargeStr = item.getPackingChargeDes();
            if (null == dto.getPackingCharge()) {
                continue;
            }

            item.setPackingChargeDes(
                    packingChargeStr
                            .concat(dto.getName())
                            .concat(Constants.SEPARATOR_COLON)
                            .concat(String.valueOf(dto.getPackingCharge().setScale(2, RoundingMode.HALF_UP)))
                            .concat("元")
                            .concat(Constants.SEPARATOR_SEMICOLON)
            );
        }
        //逐个运单发送MQ
        for (PackingConsumableReportDto dto : consumableDtoMap.values()){
            try {
                waybillConsumableReportProducer.sendOnFailPersistent(dto.getWaybillCode(), JSON.toJSONString(dto));
            }catch (Exception e){
                log.error("包装耗材确认消息同步至报表发送失败：{}" , JSON.toJSONString(dto), e);
            }
        }
    }

    //装卸车需求，只拦截待确认的
    @Override
    public Boolean needConfirmed(String waybillCode) {
        WaybillConsumableRecord record = queryOneByWaybillCode(waybillCode);
        if (record == null) {
            return false;
        }
        if(log.isInfoEnabled()) {
            log.info("运单号{}确认耗材服务结果【0：未确认，1：确认】：{}" ,waybillCode, record.getConfirmStatus());
        }
        return UNTREATED_STATE.equals(record.getConfirmStatus());
    }


    @Override
    public JdCResponse<Boolean> doWaybillConsumablePackConfirm(WaybillConsumablePackConfirmReq waybillConsumablePackConfirmReq) {
        JdCResponse<Boolean> res = new JdCResponse<>();

        String businessCode = waybillConsumablePackConfirmReq.getBusinessCode().trim();
        if (!(WaybillUtil.isPackageCode(businessCode) || WaybillUtil.isWaybillCode(businessCode))) {
            res.toFail("请输入正确的单号，支持运单号或包裹号");
            return res;
        }
        String waybillCode = WaybillUtil.getWaybillCode(waybillConsumablePackConfirmReq.getBusinessCode());
        //确认校验
        JdCResponse<WaybillConsumableRecord> confirmConsumableCheckRes = this.confirmConsumableCheck(waybillCode);
        if(!JdCResponse.CODE_SUCCESS.equals(confirmConsumableCheckRes.getCode())) {
            res.toFail(confirmConsumableCheckRes.getMessage());
            return res;
        }

        //验证ERP是否存在
//        BaseStaffSiteOrgDto userOrgInfo = baseMajorManager.getBaseStaffByErpNoCache(waybillConsumablePackConfirmReq.getUser().getUserErp());
//        if (userOrgInfo == null){
//            res.toFail("【" + waybillConsumablePackConfirmReq.getUser().getUserErp() + "】不存在与青龙基础资料中，请核实后录入！");
//            return res;
//        }
//        consumablePackAndConfirm(waybillConsumablePackConfirmReq);

        String operateErp = waybillConsumablePackConfirmReq.getUser().getUserErp();
        int operateCode = waybillConsumablePackConfirmReq.getUser().getUserCode();
        String operateName =  waybillConsumablePackConfirmReq.getUser().getUserName();

        //组装运单耗材打包人关系表数据
        List<WaybillConsumableRelationPDADto> dbBatchUpdateList = new ArrayList<>();
        for(WaybillConsumablePdaDto poTemp : waybillConsumablePackConfirmReq.getWaybillConsumableDtoList()) {
            WaybillConsumableRelationPDADto dbParam = new WaybillConsumableRelationPDADto();
            dbParam.setWaybillCode(waybillCode);
            dbParam.setPackUserErp(operateErp);
            dbParam.setOperateUserErp(operateErp);
            dbParam.setOperateUserCode(operateCode + "");
            Date date = new Date();
            dbParam.setUpdateTime(date);
            dbParam.setOperateTime(date);

            if(poTemp.getConsumableCode() == null || poTemp.getConfirmQuantity() == null) {
                throw new RuntimeException("运单耗材编码及确认数量不可为空");
            }
            dbParam.setConfirmQuantity(poTemp.getConfirmQuantity());
            dbParam.setConsumableCode(poTemp.getConsumableCode());
            dbBatchUpdateList.add(dbParam);
        }

        //组装运单耗材记录表参数
        List<WaybillConsumableRecord> confirmRecords = new ArrayList<WaybillConsumableRecord>();
        WaybillConsumableRecord record = new WaybillConsumableRecord();
        record.setId(confirmConsumableCheckRes.getData().getId());
//        record.setWaybillCode(waybillCode);
        record.setConfirmUserName(operateName);
        record.setConfirmUserErp(operateErp);
        record.setConfirmTime(new Date());
        confirmRecords.add(record);
        consumablePackAndConfirm(dbBatchUpdateList, confirmRecords);

        res.toSucceed();
        return res;
    }

    @Override
    public WaybillConsumableRecord convert2WaybillConsumableRecord(ReceivePackingConsumableDto consumableDto) {
        if (consumableDto == null) {
            return null;
        }

        WaybillConsumableRecord waybillConsumableRecord = new WaybillConsumableRecord();
        waybillConsumableRecord.setWaybillCode(consumableDto.getWaybillCode());

        //根据 packingConsumable.getDmsCode() 查询分拣中心信息
        Integer siteCode = consumableDto.getDmsCode();
        BaseStaffSiteOrgDto dto = baseMajorManager.getBaseSiteBySiteId(siteCode);
        waybillConsumableRecord.setDmsId(dto.getSiteCode());
        waybillConsumableRecord.setDmsName(dto.getSiteName());

        waybillConsumableRecord.setReceiveUserErp(consumableDto.getEntryErp());
        waybillConsumableRecord.setReceiveUserCode(String.valueOf(consumableDto.getEntryId()));
        waybillConsumableRecord.setReceiveUserName(consumableDto.getEntryName());
        waybillConsumableRecord.setReceiveTime(DateHelper.toDate(consumableDto.getPdaTime()));

        waybillConsumableRecord.setConfirmStatus(WaybillConsumableRecordService.UNTREATED_STATE);
        waybillConsumableRecord.setModifyStatus(WaybillConsumableRecordService.UNTREATED_STATE);

        return waybillConsumableRecord;
    }

    /**
     * PDA实操绑定打包人，确认打包
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    private void consumablePackAndConfirm(List<WaybillConsumableRelationPDADto> dbBatchUpdateList, List<WaybillConsumableRecord> confirmRecords ) {
        for(WaybillConsumableRelationPDADto dto : dbBatchUpdateList) {
            waybillConsumableRelationService.updateByWaybillCode(dto);
        }
        this.confirmByIds(confirmRecords);
    }

    @Override
    public JdCResponse<List<WaybillConsumablePackConfirmRes>> getWaybillConsumableInfo(WaybillConsumablePackConfirmReq waybillConsumablePackConfirmReq) {
        String methodDesc = "WaybillConsumableRecordServiceImpl.getWaybillConsumableInfo--PDA操作耗材确认查询接口--";

        JdCResponse<List<WaybillConsumablePackConfirmRes>> res = new JdCResponse<>();
        List<WaybillConsumablePackConfirmRes> resDataList = new ArrayList<>();
        WaybillConsumablePackConfirmRes resData = new WaybillConsumablePackConfirmRes();

        String businessCode = waybillConsumablePackConfirmReq.getBusinessCode();
        if (!(WaybillUtil.isPackageCode(businessCode) || WaybillUtil.isWaybillCode(businessCode))) {
            log.error(methodDesc + "error--运单号错误，businessCode={}", businessCode);
            res.toFail("请输入正确的单号，支持运单号或包裹号");
            return res;
        }
        String waybillCode = WaybillUtil.getWaybillCode(waybillConsumablePackConfirmReq.getBusinessCode());

        //确认校验
        JdCResponse<WaybillConsumableRecord> confirmConsumableCheckRes = this.confirmConsumableCheck(waybillCode);
        if(!JdCResponse.CODE_SUCCESS.equals(confirmConsumableCheckRes.getCode())) {
            res.toFail(confirmConsumableCheckRes.getMessage());
            return res;
        }

        WaybillConsumableRecord record = confirmConsumableCheckRes.getData();

        List<String> list = Arrays.asList(waybillCode);
        List<WaybillConsumableDetailInfo> waybillConsumableDetailInfoList = waybillConsumableRelationService.queryByWaybillCodes(list);
        if(CollectionUtils.isEmpty(waybillConsumableDetailInfoList)) {
            log.info(methodDesc + "--error--耗材记录查询成功，查询耗材打包人及揽收耗材详细信息为空,单号=【{}】", JsonHelper.toJson(list));
            res.toFail("查询耗材详细数据为空");
            return res;
        }
        for(WaybillConsumableDetailInfo wcdi : waybillConsumableDetailInfoList) {
            WaybillConsumablePackConfirmRes resDataTemp = new WaybillConsumablePackConfirmRes();
            resDataTemp.setWaybillCode(record.getWaybillCode());
            resDataTemp.setDmsName(record.getDmsName());
            resDataTemp.setConsumableName(wcdi.getName());
            resDataTemp.setConsumableTypeName(wcdi.getTypeName());
            resDataTemp.setReceiveQuantity(wcdi.getReceiveQuantity());
            resDataTemp.setConsumableCode(wcdi.getConsumableCode());
            resDataList.add(resDataTemp);
        }
        res.setData(resDataList);
        res.toSucceed();
        return res;
    }


    /**
     * 校验运单耗材
     * @param waybillCode
     * @return
     */
    private JdCResponse<WaybillConsumableRecord> confirmConsumableCheck(String waybillCode) {
        JdCResponse<WaybillConsumableRecord> res = new JdCResponse<>();

        WaybillConsumableRecord dbRes = this.queryOneByWaybillCode(waybillCode);
        if(null == dbRes) {
            res.toFail("此单非包装需求单，无需操作！");
            return res;
        }

        //1.该运单未被确认
        if(TREATED_STATE.equals(dbRes.getConfirmStatus())){
            res.toFail(String.format("运单号%s已有%s确认，请勿重复确认", dbRes.getWaybillCode(), dbRes.getConfirmUserErp()));
            return res;
        }
        res.toSucceed();
        res.setData(dbRes);
        return res;

    }
}
