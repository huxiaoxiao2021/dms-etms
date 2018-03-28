package com.jd.bluedragon.distribution.half.service.impl;

import com.jd.bluedragon.distribution.half.dao.PackageHalfDetailDao;
import com.jd.bluedragon.distribution.half.domain.PackageHalfDetail;
import com.jd.bluedragon.distribution.half.domain.PackageHalfVO;
import com.jd.bluedragon.distribution.transport.domain.ArBookingSpace;
import com.jd.bluedragon.distribution.waybill.service.WaybillStatusService;
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

	@Override
	public Dao<PackageHalf> getDao() {
		return this.packageHalfDao;
	}

	@Override
	public boolean save(PackageHalf packageHalf, List<PackageHalfDetail> packageHalfDetails,Integer waybillOpeType, Integer OperatorId, String OperatorName, Date operateTime) {

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
		//包裹半收时 同步运单状态
		waybillStatusService.batchUpdateWaybillPartByOperateType( packageHalf,packageHalfDetails, waybillOpeType,  OperatorId,  OperatorName, operateTime);
		//拒收触发换单


		return true;
	}
}
