package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Author zhengchengfa
 * @Date 2023/8/3 11:07
 * @Description
 */
@Service
public class BaseParamValidateService {

    //PDA分页信息pageSize不建议设置太大
    public static final Integer DEFAULT_PDA_PAGE_MAXSIZE = 100;


    public void checkUser(User user) {
        if(Objects.isNull(user) || StringUtils.isBlank(user.getUserErp())) {
            throw new JyBizException("操作人erp为空");
        }

    }
    public void checkCurrentOperate(CurrentOperate currentOperate) {
        if(Objects.isNull(currentOperate) || Objects.isNull(currentOperate.getSiteCode())) {
            throw new JyBizException("操作场地编码为空");
        }
        if(Objects.isNull(currentOperate.getOperateTime())) {
            throw new JyBizException("操作时间为空");
        }
    }

    public void checkGroupCode(String groupCode) {
        if(StringUtils.isBlank(groupCode)) {
            throw new JyBizException("岗位组为空");
        }
    }

    public void checkPositionCode(String positionCode) {
        if(StringUtils.isBlank(positionCode)) {
            throw new JyBizException("岗位码为空");
        }
    }

    /**
     * 岗位类型
     * JyFuncCodeEnum
     */
    public void checkPost(String post) {
        if(StringUtils.isBlank(post)) {
            throw new JyBizException("岗位类型为空");
        }

        if(Objects.isNull(JyFuncCodeEnum.getJyPostEnumByCode(post))) {
            throw new JyBizException("不存在当前岗位类型");
        }
    }


    public void checkPdaPage(Integer pageNo, Integer pageSize) {
        if(Objects.isNull(pageNo) || Objects.isNull(pageSize) || pageNo < 1 || pageSize < 1) {
            throw new JyBizException("页码参数错误");
        }
        if(pageSize > DEFAULT_PDA_PAGE_MAXSIZE) {//PDA分页信息pageSize不建议设置太大
            throw new JyBizException("每页查询数量超过最大值" + DEFAULT_PDA_PAGE_MAXSIZE);
        }
    }


    public void checkUserAndSite(User user, CurrentOperate currentOperate) {
        checkUser(user);
        checkCurrentOperate(currentOperate);
    }

    public void checkUserAndSiteAndGroup(User user, CurrentOperate currentOperate, String groupCode) {
        checkUser(user);
        checkCurrentOperate(currentOperate);
        checkGroupCode(groupCode);
    }

    public void checkUserAndSiteAndGroupAndPost(User user, CurrentOperate currentOperate, String groupCode, String post) {
        checkUser(user);
        checkCurrentOperate(currentOperate);
        checkGroupCode(groupCode);
        checkPost(post);
    }

    public void checkUserAndSiteAndGroupAndPositionCode(User user, CurrentOperate currentOperate, String groupCode, String positionCode) {
        checkUser(user);
        checkCurrentOperate(currentOperate);
        checkGroupCode(groupCode);
        checkPositionCode(positionCode);
    }




}
