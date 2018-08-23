package com.jd.bluedragon.distribution.consumable.service.impl;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.consumable.dao.WaybillConsumableRecordDao;
import com.jd.bluedragon.distribution.consumable.domain.*;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.fastjson.JSON;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
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
        WaybillConsumableRecord condition = new WaybillConsumableRecord();
        condition.setWaybillCode(waybillCode);
	    return waybillConsumableRecordDao.queryOneByCondition(condition);
    }

    @Override
    public boolean updateByCondition(WaybillConsumableRecord record) {
        if(record == null || record.getId() == null || StringUtils.isEmpty(record.getWaybillCode())){
            logger.info("Bw网耗材数据更新失败，参数非法：" + JsonHelper.toJson(record));
            return false;
        }
	    return waybillConsumableRecordDao.update(record);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public int confirmByIds(List<WaybillConsumableRecord> confirmRecords) {
	    if(confirmRecords == null || confirmRecords.isEmpty()){
            return 0;
        }
        if(confirmRecords.size() > MAX_ROWS){
	        logger.warn("批量确认数据超过最大支持量，数据总数：" + confirmRecords.size());
            throw new IllegalArgumentException("批量确认最大支持数量：" + MAX_ROWS);
        }

	    //1.更新主表信息
        List<Long> ids = new ArrayList<Long>(confirmRecords.size());
        for (WaybillConsumableRecord record : confirmRecords){
            ids.add(record.getId());
            record.setConfirmStatus(WaybillConsumableRecordService.TREATED_STATE);
        }
        int result = waybillConsumableRecordDao.updateByIds(confirmRecords);

        //2.发送MQ通知运单
        List<WaybillConsumableRecord> confirmedRecords = waybillConsumableRecordDao.findByIds(ids);
        sendConfirmWaybillConsumableMq(confirmedRecords);

        return result;
    }

    @Override
    public boolean canModifiy(String waybillCode) {
        Waybill waybill = waybillCommonService.findByWaybillCode(waybillCode);
        //1.标示为标识可以修改
        if(waybill != null && BusinessHelper.isWaybillConsumableOnlyConfirm(waybill.getWaybillSign())){
            WaybillConsumableRecord oldRecord = queryOneByWaybillCode(waybillCode);
            //2.且该运单未被确认
            if(oldRecord != null && oldRecord.getId() != null && UNTREATED_STATE.equals(oldRecord.getConfirmStatus())){
                return true;
            }
        }
        return false;
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
                detailDto.setPackingCode(dto.getCode());
                detailDto.setPackingName(dto.getName());
                detailDto.setPackingType(dto.getType());
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
