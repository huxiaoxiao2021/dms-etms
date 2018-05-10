package com.jd.bluedragon.distribution.crossbox.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.CrossBoxRequest;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBox;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBoxResult;
import com.jd.bluedragon.distribution.task.domain.Task;

import java.util.Date;
import java.util.List;

public interface CrossBoxService {
    int addCrossBox(CrossBox crossDmsBox);

    String checkLineExist(CrossBox crossDmsBox);

    List<CrossBox> queryByCondition(CrossBoxRequest crossDmsBoxDto, Pager<List<CrossBox>> page);

    CrossBox getCrossBoxById(Integer id);

    int updateCrossBoxByDms(CrossBox crossDmsBox);

    int deleteById(CrossBox crossDmsBox);

    String handleCrossBoxData(List<CrossBox> data, String userName);

    List<List<Object>> getExportDataByCrossBox(CrossBoxRequest paramDto);

    int activeCrossBox(Task task);

    public CrossBoxResult<String> getCrossDmsBox(Integer originalDmsId, Integer destinationDmsId);

    /**
     * 调路由系统查询路由信息
     * @param originalDmsId
     * @param destinationDmsId
     * @param predictSendTime
     * @param transportType
     * @return
     */
    public CrossBoxResult<String[]> getBoxRouter(Integer originalDmsId, Integer destinationDmsId, Date predictSendTime, Integer transportType);

    /**
     * 根据箱号 查询箱子信息 和路由信息
     * @param boxCode
     * @return
     */
    public BoxResponse getCrossDmsBoxByBoxCode(String boxCode);

    public CrossBoxResult<CrossBox> getCrossDmsBoxByOriAndDes(Integer originalDmsId, Integer destinationDmsId);
}
