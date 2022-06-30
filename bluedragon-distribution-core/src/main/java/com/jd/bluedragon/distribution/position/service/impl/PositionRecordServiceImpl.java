package com.jd.bluedragon.distribution.position.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.station.PositionData;
import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.config.JyWorkMapFuncConfigEntity;
import com.jd.bluedragon.distribution.jy.dao.config.JyWorkMapFuncConfigDao;
import com.jd.bluedragon.distribution.position.dao.PositionRecordDao;
import com.jd.bluedragon.distribution.position.domain.PositionDetailRecord;
import com.jd.bluedragon.distribution.position.domain.PositionRecord;
import com.jd.bluedragon.distribution.position.query.PositionQuery;
import com.jd.bluedragon.distribution.position.service.PositionRecordService;
import com.jd.bluedragon.distribution.station.dao.WorkStationDao;
import com.jd.bluedragon.distribution.station.dao.WorkStationGridDao;
import com.jd.bluedragon.distribution.station.domain.WorkStation;
import com.jd.bluedragon.distribution.station.domain.WorkStationGrid;
import com.jd.bluedragon.distribution.station.query.WorkStationGridQuery;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.apache.commons.collections.CollectionUtils;
import com.jd.ql.erp.util.BeanUtils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(PositionRecordServiceImpl.class);

    @Autowired
    private PositionRecordDao positionRecordDao;

    @Autowired
    private IGenerateObjectId genObjectId;

    @Autowired
    private WorkStationGridDao workStationGridDao;

    @Autowired
    private WorkStationDao workStationDao;

    @Autowired
    private JyWorkMapFuncConfigDao jyWorkMapFuncConfigDao;

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
        result.setData(positionRecordDao.queryDetailByPositionCode(positionCode));
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

    @Override
    public void syncAllData() {
        long startTime = System.currentTimeMillis();
        WorkStationGridQuery query = new WorkStationGridQuery();
        int totalCount = 0;
        int limit = 1000;
        int offset = 0;
        int count = 0;
        while (count < 200){
            query.setOffset(offset);
            query.setLimit(limit);
            List<WorkStationGrid> list = workStationGridDao.queryAllByPage(query);
            if(CollectionUtils.isEmpty(list)){
                break;
            }
            for (WorkStationGrid workStationGrid : list) {
                PositionRecord record = new PositionRecord();
                record.setSiteCode(workStationGrid.getSiteCode());
                record.setRefGridKey(workStationGrid.getBusinessKey());
                record.setPositionCode(generalPositionCode());
                record.setCreateUser(workStationGrid.getCreateUser());
                record.setUpdateUser(workStationGrid.getCreateUser());
                if(positionRecordDao.queryByBusinessKey(workStationGrid.getBusinessKey()) == null){
                    insertPosition(record);
                    totalCount ++;
                }
            }
            offset += limit;
            count ++;
        }
        logger.info("同步历史数据完成，共耗时：{}共同步:{}条记录", System.currentTimeMillis() - startTime, totalCount);
    }

	@Override
	public JdCResponse<PositionData> queryPositionWithIsMatchAppFunc(String positionCode) {
		JdCResponse<PositionData> result = queryPositionInfo(positionCode);
		if(!result.isSucceed()){
            return result;
        }
        setDefaultMenuCode(positionCode, result);
		return result;
	}

    @Override
    public JdCResponse<PositionData> queryPositionInfo(String positionCode) {
        JdCResponse<PositionData> result = new JdCResponse<PositionData>();
        result.toSucceed();
        Result<PositionDetailRecord> positionDetailResult = this.queryOneByPositionCode(positionCode);
        if(positionDetailResult == null
                || positionDetailResult.getData() == null) {
            result.toFail("无效的上岗码！");
            return result;
        }
        PositionData positionData = new PositionData();
        BeanUtils.copyProperties(positionDetailResult.getData(), positionData);
        result.setData(positionData);
        return result;
    }

    private void setDefaultMenuCode(String positionCode, JdCResponse<PositionData> result) {
        PositionData positionData = result.getData();
        WorkStation workStation = new WorkStation();
        workStation.setAreaCode(positionData.getAreaCode());
        workStation.setWorkCode(positionData.getWorkCode());
        WorkStation queryWork = workStationDao.queryByBusinessKey(workStation);
        if(queryWork == null || StringUtils.isEmpty(queryWork.getBusinessKey())){
            result.toFail(String.format("岗位码:%s对应的工序不存在，请联系分拣小秘!", positionCode));
            return;
        }
        JyWorkMapFuncConfigEntity entity = jyWorkMapFuncConfigDao.queryByBusinessKey(queryWork.getBusinessKey());
        if(entity == null || StringUtils.isEmpty(entity.getFuncCode())){
            result.toFail(String.format("岗位码:%s对应的功能编码未配置，请联系分拣小秘!", positionCode));
            return;
        }
        positionData.setDefaultMenuCode(entity.getFuncCode());
    }
}
