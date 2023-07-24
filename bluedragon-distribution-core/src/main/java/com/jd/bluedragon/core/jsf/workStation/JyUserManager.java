package com.jd.bluedragon.core.jsf.workStation;


import java.util.List;

import com.jdl.basic.api.domain.user.JyUserDto;
import com.jdl.basic.common.utils.Result;

/**
 * 
 * @author wuyoude
 *
 */
public interface JyUserManager {

    /**
     * 根据场地和岗位查询用户列表
     * @param siteCode
     * @param organizationCode
     * @param userPositionCode
     * @param userPositionName
     * @return
     */
    Result<List<JyUserDto>> queryUserListBySiteAndPosition(Integer siteCode,String organizationCode,String userPositionCode,String userPositionName);

}
