package com.jd.bluedragon.distribution.position.service.impl;

import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.position.dao.PositionRecordDao;
import com.jd.bluedragon.distribution.position.domain.PositionDetailRecord;
import com.jd.bluedragon.distribution.position.domain.PositionRecord;
import com.jd.bluedragon.distribution.position.query.PositionQuery;
import com.jd.bluedragon.distribution.position.service.PositionRecordService;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.apache.commons.lang.StringUtils;
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

    @Autowired
    private IGenerateObjectId genObjectId;

    @Override
    public Result<Integer> insertPosition(PositionRecord record) {
        Result<Integer> result = new Result<Integer>();
        result.toSuccess();
        if(StringUtils.isEmpty(record.getPositionCode())){
            record.setPositionCode(generalPositionCode());
        }
        result.setData(positionRecordDao.insert(record));
        return result;
    }

    private String generalPositionCode() {
        return DmsConstants.CODE_PREFIX_POSITION.concat(StringHelper.padZero(this.genObjectId.getObjectId(PositionRecord.class.getName()),8));
    }

    @Override
    public Result<Integer> batchInsert(List<PositionRecord> list) {
        Result<Integer> result = new Result<Integer>();
        result.toSuccess();
        for (PositionRecord positionRecord : list) {
            if(StringUtils.isEmpty(positionRecord.getPositionCode())){
                positionRecord.setPositionCode(generalPositionCode());
            }
        }
        result.setData(positionRecordDao.batchInsert(list));
        return result;
    }

    @Override
    public Result<PageDto<PositionDetailRecord>> queryPageList(PositionQuery query) {
        Result<PageDto<PositionDetailRecord>> result = new Result<PageDto<PositionDetailRecord>>();
        result.toSuccess();
        if(query.getPageSize() == null || query.getPageSize() <= 0 || query.getPageNumber() <= 0) {
            result.toFail("分页参数错误!");
            return result;
        }
        query.setLimit(query.getPageSize());
        query.setOffset((query.getPageNumber() - 1) * query.getPageSize());
        PageDto<PositionDetailRecord> pageDto = new PageDto<PositionDetailRecord>(query.getPageNumber(), query.getPageSize());
        Long totalCount = positionRecordDao.queryCount(query);
        if(totalCount > 0){
            pageDto.setResult(positionRecordDao.queryList(query));
            pageDto.setTotalRow(Integer.parseInt(Long.toString(totalCount)));
        }else {
            pageDto.setResult(new ArrayList<PositionDetailRecord>());
            pageDto.setTotalRow(0);
        }
        result.setData(pageDto);
        return result;
    }

    @Override
    public Result<PositionDetailRecord> queryOneByPositionCode(String positionCode) {
        Result<PositionDetailRecord> result = new Result<PositionDetailRecord>();
        result.toSuccess();
        return result;
    }

    @Override
    public Result<Boolean> updateByPositionCode(PositionRecord positionRecord) {
        Result<Boolean> result = new Result<Boolean>();
        result.toSuccess();
        result.setData(positionRecordDao.updateByPositionCode(positionRecord) == 1);
        return result;
    }

    @Override
    public Result<Boolean> deleteByBusinessKey(PositionRecord positionRecord) {
        Result<Boolean> result = new Result<Boolean>();
        result.toSuccess();
        result.setData(positionRecordDao.deleteByBusinessKey(positionRecord) == 1);
        return result;
    }

    @Override
    public Result<Long> queryCountByCondition(PositionQuery query) {
        Result<Long> result = new Result<Long>();
        result.toSuccess();
        result.setData(positionRecordDao.queryCount(query));
        return result;
    }
}
