package com.jd.bluedragon.distribution.crossbox.service;

import java.util.List;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.CrossBoxRequest;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBox;
import com.jd.bluedragon.distribution.crossbox.domain.CrossBoxResult;
import com.jd.bluedragon.distribution.task.domain.Task;

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

    public CrossBoxResult<CrossBox> getCrossDmsBoxByOriAndDes(Integer originalDmsId, Integer destinationDmsId);
}
