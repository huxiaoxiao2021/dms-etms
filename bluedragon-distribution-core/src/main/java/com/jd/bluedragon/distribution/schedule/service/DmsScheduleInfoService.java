package com.jd.bluedragon.distribution.schedule.service;

import java.util.List;

import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfo;
import com.jd.bluedragon.distribution.schedule.entity.DmsScheduleInfoCondition;
import com.jd.bluedragon.distribution.schedule.vo.DmsEdnBatchVo;
import com.jd.bluedragon.distribution.schedule.vo.DmsEdnPickingVo;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.common.web.mvc.api.Service;

/**
 * @ClassName: DmsScheduleInfoService
 * @Description: 分拣调度信息表，数据来源于运单和企配仓--Service接口
 * @author wuyoude
 * @date 2020年04月30日 09:15:52
 *
 */
public interface DmsScheduleInfoService extends Service<DmsScheduleInfo> {
	/**
	 * 同步调度数据到数据库
	 * @param dmsScheduleInfo
	 * @return
	 */
	boolean syncScheduleInfoToDb(DmsScheduleInfo dmsScheduleInfo);
	/**
	 * 同步企配仓相关数据到db
	 * @param dmsScheduleInfo
	 * @return
	 */
	boolean syncEdnFahuoMsgToDb(DmsScheduleInfo dmsScheduleInfo);
	
	/**
	 * 分页查询数据,返回当前实体的分页对象
	 * @param dmsScheduleInfoCondition
	 * @return
	 */
	PagerResult<DmsEdnPickingVo> queryEdnPickingListByPagerCondition(DmsScheduleInfoCondition dmsScheduleInfoCondition);
	/**
	 * 查询调度单号对应的匹配仓批次列表
	 * @param scheduleBillCode
	 * @return
	 */
	DmsEdnPickingVo queryDmsEdnPickingVo(String scheduleBillCode);
	/**
	 * 查询调度单号对应的匹配仓批次列表
	 * @param scheduleBillCode
	 * @return
	 */
	List<String> queryEdnBatchNumList(String scheduleBillCode);
	/**
	 * 查询企配仓调度明细
	 * @param scheduleBillCode
	 * @return
	 */
	List<DmsScheduleInfo> queryEdnDmsScheduleInfoList(String scheduleBillCode);
	/**
	 * 获取导出对象
	 * @param dmsScheduleInfoCondition
	 * @return
	 */
	List<List<Object>> queryEdnPickingExcelData(DmsScheduleInfoCondition dmsScheduleInfoCondition);
	/**
	 * 查询并组装明细页面
	 * @param scheduleBillCode
	 * @return
	 */
	DmsEdnPickingVo queryDmsEdnPickingVoForView(String scheduleBillCode);
	/**
	 * 打印拣货单-调用云打印生成pdf，并返回pdf外链接
	 * @param scheduleBillCode
	 * @return
	 */
	JdResponse<String> printEdnPickingList(String scheduleBillCode);
	/**
	 * 打印交接单-调用金鹏接口获取拣货单pdf下载地址
	 * @param scheduleBillCode
	 * @return
	 */
	JdResponse<List<DmsEdnBatchVo>> printEdnDeliveryReceipt(String scheduleBillCode);
}
