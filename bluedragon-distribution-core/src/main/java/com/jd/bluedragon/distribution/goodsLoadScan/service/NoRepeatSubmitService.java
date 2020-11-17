package com.jd.bluedragon.distribution.goodsLoadScan.service;

public interface NoRepeatSubmitService {
    public Long checkRepeatSubmit(Long taskId);
}
