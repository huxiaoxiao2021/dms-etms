package com.jd.bluedragon.external.crossbow.pdd.manager;

import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDResponse;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillDetailDto;
import com.jd.bluedragon.external.crossbow.pdd.domain.PDDWaybillQueryDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

/**
 * <p>
 *     拼多多公司的外部服务对接实现类 manager层
 *
 * @author wuzuxiang
 * @since 2019/10/15
 **/
public class PDDManagerImpl extends PDDManager {

    @Override
    @JProfiler(jKey = "dms.web.PDDManagerImpl.queryWaybillDetailByWaybillCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    public PDDResponse<PDDWaybillDetailDto> queryWaybillDetailByWaybillCode(PDDWaybillQueryDto request) {
        return pddExecutor(request, new TypeReference<PDDResponse<PDDWaybillDetailDto>>(){});
    }

}
