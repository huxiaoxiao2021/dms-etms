package com.jd.bluedragon.core.jsf.dms;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.etms.blocker.dto.BlockerApplyDto;
import com.jd.etms.blocker.dto.CommonDto;

/**
 * 运单拦截信息接口
 */
public interface BlockerQueryWSJsfManager {


    /**
     * 获取运单拦截信息接口
     * @param waybillCode
     * @return
     */
    JdCResponse queryExceptionOrders(String waybillCode);

    /**
     * 拦截申请接口
     * @param dto
     * @return
     */
    CommonDto<String> applyIntercept(BlockerApplyDto dto);

}
