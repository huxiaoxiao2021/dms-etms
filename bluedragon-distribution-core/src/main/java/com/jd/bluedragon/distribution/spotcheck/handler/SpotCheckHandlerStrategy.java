package com.jd.bluedragon.distribution.spotcheck.handler;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckConstants;
import com.jd.bluedragon.distribution.spotcheck.domain.SpotCheckDto;
import com.jd.bluedragon.distribution.spotcheck.enums.SpotCheckSourceFromEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 抽检处理器策略
 *
 * @author hujiping
 * @date 2021/8/10 11:34 上午
 */
@Service
public class SpotCheckHandlerStrategy {

    @Autowired
    @Qualifier("artificialSpotCheckHandler")
    private ISpotCheckHandler artificialSpotCheckHandler;

    @Autowired
    @Qualifier("androidSpotCheckHandler")
    private ISpotCheckHandler androidSpotCheckHandler;

    @Autowired
    @Qualifier("clientSpotCheckHandler")
    private ISpotCheckHandler clientSpotCheckHandler;

    @Autowired
    @Qualifier("dwsSpotCheckHandler")
    private ISpotCheckHandler dwsSpotCheckHandler;

    @Autowired
    @Qualifier("webSpotCheckHandler")
    private ISpotCheckHandler webSpotCheckHandler;

    public InvokeResult<Integer> checkIsExcess(SpotCheckDto spotCheckDto) {
        InvokeResult<Integer> result = new InvokeResult<Integer>();
        // 客户端抽检
        if(Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_CLIENT_PLATE.getName(), spotCheckDto.getSpotCheckSourceFrom())){
            return clientSpotCheckHandler.checkIsExcess(spotCheckDto);
        }
        // dws抽检
        if(Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_DWS.getName(), spotCheckDto.getSpotCheckSourceFrom())){
            return dwsSpotCheckHandler.checkIsExcess(spotCheckDto);
        }
        // web页面抽检
        if(Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_DMS_WEB.getName(), spotCheckDto.getSpotCheckSourceFrom())){
            return webSpotCheckHandler.checkIsExcess(spotCheckDto);
        }
        // 安卓抽检
        if(Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_ANDROID.getName(), spotCheckDto.getSpotCheckSourceFrom())){
            return androidSpotCheckHandler.checkIsExcess(spotCheckDto);
        }
        // 人工抽检
        if(Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_ARTIFICIAL.getName(), spotCheckDto.getSpotCheckSourceFrom())){
            return artificialSpotCheckHandler.checkIsExcess(spotCheckDto);
        }
        result.parameterError("未知抽检来源，不予处理!");
        return result;
    }

    /**
     * 抽检处理器选择
     *
     * @param spotCheckDto
     * @return
     */
    public InvokeResult<Boolean> dealSpotCheck(SpotCheckDto spotCheckDto) {
        InvokeResult<Boolean> result = new InvokeResult<Boolean>();

        // 客户端抽检
        if(Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_CLIENT_PLATE.getName(), spotCheckDto.getSpotCheckSourceFrom())){
            return clientSpotCheckHandler.dealSpotCheck(spotCheckDto);
        }
        // dws抽检
        if(Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_DWS.getName(), spotCheckDto.getSpotCheckSourceFrom())){
            return dwsSpotCheckHandler.dealSpotCheck(spotCheckDto);
        }
        // web页面抽检
        if(Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_DMS_WEB.getName(), spotCheckDto.getSpotCheckSourceFrom())){
            return webSpotCheckHandler.dealSpotCheck(spotCheckDto);
        }
        // 安卓抽检
        if(Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_ANDROID.getName(), spotCheckDto.getSpotCheckSourceFrom())){
            return androidSpotCheckHandler.dealSpotCheck(spotCheckDto);
        }
        // 人工抽检
        if(Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_ARTIFICIAL.getName(), spotCheckDto.getSpotCheckSourceFrom())){
            return artificialSpotCheckHandler.dealSpotCheck(spotCheckDto);
        }
        result.parameterError("未知抽检来源，不予处理!");
        return result;
    }

    /**
     * 校验是否超标（只校验超标，不校验其他信息）
     *
     * @param spotCheckDto
     * @return
     */
    public InvokeResult<Integer> checkIsExcessWithOutOtherCheck(SpotCheckDto spotCheckDto) {
        InvokeResult<Integer> result = new InvokeResult<Integer>();
        if(!Objects.equals(SpotCheckSourceFromEnum.SPOT_CHECK_DWS.getName(), spotCheckDto.getSpotCheckSourceFrom())){
            result.parameterError("未知抽检来源，不予处理!");
            return result;
        }
        return dwsSpotCheckHandler.checkIsExcessWithOutOtherCheck(spotCheckDto);
    }
}
