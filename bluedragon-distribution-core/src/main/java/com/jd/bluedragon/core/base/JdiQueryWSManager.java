package com.jd.bluedragon.core.base;


import com.jd.tms.jdi.dto.CommonDto;
import com.jd.tms.jdi.dto.TransWorkItemDto;

import java.util.List;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/10/8 1:55 下午
 */
public interface JdiQueryWSManager {

    /**
     * 根据派车任务明细简码获取派车任务明细
     *
     * @param simpleCode
     * @return
     */
    CommonDto<TransWorkItemDto> queryTransWorkItemBySimpleCode(String simpleCode);

}
