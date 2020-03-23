package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.coldchain.dto.ColdChainQueryUnloadTaskRequest;
import com.jd.bluedragon.distribution.coldchain.dto.ColdChainUnloadDto;
import com.jd.ccmp.ctm.dto.QueryUnloadDto;

import java.util.List;

/**
 * 冷链货物操作
 *
 */
public interface ColdChainOptimizeManager {

    /**
     * 上传卸货数据
     *
     * @param unloadDto
     * @return
     */
    boolean receiveUnloadData(ColdChainUnloadDto unloadDto);

    /**
     * 查询卸货任务
     *
     * @param request
     * @return
     */
    List<QueryUnloadDto> queryUnloadTask(ColdChainQueryUnloadTaskRequest request);

    /**
     * 卸货完成
     *
     * @param taskNo
     * @param operateErp
     * @return
     */
    boolean unloadTaskComplete(String taskNo, String operateErp);

}
