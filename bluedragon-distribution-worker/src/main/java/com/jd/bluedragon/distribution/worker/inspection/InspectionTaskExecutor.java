package com.jd.bluedragon.distribution.worker.inspection;

/**
 * @ClassName InspectionTaskExecutor
 * @Description 验货任务执行模式处理类
 * @Author wyh
 * @Date 2020/9/24 20:28
 **/
public interface InspectionTaskExecutor<E> {

    boolean process(E e);
}
