package com.jd.bluedragon.distribution.abnormalDispose.service;

import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeCondition;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeInspection;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeMain;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeSend;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年06月18日 09时:36分
 */
public interface AbnormalDisposeService {

    public PagerResult<AbnormalDisposeInspection> queryInspection(AbnormalDisposeCondition abnormalDisposeCondition);

    public PagerResult<AbnormalDisposeMain> queryMain(AbnormalDisposeCondition abnormalDisposeCondition);

    public PagerResult<AbnormalDisposeSend> querySend(AbnormalDisposeCondition abnormalDisposeCondition);

    JdResponse<String> saveAbnormalQc(AbnormalDisposeInspection abnormalDisposeInspection);

}
