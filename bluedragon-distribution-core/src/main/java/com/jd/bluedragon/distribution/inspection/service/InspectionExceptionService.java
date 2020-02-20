package com.jd.bluedragon.distribution.inspection.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.domain.InspectionEC;

/**
 * 验货差异查询及处理Service
 * @author wangzichen
 *
 */
public interface InspectionExceptionService {

	/**
	 * get inspection exception by comdition
	 * @param inspectionEC
	 * @return
	 */
	public List<InspectionEC> getListsByCondition(InspectionEC inspectionEC) throws Exception;

	/**
	 * dual with exception data in inspection
	 * @param operationType 
	 * @param inspectionEC
	 * @return
	 */
	public int exceptionCancel(List<InspectionEC> inspectionECs, Integer operationType);

	/**
	 * 多验直接配送
	 * @param inspectionECs
	 * @param operationType 
	 * @return
	 */
	public int directDistribution(List<InspectionEC> inspectionECs, Integer operationType);

	/**
	 *  异常查询，根据三方code和异常类型(1少验,2多验)
	 * @param inspectionEC
	 * @return
	 * @throws Exception 
	 */
	public List<InspectionEC> getByThird(InspectionEC inspectionEC) throws Exception;

	/**
	 * 检查三方是否完验
	 * @param createSiteCode
	 * @param receiveSiteCode
	 * @param boxCode
	 * @return
	 */
	public int queryExceptions(Integer createSiteCode, Integer receiveSiteCode,
			String boxCode);

	/**
	 * 检查包裹是否已经发车
	 * @param inspectionEC
	 * @return
	 */
	public boolean checkSendByInspectionEC(InspectionEC inspectionEC);
	
	/**
	 * 检查包裹是否已经异常处理过
	 * @param inspectionECs
	 * @param operationType
	 * @return List<InspectionEC> 
	 */
	public String  checkDispose(List<InspectionEC> inspectionECs,
			Integer operationType);

	/**
	 * 只验证send_d是否有发货数据
	 * @param bean
	 * @return
	 */
	public boolean checkSendDOnly(InspectionEC bean);
	
	/**
	 * 查询是否已经三方验货
	 * @param inspectionEC
	 * @return
	 */
	public Integer inspectionCount(  InspectionEC inspectionEC );

	/**
	 * 三方差异查询总条数
	 * @param paramMap
	 * @return
	 */
	public int totalThirdByParams(Map<String, Object> paramMap);

	/**
	 * 三方差异按照条件查询
	 * @param paramMap
	 * @return
	 */
	public List<InspectionEC> queryThirdByParams(Map<String, Object> paramMap);

	InspectionEC get(Long checkId);
	
	
	/**
	 * 写入业务表数据和日志数据
	 * @param  Inspection
	 * */
	public void saveData(Inspection inspection,String methodName);
}
