package com.jd.bluedragon.distribution.external.intensive.service;

import com.jd.bluedragon.distribution.jsf.domain.PositionData;
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
public interface ForeignUserSignService {
    
    /**
     * 查询岗位码信息
     * 
     * @param positionCode
     * @return
     */
    JdResponse<PositionData> queryPositionInfo(String positionCode);
    
    /**
     * 签到前校验
     * 
     * @param userSignRequest
     * @return
     */
    JdResponse<UserSignRecordData> checkBeforeSignIn(UserSignRequest userSignRequest);
    
    /**
     * 自动签到、签退-执行添加、移除组员操作
     * @param userSignRequest
     * @return
     */
    JdResponse<UserSignRecordData> signAutoWithGroup(UserSignRequest userSignRequest);

    /**
     * 手动签退
     * 
     * @param userSignRequest
     * @return
     */
    JdResponse<UserSignRecordData> signOutWithGroup(UserSignRequest userSignRequest);

    /**
     * 签到作废
     * 
     * @param userSignRequest
     * @return
     */
    JdResponse<UserSignRecordData> deleteUserSignRecord(UserSignRequest userSignRequest);

    /**
     * 分页查询签到数据
     * 
     * @param query
     * @return
     */
    JdResponse<PageDto<UserSignRecordData>> querySignListByOperateUser(UserSignQueryRequest query);
}
