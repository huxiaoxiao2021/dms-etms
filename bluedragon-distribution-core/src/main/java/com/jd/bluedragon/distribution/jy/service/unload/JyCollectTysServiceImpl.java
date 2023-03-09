package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.jy.api.JyCollectTysService;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectQueryReqDto;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectReportDetailResDto;
import com.jd.bluedragon.distribution.jy.dto.collect.CollectReportResDto;
import com.jd.bluedragon.distribution.jy.service.collect.JyCollectService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @Author zhengchengfa
 * @Description //转运集齐服务
 * @date
 **/
public class JyCollectTysServiceImpl implements JyCollectTysService {

    @Autowired
    private JyCollectService jyCollectService;

    @Override
    public InvokeResult<CollectReportResDto> findCollectInfo(CollectQueryReqDto collectQueryReqDto) {
        return jyCollectService.findCollectInfo(collectQueryReqDto);
    }


    @Override
    public InvokeResult<CollectReportDetailResDto> findCollectDetail(CollectQueryReqDto collectQueryReqDto) {
        return jyCollectService.findCollectDetail(collectQueryReqDto);
    }
}
