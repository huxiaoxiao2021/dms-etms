package com.jd.bluedragon.distribution.mergeWaybillCodeReturn.service;

import com.jd.bluedragon.distribution.mergeWaybillCodeReturn.domain.MergeWaybillMessage;
import com.jd.ldop.center.api.reverse.dto.ReturnSignatureMessageDTO;


/**
 * @ClassName: 123
 * @Description: 123
 * @author: hujiping
 * @date: 2018/9/17 20:27
 */
public interface MergeWaybillCodeReturnService {

    void sendTrace(MergeWaybillMessage message);

    Boolean compareWith(ReturnSignatureMessageDTO data, ReturnSignatureMessageDTO data1);
}
