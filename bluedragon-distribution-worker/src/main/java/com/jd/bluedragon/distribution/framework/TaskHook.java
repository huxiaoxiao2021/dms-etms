package com.jd.bluedragon.distribution.framework;

/**
 * 任务挂勾
 * Created by wangtingwei on 2017/1/13.
 */
public interface TaskHook<T extends TaskExecuteContext>  {
    /**
     * 勾子处理
     * @param t
     */
    public int hook(T t);

    /**
     * 跳过当前勾子
     * @param t
     * @return
     */
    boolean escape(T t);
}
