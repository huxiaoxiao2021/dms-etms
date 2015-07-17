package com.jd.bluedragon.distribution.task.domain;

/**
 * Worker 执行结果
 * <pre>
 *     SUCCESS 执行成功的，不会再次执行
 *     REPEAT 暂时执行失败，需要再次执行
 *     FAILED 执行失败的，不需要再执行
 * </pre>
 * Created by dudong on 2014/12/11.
 */
public enum TaskResult {

    SUCCESS,REPEAT,FAILED;

    public static boolean toBoolean(TaskResult taskResult){
        switch (taskResult){
            case REPEAT:{
                return false;
            }
            case FAILED:{
                return false;
            }
            case SUCCESS:{
                return true;
            }
        }
        return false;
    }
}
