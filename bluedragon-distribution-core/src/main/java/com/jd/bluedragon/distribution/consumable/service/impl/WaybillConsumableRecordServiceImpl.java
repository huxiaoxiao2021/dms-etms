package com.jd.bluedragon.distribution.consumable.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.consumable.dao.WaybillConsumableRecordDao;
import com.jd.bluedragon.distribution.consumable.domain.*;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.fastjson.JSON;
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
public class WaybillConsumableRecordServiceImpl extends BaseService<WaybillConsumableRecord> implements WaybillConsumableRecordService {

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
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean updateByCondition(WaybillConsumableRecord record) {
        if(record == null || (record.getId() == null && StringUtils.isEmpty(record.getWaybillCode()))){
            logger.info("Bw网耗材数据更新失败，参数非法：" + JsonHelper.toJson(record));
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
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @JProfiler(jKey = "DMSWEB.WaybillConsumableRecordService.confirmByIds", mState = {JProEnum.TP, JProEnum.FunctionError}, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public int confirmByIds(List<WaybillConsumableRecord> confirmRecords) {
	    if(confirmRecords == null || confirmRecords.isEmpty()){
            return 0;
        }
        if(confirmRecords.size() > MAX_ROWS){
	        logger.warn("批量确认数据超过最大支持量，数据总数：" + confirmRecords.size());
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
        if (record != null && ! TREATED_STATE.equals(record.getConfirmStatus())) {
            return false;
        }
        return true;
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
                    logger.error("B网包装耗材确认明细发送运单失败：" + JSON.toJSONString(dto), e);
                }
            }
        }
    }
}
