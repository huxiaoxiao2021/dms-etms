package com.jd.bluedragon.external.crossbow.itms.service;

import com.jd.bluedragon.external.crossbow.itms.domain.*;

/**
 * @ClassName TibetBizService
 * @Description 西藏项目服务层
 * @Author wyh
 * @Date 2021/6/4 14:31
 **/
public interface TibetBizService {

    /**
     * 西藏模式开关，始发场地和目的场地归属的省份配置了开关
     * @param createSiteCode
     * @param receiveSiteCode
     * @return
     */
    boolean tibetModeSwitch(Integer createSiteCode, Integer receiveSiteCode);

    /**
     * 推送发货数据
     * @param sendDetailDto
     * @return
     */
    ItmsResponse downSendDataToItms(ItmsSendDetailDto sendDetailDto);

    /**
     * 推送取消发货数据
     * @param cancelSendDto
     * @return
     */
    ItmsResponse downSendCancelDataToItms(ItmsCancelSendDto cancelSendDto);

    /**
     * 发货校验批次
     * @param checkSendCodeDto
     * @return
     */
    ItmsResponse sendCheckSendCode(ItmsSendCheckSendCodeDto checkSendCodeDto);

    /**
     * 取消发货校验批次
     * @param checkSendCodeDto
     * @return
     */
    ItmsResponse cancelSendCheckSendCode(ItmsCancelSendCheckSendCodeDto checkSendCodeDto);
}
