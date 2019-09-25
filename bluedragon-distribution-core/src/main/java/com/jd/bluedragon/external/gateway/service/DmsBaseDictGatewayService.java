package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.basedict.response.BaseDict;

import java.util.List;

/**
 * 字典信息发布物流网关
 * @author : xumigen
 * @date : 2019/9/24
 */
public interface DmsBaseDictGatewayService {

    JdCResponse<List<BaseDict>> queryListByParentId(Integer parentId);

    JdCResponse<List<BaseDict>> queryLowerLevelListByTypeCode(Integer typeCode);
}
