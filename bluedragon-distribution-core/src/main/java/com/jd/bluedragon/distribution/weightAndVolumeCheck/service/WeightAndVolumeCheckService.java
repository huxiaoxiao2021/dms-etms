package com.jd.bluedragon.distribution.weightAndVolumeCheck.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.weightAndVolumeCheck.WeightAndVolumeCheckCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.report.domain.WeightVolumeCollectDto;

import java.io.InputStream;
import java.util.List;

/**
 * @ClassName: WeightAndVolumeCheckService
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2019/4/22 17:56
 */
public interface WeightAndVolumeCheckService {


    /**
     * 上传超标图片
     * @param imageName
     * @param imageSize
     * @param inputStream
     * @return
     */
    void uploadExcessPicture(String imageName, long imageSize, InputStream inputStream) throws Exception;

    /**
     * 查看超标图片
     * @param packageCode
     * @param siteCode
     * @return
     */
    InvokeResult<String> searchExcessPicture(String packageCode,Integer siteCode);

    /**
     * 根据条件查询
     * @param condition
     * @return
     */
    PagerResult<WeightVolumeCollectDto> queryByCondition(WeightAndVolumeCheckCondition condition);

    /**
     * 导出
     * @param condition
     * @return
     */
    List<List<Object>> getExportData(WeightAndVolumeCheckCondition condition);

    /**
     * 发消息并更新
     * @param packageCode
     * @param siteCode
     * @param uploadTime
     */
    void sendMqAndUpdate(String packageCode, Integer siteCode, Long uploadTime);

    /**
     * 执行task任务
     * @param task
     * @return
     */
    boolean excuteWeightVolumeExcessTask(Task task);

    /**
     * 获取任务
     * @param type
     * @param ownSign
     * @return
     */
    List<Task> findLimitedTasks(Integer type, String ownSign);
}
