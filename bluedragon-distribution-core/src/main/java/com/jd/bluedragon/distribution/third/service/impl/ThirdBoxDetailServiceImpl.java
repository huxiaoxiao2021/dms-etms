package com.jd.bluedragon.distribution.third.service.impl;

import com.jd.bluedragon.distribution.economic.domain.EconomicNetException;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.third.domain.ThirdBoxDetail;
import com.jd.bluedragon.distribution.third.dao.ThirdBoxDetailDao;
import com.jd.bluedragon.distribution.third.service.ThirdBoxDetailService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @ClassName: ThirdBoxDetailServiceImpl
 * @Description: 三方装箱明细表--Service接口实现
 * @author wuyoude
 * @date 2020年01月07日 16:34:04
 *
 */
@Service("thirdBoxDetailService")
public class ThirdBoxDetailServiceImpl extends BaseService<ThirdBoxDetail> implements ThirdBoxDetailService {

	@Autowired
	@Qualifier("thirdBoxDetailDao")
	private ThirdBoxDetailDao thirdBoxDetailDao;

	@Override
	public Dao<ThirdBoxDetail> getDao() {
		return this.thirdBoxDetailDao;
	}


	/**
	 * 建立箱-包关系
	 *
	 * @param detail 箱-包关系
	 * @return 结果
	 */
	@Override
	public boolean sorting(ThirdBoxDetail detail) {
		return thirdBoxDetailDao.insert(detail);
	}

	/**
	 * 取消某一包裹的绑定关系
	 *
	 * @param detail 明细
	 * @return 结果
	 */
	@Override
	public boolean cancel(ThirdBoxDetail detail) {
		return thirdBoxDetailDao.cancel(detail);
	}

	/**
	 * 取消某一包裹的绑定关系 不关系某个站点 全部取消
	 *
	 * @param detail 明细
	 * @return 结果
	 */
	@Override
	public boolean cancelNoCareSite(ThirdBoxDetail detail) {
		if(detail == null){
			return true;
		}
		//限制索引 必须有箱号和运单号
		if(StringUtils.isBlank(detail.getWaybillCode()) && StringUtils.isBlank(detail.getPackageCode())){
			throw new EconomicNetException("必须存在运单号或者包裹号的某项，才允许取消");
		}
		//如果运单号为空则取包裹号中的的运单号
		if(StringUtils.isBlank(detail.getWaybillCode())){
			detail.setWaybillCode(WaybillUtil.getWaybillCode(detail.getPackageCode()));
		}

		return thirdBoxDetailDao.cancelNoCareSite(detail);
	}

	/**
	 * 查询箱子明细
	 *
	 * @param tenantCode 租户编码
	 * @param startSiteId 始发站点
	 * @param boxCode     箱号
	 * @return 结果集
	 */
	@Override
	public List<ThirdBoxDetail> queryByBoxCode(String tenantCode, Integer startSiteId, String boxCode) {
		List<ThirdBoxDetail> result = new ArrayList<>();
		Set<String> packageCodes = new HashSet<>();
		List<ThirdBoxDetail> datas = thirdBoxDetailDao.queryByBoxCode(tenantCode, startSiteId, boxCode);
		if(!CollectionUtils.isEmpty(datas)){
			//兼容上线数据过滤多余数据
			for(ThirdBoxDetail thirdBoxDetail : datas){
				if(packageCodes.contains(thirdBoxDetail.getPackageCode())){
					continue;
				}
				packageCodes.add(thirdBoxDetail.getPackageCode());
				result.add(thirdBoxDetail);
			}

		}
		return result;
	}

	/**
	 * 获取运单或包裹装箱数据
	 * 为了走索引查询包裹时也需要传入运单号
	 *
	 * @param tenantCode
	 * @param startSiteId
	 * @param waybillCode
	 * @param packageCode
	 * @return
	 */
	@Override
	public List<ThirdBoxDetail> queryByWaybillOrPackage(String tenantCode,String waybillCode, String packageCode) {
		List<ThirdBoxDetail> result = new ArrayList<>();
		Set<String> packageCodes = new HashSet<>();
		List<ThirdBoxDetail> datas = thirdBoxDetailDao.queryByWaybillOrPackage(tenantCode, waybillCode,packageCode);
		if(!CollectionUtils.isEmpty(datas)){
			//兼容上线数据过滤多余数据
			for(ThirdBoxDetail thirdBoxDetail : datas){
				if(packageCodes.contains(thirdBoxDetail.getPackageCode())){
					continue;
				}
				packageCodes.add(thirdBoxDetail.getPackageCode());
				result.add(thirdBoxDetail);
			}

		}
		return result;
	}

	/**
	 * 检查是否存在数据
	 *
	 * @param tenantCode
	 * @param startSiteId
	 * @param boxCode
	 * @return
	 */
	@Override
	public boolean isExist(String tenantCode, Integer startSiteId, String boxCode) {
		return !CollectionUtils.isEmpty(thirdBoxDetailDao.isExist(tenantCode, startSiteId, boxCode));
	}
}
