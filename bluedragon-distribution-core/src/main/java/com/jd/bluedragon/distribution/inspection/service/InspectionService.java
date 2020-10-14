package com.jd.bluedragon.distribution.inspection.service;

import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.client.domain.PdaOperateRequest;
import com.jd.bluedragon.distribution.inspection.InsepctionCheckDto;
import com.jd.bluedragon.distribution.inspection.InspectionCheckCondition;
import com.jd.bluedragon.distribution.inspection.constants.InspectionExeModeEnum;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.domain.InspectionPackProgress;
import com.jd.bluedragon.distribution.inspection.domain.InspectionResult;
import com.jd.bluedragon.distribution.jsf.domain.SortingJsfResponse;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;
import java.util.Map;


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
	 * 查询运单中已校验的数量
	 * @param condition
	 * @return
	 */
	public PagerResult<InsepctionCheckDto> findInspectionGather(InspectionCheckCondition condition);


	/**
	 * 查询运单中已校验的包裹
	 * @param inspection
	 * @return
	 */
	public PagerResult<Inspection> findInspetionedPacks(Inspection inspection);

    /**
     * 校验验货是否集齐
     * @param pdaOperateRequest
     * @return
     */
    public SortingJsfResponse gatherCheck(PdaOperateRequest pdaOperateRequest,SortingJsfResponse sortingJsfResponse);

	/**
	 * 验货数量
	 * @param inspection
	 * @return
	 */
	public Integer inspectionCount(Inspection inspection);

	/**
	 * 已验数据 按运单查询
	 * 不包含传入的包裹号
	 * @param inspection
	 * @return
	 */
	public Integer inspectionCountByWaybill(Inspection inspection);

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
	public void saveData(Inspection inspection, String methodName);

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

	/**分页查询验货任务*/
	public List<Inspection> findPageInspection(Map<String,Object> params);

	/**
	 * 查询运单的验货进度
	 *
	 * @param waybillCode
	 * @param createSiteCode
	 * @return
	 */
	InspectionPackProgress getWaybillCheckProgress(String waybillCode, Integer createSiteCode);

    /**
     * 校验运单号是否绑定集包袋
     *
     * @param waybillCode
     * @return
     */
    boolean checkIsBindMaterial(String waybillCode);

    /**
     * 确认验货任务执行模式
     *
     * @param request
     * @return
     */
    InspectionExeModeEnum findInspectionExeMode(InspectionRequest request);

    /**
     * 验货运单多包裹拆分任务，分页数量
     * @return
     */
    int getInspectionTaskPackageSplitNum();
}
