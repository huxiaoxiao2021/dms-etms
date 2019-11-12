package com.jd.bluedragon.distribution.inspection.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.inspection.dao.InspectionDao;
import com.jd.bluedragon.distribution.inspection.dao.InspectionECDao;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.domain.InspectionEC;
import com.jd.bluedragon.distribution.inspection.exception.InspectionException;
import com.jd.bluedragon.distribution.inspection.service.InspectionExceptionService;
import com.jd.bluedragon.distribution.inspection.service.InspectionNotifyService;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.receive.dao.CenConfirmDao;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.weight.domain.OpeEntity;
import com.jd.bluedragon.distribution.weight.domain.OpeObject;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private SendMDao sendMDao;

	@Autowired
	private SiteService siteService;
	
	private final static Logger log = LoggerFactory.getLogger(InspectionExceptionServiceImpl.class);

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
	public List<InspectionEC> getByThird(InspectionEC inspectionEC) throws Exception{
		List<InspectionEC> list =inspectionECDao.queryByThird(inspectionEC);
        List<InspectionEC> target=new ArrayList<InspectionEC>();
		SendM searchArgument = new SendM();
        if(null!=list&&list.size()>0) {
            for (InspectionEC item : list) {
                searchArgument.setBoxCode(item.getBoxCode());
                searchArgument.setCreateSiteCode(item.getCreateSiteCode());
                searchArgument.setReceiveSiteCode(item.getReceiveSiteCode());
                searchArgument.setSendType(item.getInspectionType());
                if (!sendMDao.checkSendByBox(searchArgument)){
                    target.add(item);
                }
            }
        }
		return target;
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
					InspectionExceptionServiceImpl.log.info("ortingService.canCancelSorting Time: {}",(endTimeCanCancelSorting-startTimeCanCancelSorting));
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
					InspectionExceptionServiceImpl.log.warn(" 更新验货异常记录状态失败（分拣、验货、收货确认、异常比对）, packageBarcode: {}",inspectionEC.getPackageBarcode());
				}
				long endTime = System.currentTimeMillis();
				InspectionExceptionServiceImpl.log.info("exceptionCancel Time: {}",(endTime-startTime));
			}
			
			if( sended==inspectionECs.size() ){
				InspectionExceptionServiceImpl.log.warn("包裹已经发货，无法取消：{} ",inspectionECs.get(0).getPackageBarcode());
				return InspectionExceptionServiceImpl.PACKAGE_SENDED;
			}
			
			return result>=inspectionECs.size() ? 1 : InspectionExceptionServiceImpl.CANCEL_FAIL;
		} catch (Exception e) {
			InspectionExceptionServiceImpl.log.info(e.getMessage(), e);
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
					InspectionExceptionServiceImpl.log.warn(" 更新验货异常记录状态失败, packageBarcode: {}",inspectionEC.getPackageBarcode());
				}
			}
			
			if( sended==inspectionECs.size() ){
				InspectionExceptionServiceImpl.log.warn("包裹已经发货，无法取消： {}",inspectionECs.get(0).getPackageBarcode());
				return InspectionExceptionServiceImpl.PACKAGE_SENDED;
			}
			return result==inspectionECs.size() ? 1 : 0;
		} catch (Exception e) {
			InspectionExceptionServiceImpl.log.error(e.getMessage(),e);
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
	 * 根据箱号查出分拣的包裹集合和验货包裹集合进行比较
	 * @param inspectionEC
	 * @param distributions
	 * @return
	 * @throws Exception
	 */

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
		List<InspectionEC> list =inspectionECDao.queryThirdByParams(paramMap);
        int count=0;
        if(null!=list&&list.size()>0) {
            SendM searchArgument = new SendM();
            for (InspectionEC item : list) {
                searchArgument.setBoxCode(item.getBoxCode());
                searchArgument.setCreateSiteCode(item.getCreateSiteCode());
                searchArgument.setReceiveSiteCode(item.getReceiveSiteCode());
                searchArgument.setSendType(item.getInspectionType());
                if (!sendMDao.checkSendByBox(searchArgument)){
                    ++count;
                }
            }
        }
		return count;
	}

	@Override
	public List<InspectionEC> queryThirdByParams(Map<String, Object> paramMap) {
		List<InspectionEC> list =inspectionECDao.queryThirdByParams(paramMap);
		List<InspectionEC> target=new ArrayList<InspectionEC>();
        if(null!=list&&list.size()>0) {
            SendM argument = new SendM();
            for (InspectionEC item : list) {
                argument.setBoxCode(item.getBoxCode());
                argument.setCreateSiteCode(item.getCreateSiteCode());
                argument.setReceiveSiteCode(item.getReceiveSiteCode());
                argument.setSendType(item.getInspectionType());
                if (!sendMDao.checkSendByBox(argument)) {
                    target.add(item);
                }
            }
        }
		return target;
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
				if(log.isInfoEnabled()){
					log.info("龙门架:{}",JsonHelper.toJson(inspection));
				}
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
			log.error("龙门架写称重任务失败:{}",JsonHelper.toJson(inspection),ex);
		}
	}
}
