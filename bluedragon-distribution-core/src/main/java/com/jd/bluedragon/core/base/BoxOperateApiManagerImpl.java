package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.zhongyouex.api.common.CommonParam;
import com.zhongyouex.api.common.CommonResponseDto;
import com.zhongyouex.order.api.box.BoxOperateApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWORKER,jKey = "DMS.BASE.WorkTaskServiceManagerImpl.findBoxIsEmpty", mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean findBoxIsEmpty(String boxCode) {
        CommonParam commonParam = new CommonParam();
        commonParam.setAppCode(APPCODE);
        commonParam.setSource(SOURCE);
        if(log.isInfoEnabled()){
            log.info("调众邮接口判断箱是否绑定运单，param：" + boxCode);
        }
        CommonResponseDto<Integer> result = boxOperateApi.findBoxIsEmpty(commonParam,boxCode);
        if(log.isInfoEnabled()){
            log.info("调众邮接口判断箱是否绑定运单，result：" + JsonHelper.toJson(result));
        }
        if (null == result || CommonResponseDto.CODE_SUCCESS != result.getCode()){
            log.warn("调众邮接口失败，失败原因" + JsonHelper.toJson(result));
        }
        return null != result && EMPTYBOX.equals(result.getData())? Boolean.TRUE: Boolean.FALSE;
    }
}
