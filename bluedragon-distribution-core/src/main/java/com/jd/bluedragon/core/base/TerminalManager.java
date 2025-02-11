package com.jd.bluedragon.core.base;

import com.jd.etms.erp.service.dto.SendInfoDto;

import java.util.List;

/**
 * @ClassName: TerminalManager
 * @Description: 终端包装类
 * @author: hujiping
 * @date: 2019/4/2 15:15
 */
public interface TerminalManager {

    /**
     * 从终端获取发货明细
     *
     * @param boxCode
     * @return
     */
    List<SendInfoDto> getSendDetailsFromZD(String boxCode);

}
