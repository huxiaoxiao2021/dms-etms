package com.jd.bluedragon.distribution.alliance.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.alliance.AllianceBusiDeliveryDetailDto;
import com.jd.bluedragon.distribution.alliance.AllianceBusiDeliveryDto;
import com.jd.bluedragon.distribution.alliance.AllianceBusiFailDetailDto;
import com.jd.bluedragon.distribution.alliance.AllianceBusiOpeTypeEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.weight.domain.HandOverWeightDto;
import com.jd.bluedragon.distribution.weight.domain.WeightOpeDto;
import com.jd.bluedragon.distribution.wss.dto.BaseEntity;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.util.StringUtils;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.alliance.domain.AllianceBusiDeliveryDetail;
import com.jd.bluedragon.distribution.alliance.dao.AllianceBusiDeliveryDetailDao;
import com.jd.bluedragon.distribution.alliance.service.AllianceBusiDeliveryDetailService;

import java.util.*;

/**
 *
 * @ClassName: AllianceBusiDeliveryDetailServiceImpl
 * @Description: 加盟商计费交付明细表--Service接口实现
 * @author wuyoude
 * @date 2019年07月10日 15:35:36
 *
 */
@Service("allianceBusiDeliveryDetailService")
public class AllianceBusiDeliveryDetailServiceImpl extends BaseService<AllianceBusiDeliveryDetail> implements AllianceBusiDeliveryDetailService {

	@Autowired
	@Qualifier("allianceBusiDeliveryDetailDao")
	private AllianceBusiDeliveryDetailDao allianceBusiDeliveryDetailDao;

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Autowired
	private TaskService taskService;

	@Autowired
	@Qualifier("opeWeightProducer")
	private DefaultJMQProducer opeWeightProducer;

    @Autowired
    @Qualifier("handOverWeightProducer")
    private DefaultJMQProducer handOverWeightProducer;

	@Autowired
	private WaybillQueryManager waybillQueryManager;

	@Override
	public Dao<AllianceBusiDeliveryDetail> getDao() {
		return this.allianceBusiDeliveryDetailDao;
	}

	@Override
	public boolean checkExist(String waybillCode) {
		return allianceBusiDeliveryDetailDao.checkExist(waybillCode)>0;
	}

	/**
	 * 加盟商交接环节
	 * @param dto
	 * @return
	 */
	@JProfiler(jKey = "DMS.AllianceBusiDeliveryDetailService.allianceBusiDelivery",
			mState = {JProEnum.TP, JProEnum.FunctionError},jAppName = Constants.UMP_APP_NAME_DMSWEB)
	public BaseEntity<List<AllianceBusiFailDetailDto>> allianceBusiDelivery(AllianceBusiDeliveryDto dto) {
		logger.debug("加盟商交接环节入参:"+JsonHelper.toJson(dto));
		//校验入参
		BaseEntity<List<AllianceBusiFailDetailDto>> result = checkParam(dto);
		if(result.getCode()!= BaseEntity.CODE_SUCCESS  && dto.getDatas().isEmpty()){
			//此时为全部失败 直接返回
			return result;
		}
		if(!dto.getForce()){
			//非强制
			//校验预付款余额不足
			checkMoney(dto,result);
			if(result.getCode()!= BaseEntity.CODE_SUCCESS  && dto.getDatas().isEmpty()){
				//此时为全部失败 直接返回
				return result;
			}
		}

		//入池
		batchInsert(convertDto(dto));

		//记录称重数据
		weight(dto);

		//推送交接数据
		handoversWaybillTrack(dto);

		//推送交接称重数据至终端，供终端做差异报表
		handOverWeight(dto);


		return result;
	}

	private boolean batchInsert(List<AllianceBusiDeliveryDetail> allianceBusiDeliveryDetails){
		List<AllianceBusiDeliveryDetail> bufferList = new ArrayList<>();
		for(AllianceBusiDeliveryDetail allianceBusiDeliveryDetail : allianceBusiDeliveryDetails){
			bufferList.add(allianceBusiDeliveryDetail);
			if(bufferList.size()==50){
				if(this.getDao().batchInsert(bufferList)){
					bufferList.clear();
				}else{
					return false;
				}
			}
		}
		if(bufferList.size() > 0){
			return this.getDao().batchInsert(bufferList);
		}
		return true;
	}


	@Override
	public boolean checkMoney(String waybillCode) {
		String allianceBusiId = getAllianceBusiId(waybillCode);
		if(StringUtils.isNotBlank(allianceBusiId)){
			return baseMajorManager.allianceBusiMoneyEnough(allianceBusiId);
		}
		return true;
	}

	/**
	 * 校验
	 * @param dto
	 * @return
	 */
	private BaseEntity<List<AllianceBusiFailDetailDto>> checkParam(AllianceBusiDeliveryDto dto){
		BaseEntity<List<AllianceBusiFailDetailDto>> result = new BaseEntity<>(BaseEntity.CODE_SUCCESS,BaseEntity.MESSAGE_SUCCESS);
		//失败提示对象
		List<AllianceBusiFailDetailDto> failDetailDtos = new ArrayList<>();
		//校验失败需要移除的数据
		List<AllianceBusiDeliveryDetailDto> removeLists = new ArrayList<>();
		result.setData(failDetailDtos);
		if(dto.getOpeType() == 0 || AllianceBusiOpeTypeEnum.getEnumByKey(dto.getOpeType()) == null){
			result.setMessage("参数校验失败：操作类型未匹配！");
			result.setCode(BaseEntity.CODE_PARAM_ERROR);
			return result;
		}else if(dto.getOperatorId()==null ||  dto.getOperatorId().compareTo(Integer.valueOf(0))<=0){
			result.setMessage("参数校验失败：操作人ID不能为空！");
			result.setCode(BaseEntity.CODE_PARAM_ERROR);
			return result;
		}else if(StringUtils.isBlank(dto.getOperatorName())){
			result.setMessage("参数校验失败：操作人名称不能为空！");
			result.setCode(BaseEntity.CODE_PARAM_ERROR);
			return result;
		}else if(dto.getOperatorSiteCode()==null ||  dto.getOperatorSiteCode().compareTo(Integer.valueOf(0))<=0){
			result.setMessage("参数校验失败：操作人所属单位ID不能为空！");
			result.setCode(BaseEntity.CODE_PARAM_ERROR);
			return result;
		}else if(StringUtils.isBlank(dto.getOperatorSiteName())){
			result.setMessage("参数校验失败：操作人所属单位名称不能为空！");
			result.setCode(BaseEntity.CODE_PARAM_ERROR);
			return result;
		}else if(dto.getDatas()!=null && dto.getDatas().size()>0){
			for(AllianceBusiDeliveryDetailDto detailDto : dto.getDatas()){
				AllianceBusiFailDetailDto failDetailDto = new AllianceBusiFailDetailDto();
				failDetailDto.setOpeCode(detailDto.getOpeCode());
				if(StringUtils.isBlank(detailDto.getOpeCode())){
					failDetailDto.setFailMessage("参数校验失败：操作单号为空！");
					failDetailDtos.add(failDetailDto);
					removeLists.add(detailDto);
				}else if(!WaybillUtil.isWaybillCode(detailDto.getOpeCode()) && !WaybillUtil.isPackageCode(detailDto.getOpeCode())){
					failDetailDto.setFailMessage("参数校验失败：操作单号既不是运单号也不是包裹号！");
					failDetailDtos.add(failDetailDto);
					removeLists.add(detailDto);
				}else if(detailDto.getOperateTimeMillis() == 0){
					failDetailDto.setFailMessage("参数校验失败：操作时间不能为空！");
					failDetailDtos.add(failDetailDto);
					removeLists.add(detailDto);
				}else if((detailDto.getVolume()==null || detailDto.getVolume().compareTo(Double.valueOf(0))<=0)){
					//无体积时
					if(detailDto.getHeight()==null || detailDto.getHeight().compareTo(Double.valueOf(0))<=0){
						failDetailDto.setFailMessage("参数校验失败：高度不能为空或非正数！");
						failDetailDtos.add(failDetailDto);
						removeLists.add(detailDto);
					}else if(detailDto.getLength()==null || detailDto.getLength().compareTo(Double.valueOf(0))<=0){
						failDetailDto.setFailMessage("参数校验失败：长度不能为空或非正数！");
						failDetailDtos.add(failDetailDto);
						removeLists.add(detailDto);
					}else if(detailDto.getWidth()==null || detailDto.getWidth().compareTo(Double.valueOf(0))<=0){
						failDetailDto.setFailMessage("参数校验失败：宽度不能为空或非正数！");
						failDetailDtos.add(failDetailDto);
						removeLists.add(detailDto);
					}
				}else if(detailDto.getWeight()==null || detailDto.getWeight().compareTo(Double.valueOf(0))<=0){
					failDetailDto.setFailMessage("参数校验失败：重量不能为空或非正数！");
					failDetailDtos.add(failDetailDto);
					removeLists.add(detailDto);
				}
			}
		}else if(dto.getDatas()==null || dto.getDatas().size()>100){
			result.setMessage(BaseEntity.MESSAGE_SIZE_ERROR);
			result.setCode(BaseEntity.CODE_SIZE_ERROR);
			return result;
		}


		if(failDetailDtos.size()>0){
			result.setCode(BaseEntity.CODE_PARAM_ERROR);
			dto.getDatas().removeAll(removeLists);
		}
		return result;

	}

	/**
	 * 校验预付款余额
	 * @param dto
	 * @return
	 */
	private BaseEntity<List<AllianceBusiFailDetailDto>> checkMoney(AllianceBusiDeliveryDto dto,BaseEntity<List<AllianceBusiFailDetailDto>> result){
		List<AllianceBusiFailDetailDto> failList = result.getData()==null?new ArrayList<AllianceBusiFailDetailDto>():result.getData();
		Set<String> allianceBusiIds = new HashSet<>();
		Set<String> waybillCodes = new HashSet<>();
		for(AllianceBusiDeliveryDetailDto detailDto : dto.getDatas()){
			AllianceBusiFailDetailDto failDetailDto = new AllianceBusiFailDetailDto();
			failDetailDto.setOpeCode(detailDto.getOpeCode());
			//校验是否已入池 已入池无需校验余额
			String waybillCode = detailDto.getOpeCode();
			if(WaybillUtil.isPackageCode(detailDto.getOpeCode())){
				waybillCode = WaybillUtil.getWaybillCode(detailDto.getOpeCode());
			}

			//获取加盟商ID
			String allianceBusiId = getAllianceBusiId(waybillCode);
			if(StringUtils.isBlank(allianceBusiId)){
				//未获取到加盟商ID
				failDetailDto.setFailMessage("未获取到加盟商ID");
				failList.add(failDetailDto);
				continue;
			}
			if(!allianceBusiIds.contains(allianceBusiId)){
				allianceBusiIds.add(allianceBusiId);
				if(!waybillCodes.contains(waybillCode)&&!checkExist(waybillCode)) {
					//需要校验余额是否充足
					if(!baseMajorManager.allianceBusiMoneyEnough(allianceBusiId)){
						failDetailDto.setFailMessage("加盟商预付款余额不足");
						failList.add(failDetailDto);
						continue;
					}
				}
			}

		}
		if(failList.size()>0){
			result.setCode(BaseEntity.CODE_PARAM_ERROR);
			//剔除余额不足
			dto.getDatas().removeAll(failList);
		}

		return result;
	}


	private List<AllianceBusiDeliveryDetail> convertDto(AllianceBusiDeliveryDto dto){
		List<AllianceBusiDeliveryDetail> pojos = new ArrayList<>();
		for(AllianceBusiDeliveryDetailDto detailDto : dto.getDatas()){
			AllianceBusiDeliveryDetail pojo = new AllianceBusiDeliveryDetail();
			pojo.setOperateTime(new Date(detailDto.getOperateTimeMillis()));
			if(WaybillUtil.isWaybillCode(detailDto.getOpeCode())){
				pojo.setWaybillCode(detailDto.getOpeCode());
			}else{
				pojo.setWaybillCode(WaybillUtil.getWaybillCode(detailDto.getOpeCode()));
				pojo.setPackageCode(detailDto.getOpeCode());
			}
			pojo.setSysSource(dto.getOpeType());
			pojos.add(pojo);
		}

		return pojos;
	}

	/**
	 * 称重对象转换
	 * @param dto
	 * @return
	 */
	private List<Message> convertWeightOpeDto(AllianceBusiDeliveryDto dto){

		List<Message> messages = new ArrayList<>();
		Integer weightOpeType = Integer.valueOf(0); //揽收 4  派送5
		if(AllianceBusiOpeTypeEnum.SITE_RECEIVE.getCode() == dto.getOpeType() || AllianceBusiOpeTypeEnum.SORTING_RECEIVE.getCode() == dto.getOpeType()){
			weightOpeType = 4;
		}else if(AllianceBusiOpeTypeEnum.SITE_SEND.getCode() == dto.getOpeType() || AllianceBusiOpeTypeEnum.SORTING_SEND.getCode() == dto.getOpeType()){
			weightOpeType = 5;
		}
		for(AllianceBusiDeliveryDetailDto detailDto : dto.getDatas()){
			WeightOpeDto weightOpeDto = new WeightOpeDto();
			Message message = new Message();
			weightOpeDto.setOperateCode(detailDto.getOpeCode());
			if(WaybillUtil.isWaybillCode(detailDto.getOpeCode())){
				weightOpeDto.setCodeType(1);
			}else{
				weightOpeDto.setCodeType(2);
			}
			weightOpeDto.setOperateSite(dto.getOperatorSiteName());
			weightOpeDto.setOperateSiteId(dto.getOperatorSiteCode());
			weightOpeDto.setOperateUser(dto.getOperatorName());
			weightOpeDto.setOperateUserId(dto.getOperatorId());
			weightOpeDto.setOperateTime(detailDto.getOperateTimeMillis());
			weightOpeDto.setOpeType(weightOpeType);
			weightOpeDto.setVolume(detailDto.getVolume());
			weightOpeDto.setVolumeHeight(detailDto.getHeight());
			weightOpeDto.setVolumeLength(detailDto.getLength());
			weightOpeDto.setVolumeWidth(detailDto.getWidth());
			weightOpeDto.setWeight(detailDto.getWeight());

			message.setTopic(opeWeightProducer.getTopic());
			message.setText(JsonHelper.toJson(weightOpeDto));
			message.setBusinessId(weightOpeDto.getOperateCode());
			messages.add(message);
		}
		return messages;
	}


    /**
     * 交接称重对象转换
     * @param dto
     * @return
     */
    private List<Message> convertHandOverWeightDto(AllianceBusiDeliveryDto dto){

        List<Message> messages = new ArrayList<>();
        for(AllianceBusiDeliveryDetailDto detailDto : dto.getDatas()){
            HandOverWeightDto handOverWeightDto = new HandOverWeightDto();
            Message message = new Message();
            if(WaybillUtil.isWaybillCode(detailDto.getOpeCode())){
                handOverWeightDto.setWaybillCode(detailDto.getOpeCode());
            }else{
                handOverWeightDto.setWaybillCode(WaybillUtil.getWaybillCode(detailDto.getOpeCode()));
            }
            handOverWeightDto.setPackageCode(detailDto.getOpeCode());
            handOverWeightDto.setAgainOperatorId(dto.getOperatorId());
            handOverWeightDto.setAgainOperatorName(dto.getOperatorName());
            handOverWeightDto.setAgainOperatorTime(new Date(detailDto.getOperateTimeMillis()));
            if(detailDto.getVolume()!=null && detailDto.getVolume().compareTo(new Double(0)) > 0){
                handOverWeightDto.setAgainVolumn(detailDto.getVolume().toString());
            }else{
                handOverWeightDto.setAgainVolumn(detailDto.getLength().toString()+"*"+detailDto.getWidth().toString()+"*"+detailDto.getHeight().toString());
            }

            handOverWeightDto.setAgainWeight(detailDto.getWeight());
            handOverWeightDto.setOperatorSiteCode(dto.getOperatorSiteCode());



            message.setTopic(handOverWeightProducer.getTopic());
            message.setText(JsonHelper.toJson(handOverWeightDto));
            message.setBusinessId(detailDto.getOpeCode());
            messages.add(message);
        }
        return messages;
    }

	/**
	 * 称重
	 * @param dto
	 */
	private void weight(AllianceBusiDeliveryDto dto){

		opeWeightProducer.batchSendOnFailPersistent(convertWeightOpeDto(dto));


	}

	/**
	 * 转发交接称重流水
	 * @param dto
	 */
	private void handOverWeight(AllianceBusiDeliveryDto dto){

		handOverWeightProducer.batchSendOnFailPersistent(convertHandOverWeightDto(dto));


	}

	/**
	 * 交接
	 * @param dto
	 */
	private void handoversWaybillTrack(AllianceBusiDeliveryDto dto){
		Integer waybillStatusOpeType = Integer.valueOf(0);
		if(AllianceBusiOpeTypeEnum.SITE_RECEIVE.getCode() == dto.getOpeType() || AllianceBusiOpeTypeEnum.SORTING_RECEIVE.getCode() == dto.getOpeType()){
			waybillStatusOpeType = WaybillStatus.WAYBILL_TRACK_RECEIVE_HANDOVERS;
		}else if(AllianceBusiOpeTypeEnum.SITE_SEND.getCode() == dto.getOpeType() || AllianceBusiOpeTypeEnum.SORTING_SEND.getCode() == dto.getOpeType()){
			waybillStatusOpeType = WaybillStatus.WAYBILL_TRACK_SEND_HANDOVERS;
		}

		for(AllianceBusiDeliveryDetailDto detailDto : dto.getDatas()){
			Task tTask = new Task();
			tTask.setKeyword1(detailDto.getOpeCode());
			tTask.setCreateSiteCode(dto.getOperatorSiteCode());
			tTask.setCreateTime(new Date(detailDto.getOperateTimeMillis()));
			tTask.setKeyword2(String.valueOf(waybillStatusOpeType));
			tTask.setReceiveSiteCode(0);
			tTask.setType(Task.TASK_TYPE_WAYBILL_TRACK);
			tTask.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
			tTask.setSequenceName(Task.getSequenceName(Task.TABLE_NAME_POP));
			String ownSign = BusinessHelper.getOwnSign();
			tTask.setOwnSign(ownSign);

			WaybillStatus status=new WaybillStatus();
			status.setOperateType(waybillStatusOpeType);
			if(WaybillUtil.isWaybillCode(detailDto.getOpeCode())){
				status.setWaybillCode(detailDto.getOpeCode());
				status.setPackageCode(detailDto.getOpeCode());
			}else{
				status.setWaybillCode(WaybillUtil.getWaybillCode(detailDto.getOpeCode()));
				status.setPackageCode(detailDto.getOpeCode());
			}

			status.setOperateTime(new Date(detailDto.getOperateTimeMillis()));
			status.setOperator(dto.getOperatorName());
			status.setOperatorId(dto.getOperatorId());
			//status.setRemark(待定);
			status.setCreateSiteCode(dto.getOperatorSiteCode());
			status.setCreateSiteName(dto.getOperatorSiteName());
			tTask.setBody(JsonHelper.toJson(status));
			taskService.add(tTask, true);
		}

	}

	/**
	 * 根据运单号获取加盟商ID
	 * @param waybillCode
	 * @return
	 */
	private String getAllianceBusiId(String waybillCode){
		WChoice wChoice = new WChoice();
		wChoice.setQueryWaybillM(true);
		wChoice.setQueryWaybillC(true);
		wChoice.setQueryWaybillExtend(true);
		com.jd.etms.waybill.domain.BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,wChoice);
		if(baseEntity.getResultCode() == 1 && baseEntity.getData().getWaybill()!=null &&baseEntity.getData().getWaybill().getWaybillExt()!=null){
			if(BusinessUtil.isAllianceBusi(baseEntity.getData().getWaybill().getWaybillSign())){
				return baseEntity.getData().getWaybill().getWaybillExt().getPartnerId();
			}
		}else{
			logger.error("获取加盟商ID失败"+waybillCode+"|"+JsonHelper.toJson(baseEntity));
		}
		return null;
	}
}
