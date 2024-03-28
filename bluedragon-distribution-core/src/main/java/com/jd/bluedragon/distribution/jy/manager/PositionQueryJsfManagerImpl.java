package com.jd.bluedragon.distribution.jy.manager;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.core.jsf.workStation.WorkGridManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.utils.NoticeUtils;
import com.jd.fastjson.JSON;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.api.domain.workStation.WorkGrid;
import com.jdl.basic.api.service.position.PositionQueryJsfService;
import com.jdl.basic.common.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PositionQueryJsfManagerImpl implements PositionQueryJsfManager {
    private static final Logger log = LoggerFactory.getLogger(PositionQueryJsfManagerImpl.class);

    @Autowired
    private PositionQueryJsfService positionQueryBasicJsfService;

    @Autowired
    private WorkGridManager workGridManager;
    private void logInfo(String message, Object... objects) {
        if (log.isInfoEnabled()) {
            log.info(message, objects);
        }
    }
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMSWEB.PositionQueryJsfManagerImpl.queryOneByPositionCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public Result<PositionDetailRecord> queryOneByPositionCode(String positionCode) {
        return positionQueryBasicJsfService.queryOneByPositionCode(positionCode);
    }



    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "PositionQueryJsfManagerImpl.pushInfoToPositionMainErp",
            jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.Heartbeat, JProEnum.FunctionError})
    public InvokeResult pushInfoToPositionMainErp(String operatorErp, String positionCode, String title, String content) {
        InvokeResult result = new InvokeResult();
        result.success();

        // 再根据网格码查询网格
        final com.jdl.basic.common.utils.Result<PositionDetailRecord> positionDetailRecordResult = this.queryOneByPositionCode(positionCode);
        if (positionDetailRecordResult == null) {
            log.error("根据岗位码{}查询网格信息为空", positionCode);
            result.error();
            return result;
        }
        if (!positionDetailRecordResult.isSuccess()) {
            log.error("根据岗位码{}查询网格信息失败,res={}", positionCode, JSON.toJSONString(positionDetailRecordResult));
            result.error(positionDetailRecordResult.getMessage());
            return result;
        }
        final PositionDetailRecord positionDetailRecord = positionDetailRecordResult.getData();
        if (positionDetailRecord == null) {
            log.error("根据岗位码{}查询网格信息data为空,res={}", positionCode, JSON.toJSONString(positionDetailRecordResult));
            result.error();
            return result;
        }

        // 再根据网格信息查询网格负责人
        final com.jdl.basic.common.utils.Result<WorkGrid> workGridResult = workGridManager.queryByWorkGridKey(positionDetailRecord.getRefWorkGridKey());
        if (workGridResult == null) {
            log.error("根据岗位码{}查询关联网格信息为空，refWorkGridKey={}", positionCode, positionDetailRecord.getRefWorkGridKey());
            result.error();
            return result;
        }
        if (!workGridResult.isSuccess()) {
            log.error("根据岗位码{}查询关联网格信息失败，refWorkGridKey={},res={}", positionCode, positionDetailRecord.getRefWorkGridKey(), JSON.toJSONString(positionDetailRecordResult));
            result.error();
            return result;
        }
        final WorkGrid workGrid = workGridResult.getData();
        if (workGrid == null) {
            log.error("根据岗位码{}查询关联网格信息data为空，refWorkGridKey={},res={}", positionCode, positionDetailRecord.getRefWorkGridKey(), JSON.toJSONString(positionDetailRecordResult));
            result.error();
            return result;
        }

        List<String> erpList = new ArrayList<>(Arrays.asList(workGrid.getOwnerUserErp()));

        logInfo("给网格码负责人推送京me提醒信息，当前人erp-{}，上级erp-{}，岗位码={}，标题={}，content-{}", operatorErp, erpList, positionCode, title, content);

        NoticeUtils.noticeToTimelineWithNoUrl(title, content, erpList);

        return result;
    }
}
