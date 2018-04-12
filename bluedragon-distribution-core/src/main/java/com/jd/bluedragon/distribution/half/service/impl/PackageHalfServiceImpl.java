package com.jd.bluedragon.distribution.half.service.impl;

import com.jd.bluedragon.core.base.LDOPManager;
import com.jd.bluedragon.distribution.half.dao.PackageHalfDetailDao;
import com.jd.bluedragon.distribution.half.domain.PackageHalfDetail;
import com.jd.bluedragon.distribution.half.domain.PackageHalfVO;
import com.jd.bluedragon.distribution.half.service.PackageHalfRedeliveryService;
import com.jd.bluedragon.distribution.transport.domain.ArBookingSpace;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.distribution.waybill.service.WaybillStatusService;
import com.jd.ldop.center.api.reverse.dto.WaybillReverseDTO;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.half.domain.PackageHalf;
import com.jd.bluedragon.distribution.half.dao.PackageHalfDao;
import com.jd.bluedragon.distribution.half.service.PackageHalfService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: PackageHalfServiceImpl
 * @Description: 包裹半收操作--Service接口实现
 * @author wuyoude
 * @date 2018年03月20日 17:33:21
 *
 */
@Service("packageHalfService")
public class PackageHalfServiceImpl extends BaseService<PackageHalf> implements PackageHalfService {

	@Autowired
	@Qualifier("packageHalfDao")
	private PackageHalfDao packageHalfDao;

	@Autowired
	@Qualifier("packageHalfDetailDao")
	private PackageHalfDetailDao packageHalfDetailDao;

	@Autowired
	@Qualifier("waybillStatusService")
	private WaybillStatusService waybillStatusService;

	@Autowired
	@Qualifier("ldopManager")
	private LDOPManager ldopManager;

	@Override
	public Dao<PackageHalf> getDao() {
		return this.packageHalfDao;
	}

	@Autowired
	public PackageHalfRedeliveryService packageHalfRedeliveryService;

	@Override
	public boolean save(PackageHalf packageHalf, List<PackageHalfDetail> packageHalfDetails,Integer waybillOpeType, Integer OperatorId, String OperatorName, Date operateTime ,Integer packageCount,Integer orgId,Integer createSiteCode) {

		getDao().insert(packageHalf);

		//批量保存 半收操作明细
		List<PackageHalfDetail> bufferList = new ArrayList<PackageHalfDetail>();
		for(PackageHalfDetail packageHalfDetail :packageHalfDetails){
			bufferList.add(packageHalfDetail);
			if(bufferList.size()==10){
				if(packageHalfDetailDao.batchInsert(bufferList)){
					bufferList.clear();
				}else{
					return false;
				}
			}
		}
		if(bufferList.size() > 0){
			if(!packageHalfDetailDao.batchInsert(bufferList)){
				return false;
			}
		}


		boolean isNeedToLDOP = false;
		WaybillReverseDTO waybillReverseDTO = null;
		//拒收触发换单
		if(waybillOpeType.equals(WaybillStatus.WAYBILL_OPE_TYPE_REJECT)){
			//整单拒收
			 waybillReverseDTO  = makeWaybillReverseDTO(packageHalf.getWaybillCode(),OperatorId,OperatorName,operateTime,packageCount,orgId,createSiteCode,true);
			isNeedToLDOP = true;
		}else if(waybillOpeType.equals(WaybillStatus.WAYBILL_OPE_TYPE_HALF_SIGNIN)){
			//包裹拒收
			 waybillReverseDTO = makeWaybillReverseDTO(packageHalf.getWaybillCode(),OperatorId,OperatorName,operateTime,packageCount,orgId,createSiteCode,false);
			isNeedToLDOP = true;

		}
		if(isNeedToLDOP && waybillReverseDTO!=null){
			if(!ldopManager.waybillReverse(waybillReverseDTO)){
				return false;
			}
		}

		//包裹半收时 同步运单状态
		boolean waybillResult = waybillStatusService.batchUpdateWaybillPartByOperateType( packageHalf,packageHalfDetails, waybillOpeType,  OperatorId,  OperatorName, operateTime);
		if(!waybillResult){
			return false;
		}

		//同步包裹半收协商再投状态
		packageHalfRedeliveryService.updateDealStateByWaybillCode(packageHalf.getWaybillCode(),OperatorId,"",OperatorName);


		return true;
	}

	/**
	 * 组装外单换单入参
	 * @param waybillCode 运单号
	 * @param OperatorId 操作人ID
	 * @param OperatorName 操作人
	 * @param operateTime 操作时间
	 * @param packageCount 拒收包裹数量
	 * @param isTotal 是否是整单拒收
	 * @return
	 */
	private WaybillReverseDTO makeWaybillReverseDTO(String waybillCode,Integer OperatorId, String OperatorName, Date operateTime ,Integer packageCount,Integer orgId,Integer createSiteCode,boolean isTotal){
		WaybillReverseDTO waybillReverseDTO = new WaybillReverseDTO();
		waybillReverseDTO.setSource(2); //分拣中心
		if(isTotal){
			waybillReverseDTO.setReverseType(1);// 整单拒收
		}else{
			waybillReverseDTO.setReverseType(2);// 包裹拒收
		}

		waybillReverseDTO.setWaybillCode(waybillCode);
		waybillReverseDTO.setOperateUserId(OperatorId);
		waybillReverseDTO.setOperateUser(OperatorName);
		waybillReverseDTO.setOrgId(orgId);
		waybillReverseDTO.setSortCenterId(createSiteCode);
		waybillReverseDTO.setOperateTime(operateTime);
		waybillReverseDTO.setReturnType(0);//默认
		waybillReverseDTO.setPackageCount(packageCount);

		return waybillReverseDTO;
	}

	/**
	 * 保存失败的时候清除操作记录。。
	 * 保存数据消息量太大。容易占用过多缓存区。不启用事务
	 * @param waybillCode
	 */
	@Override
	public void deleteOfSaveFail(String waybillCode){
		//packageHalfDao.deleteOfSaveFail(waybillCode); 操作日志保留
		packageHalfDetailDao.deleteOfSaveFail(waybillCode);
	}
}
