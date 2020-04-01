package com.jd.bluedragon.distribution.material.service;

import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.material.domain.DmsMaterialSend;

import java.util.List;

/**
 * @ClassName MaterialSendService
 * @Description
 * @Author wyh
 * @Date 2020/3/13 17:34
 **/
public interface MaterialSendService {

    JdResult<Boolean> saveMaterialSend(List<DmsMaterialSend> materialSends, Boolean saveFlow);

    JdResult<List<DmsMaterialSend>> listMaterialSendBySendCode(String sendCode, Long createSiteCode);

    JdResult<Long> countMaterialSendRecordByBatchCode(String sendCode, Long createSiteCode);
}
