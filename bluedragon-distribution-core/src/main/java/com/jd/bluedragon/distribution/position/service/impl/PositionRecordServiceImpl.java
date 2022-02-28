package com.jd.bluedragon.distribution.position.service.impl;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.position.dao.PositionRecordDao;
import com.jd.bluedragon.distribution.position.domain.PositionDetailRecord;
import com.jd.bluedragon.distribution.position.domain.PositionRecord;
import com.jd.bluedragon.distribution.position.query.PositionQuery;
import com.jd.bluedragon.distribution.position.service.PositionRecordService;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 岗位查询服务实现
 *
 * @author hujiping
 * @date 2022/2/25 5:47 PM
 */
@Service
public class PositionRecordServiceImpl implements PositionRecordService {

    @Autowired
    private PositionRecordDao positionRecordDao;

    @Override
    public Result<Integer> insertPosition(PositionRecord record) {
        Result<Integer> result = new Result<Integer>();
        result.toSuccess();
        result.setData(positionRecordDao.insert(record));
        return result;
    }

    @Override
    public Result<Integer> batchInsert(List<PositionRecord> list) {
        Result<Integer> result = new Result<Integer>();
        result.toSuccess();
        result.setData(positionRecordDao.batchInsert(list));
        return result;
    }

    @Override
    public Result<PageDto<PositionDetailRecord>> queryPageList(PositionQuery query) {
        Result<PageDto<PositionDetailRecord>> result = new Result<PageDto<PositionDetailRecord>>();
        result.toSuccess();
        PageDto<PositionDetailRecord> pageDto = new PageDto<PositionDetailRecord>(query.getPageNumber(), query.getPageSize());
        Long totalCount = positionRecordDao.queryCount(query);
        if(totalCount != null && totalCount > 0){
            pageDto.setResult(positionRecordDao.queryList(query));
            pageDto.setTotalRow(totalCount.intValue());
        }else {
            pageDto.setResult(new ArrayList<PositionDetailRecord>());
            pageDto.setTotalRow(0);
        }
        return result;
    }

    @Override
    public Result<Boolean> updateByPositionCode(String positionCode) {
        Result<Boolean> result = new Result<Boolean>();
        result.toSuccess();
        result.setData(positionRecordDao.updateByPositionCode(positionCode) == 1);
        return result;
    }

    @Override
    public Result<Boolean> deleteByBusinessKey(String businessKey) {
        Result<Boolean> result = new Result<Boolean>();
        result.toSuccess();
        result.setData(positionRecordDao.deleteByBusinessKey(businessKey) == 1);
        return result;
    }
}
