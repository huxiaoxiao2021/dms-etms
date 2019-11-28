package com.jd.bluedragon.core.base;

import com.jd.tms.basic.dto.ConfNodeCarrierDto;

/**
 * 运输基础字段接口包装
 * @author : xumigen
 * @date : 2019/9/16
 */
public interface BasicQueryWSManager {

    /**
     * 根据网点编码查询承运商
     * @param nodeCode 始发车站
     * @return
     */
    ConfNodeCarrierDto getCarrierByNodeCode(String nodeCode);
}
