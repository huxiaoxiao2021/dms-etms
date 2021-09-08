package com.jd.bluedragon.distribution.loadAndUnload.service;


import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarDistribution;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCarTransBoard;

import java.util.List;

/**
 * 卸车板关系通用实现
 *
 * @author: hujiping
 * @date: 2020/6/23 20:05
 */
public interface UnloadCarTransBoardCommonService {

    /**
     * 新增
     * @param detail
     * @return*/
    public int add(UnloadCarTransBoard detail);

    /**
     * 更新包裹数量
     * @param detail
     * @return
     */
    public int updateCount(UnloadCarTransBoard detail);

    /**
     * 获取封车编码下板号
     * @param sealCarCode
     * @return
     */
    public List<String> searchBoardsBySealCode(String sealCarCode);

    /**
     * 根据封车编码查询
     * @param sealCarCode
     * @return
     */
    public UnloadCarTransBoard searchBySealCode(String sealCarCode);

    /**
     * 根据板号查询
     */
    public UnloadCarTransBoard searchByBoardCode(String boardCode);

    /**
     * 根据封车编码和板号查询
     */
    public UnloadCarTransBoard searchBySealCodeAndBoardCode(String sealCarCode, String boardCode);

}
