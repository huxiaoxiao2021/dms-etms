package com.jd.bluedragon.distribution.inspection.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.inspection.dao.InspectionDao;
import com.jd.bluedragon.distribution.inspection.dao.InspectionECDao;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.domain.InspectionEC;
import com.jd.bluedragon.distribution.inspection.domain.InspectionMQBody;
import com.jd.bluedragon.distribution.inspection.exception.InspectionException;
import com.jd.bluedragon.distribution.inspection.service.InspectionExceptionService;
import com.jd.bluedragon.distribution.inspection.service.InspectionNotifyService;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.receive.dao.CenConfirmDao;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.weight.domain.OpeEntity;
import com.jd.bluedragon.distribution.weight.domain.OpeObject;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.CollectionHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 验货差异查询及处理Service
 * @author wangzichen
 *
 */
@Service
public class InspectionExceptionServiceImpl implements InspectionExceptionService{
	
	@Autowired
	private InspectionECDao inspectionECDao;
	
	@Autowired
	private InspectionDao inspectionDao;
	
	@Autowired
	private CenConfirmDao cenConfirmDao;
	
	@Autowired
	private BoxService boxService;
	
	@Autowired
	private SortingService sortingService;
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private DeliveryService deliveryService;
	
	@Autowired
    private SendDatailDao sendDetailDao;
	
	@Autowired
    private InspectionService  inspectionService;

	@Autowired
	private SiteService siteService;
	
	private final static Logger logger = Logger.getLogger(InspectionExceptionServiceImpl.class);

	/*包裹已经发货*/
	public static final int PACKAGE_SENDED = -10000;

	/*取消或者退回运单下所有包裹*/
	public static final int WAYBILL_CANCEL = -1001;
	
	/*异常处理失败*/
	public static final int CANCEL_FAIL = -1;

    @Autowired
    private InspectionNotifyService inspectionNotifyService;
	/**
	 * get inspection exception by condition
	 * @param inspectionEC
	 * @return
	 */
	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<InspectionEC> getListsByCondition(InspectionEC inspectionEC) throws Exception{
		return inspectionECDao.selectSelective(inspectionEC);
	}
	
	/**
	 *  异常查询，根据三方code和异常类型(1少验,2多验)
	 * @param inspectionEC
	 * @return
	 * @throws Exception 
	 */
	@Override
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public List<InspectionEC> getByThird(InspectionEC inspectionEC) throws Exception{
		return inspectionECDao.queryByThird(inspectionEC);
	}

	/**
	 * dual with exception data in inspection
	 * @param operationType 
	 * @param inspectionEC
	 * @return
	 */
	@Override
	@JProfiler(jKey = "DMSWEB.InspectionExceptionService.exceptionCancel", mState = {JProEnum.TP})
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int exceptionCancel(List<InspectionEC> inspectionECs, Integer operationType) {
		int sortingResult = 0;
		int inspectionResult =0 ;
		int result = 0;
		int sended = 0;
		try {
			for( InspectionEC inspectionEC:inspectionECs){
				//如果已经发货，则不能取消分拣、验货
				long startTime = System.currentTimeMillis();
				if(checkSendByInspectionEC(inspectionEC)){
					sended++;
					continue;
				}
				if( InspectionEC.INSPECTIONEC_TYPE_SEND_BACK!=operationType){
					//取消分拣
					Sorting sorting = new Sorting();
			        sorting.setCreateSiteCode(inspectionEC.getCreateSiteCode());
			        sorting.setUpdateUser(inspectionEC.getUpdateUser());
			        sorting.setUpdateUserCode(inspectionEC.getUpdateUserCode());
			        sorting.setBoxCode(inspectionEC.getBoxCode());
			        sorting.setPackageCode(inspectionEC.getPackageBarcode());
			        sorting.setWaybillCode(inspectionEC.getWaybillCode());
			        sorting.setReceiveSiteCode(inspectionEC.getReceiveSiteCode());
			        sorting.setType(Inspection.BUSSINESS_TYPE_THIRD_PARTY);//三方
			        long startTimeCanCancelSorting = System.currentTimeMillis();
					if(sortingService.canCancelSorting(sorting)){
						sortingResult ++;
					}
					long endTimeCanCancelSorting = System.currentTimeMillis();
					InspectionExceptionServiceImpl.logger.info("ortingService.canCancelSorting Time: "+(endTimeCanCancelSorting-startTimeCanCancelSorting));
				}
				if( InspectionEC.INSPECTIONEC_TYPE_CANCEL!=operationType){
					//取消验货记录及收货确认记录
					Inspection inspection = new Inspection.Builder( inspectionEC.getPackageBarcode(), inspectionEC.getCreateSiteCode() )
						.receiveSiteCode( inspectionEC.getReceiveSiteCode() ).boxCode( inspectionEC.getBoxCode() )
						.inspectionType(Inspection.BUSSINESS_TYPE_THIRD_PARTY)
						.updateUser(inspectionEC.getUpdateUser()).updateUserCode(inspectionEC.getUpdateUserCode()).build();
					inspectionResult += inspectionDao.updateYnByPackage(inspection);
					CenConfirm cenConfirm = new CenConfirm.Builder(inspectionEC.getPackageBarcode(), inspectionEC.getCreateSiteCode() )
						.receiveSiteCode( inspectionEC.getReceiveSiteCode() ).boxCode( inspectionEC.getBoxCode() ).type(Inspection.BUSSINESS_TYPE_THIRD_PARTY).build();
					inspectionResult += cenConfirmDao.updateYnByPackage(cenConfirm);
				}
				inspectionEC.setStatus(operationType);
				if(InspectionEC.INSPECTIONEC_TYPE_SEND_BACK==operationType || InspectionEC.INSPECTIONEC_TYPE_SEND==operationType){
					inspectionEC.setInspectionECType(InspectionEC.INSPECTIONEC_TYPE_MORE);
				}else if(InspectionEC.INSPECTIONEC_TYPE_CANCEL==operationType){
					inspectionEC.setInspectionECType(InspectionEC.INSPECTIONEC_TYPE_LESS);
				}
				
				result += inspectionECDao.updateStatus(inspectionEC);
				if( result<=0 && sortingResult<=0 && inspectionResult<=0){
					InspectionExceptionServiceImpl.logger.info(" 更新验货异常记录状态失败（分拣、验货、收货确认、异常比对）, packageBarcode: "+inspectionEC.getPackageBarcode());
				}
				long endTime = System.currentTimeMillis();
				InspectionExceptionServiceImpl.logger.info("exceptionCancel Time: "+(endTime-startTime));
			}
			
			if( sended==inspectionECs.size() ){
				InspectionExceptionServiceImpl.logger.error("包裹已经发货，无法取消： "+inspectionECs.get(0).getPackageBarcode());
				return InspectionExceptionServiceImpl.PACKAGE_SENDED;
			}
			
			return result>=inspectionECs.size() ? 1 : InspectionExceptionServiceImpl.CANCEL_FAIL;
		} catch (Exception e) {
			InspectionExceptionServiceImpl.logger.info(e.getMessage(), e);
			throw new InspectionException(" called method exceptionCancel fail ");
		}
	}

	/**
	 * 多验直接配送
	 * @param inspectionECs
	 * @param operationType 
	 * @return
	 */
	@Override
	@JProfiler(jKey= "DMSWEB.InspectionExceptionService.directDistribution")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int directDistribution(List<InspectionEC> inspectionECs, Integer operationType) {
		
		int result = 0;
		int sended = 0;
		try {
			for( InspectionEC inspectionEC:inspectionECs){
				//如果已经发货，则不能取消分拣、验货
				if(checkSendByInspectionEC(inspectionEC)){
					sended++;
					continue;
				}
				
		        SendDetail sendDetail = new SendDetail.Builder(inspectionEC.getPackageBarcode(), inspectionEC.getCreateSiteCode())
		        	.createUser(inspectionEC.getUpdateUser()).createUserCode(inspectionEC.getUpdateUserCode())
		        	.boxCode(inspectionEC.getBoxCode()).waybillCode(inspectionEC.getWaybillCode())
		        	.receiveSiteCode(inspectionEC.getReceiveSiteCode()).sendType(Constants.BUSSINESS_TYPE_THIRD_PARTY)
		        	.createTime(inspectionEC.getUpdateTime()).updateTime(inspectionEC.getUpdateTime()).isCancel(0).build();
		        	
				deliveryService.saveOrUpdate(sendDetail);//调用多验直接配送,即发货，插入 send_d
				inspectionEC.setStatus(operationType);
				inspectionEC.setInspectionECType(InspectionEC.INSPECTIONEC_TYPE_MORE);
				//更新exception_status和box_code 
				result += inspectionECDao.updateForSorting(inspectionEC);
				if( result<=Constants.NO_MATCH_DATA ){
					InspectionExceptionServiceImpl.logger.info(" 更新验货异常记录状态失败, packageBarcode: "+inspectionEC.getPackageBarcode());
				}
			}
			
			if( sended==inspectionECs.size() ){
				InspectionExceptionServiceImpl.logger.error("包裹已经发货，无法取消： "+inspectionECs.get(0).getPackageBarcode());
				return InspectionExceptionServiceImpl.PACKAGE_SENDED;
			}
			return result==inspectionECs.size() ? 1 : 0;
		} catch (Exception e) {
			InspectionExceptionServiceImpl.logger.error(e.getMessage(),e);
			throw new InspectionException( " called method  directDistribution fail " );
		}
	}
	
	/**
	 * 如果已经发货，则该包裹不能做取消操作
	 * @param inspectionEC
	 * @return
	 */
	public boolean checkSendByInspectionEC(InspectionEC inspectionEC) {
		SendDetail sendDetail = new SendDetail.Builder( inspectionEC.getPackageBarcode(), inspectionEC.getCreateSiteCode() )
			.receiveSiteCode( inspectionEC.getReceiveSiteCode() ).boxCode( inspectionEC.getBoxCode() ).sendType(Inspection.BUSSINESS_TYPE_THIRD_PARTY).build();
		return deliveryService.checkSendByPackage(sendDetail);
	}
	
	/**
	 * 只验证send_d是否有发货数据
	 * @param bean
	 * @return
	 */
	@Override
	public boolean checkSendDOnly(InspectionEC inspectionEC) {
		SendDetail sendDetail = new SendDetail.Builder( inspectionEC.getPackageBarcode(), inspectionEC.getCreateSiteCode() )
		.waybillCode(inspectionEC.getWaybillCode()).receiveSiteCode( inspectionEC.getReceiveSiteCode() ).sendType(Inspection.BUSSINESS_TYPE_THIRD_PARTY).build();
		boolean result = sendDetailDao.checkSendSendCode(sendDetail);
		return result;
	}
	
	/**
	 * 异常对比，差异信息生成
	 */
	@JProfiler(jKey= "DMSWEB.InspectionExceptionService.exceptionCompare")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void exceptionCompare(InspectionEC inspectionEC) throws Exception{
		
         String boxCode = inspectionEC.getBoxCode();
         //step 1.调用志澎接口查询Task里的boxCode是否分拣完毕，若未分拣完毕，则不执行下面的操作
         //并且更新箱子的状态,为
         Box boxInit = new Box();
         boxInit.setCreateSiteCode(inspectionEC.getCreateSiteCode());
         boxInit.setCodes("'"+boxCode+"'");
         boxInit.setStatus(Box.BOX_STATUS_INSPECT_PROCESSING);
    	 int boxInitStatus = boxService.updateStatusByCodes(boxInit);
         if(boxInitStatus!=1){
        	 InspectionExceptionServiceImpl.logger.info(" Comparative inspection exception , update initial state of box fail, box code: "+boxCode);
         }
    	 
         /*如果当前箱子还有未处理的分拣记录，则都记录为多验*/
         unhandleSortingByBox(inspectionEC);
         
         //调用分拣记录：通过箱号获取包裹集合
         List<Sorting> distributions = getSortingRecords(inspectionEC);
         //如果无分拣记录，则为多验
         if (null == distributions || distributions.isEmpty()) {
 			inspectionEC.setInspectionECType(InspectionEC.INSPECTIONEC_TYPE_MORE);
 			inspectionECDao.updateInspectionECType(inspectionEC);
 			return ;
 		}
         
         //step2.进行异常比对，根据箱号查出分拣的包裹集合和验货包裹集合进行比较
         //生成多验和少验记录
         Set<String> sortingSet = compareSortingInspection(inspectionEC, distributions);
         
         //完验操作，当分拣的正常记录与验货的正常记录相同是，更新箱子状态为4表示完验
         inspectionFinishByBox(boxCode,inspectionEC, sortingSet);
	}

	/**
	 * 以箱子为单位箱子，判断验货中所有正常的，和已经直接配送的包裹，是否等于分拣的记录
	 * 如果验货与分拣相等，则把箱子的状态改为4，表示完验，可以发货
	 * @param boxCode
	 * @param inspectionEC
	 * @param sortingSet
	 */
	private void inspectionFinishByBox(String boxCode, InspectionEC inspectionEC, Set<String> sortingSet) {
		InspectionEC inspectionECLast = new InspectionEC();
        inspectionECLast.setBoxCode(boxCode);
        inspectionECLast.setReceiveSiteCode(inspectionEC.getReceiveSiteCode());
        inspectionECLast.setCreateSiteCode(inspectionEC.getCreateSiteCode());
        
        List<InspectionEC> lastList = inspectionECDao.queryLast(inspectionECLast);//查出最后所有正常的，和已经处理的包裹，是否等于分拣的记录
        
        int result = 0;
        for(InspectionEC eachInspectionEC :lastList){
        	if(sortingSet.contains(getInspectionSetStr(Inspection.toInspectionByEC(eachInspectionEC)))) {
	            result ++;
            }
        }
        
        //step3.执行完验操作，检查是否更新box表的状态
        //需要判断箱子状态是否为2，才可以更新
        //箱子状态：未使用(0)、打印（1）、分拣（2）、正在三方验货（3）、三方验货完毕(4)、发货完毕(5)、正在发车（6）、发车完毕(7)
        if(result>0 && result == sortingSet.size() ){
        	Box box = new Box();
        	box.setCreateSiteCode(inspectionEC.getCreateSiteCode());
       	 	box.setCodes("'"+boxCode+"'");
       	 	box.setStatus(Box.BOX_STATUS_INSPECT);
       	 	int boxInspectionFinishState =  boxService.updateStatusByCodes(box);
       	 	if( boxInspectionFinishState!=1 ){
       	 		InspectionExceptionServiceImpl.logger.info("Comparative inspection exception , update box status in end of inspection fail that box code is "+boxCode);
       	 	}
        }
	}

	/**
	 * 根据箱号查出分拣的包裹集合和验货包裹集合进行比较
	 * @param inspectionEC
	 * @param distributions
	 * @return
	 * @throws Exception
	 */
	private Set<String> compareSortingInspection(InspectionEC inspectionEC, List<Sorting> distributions) throws Exception{
		List<Inspection> inspPackages = inspectionDao.queryListByBox(Inspection.toInspectionByEC(inspectionEC));// 异常比对表数据

		// 写入set
		Set<String> sortingSet = new HashSet<String>();// 分拣记录
		for (Sorting sortingEach : distributions) {
			sortingSet.add(getSortingSetStr(sortingEach));
		}
		Set<String> inspectionSet = new HashSet<String>();// 验货记录
		for (Inspection inspectionEach : inspPackages) {
			inspectionSet.add(getInspectionSetStr(inspectionEach));
		}

		Set<InspectionEC> more = new HashSet<InspectionEC>();
		Set<InspectionEC> less = new HashSet<InspectionEC>();
		Set<InspectionEC> normal = new HashSet<InspectionEC>();

		// compare
		for (Sorting comSorting : distributions) {
			InspectionEC sortingEach = new InspectionEC(
					comSorting.getBoxCode(), comSorting.getPackageCode(),
					comSorting.getCreateSiteCode(), comSorting.getReceiveSiteCode());

			if (inspectionSet.contains(getSortingSetStr(comSorting))) {
				sortingEach.setInspectionECType(InspectionEC.INSPECTIONEC_TYPE_NORMAL);
				normal.add(sortingEach);
			} else {// 如果在验货表不存在，在装箱表里存在，则为少验
				sortingEach.setInspectionECType(InspectionEC.INSPECTIONEC_TYPE_LESS);
				sortingEach.setInspectionType(Inspection.BUSSINESS_TYPE_THIRD_PARTY);
				sortingEach.setStatus(InspectionEC.INSPECTION_EXCEPTION_STATUS_HANDLED);
				sortingEach.setWaybillCode(BusinessHelper.getWaybillCodeByPackageBarcode(sortingEach.getPackageBarcode()));
				less.add(sortingEach);
			}
		}

		for (Inspection inspectionEach : inspPackages) {
			InspectionEC inspectionECEach = InspectionEC.toInspectionECByInspection(inspectionEach);
			if (sortingSet.contains(getInspectionSetStr(inspectionEach))) {
				inspectionECEach.setInspectionECType(InspectionEC.INSPECTIONEC_TYPE_NORMAL);
				normal.add(inspectionECEach);
			} else {// 如果在验货表存在，在装箱表里不存在，则为多验
				inspectionECEach.setInspectionECType(InspectionEC.INSPECTIONEC_TYPE_MORE);
				more.add(inspectionECEach);
			}
		}

		// 多验或者正常的数据直接更改inspection_e_c_type字段和status字段即可
		inspectionECDao.batchUpdateInspectionECType(normal);
		inspectionECDao.batchUpdateInspectionECType(more);
		// 少验需要则标记为少验记录
		CollectionHelper<InspectionEC> helper = new CollectionHelper<InspectionEC>();
		this.insertOrUpdateBatch(helper.toList(less));

		return sortingSet;
	}

	/**
	 * 获取所有该箱子的分拣记录
	 * @param boxCode
	 * @param inspectionEC
	 * @return
	 * @throws Exception
	 */
	private List<Sorting> getSortingRecords(InspectionEC inspectionEC) throws Exception{
		List<Sorting> distributions = new ArrayList<Sorting>();
		Sorting sorting = new Sorting();
		sorting.setBoxCode(inspectionEC.getBoxCode());
		sorting.setCreateSiteCode(inspectionEC.getCreateSiteCode());
		sorting.setType(Inspection.BUSSINESS_TYPE_THIRD_PARTY);
		try {
			distributions = sortingService.findByBoxCode(sorting);
		} catch (Exception e) {
			InspectionExceptionServiceImpl.logger.error("异常比对Worker，调用 异常，异常信息为："+e.getMessage(),e);
		}

		 if (null == distributions || distributions.isEmpty()) {
	 			InspectionExceptionServiceImpl.logger.warn(" Comparative inspection exception , box code : "
	 					+ inspectionEC.getBoxCode() + " & createSiteCode :"
	 					+ sorting.getCreateSiteCode()
	 					+ " from sorting are not exist ");
		 }
		return distributions;
	}

	/**
	 * 若箱子还有位处理的分拣任务，则验货记录置为多验
	 * @param inspectionEC
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	private void unhandleSortingByBox(InspectionEC inspectionEC) throws Exception{
		Task task = new Task();
        task.setCreateSiteCode(inspectionEC.getCreateSiteCode());
        task.setType(Task.TASK_TYPE_SORTING);
        task.setBoxCode(inspectionEC.getBoxCode());
        task.setTableName(task.getTableName(task.getType()));
        task.setStatuses(Task.TASK_STATUS_UNHANDLED+","+Task.TASK_STATUS_PROCESSING);
        List<Task> boxTask =  taskService.findTasks(task);//call task data for check box is distributed, parameter boxCode
        if( !boxTask.isEmpty() ){//如果任务表里有某箱号未处理的分拣任务,则认为是多验
        	InspectionExceptionServiceImpl.logger.info(" Comparative inspection exception , box code : "+inspectionEC.getBoxCode()+" from task_sorting is not exist ");
        	inspectionEC.setInspectionECType(InspectionEC.INSPECTIONEC_TYPE_MORE);
        	inspectionEC.setStatus(0);//需要设置前置条件：where exception_status=0，表示未处理的记录
        	inspectionECDao.updateInspectionECType(inspectionEC);
        	return;
        }
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	private void insertOrUpdateBatch(List<InspectionEC> list) throws Exception{
		if( null==list || list.isEmpty() ) {
	        return;
        }
		for( InspectionEC inspectionEC:list ){
			int result = inspectionECDao.updateInspectionECType(inspectionEC);
			if(result!=1){
				inspectionECDao.add(InspectionECDao.namespace, inspectionEC);
			}
		}
	}

	/**
	 * 获得验货记录Inspection的唯一串，由四部分组成：箱号、包裹、创建站点、接收站点
	 * 由^分隔
	 * @param inspectionEach
	 * @return
	 */
	private String getInspectionSetStr(Inspection inspectionEach) {
		return inspectionEach.getBoxCode()+"^"+inspectionEach.getPackageBarcode()
   			 +"^"+inspectionEach.getCreateSiteCode()+"^"+inspectionEach.getReceiveSiteCode();
	}

	/**
	 * 获得分拣记录Sorting的唯一串，由四部分组成：箱号、包裹、创建站点、接收站点
	 * 由^分隔
	 * @param sortingEach
	 * @return
	 */
	private String getSortingSetStr(Sorting sortingEach) {
		return sortingEach.getBoxCode()+"^"+sortingEach.getPackageCode()
   			 +"^"+sortingEach.getCreateSiteCode()+"^"+sortingEach.getReceiveSiteCode();
	}

	/**
	 * 检查三方是否完验
	 * @param createSiteCode
	 * @param receiveSiteCode
	 * @param boxCode
	 * @return
	 */
	@Override
	public int queryExceptions(Integer createSiteCode, Integer receiveSiteCode,
			String boxCode) {
		Integer exceptionCount = inspectionECDao.exceptionCountByBox(createSiteCode, receiveSiteCode, boxCode);
		Integer unInspectionCount = inspectionECDao.boxUnInspection(createSiteCode, receiveSiteCode, boxCode);
		if(unInspectionCount.equals(exceptionCount)){
			return Constants.NO_MATCH_DATA;
		}	
		int result = inspectionECDao.queryExceptionsCore(createSiteCode, receiveSiteCode, boxCode);
		return result;
	}

	/**
	 * 检查包裹是否已经异常处理过
	 * @param inspectionECs
	 * @param operationType
	 * @return List<InspectionEC>
	 */
	@Override
	public String checkDispose(List<InspectionEC> inspectionECs,
			Integer operationType) {
		
		for( InspectionEC inspectionEC:inspectionECs ){
			List<InspectionEC> inspectionECStatus = inspectionECDao.checkDispose( inspectionEC);
			if( null==inspectionECStatus || inspectionECStatus.isEmpty() ) {
	            continue;
            }
			InspectionEC inspectionECEach = inspectionECStatus.get(0);
			if( InspectionEC.INSPECTIONEC_TYPE_SEND_BACK==operationType && InspectionEC.INSPECTIONEC_TYPE_MORE!=inspectionECEach.getInspectionECType() ){
				return "包裹"+inspectionEC.getPackageBarcode()+" 非多验记录，不能多验退回";
			}else if( inspectionECEach.getStatus() > InspectionEC.INSPECTION_EXCEPTION_STATUS_HANDLED ){
				return "包裹"+inspectionEC.getPackageBarcode()+" 已经处理，不能再次处理";
			}
		}
		
		return null;
	}

	/**
	 * 查询是否已经三方验货
	 */
	@Override
	public Integer inspectionCount(InspectionEC inspectionEC) {
		return inspectionECDao.inspectionCount(inspectionEC);
	}

	@Override
	public int totalThirdByParams(Map<String, Object> paramMap) {
		
		return inspectionECDao.totalThirdByParams(paramMap);
	}

	@Override
	public List<InspectionEC> queryThirdByParams(Map<String, Object> paramMap) {
		return inspectionECDao.queryThirdByParams(paramMap);
	}

	@Override
	public InspectionEC get( Long checkId ){
		return inspectionECDao.get(InspectionECDao.namespace, checkId);
	}

	@Override
	public void saveData(Inspection inspection) {
		// TODO Auto-generated method stub
		inspectionService.saveData(inspection);

		try {//FIXME:看看龙门架是否能拆出
			if ((inspection.getLength() != null && inspection.getLength() > 0)
					|| (inspection.getWidth() != null && inspection.getWidth() > 0)
					|| (inspection.getHigh() != null && inspection.getHigh() > 0)) {
				logger.info("龙门架:"+JsonHelper.toJson(inspection));
				OpeEntity opeEntity = new OpeEntity();
				opeEntity.setOpeType(1);//分拣中心称重
				opeEntity.setWaybillCode(inspection.getWaybillCode());
				opeEntity.setOpeDetails(new ArrayList<OpeObject>());

				OpeObject obj = new OpeObject();
				obj.setOpeSiteId(inspection.getCreateSiteCode());
				BaseStaffSiteOrgDto dto = siteService.getSite(inspection.getCreateSiteCode());
				obj.setOpeSiteName(dto.getSiteName());
				obj.setpWidth(inspection.getWidth());
				obj.setpLength(inspection.getLength());
				obj.setpHigh(inspection.getHigh());
				obj.setPackageCode(inspection.getPackageBarcode());
				obj.setOpeUserId(inspection.getCreateUserCode());
				obj.setOpeUserName(inspection.getCreateUser());
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				obj.setOpeTime(simpleDateFormat.format(inspection.getCreateTime()));

				opeEntity.getOpeDetails().add(obj);
				String body = "[" + JsonHelper.toJson(opeEntity) + "]";
				Task task = new Task();
				task.setBody(body);
				task.setType(Task.TASK_TYPE_WEIGHT);
				task.setTableName(Task.getTableName(Task.TASK_TYPE_WEIGHT));
				task.setCreateSiteCode(opeEntity.getOpeDetails().get(0).getOpeSiteId());
				task.setKeyword1(String.valueOf(opeEntity.getOpeDetails().get(0).getOpeSiteId()));
				task.setKeyword2("上传长宽高");
				task.setBody(body);
				task.setBoxCode("");
				task.setSequenceName(Task.getSequenceName(task.getTableName()));
				task.setReceiveSiteCode(opeEntity.getOpeDetails().get(0).getOpeSiteId());
				task.setOwnSign(BusinessHelper.getOwnSign());
				taskService.add(task);
			}
		} catch (Exception ex) {
			logger.error("龙门架写称重任务失败",ex);
		}
	}
}
