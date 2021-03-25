package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.box.request.BoxRelationReq;
import com.jd.bluedragon.common.dto.box.response.BoxCodeGroupBinDingDto;
import com.jd.bluedragon.common.dto.box.response.BoxDto;
import com.jd.bluedragon.common.dto.box.response.BoxRelationDto;

import java.util.List;

/**
 * 箱号相关
 * 发布到物流网关 由安卓调用
 * @author : xumigen
 * @date : 2019/7/3
 */
public interface BoxGatewayService {

    JdVerifyResponse<BoxDto> boxValidation(String boxCode, Integer operateType);

    JdCResponse<List<BoxDto>> getGroupEffectiveBoxes(String boxCode);

    JdCResponse<BoxDto> getBoxInfo(String boxCode);

    /**
     * 查询箱号绑定关系
     * @param req
     * @return
     */
    JdCResponse<List<BoxRelationDto>> getBoxRelations(BoxRelationReq req);

    /**
     * 保存箱号绑定关系
     * @param req
     * @return
     */
    JdCResponse<Boolean> submitBoxBinding(BoxRelationReq req);

    JdVerifyResponse<BoxDto> boxValidationAndCheck(String boxCode,Integer operateType,Integer siteCode);
}
