package com.jd.bluedragon.distribution.asynbuffer.service;
import IceInternal.Ex;
import com.jd.bluedragon.distribution.task.domain.Task;

/**
 * Created by xumei3 on 2017/4/17.
 */
public interface AsynBufferService {
        //分拣中心收货
        public boolean receiveTaskProcess(Task task)throws Exception;

        //分拣中心验货
        public boolean inspectionTaskProcess(Task task)throws Exception;

        //处理运单号关联包裹任务
        public boolean partnerWaybillTaskProcess(Task task)throws Exception;

        //封签[封箱]异常任务
        public boolean shieldsBoxErrorTaskProcess(Task task)throws Exception;

        //封签[封车]异常任务
        public boolean shieldsCarErrorTaskProcess(Task task)throws Exception;

        //运单号关联包裹回传
        public boolean partnerWaybillSynchroTaskProcess(Task task)throws Exception;

        //支线发车推送运单任务
        public boolean thirdDepartureTaskProcess(Task task)throws Exception;

        //封箱解封箱Redis任务
        public boolean sealBoxTaskProcess(Task task)throws Exception;

        //分拣退货Redis任务
        public boolean sortingReturnTaskProcess(Task task)throws Exception;

        //分拣
        public boolean sortingTaskProcess(Task task)throws Exception;

        /**
         * 分拣核心操作成功后的补充操作
         *
         * @param task
         * @return
         */
        boolean executeSortingSuccess(Task task);

        //统一处理task_send入口，根据keyword1对应具体的方法
        boolean taskSendProcess(Task task)throws Exception;

        //称重信息回传运单中心（将废弃
        @Deprecated
        boolean weightTaskProcess(Task task) throws Exception;

        //称重信息回传运单中心(整合
        boolean weightVolumeTaskProcess(Task task) throws Exception;

        //龙门架自动发货任务
        boolean scannerFrameDispatchProcess(Task task) throws Exception;

        //平台打印补验货数据
        boolean popPrintInspection(Task task) throws Exception;

    /**
     *  验货多包裹拆分任务
     * @param task
     * @return
     * @throws Exception
     */
    boolean inspectionSplitWaybillProcess(Task task) throws Exception;

    /**
     * 发货异步任务
     * @param task
     * @return
     * @throws Exception
     */
    boolean deliverySendProcess(final Task task) throws Exception;

    /**
     * 打印回调异步处理任务
     * @param task
     * @return
     * @throws Exception
     */
    boolean printCallbackProcess(Task task) throws Exception;

}
