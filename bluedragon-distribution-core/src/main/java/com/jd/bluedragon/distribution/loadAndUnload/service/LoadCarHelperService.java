package com.jd.bluedragon.distribution.loadAndUnload.service;

import com.jd.bluedragon.distribution.loadAndUnload.LoadCarHelper;

import java.util.List;

/**
 * @program: bluedragon-distribution
 * @description:
 * @author: wuming
 * @create: 2020-10-19 20:22
 */
public interface LoadCarHelperService {

    /**
     * 批量插入
     *
     * @param dataList
     * @return
     */
    int batchInsert(List<LoadCarHelper> dataList);

    /**
     * 根据登录人信息查询任务
     *
     * @param loginUserErp
     * @return
     */
    List<Long> selectByCreateUserErp(String loginUserErp);

    List<Long> selectByHelperErp(String loginUserErp);

    List<Long> selectIdsByErp(String loginUserErp);
    /**
     * 删除任务同步更新协助人表
     *
     * @param taskId
     * @return
     */
    int deleteById(Long taskId);


}
