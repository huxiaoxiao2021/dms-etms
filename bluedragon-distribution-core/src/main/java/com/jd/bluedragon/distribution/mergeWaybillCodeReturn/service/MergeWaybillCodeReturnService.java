package com.jd.bluedragon.distribution.mergeWaybillCodeReturn.service;

import com.jd.bluedragon.distribution.mergeWaybillCodeReturn.domain.MergeWaybillCodeReturnRequest;
import com.jd.bluedragon.distribution.mergeWaybillCodeReturn.domain.MergeWaybillMessage;
import com.jd.ldop.center.api.reverse.dto.ReturnSignatureMessageDTO;
import com.jd.ldop.center.api.reverse.dto.WaybillReturnSignatureDTO;
import com.jd.ql.dms.common.domain.JdResponse;


/**
 * @ClassName: 123
 * @Description: 123
 * @author: hujiping
 * @date: 2018/9/17 20:27
 */
public interface MergeWaybillCodeReturnService {
    /**
     * 发旧单号和新单号全程跟踪
     * @param message
     */
    void sendTrace(MergeWaybillMessage message);
    /**
     * 判断是否相同
     * @param data
     * @param secondData
     * @return
     */
    Boolean compareWith(ReturnSignatureMessageDTO data, ReturnSignatureMessageDTO secondData);
    /**
     * 根据旧运单号合单生成新的运单号
     * @param dto
     */
    JdResponse mergeWaybillCode(WaybillReturnSignatureDTO dto,MergeWaybillCodeReturnRequest mergeWaybillCodeReturnRequest);
    /**
     * 判断是否可以合单
     * @param waybillCode
     * @param secondWaybillCode
     * @return
     */
    JdResponse checkIsMerge(String waybillCode, String secondWaybillCode);
}
