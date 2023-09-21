package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.tms.jdi.dto.CommonDto;
import com.jd.tms.jdi.param.TransWorkItemQueueCallParam;
import com.jd.tms.jdi.ws.JdiTransQueueWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("jdiTransQueueWSManager")
public class JdiTransQueueWSManagerImpl implements JdiTransQueueWSManager {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JdiTransQueueWS jdiTransQueueWS;

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMS.BASE.JdiTransQueueWSManagerImpl.sendCarArriveStatus",mState = {JProEnum.TP, JProEnum.FunctionError})
    public CommonDto<String> callByWorkItem(TransWorkItemQueueCallParam param) {
        try {
            return jdiTransQueueWS.callByWorkItem(param);
        } catch (Exception e) {
            logger.warn("运输任务明细叫号失败！ 入参{}", JsonHelper.toJson(param));
            return null;
        }
    }
}
