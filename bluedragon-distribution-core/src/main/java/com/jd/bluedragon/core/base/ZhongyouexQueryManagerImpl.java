package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.zhongyouex.api.common.CommonParam;
import com.zhongyouex.order.api.box.BoxOperateApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ZhongyouexQueryManagerImpl  implements ZhongyouexQueryManager{

    private static final Logger log = LoggerFactory.getLogger(ZhongyouexQueryManagerImpl.class);

    private static final String APPCODE = "JD_EXPRESS_FRONT";
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
        log.info("调众邮接口判断箱是否绑定运单，param：" + boxCode);
        Integer result = boxOperateApi.findBoxIsEmpty(commonParam,boxCode);
        log.info("调众邮接口判断箱是否绑定运单，result：" + result);
        return EMPTYBOX.equals(result)? Boolean.TRUE: Boolean.FALSE;
    }
}
