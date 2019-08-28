package com.jd.bluedragon.distribution.collect.service.impl;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlaceCondition;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlaceTypeEnum;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlaceType;
import com.jd.bluedragon.distribution.collect.dao.CollectGoodsPlaceTypeDao;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsPlaceTypeService;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @ClassName: CollectGoodsPlaceTypeServiceImpl
 * @Description: 集货位类型表--Service接口实现
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
@Service("collectGoodsPlaceTypeService")
public class CollectGoodsPlaceTypeServiceImpl extends BaseService<CollectGoodsPlaceType> implements CollectGoodsPlaceTypeService {


	private static final int SMALL_MIN_PACK_NUM = 1;
	private static final int SMALL_MAX_PACK_NUM = 10;
	private static final int MIDDLE_MIN_PACK_NUM = 11;
	private static final int MIDDLE_MAX_PACK_NUM = 100;
	private static final int BIG_MIN_PACK_NUM = 101;
	private static final int BIG_MAX_PACK_NUM = 20000;
	private static final int MAX_WAYBILL_NUM = 1;




	private static final int DEFALULT_NUM = 0;

	@Autowired
	@Qualifier("collectGoodsPlaceTypeDao")
	private CollectGoodsPlaceTypeDao collectGoodsPlaceTypeDao;

	@Override
	public Dao<CollectGoodsPlaceType> getDao() {
		return this.collectGoodsPlaceTypeDao;
	}

	@Override
	public boolean findExistByCreateSiteCode(CollectGoodsPlaceType e) {
		return collectGoodsPlaceTypeDao.findExistByCreateSiteCode(e)>0?true:false;
	}

	/**
	 * 初始集货类型
	 * @param e
	 * @return
	 */
	@Override
	public boolean initPlaceType(CollectGoodsPlaceType e) {
		List<CollectGoodsPlaceType> collectGoodsPlaceTypes = convert(e,SMALL_MIN_PACK_NUM,SMALL_MAX_PACK_NUM,MAX_WAYBILL_NUM,
				MIDDLE_MIN_PACK_NUM,MIDDLE_MAX_PACK_NUM,MAX_WAYBILL_NUM,BIG_MIN_PACK_NUM,BIG_MAX_PACK_NUM,MAX_WAYBILL_NUM);

		return getDao().batchInsert(collectGoodsPlaceTypes);
	}

	@Override
	public List<CollectGoodsPlaceType> findByCreateSiteCode(CollectGoodsPlaceType e) {
		return collectGoodsPlaceTypeDao.findByCreateSiteCode(e);
	}

	@Override
	public boolean deleteByCreateSiteCode(CollectGoodsPlaceType e) {

		return collectGoodsPlaceTypeDao.deleteByCreateSiteCode(e)>0;
	}

	@Override
	public List<CollectGoodsPlaceType> convert(CollectGoodsPlaceType placeType ,int smallPackMinNum, int smallPackMaxNum,
											   int smallWaybillMaxNum, int middlePackMinNum, int middlePackMaxNum, int middleWaybillMaxNum,
											   int bigPackMinNum, int bigPackMaxNum, int bigWaybillMaxNum) {

		List<CollectGoodsPlaceType> collectGoodsPlaceTypes = new ArrayList<>();
		for(CollectGoodsPlaceTypeEnum collectGoodsPlaceTypeEnum : CollectGoodsPlaceTypeEnum.values()){
			CollectGoodsPlaceType collectGoodsPlaceType = new CollectGoodsPlaceType();
			collectGoodsPlaceType.setCreateSiteCode(placeType.getCreateSiteCode());
			collectGoodsPlaceType.setCreateSiteName(placeType.getCreateSiteName());
			collectGoodsPlaceType.setCreateUser(placeType.getCreateUser());
			if(collectGoodsPlaceTypeEnum.equals(CollectGoodsPlaceTypeEnum.SMALL_TYPE_1)){
				collectGoodsPlaceType.setMaxPackNum(smallPackMaxNum);
				collectGoodsPlaceType.setMinPackNum(smallPackMinNum);
				collectGoodsPlaceType.setMaxWaybillNum(smallWaybillMaxNum);
				collectGoodsPlaceType.setCollectGoodsPlaceType(Integer.valueOf(CollectGoodsPlaceTypeEnum.SMALL_TYPE_1.getCode()));
			}else if(collectGoodsPlaceTypeEnum.equals(CollectGoodsPlaceTypeEnum.MIDDLE_TYPE_2)){
				collectGoodsPlaceType.setMaxPackNum(middlePackMaxNum);
				collectGoodsPlaceType.setMinPackNum(middlePackMinNum);
				collectGoodsPlaceType.setMaxWaybillNum(middleWaybillMaxNum);
				collectGoodsPlaceType.setCollectGoodsPlaceType(Integer.valueOf(CollectGoodsPlaceTypeEnum.MIDDLE_TYPE_2.getCode()));

			}else if(collectGoodsPlaceTypeEnum.equals(CollectGoodsPlaceTypeEnum.BIG_TYPE_3)){
				collectGoodsPlaceType.setMaxPackNum(bigPackMaxNum);
				collectGoodsPlaceType.setMinPackNum(bigPackMinNum);
				collectGoodsPlaceType.setMaxWaybillNum(bigWaybillMaxNum);
				collectGoodsPlaceType.setCollectGoodsPlaceType(Integer.valueOf(CollectGoodsPlaceTypeEnum.BIG_TYPE_3.getCode()));

			}else if(collectGoodsPlaceTypeEnum.equals(CollectGoodsPlaceTypeEnum.BIG_TYPE_4)){
				collectGoodsPlaceType.setMaxPackNum(DEFALULT_NUM);
				collectGoodsPlaceType.setMinPackNum(DEFALULT_NUM);
				collectGoodsPlaceType.setMaxWaybillNum(DEFALULT_NUM);
				collectGoodsPlaceType.setCollectGoodsPlaceType(Integer.valueOf(CollectGoodsPlaceTypeEnum.BIG_TYPE_4.getCode()));

			}

			collectGoodsPlaceTypes.add(collectGoodsPlaceType);
		}

		return collectGoodsPlaceTypes;
	}

	@Override
	public boolean savePalceTypeByCollectGoodsPlace(CollectGoodsPlaceCondition condition) {

		CollectGoodsPlaceType placeType = new CollectGoodsPlaceType();
		placeType.setCreateSiteCode(condition.getCreateSiteCode());
		placeType.setCreateSiteName(condition.getCreateSiteName());
		placeType.setCreateUser(condition.getCreateUser());


		List<CollectGoodsPlaceType> collectGoodsPlaceTypes = convert(placeType,SMALL_MIN_PACK_NUM,condition.getSmallPackMaxNum(),condition.getSmallWaybillMaxNum(),
				condition.getSmallPackMaxNum()+1,condition.getMiddlePackMaxNum(),condition.getMiddleWaybillMaxNum()
				,condition.getMiddlePackMaxNum()+1,BIG_MAX_PACK_NUM,condition.getBigWaybillMaxNum());

		return getDao().batchInsert(collectGoodsPlaceTypes);

	}
}
