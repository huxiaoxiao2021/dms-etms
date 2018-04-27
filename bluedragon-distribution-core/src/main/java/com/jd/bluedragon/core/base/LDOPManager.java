package com.jd.bluedragon.core.base;

import com.jd.ldop.center.api.reverse.dto.WaybillReverseDTO;
import com.jd.ql.dms.common.domain.JdResponse;

import java.util.List;

/**
 * 外单jsf接口 包装类
 */
public interface LDOPManager {

    /**
     * 外单自动换单接口
     * @param waybillReverseDTO
     * @return
     */
    boolean waybillReverse(WaybillReverseDTO waybillReverseDTO,JdResponse<Boolean> rest);
}
