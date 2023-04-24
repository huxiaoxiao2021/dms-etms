package com.jd.bluedragon.core.base;

import com.jd.ql.erp.dto.ResponseDTO;
import com.jd.ql.erp.dto.delivery.DeliveredReqDTO;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/4/24 11:13
 * @Description:
 */
public interface DeliveryWSManager {

    ResponseDTO delivered(DeliveredReqDTO deliveredReqDTO);
}
