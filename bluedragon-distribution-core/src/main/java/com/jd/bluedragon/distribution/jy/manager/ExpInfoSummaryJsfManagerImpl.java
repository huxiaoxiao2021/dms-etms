package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.Constants;
import com.jd.ps.data.epf.dto.CommonDto;
import com.jd.ps.data.epf.dto.ExpInfoSumaryInputDto;
import com.jd.ps.data.epf.service.ExpInfoSummaryJsfService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExpInfoSummaryJsfManagerImpl implements ExpInfoSummaryJsfManager {
    @Autowired
    private ExpInfoSummaryJsfService expInfoSummaryJsfService;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.ExpInfoSummaryJsfManagerImpl.addExpInfoDetail", mState = {JProEnum.TP, JProEnum.FunctionError})
    public CommonDto addExpInfoDetail(ExpInfoSumaryInputDto var1) {
        return expInfoSummaryJsfService.addExpInfoDetail(var1);
    }
}
