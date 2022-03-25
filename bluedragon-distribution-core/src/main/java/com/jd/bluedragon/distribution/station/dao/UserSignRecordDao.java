package com.jd.bluedragon.distribution.station.dao;

import java.util.Date;
import java.util.List;

import com.jd.bluedragon.distribution.station.domain.UserSignNoticeJobItemVo;
import com.jd.bluedragon.distribution.station.domain.UserSignNoticeVo;
import com.jd.bluedragon.distribution.station.domain.UserSignNoticeWaveItemVo;
import com.jd.bluedragon.distribution.station.domain.UserSignRecord;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordReportSumVo;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordReportVo;
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;
import org.apache.ibatis.annotations.Param;

/**
 * 人员签到表--Dao接口
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
public interface UserSignRecordDao {

	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	int insert(UserSignRecord insertData);
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	int updateById(UserSignRecord updateData);
	/**
	 * 根据id删除数据
	 * @param id
	 * @return
	 */
	int deleteById(UserSignRecord deleteData);
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	UserSignRecord queryById(Long id);
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	List<UserSignRecord> queryList(UserSignRecordQuery query);
	/**
	 * 按条件分页查询-导出
	 * @param query
	 * @return
	 */
	List<UserSignRecord> queryListForExport(UserSignRecordQuery query);
	/**
	 * 按条件查询数量
	 * @param query
	 * @return
	 */
	long queryCount(UserSignRecordQuery query);
	/**
	 * 按条件分页查询报表
	 * @param query
	 * @return
	 */
	List<UserSignRecordReportVo> queryReportList(UserSignRecordQuery query);
	/**
	 * 按条件查询报表数量
	 * @param query
	 * @return
	 */
	long queryReportCount(UserSignRecordQuery query);
	/**
	 * 根据业务主键查询
	 * @param signInRequest
	 * @return
	 */
	UserSignRecord queryByBusinessKey(UserSignRecord signInRequest);
	/**
	 * 查询用户最近一次签到信息
	 * @param query
	 * @return
	 */
	UserSignRecord queryLastSignRecord(UserSignRecordQuery query);
	/**
	 * 按条件查询统计
	 * @param query
	 * @return
	 */
	UserSignRecordReportSumVo queryReportSum(UserSignRecordQuery query);
	/**
	 * 统计总数
	 * @param query
	 * @return
	 */
	UserSignNoticeVo queryUserSignNoticeVo(UserSignRecordQuery query);
	/**
	 * 班次维度统计
	 * @param query
	 * @return
	 */
	List<UserSignNoticeWaveItemVo> queryUserSignNoticeWaveItems(UserSignRecordQuery query);
	/**
	 * 工种维度统计
	 * @param query
	 * @return
	 */
	List<UserSignNoticeJobItemVo> queryUserSignNoticeJobItems(UserSignRecordQuery query);
	
    List<Long> querySignInMoreThanSpecifiedTime(Date signInTime, Integer limit);

    int signOutById(UserSignRecord signOutRequest, List<Long> list);

}
