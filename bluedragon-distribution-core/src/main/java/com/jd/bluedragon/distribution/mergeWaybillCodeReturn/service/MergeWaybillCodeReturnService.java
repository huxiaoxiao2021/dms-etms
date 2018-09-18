package com.jd.bluedragon.distribution.mergeWaybillCodeReturn.service;

import com.jd.bluedragon.distribution.mergeWaybillCodeReturn.domain.MergeWaybillMessage;
import com.jd.ldop.center.api.reverse.dto.ReturnSignatureMessageDTO;

import java.util.List;

/**
 * @ClassName: 123
 * @Description: 123
 * @author: hujiping
 * @date: 2018/9/17 20:27
 */
public interface MergeWaybillCodeReturnService {
    Boolean compare(ReturnSignatureMessageDTO data, ReturnSignatureMessageDTO data1) throws IllegalAccessException;

    void sendTrace(MergeWaybillMessage message);
}
