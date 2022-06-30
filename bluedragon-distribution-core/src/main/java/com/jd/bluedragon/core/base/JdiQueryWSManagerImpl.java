package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.tms.jdi.dto.CommonDto;
import com.jd.tms.jdi.dto.TransWorkItemDto;
import com.jd.tms.jdi.ws.JdiQueryWS;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/10/8 2:18 下午
 */
@Service("jdiQueryWSManager")
public class JdiQueryWSManagerImpl implements JdiQueryWSManager {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JdiQueryWS jdiQueryWS;

    @Override
    public CommonDto<TransWorkItemDto> queryTransWorkItemBySimpleCode(String simpleCode) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.JdiQueryWSManager.queryTransWorkItemBySimpleCode",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            if(StringUtils.isEmpty(simpleCode)){
                return null;
            }
            return jdiQueryWS.queryTransWorkItemBySimpleCode(simpleCode);
        }catch (Exception e){
            logger.warn("根据派车任务明细简码:{}获取派车任务明细异常!", simpleCode);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }


}
