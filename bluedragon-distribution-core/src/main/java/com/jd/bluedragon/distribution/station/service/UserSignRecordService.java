package com.jd.bluedragon.distribution.station.service;

import java.util.List;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.station.domain.UserSignNoticeVo;
import com.jd.bluedragon.distribution.station.domain.UserSignRecord;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordReportSumVo;
import com.jd.bluedragon.distribution.station.domain.UserSignRecordReportVo;
import com.jd.bluedragon.distribution.station.query.UserSignRecordQuery;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * 人员签到表--Service接口
 * 
 * @author wuyoude
 * @date 2021年12月30日 14:30:43
 *
 */
public interface UserSignRecordService {

	/**
	 * 插入一条数据
	 * @param insertData
	 * @return
	 */
	Result<Boolean> insert(UserSignRecord insertData);
	/**
	 * 根据id更新数据
	 * @param updateData
	 * @return
	 */
	Result<Boolean> updateById(UserSignRecord updateData);
	/**
	 * 根据id删除数据
	 * @param deleteData
	 * @return
	 */
	Result<Boolean> deleteById(UserSignRecord deleteData);
	/**
	 * 根据id查询
	 * @param id
	 * @return
	 */
	Result<UserSignRecord> queryById(Long id);
	/**
	 * 按条件分页查询
	 * @param query
	 * @return
	 */
	Result<PageDto<UserSignRecord>> queryPageList(UserSignRecordQuery query);
	/**
	 * 按条件分页查询报表
	 * @param query
	 * @return
	 */
	Result<PageDto<UserSignRecordReportVo>> queryReportPageList(UserSignRecordQuery query);
	/**
	 * 签到
	 * @param signInRequest
	 * @return
	 */
	Result<Boolean> signIn(UserSignRecord signInRequest);
	/**
	 * 签退
	 * @param signOutRequest
	 * @return
	 */
	Result<Boolean> signOut(UserSignRecord signOutRequest);
	/**
	 * 查询最近的一次签到数据
	 * @param query
	 * @return
	 */
	Result<UserSignRecord> queryLastSignRecord(UserSignRecordQuery query);
	/**
	 * 按条件查询统计
	 * @param query
	 * @return
	 */
	Result<UserSignRecordReportSumVo> queryReportSum(UserSignRecordQuery query);

    /**
     * 自动处理签到数据签退
     * @return
     */
    Result<Integer> autoHandleSignInRecord();
	/**
	 * 按条件查询数量
	 * @param query
	 * @return
	 */
	Result<Long> queryCount(UserSignRecordQuery query);
	/**
	 * 按条件查询列表-导出
	 * @param query
	 * @return
	 */
	Result<List<UserSignRecord>> queryListForExport(UserSignRecordQuery query);
    /**
     * 根据条件查询-转成通知对象
     * @param query
     * @return
     */
	Result<UserSignNoticeVo> queryUserSignRecordToNoticeVo(UserSignRecordQuery query);
}
