package com.jd.bluedragon.core.base;

import com.jd.bluedragon.CloudPrintConstants;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import com.jdl.print.dto.render.PrintResultDTO;
import com.jdl.print.dto.render.RenderResultDTO;
import com.jdl.print.dto.render.ReprintQueryRenderDTO;
import com.jdl.print.service.RenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * 国际化云打印接口
 *
 * @author hujiping
 * @date 2023/7/19 2:11 PM
 */
@Service("internationalCloudPrintManager")
public class InternationalCloudPrintManagerImpl implements InternationalCloudPrintManager {

    private static final Logger logger = LoggerFactory.getLogger(InternationalCloudPrintManagerImpl.class);
    
    @Autowired
    private RenderService renderService;
    
    @Override
    public List<RenderResultDTO> internationalCloudPrint(ReprintQueryRenderDTO renderQuery) {
        CallerInfo callerInfo = ProfilerHelper.registerInfo("com.jd.bluedragon.core.base.InternationalCloudPrintManager.jdCloudPrint");
        try {
            // 系统来源：分拣
            renderQuery.setCallSystem(Constants.SYSTEM_CODE_OWON);
            PrintResultDTO printResultDTO = renderService.render4OuterReprint(renderQuery);
            if(printResultDTO == null){
                logger.error("国际化调用云打印失败! req:{}", JsonHelper.toJson(renderQuery));
                return null;
            }
            if(!Objects.equals(printResultDTO.getCode(), CloudPrintConstants.PRINT_CODE_SUC)){
                logger.error("国际化调用云打印失败! req:{} msg:{}", JsonHelper.toJson(renderQuery), printResultDTO.getMsg());
                return null;
            }
            return printResultDTO.getResult();
        } catch (Exception e) {
            Profiler.functionError(callerInfo);
            logger.error("国际化调用云打印失败！req:{}",JsonHelper.toJson(renderQuery), e);
        } finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }
    
}
