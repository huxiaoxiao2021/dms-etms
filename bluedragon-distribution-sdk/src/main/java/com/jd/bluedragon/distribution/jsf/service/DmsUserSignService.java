package com.jd.bluedragon.distribution.jsf.service;

import com.jd.bluedragon.distribution.jsf.domain.UserSignQueryRequest;
import com.jd.bluedragon.distribution.jsf.domain.UserSignRecordData;
import com.jd.bluedragon.distribution.jsf.domain.UserSignRequest;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/11/23 17:31
 * @Description: 用户签到、签退 服务
 */
public interface DmsUserSignService {

    /**
     * 自动签到、签退-执行添加、移除组员操作
     * @param userSignRequest
     * @return
     */
    JdResponse<UserSignRecordData> signAutoWithGroup(UserSignRequest userSignRequest);

    JdResponse<PageDto<UserSignRecordData>> querySignListByOperateUser(UserSignQueryRequest query);
}
