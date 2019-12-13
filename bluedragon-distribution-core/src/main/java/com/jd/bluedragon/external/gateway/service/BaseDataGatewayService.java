package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.basedata.response.BaseDataDictDto;

import java.util.List;

/**
 * 基础信息发布物流网关
 * @author : xumigen
 * @date : 2019/9/10
 */
public interface BaseDataGatewayService {

    JdCResponse<List<BaseDataDictDto>> getBaseDictionaryTree(int typeGroup);

    /**
     * 根据类型分组获取数据字典信息
     *
     * @param typeGroups
     * @return
     */
    JdCResponse<List<BaseDataDictDto>> getBaseDictByTypeGroups(List<Integer> typeGroups);

}
