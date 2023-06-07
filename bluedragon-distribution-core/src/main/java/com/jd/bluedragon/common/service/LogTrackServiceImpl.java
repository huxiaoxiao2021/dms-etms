package com.jd.bluedragon.common.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.command.JdCommandService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.logTrack.LogTrackService;
import com.jd.bluedragon.distribution.print.domain.LogDto;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.mdc.LogWriteUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.AccessType;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liwenji
 * @description 
 * @date 2023-06-07 14:45
 */
@Service("logTrackService")
@Slf4j
public class LogTrackServiceImpl implements LogTrackService {
    
    @Autowired
    private JdCommandService jdCommandService;

    @Override
    @JProfiler(jKey = "dmsWeb.jsf.server.PackagePrintServiceImpl.checkPrintCrossTableTrolley", jAppName= Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResult<List<LogDto>> checkPrintCrossTableTrolley(String printRequest) {
        if(log.isInfoEnabled()){
            log.info("查询包裹信息参数：{}", JsonHelper.toJson(printRequest));
        }
        JdResult<List<LogDto>> result = new JdResult<>();
        result.toSuccess();
        if(printRequest == null){
            result.toFail("传入的参数不能为空！");
            return result;
        }
        try{
            LogWriteUtil.init();
            jdCommandService.execute(printRequest);
            List<LogDto> list = LogWriteUtil.getLogList();
            result.setData(list);
            return result;
        }catch (Exception e) {
            log.error("校验滑道笼车信息异常：{}", JsonHelper.toJson(printRequest),e);
        }finally {
            LogWriteUtil.clear();
        }
        return null;
    }

}
