package com.jd.bluedragon.distribution.collect.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDetailCondition;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlaceStatusEnum;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlaceTypeEnum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.common.util.StringUtils;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDetail;
import com.jd.bluedragon.distribution.collect.dao.CollectGoodsDetailDao;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsDetailService;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @ClassName: CollectGoodsDetailServiceImpl
 * @Description: --Service接口实现
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
@Service("collectGoodsDetailService")
public class CollectGoodsDetailServiceImpl extends BaseService<CollectGoodsDetail> implements CollectGoodsDetailService {

	@Autowired
	@Qualifier("collectGoodsDetailDao")
	private CollectGoodsDetailDao collectGoodsDetailDao;

	@Override
	public Dao<CollectGoodsDetail> getDao() {
		return this.collectGoodsDetailDao;
	}


	/**
	 * 转移集货位
	 *  包裹 转移整个运单
	 *
	 *  货位 转移整个货位
	 * @param sourcePlaceCode
	 * @param targetPlaceCode
	 * @param createSiteCode
	 * @param packageCode
	 * @return
	 */
	@Override
	public boolean transfer(String sourcePlaceCode, String targetPlaceCode, Integer targetPlaceType,Integer createSiteCode, String packageCode) {

		String waybillCode = null;
		if(StringUtils.isNotBlank(packageCode)){
			waybillCode = WaybillUtil.getWaybillCode(packageCode);
		}
		return collectGoodsDetailDao.transfer(sourcePlaceCode,targetPlaceCode,targetPlaceType,createSiteCode,waybillCode);
	}

	/**
	 * 清空数据（物理删除，防止表数据过多）
	 *
	 * @param collectGoodsDetail
	 * @return
	 */
	@Override
	public boolean clean(CollectGoodsDetail collectGoodsDetail) {
		return collectGoodsDetailDao.deleteByCollectGoodsDetail(collectGoodsDetail)>0;
	}

	/**
	 * 根据条件获取数据 不分页
	 *
	 * @param collectGoodsDetail
	 * @return
	 */
	@Override
	public List<CollectGoodsDetail> findNoPage(CollectGoodsDetail collectGoodsDetail) {
		return null;
	}

	/**
	 * 查找已集货数据
	 * 按运单维度返回
	 *
	 * @param collectGoodsDetail
	 * @return
	 */
	@Override
	public List<CollectGoodsDetailCondition> findScanWaybill(CollectGoodsDetail collectGoodsDetail) {
		return collectGoodsDetailDao.findSacnWaybill(collectGoodsDetail);
	}

	/**
	 * 查找已集货数据
	 *
	 * @param collectGoodsDetailCondition@return
	 */
	@Override
	public List<CollectGoodsDetail> queryByCondition(CollectGoodsDetailCondition collectGoodsDetailCondition) {
		return collectGoodsDetailDao.queryByCondition(collectGoodsDetailCondition);
	}

	@Override
	public boolean checkExist(String pakcageCode,String areaCode,String placeCode, Integer createSiteCode) {

		CollectGoodsDetail collectGoodsDetail = new CollectGoodsDetail();
		collectGoodsDetail.setCreateSiteCode(createSiteCode);
		collectGoodsDetail.setPackageCode(pakcageCode);
		collectGoodsDetail.setCollectGoodsAreaCode(areaCode);
		collectGoodsDetail.setCollectGoodsPlaceCode(placeCode);
		return collectGoodsDetailDao.findCount(collectGoodsDetail) > 0;
	}

	/**
	 * 导出记录
	 *
	 * @param collectGoodsDetailCondition
	 * @return
	 */
	@Override
	public List<List<Object>> getExportData(CollectGoodsDetailCondition collectGoodsDetailCondition) {
		List<List<Object>> resList = new ArrayList<List<Object>>();

		List<Object> heads = new ArrayList<Object>();

		heads.add("集货区");
		heads.add("货位类型");
		heads.add("集货位");

		heads.add("运单号");
		heads.add("总包裹数");
		heads.add("实际扫描包裹数");
		heads.add("集货时间");
		heads.add("集货人");

		resList.add(heads);


		collectGoodsDetailCondition.setLimit(5000);
		PagerResult<CollectGoodsDetail> queryByPagerCondition = this.queryByPagerCondition(collectGoodsDetailCondition);

		List<CollectGoodsDetail> rows = queryByPagerCondition.getRows();

		for(CollectGoodsDetail row : rows){
			List<Object> body = new ArrayList<Object>();

			body.add(row.getCollectGoodsAreaCode());
			body.add(row.getCollectGoodsPlaceType()==null?"": CollectGoodsPlaceTypeEnum.getEnumByKey(row.getCollectGoodsPlaceType().toString()).getName());
			body.add(row.getCollectGoodsPlaceCode());
			//body.add(row.getCollectGoodsPlaceStatus()==null?"": CollectGoodsPlaceStatusEnum.getEnumByKey(row.getCollectGoodsPlaceStatus().toString()).getName());
			body.add(row.getWaybillCode());
			body.add(row.getPackageCount());
			body.add(row.getScanPackageCount());
			body.add(DateHelper.formatDate(row.getCreateTime(), Constants.DATE_TIME_FORMAT));
			body.add(row.getCreateUser());

			resList.add(body);
		}

		return resList;
	}

	@Override
	public CollectGoodsDetail findCollectGoodsDetailByPackageCode(String packageCode){
		return collectGoodsDetailDao.findCollectGoodsDetailByPackageCode(packageCode);
	}

}
