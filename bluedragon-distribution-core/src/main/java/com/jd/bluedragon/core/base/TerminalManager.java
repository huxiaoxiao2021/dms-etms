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
     * 获取终端箱号中所有运单信息
     * @param boxCode
     * @return
     */
    com.jd.etms.erp.service.domain.BaseEntity<List<SendInfoDto>> getSendDetails(String boxCode);

}
