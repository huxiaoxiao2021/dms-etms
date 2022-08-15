package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.ExpTaskDetailDto;
import com.jd.bluedragon.common.dto.jyexpection.response.StatisticsByGridDto;
import com.jd.bluedragon.common.dto.jyexpection.response.StatisticsByStatusDto;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskExceptionTagEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizTaskExceptionEntity;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import com.jdl.basic.api.service.position.PositionQueryJsfService;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.common.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class JyExceptionServiceImpl implements JyExceptionService {

    private Logger logger = LoggerFactory.getLogger(JyExceptionServiceImpl.class);

    @Autowired
    private JyBizTaskExceptionDao jyBizTaskExceptionDao;
    @Autowired
    private PositionQueryJsfService positionQueryJsfService;
    @Autowired
    private BasicPrimaryWS basicPrimaryWS;
    /**
     * 通用异常上报入口-扫描
     *
     * @param req
     */
    @Override
    public JdCResponse<Object> uploadScan(ExpUploadScanReq req) {
        return null;
    }

    /**
     * 组织entity
     * @param req
     * @return
     */
    private JyBizTaskExceptionEntity convertDto2Entity(ExpUploadScanReq req){
        JyBizTaskExceptionEntity entity = new JyBizTaskExceptionEntity();

        //entity.setType(req.getType().getCode());
        //entity.setBizId(req.getBizId());
        //entity.setType(req.getType().getCode());
        //entity.setBarCode(req.getBarCode());
        //entity.setTags(JyBizTaskExceptionTagEnum.valueOf(req.getTags()));
        //entity.setSource(req.getSource().getCode());

        //岗位码相关
        Result<PositionDetailRecord> positionDetailRecordResult = positionQueryJsfService.queryOneByPositionCode(req.getPositionCode());
        PositionDetailRecord data = positionDetailRecordResult.getData();
        if (data == null){
            logger.error("createScheduleTask req:{}",req.getPositionCode());
            throw new RuntimeException("无效的网格码");
        }
        entity.setFloor(data.getFloor());
        entity.setAreaCode(data.getAreaCode());
        entity.setAreaName(data.getAreaName());
        entity.setGridCode(data.getGridCode());
        entity.setGridNo(data.getGridNo());

        BaseStaffSiteOrgDto baseStaffByErp = basicPrimaryWS.getBaseStaffByErp(req.getUserErp());
        entity.setCreateUserErp(req.getUserErp());
        entity.setCreateUserName(baseStaffByErp.getStaffName());
        Date now = new Date();
        entity.setCreateTime(now);
        entity.setUpdateUserErp(req.getUserErp());
        entity.setUpdateUserName(baseStaffByErp.getStaffName());
        entity.setUpdateTime(now);
        return entity;
    }

    /**
     * 按取件状态统计
     *
     * @param req
     */
    @Override
    public JdCResponse<List<StatisticsByStatusDto>> statisticsByStatus(ExpBaseReq req) {
        JdCResponse<List<StatisticsByStatusDto>> result = new JdCResponse<>();
        //岗位码相关
        Result<PositionDetailRecord> positionDetailRecordResult = positionQueryJsfService.queryOneByPositionCode(req.getPositionCode());
        PositionDetailRecord data = positionDetailRecordResult.getData();
        if (data == null){
            logger.error("createScheduleTask req:{}",req.getPositionCode());
            result.toFail("无效的网格码");
        }
        String gridRid = data.getSiteCode()+"-"+data.getFloor()+"-"+data.getGridCode();
        List<StatisticsByStatusDto> statisticStatusResps = jyBizTaskExceptionDao.getStatusStatistic(gridRid);
        result.setData(statisticStatusResps);
        result.toSucceed();
        return result;
    }

    /**
     * 网格待取件列表统计接口
     *
     * @param req
     */
    @Override
    public JdCResponse<List<StatisticsByGridDto>> getGridStatisticsPageList(StatisticsByGridReq req) {
        return null;
    }

    /**
     * 取件进行中数据统计
     *
     * @param req
     */
    @Override
    public JdCResponse<List<StatisticsByGridDto>> getReceivingCount(StatisticsByGridReq req) {
        return null;
    }

    /**
     * 任务列表接口
     *
     * @param req
     */
    @Override
    public JdCResponse<List<StatisticsByGridDto>> getExceptionTaskPageList(ExpTaskPageReq req) {
        return null;
    }

    /**
     * 任务领取接口
     *
     * @param req
     */
    @Override
    public JdCResponse<Object> receive(ExpReceiveReq req) {
        return null;
    }

    /**
     * 任务明细
     *
     * @param req
     */
    @Override
    public JdCResponse<ExpTaskDetailDto> getTaskDetail(ExpTaskByIdReq req) {
        return null;
    }

    /**
     * 处理任务接口
     *
     * @param req
     */
    @Override
    public JdCResponse<Object> processTask(ExpTaskDetailReq req) {
        return null;
    }
}
