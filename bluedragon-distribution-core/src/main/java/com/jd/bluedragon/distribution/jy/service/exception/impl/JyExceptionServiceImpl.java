package com.jd.bluedragon.distribution.jy.service.exception.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.*;
import com.jd.bluedragon.common.dto.jyexpection.response.*;
import com.jd.bluedragon.distribution.jy.dao.exception.JyBizTaskExceptionDao;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskExceptionSourceEnum;
import com.jd.bluedragon.distribution.jy.exception.*;
import com.jd.bluedragon.distribution.jy.service.exception.JyExceptionService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.jim.cli.Cluster;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import com.jdl.basic.api.service.position.PositionQueryJsfService;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import com.jdl.basic.common.utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class JyExceptionServiceImpl implements JyExceptionService {

    private Logger logger = LoggerFactory.getLogger(JyExceptionServiceImpl.class);

    @Autowired
    private JyBizTaskExceptionDao jyBizTaskExceptionDao;
    @Autowired
    private PositionQueryJsfService positionQueryJsfService;
    @Autowired
    private BasicPrimaryWS basicPrimaryWS;
    @Autowired
    private WaybillCacheService waybillCacheService;
    @Autowired
    private SendDetailService sendDetailService;
    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClientCache;
    /**
     * 通用异常上报入口-扫描
     *
     * @param req
     */
    @Override
    public JdCResponse<Object> uploadScan(ExpUploadScanReq req) {


//        9.	卸车入口：根据操作异常上报人员此前扫描验货的3个包裹号获取到对应上游发货批次号，后续作为批次号信息辅助录入
//        10.	通用扫描入口（右上角点点点）：上报时不记录任何信息
//        11.	发货入口：操作异常上报人员此前扫描发货的3个包裹对应的发货目的地id，后续作为下级地信息辅助录入
        if (req.getRecentPackageCodeList() != null && !req.getRecentPackageCodeList().isEmpty()) {
            // 发货
            if (Objects.equals(req.getSource(), JyBizTaskExceptionSourceEnum.SEND.getCode())) {
                queryRecentSendInfo(req);
            }
            // 卸车
            if (Objects.equals(req.getSource(), JyBizTaskExceptionSourceEnum.UN_LOAD.getCode())) {
                queryRecentSendInfo(req);
            }
        }


        return null;
    }

    private void queryRecentSendInfo(ExpUploadScanReq req) {
        for (String barcode : req.getRecentPackageCodeList()) {
            if (!WaybillUtil.isPackageCode(barcode)) {
                continue;
            }
            SendDetail param = new SendDetail();
            param.setPackageBarcode(barcode);
            param.setCreateSiteCode(req.getSiteId());
            //未取消 & 已发货
            param.setIsCancel(0);
            param.setStatus(1);
            SendDetail sendDetail = sendDetailService.queryOneSendDatailBySendM(param);
            if (sendDetail == null) {
                continue;
            }
        }
    }

    private void queryRecentInspectInfo(ExpUploadScanReq req) {

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
    public JdCResponse<List<ProcessingNumByGridDto>> getReceivingCount(StatisticsByGridReq req) {
        return null;
    }

    /**
     * 任务列表接口
     *
     * @param req
     */
    @Override
    public JdCResponse<List<ExpTaskDto>> getExceptionTaskPageList(ExpTaskPageReq req) {
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

}
