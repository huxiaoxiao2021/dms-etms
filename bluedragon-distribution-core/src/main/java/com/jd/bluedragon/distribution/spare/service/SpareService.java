package com.jd.bluedragon.distribution.spare.service;

import java.util.List;

import com.jd.bluedragon.distribution.api.request.SpareRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spare.domain.Spare;

public interface SpareService {
    
    Integer update(Spare spare);
    
    List<Spare> print(Spare spare);
    
    Integer reprint(Spare spare);
    
    List<Spare> findSpares(Spare spare);
    
    Spare findBySpareCode(String spareCode);
    /**
     * 批量生成备件条码
     * @param spareReq
     * @return
     */
    InvokeResult<List<Spare>> genCodes(SpareRequest spareReq);
    
}
