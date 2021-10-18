package com.jd.bluedragon.distribution.delivery.callback;

import java.util.List;

/**
 * @ClassName IDeliveryProcessCallback
 * @Description
 * @Author wyh
 * @Date 2021/8/4 11:15
 **/
public interface IDeliveryProcessCallback<T> {

    /**
     * 发货分页任务执行完成，是否生成发货全程跟踪任务
     * @param callback
     */
    void callback(List<T> callback);
}
