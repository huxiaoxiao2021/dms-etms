package com.jd.bluedragon.distribution.record.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.record.dao.WaybillHasnoPresiteRecordDao;
import com.jd.bluedragon.distribution.record.dto.WaybillHasnoPresiteRecordQo;
import com.jd.bluedragon.distribution.record.enums.WaybillHasnoPresiteRecordCallStatusEnum;
import com.jd.bluedragon.distribution.record.enums.WaybillHasnoPresiteRecordStatusEnum;
import com.jd.bluedragon.distribution.record.model.WaybillHasnoPresiteRecord;
import com.jd.bluedragon.distribution.record.service.WaybillHasnoPresiteRecordService;
import com.jd.bluedragon.distribution.record.vo.WaybillHasnoPresiteRecordVo;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * 无滑道查询
 *
 * @copyright jd.com 京东物流JDL
 */
@Service("waybillHasnoPresiteRecordService")
public class WaybillHasnoPresiteRecordServiceImpl implements WaybillHasnoPresiteRecordService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillHasnoPresiteRecordDao waybillHasnoPresiteRecordDao;

    /**
     * 获取总数
     * @param query 请求参数
     * @return Result
     */
    @Override
    public Response<Long> selectCount(WaybillHasnoPresiteRecordQo query) {
        log.info("WaybillHasnoPresiteRecordServiceImpl.selectCount param {}", JsonHelper.toJson(query));
        Response<Long> result = new Response<>();
        result.toSucceed();
        try {
            Response<Void> checkAndSetResult = this.checkAndSetPram4SelectParam(query);
            if(!checkAndSetResult.isSucceed()){
                result.toError(checkAndSetResult.getMessage());
                return result;
            }
            long total = waybillHasnoPresiteRecordDao.selectCount(query);
            result.setData(total);
        } catch (Exception e) {
            log.error("WaybillHasnoPresiteRecordServiceImpl.selectCount exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        return result;
    }

    /**
     * 获取列表
     * @param query 请求参数
     * @return Result
     */
    @Override
    public Response<List<WaybillHasnoPresiteRecord>> selectList(WaybillHasnoPresiteRecordQo query) {
        log.info("WaybillHasnoPresiteRecordServiceImpl.selectList param {}", JsonHelper.toJson(query));
        Response<List<WaybillHasnoPresiteRecord>> result = new Response<>();
        result.toSucceed();
        try {
            Response<Void> checkAndSetResult = this.checkAndSetPram4SelectParam(query);
            if(!checkAndSetResult.isSucceed()){
                result.toError(checkAndSetResult.getMessage());
                return result;
            }
            List<WaybillHasnoPresiteRecord> dataList = waybillHasnoPresiteRecordDao.selectList(query);
            result.setData(dataList);
        } catch (Exception e) {
            log.error("WaybillHasnoPresiteRecordServiceImpl.selectList exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        return result;
    }

    private Response<Void> checkAndSetPram4SelectParam(WaybillHasnoPresiteRecordQo query){
        Response<Void> result = new Response<>();
        result.toSucceed();
        Integer hourRange = query.getHourRange();
        if(hourRange == null || hourRange <= 0) {
        	hourRange = DateHelper.ONE_DAY_HOURS;
        }
        Date endTime = new Date();
        Date startTime = DateHelper.newTimeRangeHoursAgo(endTime, hourRange);
        query.setStartTimeTs(startTime);
        query.setEndTimeTs(endTime);
        return result;
    }

    /**
     * 获取分页列表
     * @param query 请求参数
     * @return Result
     */
    @Override
    public Response<PageDto<WaybillHasnoPresiteRecordVo>> selectPageList(WaybillHasnoPresiteRecordQo query) {
        log.info("WaybillHasnoPresiteRecordServiceImpl.selectPageList param {}", JsonHelper.toJson(query));
        Response<PageDto<WaybillHasnoPresiteRecordVo>> result = new Response<>();
        result.toSucceed();
        PageDto<WaybillHasnoPresiteRecordVo> pageDto = new PageDto<>(query.getPageNumber(), query.getPageSize());
        List<WaybillHasnoPresiteRecordVo> dataList = new ArrayList<>();
        try {
            Response<Void> checkResult = this.checkPram4SelectPageList(query);
            if(!checkResult.isSucceed()){
                result.toError(checkResult.getMessage());
                return result;
            }
            Response<Void> checkAndSetResult = this.checkAndSetPram4SelectParam(query);
            if(!checkAndSetResult.isSucceed()){
                result.toError(checkAndSetResult.getMessage());
                return result;
            }
            long total = waybillHasnoPresiteRecordDao.selectCount(query);
            pageDto.setTotalRow(new Long(total).intValue());
            if (total > 0) {
                List<WaybillHasnoPresiteRecord> rawDataList = waybillHasnoPresiteRecordDao.selectList(query);
                for (WaybillHasnoPresiteRecord waybillHasnoPresiteRecord : rawDataList) {
                    WaybillHasnoPresiteRecordVo vo = new WaybillHasnoPresiteRecordVo();
                    BeanUtils.copyProperties(waybillHasnoPresiteRecord, vo);
                    vo.setStatusDesc(WaybillHasnoPresiteRecordStatusEnum.getNameByCode(vo.getStatus()));
                    vo.setCallStatusDesc(WaybillHasnoPresiteRecordCallStatusEnum.getNameByCode(vo.getStatus()));
                    dataList.add(vo);
                }
            }
            pageDto.setResult(dataList);
        } catch (Exception e) {
            log.error("WaybillHasnoPresiteRecordServiceImpl.selectPageList exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        result.setData(pageDto);
        return result;
    }

    private Response<Void> checkPram4SelectPageList(WaybillHasnoPresiteRecordQo query){
        Response<Void> result = new Response<>();
        result.toSucceed();
        if(query.getPageNumber() <= 0){
            result.toError("参数错误，pageNumber必须大于0");
            return result;
        }
        if(query.getPageSize() == null){
            result.toError("参数错误，pageSize不能为空");
            return result;
        }
        return result;

    }
}
