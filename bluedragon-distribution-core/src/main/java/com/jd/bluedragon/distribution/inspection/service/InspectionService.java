package com.jd.bluedragon.distribution.inspection.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.domain.InspectionResult;
import com.jd.bluedragon.distribution.task.domain.Task;


/**
 * for inspection
 * @author wangzichen
 *
 */
public interface InspectionService {

	
	/**
	 * 用于自营和退货
	 * 
	 * @param inspections 
	 * @return
	 * @throws Exception 
	 * 
	 */
	public void inspectionCore( List<Inspection> inspections ) throws Exception;

	
	/**
	 * 根据运单号或包裹号来返回Inspection集合
	 * @param requestBean
	 * @return 
	 */
	public List<Inspection> prepareInspection(InspectionRequest requestBean);

	public List<Inspection> parseInspections(Task task);
	
	/**
	 * 验货数量
	 * @param inspection
	 * @return
	 */
	public Integer inspectionCount(Inspection inspection);
	
	/**
	 * 增加POP收货数据
	 * @param pop
	 * @return
	 */
	public int addInspectionPop(Inspection pop);


	/**
	 * 按条件查询POP交接清单总数
	 * @param paramMap
	 * @return
	 */
	public int findPopJoinTotalCount(Map<String, Object> paramMap);


	/**
	 * 按条件查询POP交接清单集合
	 * @param paramMap
	 * @return
	 */
	public List<Inspection> findPopJoinList(Map<String, Object> paramMap);
	
	/**
	 * 按条件查询POP交接清单集合
	 * @param waybillCodes
	 * @return
	 */
	public List<Inspection> findPopByWaybillCodes(List<String> waybillCodes);


	public int findBPopJoinTotalCount(Map<String, Object> paramMap);


	public List<Inspection> findBPopJoinList(Map<String, Object> paramMap);

	/**
	 * 根据包裹号查询验货记录
	 * @param inspection
	 * @return
	 */
	public boolean haveInspection(Inspection inspection);

	/**
	 * 从自动分拣机总部数据抓数据放到 交接表
	 * @param  task
	 * */
	public boolean dealHandoverPackages(Task task);
	
	/**
	 * 写入业务表数据和日志数据
	 * @param  inspection
	 * */
	public void saveData(Inspection inspection);

    /**
     * 更新或插入数据
     * @param inspection
     * @return
     */
    public  Integer insertOrUpdate(Inspection inspection);

    public void thirdPartyWorker(Inspection inspection);

    public void pushOEMToWMS(Inspection inspection);

    public InspectionResult getInspectionResult(Integer dmsSiteCode, String waybillCode);

	/**
	 * 按条件查询验货记录
	 *
	 * */
	public List<Inspection> queryByCondition(Inspection inspection);


	/**
	 * 平台打印，补验货任务
	 * @param task
	 * @param ownSign
	 * @return
	 * @throws Exception
	 */
	public boolean popPrintInspection(Task task, String ownSign) throws Exception;

	/**
	 * 获取金鹏订单提示语
	 * @param dmsSiteCode
	 * @param waybillCode
	 * @return
	 */
	String getHintMessage(Integer dmsSiteCode, String waybillCode);
}
