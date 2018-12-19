package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.reverse.domain.LocalClaimInfoRespDTO;
import com.jd.uad.api.claim.facade.claim.resp.ClaimInfoRespDTO;
import com.jd.uad.api.core.APIResultDTO;

import java.util.List;

/**
 * 理赔系统接口
 */
public interface OBCSManager {

    /**
     * 获取理赔信息
     *
     * @param clueType   线索类型   1-青龙运单号,2-盘亏单号,3-大件运单号,4- TC转运单（中小件）,7- ECLP理赔申请单号
     * @param clueValue  线索值
     * @return
     */
    LocalClaimInfoRespDTO getClaimListByClueInfo(int clueType, String clueValue);
}
