package com.jd.bluedragon.distribution.consumable.service.impl;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.consumable.request.WaybillConsumablePackConfirmReq;
import com.jd.bluedragon.common.dto.consumable.response.WaybillConsumablePackConfirmRes;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.consumable.dao.WaybillConsumableRecordDao;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableDetailDto;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableDetailInfo;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableDto;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableExportDto;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecord;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecordCondition;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumablePDAService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private static int WAYBILL_CONSUMABLE_MESSAGE_TYPE = 2;

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
            log.warn("运单号{}需要使用包装耗材服务，但是不存在包装耗材服务任务，需对TOPIC：【bd_pack_sync_waybill】查询归档",waybillCode);
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

        JdCResponse<Boolean> res = new JdCResponse<Boolean>();
        //todo zcf 打包确认逻辑
        res.toFail("操作失败");
        return res;
    }

    @Override
    public JdCResponse<WaybillConsumablePackConfirmRes> getWaybillConsumableInfo(WaybillConsumablePackConfirmReq waybillConsumablePackConfirmReq) {
        JdCResponse<WaybillConsumablePackConfirmRes> res = new JdCResponse<>();
        //todo zcf 打包确认查询逻辑
        res.toFail("操作失败");
        return res;
    }
}
