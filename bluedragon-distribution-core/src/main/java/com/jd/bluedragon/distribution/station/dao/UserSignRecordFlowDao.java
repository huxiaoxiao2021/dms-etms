package com.jd.bluedragon.distribution.station.dao;

import java.util.Date;
import java.util.List;

import com.jd.bluedragon.distribution.station.domain.UserSignNoticeJobItemVo;
import com.jd.bluedragon.distribution.station.domain.UserSignNoticeVo;
import com.jd.bluedragon.distribution.station.domain.UserSignNoticeWaveItemVo;
import com.jd.bluedragon.common.dto.station.UserSignQueryRequest;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.bluedragon.distribution.station.domain.UserSignRecord;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordFlow;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordReportSumVo;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordReportVo;
import com.jd.bluedragon.distribution.station.query.UserSignRecordFlowQuery;
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;

/**
 * 人员签到表--Dao接口
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
public interface UserSignRecordFlowDao {

	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	int insert(UserSignRecordFlow insertData);
	/**
	 * 更新流程状态数据
	 * @param updateData
	 * @return
	 */
	int updateFlowStatusById(UserSignRecordFlow updateData);	
	/**
	 * 根据flowBizCode查询
	 * @param flowBiz
	 * @return
	 */
	UserSignRecordFlow queryByFlowBizCode(String flowBizCode);
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	List<UserSignRecordFlow> queryDataList(UserSignRecordFlowQuery query);
	/**
	 * 按条件查询数量
	 * @param query
	 * @return
	 */
	Integer queryDataCount(UserSignRecordFlowQuery query);
	
	Integer queryCountForCheckUnCompletedFlow(UserSignRecordFlowQuery checkFlowQuery);
}
