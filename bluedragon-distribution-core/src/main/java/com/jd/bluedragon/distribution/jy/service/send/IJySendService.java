package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.send.JySendEntity;

/**
 * @ClassName IJySendService
 * @Description
 * @Author wyh
 * @Date 2022/6/4 17:25
 **/
public interface IJySendService {

    /**
     * 查询一条发货任务的异常记录（强发或拦截）
     * @param entity
     * @return
     */
    JySendEntity findSendRecordExistAbnormal(JySendEntity entity);
}
