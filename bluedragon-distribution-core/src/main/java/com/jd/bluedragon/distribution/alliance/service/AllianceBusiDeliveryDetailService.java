package com.jd.bluedragon.distribution.alliance.service;

import com.jd.bluedragon.distribution.alliance.AllianceBusiDeliveryDto;
import com.jd.bluedragon.distribution.alliance.AllianceBusiFailDetailDto;
import com.jd.bluedragon.distribution.alliance.domain.AllianceBusiDeliveryDetail;
import com.jd.bluedragon.distribution.wss.dto.BaseEntity;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

/**
 *
 * @ClassName: AllianceBusiDeliveryDetailService
 * @Description: 加盟商计费交付明细表--Service接口
 * @author wuyoude
 * @date 2019年07月10日 15:35:36
 *
 */
public interface AllianceBusiDeliveryDetailService extends Service<AllianceBusiDeliveryDetail> {

    /**
     * 按运单校验是否已入交接池
     * @param waybillCode
     * @return
     */
    boolean checkExist(String waybillCode);

    /**
     * 交接
     * @param dto
     * @return
     */
    BaseEntity<List<AllianceBusiFailDetailDto>> allianceBusiDelivery(AllianceBusiDeliveryDto dto);

    /**
     * 按运单校验加盟商余额是否充足
     * @param waybillCode
     * @return
     */
    boolean checkMoney(String waybillCode);
}
