package com.jd.bluedragon.distribution.box.service;

import com.jd.bluedragon.distribution.box.domain.Box;

import java.util.List;

/**
 * @ClassName: GroupBoxService
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2018/12/11 21:45
 */
public interface GroupBoxService {

    /**
     * 批量新增
     * @param groupList
     * @return
     */
    Integer batchAdd(List<Box> groupList);
    /**
     * 根据分组箱号获取此分组所有箱号
     * @param boxCode
     * @return
     */
    List<Box> getAllBoxByBoxCode(String boxCode);
}
