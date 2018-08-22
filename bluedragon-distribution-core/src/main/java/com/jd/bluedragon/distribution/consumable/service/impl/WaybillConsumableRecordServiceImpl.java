package com.jd.bluedragon.distribution.consumable.service.impl;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.consumable.dao.WaybillConsumableRecordDao;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableDetailDto;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableDto;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableExportDto;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecord;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
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
    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public int confirmByIds(List<WaybillConsumableRecord> confirmRecords) {
        int result = waybillConsumableRecordDao.updateByIds(confirmRecords);
        //TODO 查出明细，发送运单MQ
        Map<String, WaybillConsumableDto> consumableDtoMap = new HashMap<String, WaybillConsumableDto>(confirmRecords.size());
        for (WaybillConsumableRecord record : confirmRecords){
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
        List<WaybillConsumableExportDto> exportDtos = waybillConsumableRelationService.queryByWaybillCodes(new ArrayList<String>(consumableDtoMap.keySet()));
        for(WaybillConsumableExportDto dto : exportDtos){

        }

        sendConfirmWaybillConsumableMq(new ArrayList<WaybillConsumableDto>(consumableDtoMap.values()));
        return result;
    }

    /**
     * 发送确认明细MQ通知运单
     * @param consumableDtos
     */
    private void sendConfirmWaybillConsumableMq(List<WaybillConsumableDto> consumableDtos){
//        waybillConsumableRelationService.
    }
}
