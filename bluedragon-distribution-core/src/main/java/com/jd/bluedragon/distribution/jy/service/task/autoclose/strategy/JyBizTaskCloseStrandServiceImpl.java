package com.jd.bluedragon.distribution.jy.service.task.autoclose.strategy;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyBizStrandTaskStatusEnum;
import com.jd.bluedragon.common.dto.operation.workbench.strand.JyStrandReportScanReq;
import com.jd.bluedragon.distribution.jy.dao.strand.JyBizTaskStrandReportDao;
import com.jd.bluedragon.distribution.jy.service.strand.JyBizTaskStrandReportDealService;
import com.jd.bluedragon.distribution.jy.service.strand.JyBizTaskStrandReportService;
import com.jd.bluedragon.distribution.jy.service.task.autoclose.dto.AutoCloseTaskPo;
import com.jd.bluedragon.distribution.jy.strand.JyBizTaskStrandReportEntity;
import com.jd.dms.java.utils.sdk.base.Result;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Objects;

/**
 * 拣运-滞留任务关闭实现
 *
 * @author hujiping
 * @date 2023/4/17 7:37 PM
 */
@Slf4j
@Service("jyBizTaskCloseStrandService")
public class JyBizTaskCloseStrandServiceImpl extends JyBizTaskCloseAbstractService {

    @Autowired
    private JyBizTaskStrandReportDealService jyBizTaskStrandReportDealService;
    
    @Autowired
    private JyBizTaskStrandReportService jyBizTaskStrandReportService;

    @Override
    @JProfiler(jKey = "DMS.WORKER.JyBizTaskCloseStrandServiceImpl.closeTask", jAppName = Constants.UMP_APP_NAME_DMSWORKER, 
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<Void> closeTask(AutoCloseTaskPo autoCloseTaskPo) {
        log.info("JyBizTaskCloseStrandServiceImpl.closeTask param {}", JSON.toJSONString(autoCloseTaskPo));
        Result<Void> result = Result.success();
        try {
            // 查询主任务
            String bizId = autoCloseTaskPo.getBizId();
            JyBizTaskStrandReportEntity taskEntity = jyBizTaskStrandReportService.queryOneByBiz(bizId);
            if(taskEntity == null){
                log.error("未查询到:{}的滞留任务!", bizId);
                return result;
            }
            if(Objects.equals(taskEntity.getTaskStatus(), JyBizStrandTaskStatusEnum.CANCEL.getCode())){
                log.warn("滞留任务:{}已取消!", bizId);
                return result;
            }
            if(Objects.equals(taskEntity.getTaskStatus(), JyBizStrandTaskStatusEnum.OVER_TIME.getCode()) 
                    || Objects.equals(taskEntity.getTaskStatus(), JyBizStrandTaskStatusEnum.COMPLETE.getCode())){
                log.warn("滞留任务:{}已完成!", bizId);
                return result;
            }
            // 提交任务
            JyStrandReportScanReq scanRequest = new JyStrandReportScanReq();
            scanRequest.setBizId(bizId);
            scanRequest.setOperateUserErp(Constants.SYS_CODE_DMS);
            jyBizTaskStrandReportDealService.strandReportSubmit(scanRequest);
            // 更新任务状态为'超时'
            JyBizTaskStrandReportEntity entity = new JyBizTaskStrandReportEntity();
            entity.setBizId(bizId);
            entity.setTaskStatus(JyBizStrandTaskStatusEnum.OVER_TIME.getCode());
            entity.setUpdateUserErp(Constants.DEFAULT_OWN_SIGN_VALUE);
            entity.setSubmitTime(new Date());
            entity.setSubmitUserErp(Constants.SYS_CODE_DMS);
            jyBizTaskStrandReportService.updateStatus(entity);
        } catch (Exception e) {
            log.error("JyBizTaskCloseStrandServiceImpl.closeTask exception {}", JSON.toJSONString(autoCloseTaskPo), e);
            result.toFail("系统异常");
        }
        return result;
    }
}
