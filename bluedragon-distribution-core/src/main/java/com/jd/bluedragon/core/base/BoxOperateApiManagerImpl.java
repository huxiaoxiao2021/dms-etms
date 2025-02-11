package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.proxy.Profiler;
import com.zhongyouex.api.common.CommonParam;
import com.zhongyouex.api.common.CommonResponseDto;
import com.zhongyouex.order.api.box.BoxOperateApi;
import com.zhongyouex.order.api.dto.BoxDetailInfoDto;
import com.zhongyouex.order.api.dto.BoxDetailResultDto;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BoxOperateApiManagerImpl implements BoxOperateApiManager {

    private static final Logger log = LoggerFactory.getLogger(BoxOperateApiManagerImpl.class);

    @Value("${ZhongyouexQueryManagerImpl.APPCODE}")
    private String APPCODE;
    private static final String SOURCE = "1020";

    //1:非空箱   0：空箱
    private static final Integer NOTEMPTYBOX = 1;
    private static final Integer EMPTYBOX = 0;

    @Autowired
    private BoxOperateApi boxOperateApi;

    /**
     * 判断箱是否空箱
     * @param boxCode
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.BASE.BoxOperateApiManagerImpl.findBoxIsEmpty", mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean findBoxIsEmpty(String boxCode) {
        if(log.isInfoEnabled()){
            log.info("调众邮接口判断箱是否绑定运单，param：" + boxCode);
        }
        CommonResponseDto<Integer> result = boxOperateApi.findBoxIsEmpty(initCommonParam(),boxCode);
        if(log.isInfoEnabled()){
            log.info("调众邮接口判断箱是否绑定运单，result：" + JsonHelper.toJson(result));
        }
        if (null == result || CommonResponseDto.CODE_SUCCESS != result.getCode()){
            log.warn("调众邮接口失败，失败原因" + JsonHelper.toJson(result));
        }
        return null != result && EMPTYBOX.equals(result.getData())? Boolean.TRUE: Boolean.FALSE;
    }

    /**
     * 获取箱包明细
     * @param boxCode
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.BASE.BoxOperateApiManagerImpl.findBoxDetailInfoList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<BoxDetailInfoDto> findBoxDetailInfoList(String boxCode) {
        BoxDetailResultDto resultDto = boxOperateApi.findBoxDetailInfoList(initCommonParam(),boxCode);
        if(resultDto!=null && CommonResponseDto.CODE_SUCCESS == resultDto.getCode()){
            if(!CollectionUtils.isEmpty(resultDto.getBoxDetailList()) && resultDto.getBoxDetailList().size() > 300){
                String errorMessage = boxCode + " 装箱数量 "+ resultDto.getBoxDetailList().size();
                Profiler.businessAlarm("DMS.BASE.BoxOperateApiManagerImpl.findBoxDetailInfoList.big", System.currentTimeMillis(), errorMessage);
            }
            return resultDto.getBoxDetailList();
        }
        return null;
    }

    /**
     * 初始公共参数
     * @return
     */
    private CommonParam initCommonParam(){
        CommonParam commonParam = new CommonParam();
        commonParam.setAppCode(APPCODE);
        commonParam.setSource(SOURCE);
        return commonParam;
    }

}
