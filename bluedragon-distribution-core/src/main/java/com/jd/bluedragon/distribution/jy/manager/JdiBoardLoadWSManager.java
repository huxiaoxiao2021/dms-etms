package com.jd.bluedragon.distribution.jy.manager;

import com.jd.tms.jdi.dto.BoardLoadDto;

import java.util.List;

/**
 * @author liwenji
 * @description 运输组板相关接口
 * @date 2023-09-27 14:13
 */
public interface JdiBoardLoadWSManager {

    /**
     * 查询整板装车数据
     * @param boardLoadDto
     * @return
     */
    List<BoardLoadDto> queryBoardLoad(BoardLoadDto boardLoadDto);
}
