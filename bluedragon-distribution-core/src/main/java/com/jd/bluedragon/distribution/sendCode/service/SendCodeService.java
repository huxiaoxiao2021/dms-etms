package com.jd.bluedragon.distribution.sendCode.service;

import com.jd.bluedragon.distribution.sendCode.domain.SendCodeDto;

/**
 * <p>
 *     批次号业务单号的service服务接口
 *
 * @author wuzuxiang
 * @since 2020/2/24
 **/
public interface SendCodeService {

    /**
     * 创建批次号业务单号
     * @return
     */
    Boolean createSendCode(Integer createSiteCode, Integer receiveSiteCode, String createUser, Boolean isFresh, String fromSource);

    /**
     * 查询批次号的对象
     * @param code 批次号
     * @return
     */
    SendCodeDto queryByCode(String code);

    /**
     * 校验批次号是否具有生鲜属性
     * @param sendCode 批次号
     * @return
     */
    Boolean isFreshSendCode(String sendCode);
}
