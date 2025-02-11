package com.jd.bluedragon.distribution.station.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.jd.bluedragon.common.domain.JobCodeHoursDto;
import com.jd.bluedragon.distribution.station.domain.*;
import com.jd.bluedragon.common.dto.station.UserSignQueryRequest;
import com.jd.bluedragon.common.dto.station.UserSignRecordData;
import com.jd.bluedragon.distribution.station.query.UserSignRecordFlowQuery;
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;

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
	 * 查询用户最近一次签到信息
	 * @param query
	 * @return
	 */
	UserSignRecord queryLastSignRecord(UserSignRecordQuery query);
	/**
	 * 查询用户最近一次未签退的签到信息
	 * @param query
	 * @return
	 */
	UserSignRecord queryLastUnSignOutRecord(UserSignRecordQuery query);
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
	
    List<Long> querySignInMoreThanSpecifiedTime(List<Integer> allSpecialJobCodeList,List<JobCodeHoursDto> jobCodeHoursList, Date signInTimeStart, Date signInTime,List<Integer> siteCodeList, String env, Integer limit);
    
    int signOutById(UserSignRecord signOutRequest, List<Long> list);
    /**
     * 查询-数量
     * @param query
     * @return
     */
	Long queryCountWithPosition(UserSignQueryRequest query);
	/**
	 * 分页查询
	 * @param query
	 * @return
	 */
	List<UserSignRecordData> queryListWithPosition(UserSignQueryRequest query);
	/**
	 * 查询用户最近的一次签到记录，返回UserSignRecordData
	 * @param query
	 * @return
	 */
	UserSignRecordData queryLastUserSignRecordData(UserSignQueryRequest query);
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	UserSignRecordData queryUserSignRecordDataById(Long id);
	/**
	 * 根据id列表查询
	 * @param idList
	 * @return
	 */
	List<UserSignRecordData> queryUserSignRecordDataByIds(List<Long> idList);
	/**
	 * 按岗位查询已签未退的签到记录
	 * @param query
	 * @return
	 */
	List<UserSignRecord> queryUnSignOutListWithPosition(UserSignQueryRequest query);
	/**
	 * 查询操作人签到的数量
	 * @param query
	 * @return
	 */
	Long queryCountByOperateUser(UserSignQueryRequest query);
	/**
	 * 查询操作人签到的数据
	 * @param query
	 * @return
	 */
	List<UserSignRecordData> queryListByOperateUser(UserSignQueryRequest query);

	/**
	 * 根据网格业务主键查询签到记录
	 * @param query
	 * @return
	 */
	List<UserSignRecord> queryUnsignedOutRecordByRefGridKey(UserSignQueryRequest query);

	Long queryTotalUnsignedOutRecordByRefGridKey(String refGridKey);

	Integer queryCountForFlow(UserSignRecordQuery historyQuery);

	List<UserSignRecord> queryDataListForFlow(UserSignRecordQuery historyQuery);

	Integer queryCountForCheckSignTime(UserSignRecordFlowQuery checkQuery);
	/**
	 * 根据id查询，不过滤yn=1
	 * @param recordId
	 * @return
	 */
	UserSignRecord queryByIdForFlow(Long recordId);

    List<UserSignRecord> listSignRecordByTime(UserSignRecordQuery query);

	/**
	 * 按条件查询签到用户信息
	 * @param query
	 * @return
	 */
	List<BaseUserSignRecordVo> querySignInUserByCondition(UserSignRecordQuery query);


	/**
	 * 用business_key查询
	 * @return
	 */
	List<UserSignRecord> queryByBusinessKeyAndJobCode(UserSignRecordQuery query);

	/**
	 * 查询用户第一次签到记录，返回UserSignRecordData
	 * @param query
	 * @return
	 */
	UserSignRecordData queryFirstUserSignRecordData(UserSignQueryRequest query);


	UserSignRecord queryLastUnSignOutRecordIgnoreCase(List<String> list);

	UserSignRecord queryFirstExistGridRecord(UserSignRecordQuery query);

	public int signOutTimeById(UserSignRecord param, List<Long> list, List<JobCodeHoursDto> jobCodeHoursDtoList) ;
	
	
	List<BaseUserSignRecordVo> queryByGridSign(UserSignRecordQuery query);

	List<BaseUserSignRecordVo> queryMonitorRoomPerson(UserSignRecordQuery query);
	
}
