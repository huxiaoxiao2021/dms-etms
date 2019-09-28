package com.jd.bluedragon.distribution.collect.service.impl;


import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.collect.domain.*;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsAreaService;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsDetailService;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsPlaceTypeService;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.jddl.executor.function.scalar.filter.In;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.collect.dao.CollectGoodsPlaceDao;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsPlaceService;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 *
 * @ClassName: CollectGoodsPlaceServiceImpl
 * @Description: 集货位表--Service接口实现
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
@Service("collectGoodsPlaceService")
public class CollectGoodsPlaceServiceImpl extends BaseService<CollectGoodsPlace> implements CollectGoodsPlaceService {


	private final String ABNORMAL_CODE = "999";



	@Autowired
	@Qualifier("collectGoodsPlaceDao")
	private CollectGoodsPlaceDao collectGoodsPlaceDao;

	@Autowired
	@Qualifier("collectGoodsAreaService")
	private CollectGoodsAreaService collectGoodsAreaService;

    @Autowired
	private WaybillCommonService waybillCommonService;

    @Autowired
    private OperationLogService operationLogService;

	@Autowired
	@Qualifier("collectGoodsPlaceTypeService")
	private CollectGoodsPlaceTypeService collectGoodsPlaceTypeService;

	@Autowired
	private CollectGoodsDetailService collectGoodsDetailService;

	@Override
	public Dao<CollectGoodsPlace> getDao() {
		return this.collectGoodsPlaceDao;
	}

	@Override
	@Transactional(value = "main_undiv", propagation = Propagation.REQUIRED)
	public boolean saveAll(List<CollectGoodsPlace> collectGoodsPlaces) {
		if(collectGoodsPlaces.isEmpty()){
			return true;
		}

		//保存 集货区
		List<CollectGoodsArea> collectGoodsAreas = new ArrayList<>();
		List<String> collectGoodsAreaCodes = new ArrayList<>();
		List<CollectGoodsPlace> initAbnormalPlaces = new ArrayList<>();
		for(CollectGoodsPlace collectGoodsPlace : collectGoodsPlaces){
			String collectGoodsAreaCode  = collectGoodsPlace.getCollectGoodsAreaCode();
			if(collectGoodsAreaCodes.contains(collectGoodsAreaCode)){
				continue;
			}

			collectGoodsAreaCodes.add(collectGoodsAreaCode);
			CollectGoodsArea collectGoodsArea = new CollectGoodsArea();

			collectGoodsArea.setCollectGoodsAreaCode(collectGoodsAreaCode);
			collectGoodsArea.setCreateSiteCode(collectGoodsPlace.getCreateSiteCode());
			collectGoodsArea.setCreateSiteName(collectGoodsPlace.getCreateSiteName());
			collectGoodsArea.setCreateUser(collectGoodsPlace.getCreateUser());
			//检查是否已存在此值
			if(collectGoodsAreaService.findExistByAreaCode(collectGoodsArea)){
				throw new RuntimeException("保存集货区失败，已存在此集货区编码");
			}

			//每个区初始一个异常位
			CollectGoodsPlace abnormalPlace = new CollectGoodsPlace();
			BeanUtils.copyProperties(collectGoodsPlace,abnormalPlace);
			abnormalPlace.setCollectGoodsPlaceType(Integer.valueOf(CollectGoodsPlaceTypeEnum.BIG_TYPE_4.getCode()));
			abnormalPlace.setCollectGoodsPlaceCode(abnormalPlace.getCollectGoodsAreaCode()+ABNORMAL_CODE);
			initAbnormalPlaces.add(abnormalPlace);


			collectGoodsAreas.add(collectGoodsArea);
		}
		if(!collectGoodsAreas.isEmpty()){
			if(!collectGoodsAreaService.batchAdd(collectGoodsAreas)){
				throw new CollectException("保存集货区失败");
			}
		}

		//初始化集货类型
		CollectGoodsPlaceType queryParam = new CollectGoodsPlaceType();
		queryParam.setCreateSiteCode(collectGoodsPlaces.get(0).getCreateSiteCode());
		if(!collectGoodsPlaceTypeService.findExistByCreateSiteCode(queryParam)){
			//不存在才去初始化
			CollectGoodsPlaceType initParam = new CollectGoodsPlaceType();
			initParam.setCreateSiteCode(collectGoodsPlaces.get(0).getCreateSiteCode());
			initParam.setCreateSiteName(collectGoodsPlaces.get(0).getCreateSiteName());
			initParam.setCreateUser(collectGoodsPlaces.get(0).getCreateUser());
			if(!collectGoodsPlaceTypeService.initPlaceType(initParam)){
				throw new CollectException("初始集货位类型失败");
			}
		}
		//保存集货位
		collectGoodsPlaces.addAll(initAbnormalPlaces);
		if(!collectGoodsPlaceDao.batchInsert(collectGoodsPlaces)){
			throw new CollectException("保存集货位失败");
		}

		return true;
	}

	@Override
	@Transactional(value = "main_undiv", propagation = Propagation.REQUIRED)
	public boolean savePalceType(CollectGoodsPlaceCondition condition) {
		//删除原有配置
		CollectGoodsPlaceType deleteParam = new CollectGoodsPlaceType();
		deleteParam.setCreateSiteCode(condition.getCreateSiteCode());
		collectGoodsPlaceTypeService.deleteByCreateSiteCode(deleteParam);

		//新增
		return collectGoodsPlaceTypeService.savePalceTypeByCollectGoodsPlace(condition);

	}


    /**
	 * 校验 集货位是否是空闲
	 *
	 * @param collectGoodsPlace
	 * @return
	 */
	@Override
	public boolean checkPalceIsEmpty(CollectGoodsPlace collectGoodsPlace) {

		return collectGoodsPlaceDao.findPlaceNotEmpty(collectGoodsPlace)==0;
	}

    @Override
    public boolean updateStatus(CollectGoodsPlace collectGoodsPlace) {
        return collectGoodsPlaceDao.updateStatus(collectGoodsPlace);
    }

    /**
     * 获得推荐货位
     *
     * 	小件包裹---原货位号>小单>中单>大单>满格异常提示
       	中件包裹---原货位号>中单>大单>小单>满格异常提示
       	大件包裹---原货位号>大单>中单>满格异常提示
     *
     * @param packageCode
     * @param createSiteCode
     * @return
     */
    @Override
    public CollectGoodsPlace recommendPlace(String packageCode,String areaCode, Integer createSiteCode,InvokeResult<CollectGoodsDTO> result) {
        //包裹总数
        int packCount = waybillCommonService.getPackNum(WaybillUtil.getWaybillCode(packageCode)).getData();
        int maxScanWaybillNum = 0;
        //此运单是否已集齐
        boolean isCollectAll = false;
        boolean isScanWaybill = false;

        //所属货位类型
        CollectGoodsPlaceTypeEnum belongsType = null;
        CollectGoodsPlace belongsPlace = null;
        //获取当前站点配置的站点类型
        CollectGoodsPlaceType queryPlaceTypeParam = new CollectGoodsPlaceType();
        queryPlaceTypeParam.setCreateSiteCode(createSiteCode);
        List<CollectGoodsPlaceType> allPlaceTypes = collectGoodsPlaceTypeService.findByCreateSiteCode(queryPlaceTypeParam);

        //查找此包裹对应的运单是否已上过某个货位
        CollectGoodsDetail queryParam = new CollectGoodsDetail();
        queryParam.setWaybillCode(WaybillUtil.getWaybillCode(packageCode));
        queryParam.setCreateSiteCode(createSiteCode);
        List<CollectGoodsDetailCondition> ScanWaybillList = collectGoodsDetailService.findScanWaybill(queryParam);
        if(ScanWaybillList!=null && !ScanWaybillList.isEmpty()){

            String placeCode = ScanWaybillList.get(0).getCollectGoodsPlaceCode();
            int scanPackCount = ScanWaybillList.get(0).getScanPackageCount();
            if(packCount == scanPackCount+1){
                //说明放完此包裹后改运单已集齐。提示自动释放货位
                isCollectAll = true;
                result.setCode(CollectGoodsCommonServiceImpl.COLLECT_ALL_TIP_CODE);
                result.setMessage("当前运单共"+packCount+"件，已集齐");
            }else{
                result.setMessage("当前运单共"+packCount+"件，已扫"+(ScanWaybillList.get(0).getScanPackageCount()+1)+"件");
            }
            //记录已扫标识
            isScanWaybill = true;
            CollectGoodsPlace queryPlaceParam = new CollectGoodsPlace();
            queryPlaceParam.setCreateSiteCode(createSiteCode);
            queryPlaceParam.setCollectGoodsPlaceCode(placeCode);
            belongsPlace = collectGoodsPlaceDao.findPlaceByCode(queryPlaceParam);
        }else{
            //此包裹未上过集货位， 则按照规定逻辑匹配

            //获取所选集货区所有的集货位 和 货位类型配置
            CollectGoodsPlace queryPlaceParam = new CollectGoodsPlace();
            queryPlaceParam.setCreateSiteCode(createSiteCode);
            queryPlaceParam.setCollectGoodsAreaCode(areaCode);
            List<CollectGoodsPlace> allPlaces = collectGoodsPlaceDao.findPlaceByAreaCode(queryPlaceParam);
            if(allPlaces==null || allPlaces.isEmpty()){
                throw new CollectException("集货区下未查询到集货位");
            }


            //根据包裹总数判断 货位类型
            for(CollectGoodsPlaceType collectGoodsPlaceType : allPlaceTypes){
                if(packCount >= collectGoodsPlaceType.getMinPackNum() && packCount <= collectGoodsPlaceType.getMaxPackNum()){
                    belongsType = CollectGoodsPlaceTypeEnum.getEnumByKey(collectGoodsPlaceType.getCollectGoodsPlaceType().toString());
                }
            }
            if(belongsType==null){
                throw new CollectException("未匹配到对应货位类型");
            }
            //返回按匹配规则顺序货位
            List<CollectGoodsPlace> orderPlaces = getOrderMappingPlace(belongsType,allPlaces);
            if(orderPlaces==null || orderPlaces.isEmpty()){
                throw new CollectException("未匹配到对应货位,请先维护货位");
            }

            for(CollectGoodsPlace collectGoodsPlace : orderPlaces){
                //优先判断货位状态 是否已满
                if(CollectGoodsPlaceStatusEnum.FULL_2.getCode().equals(collectGoodsPlace.getCollectGoodsPlaceStatus().toString())){
                    continue;
                }
                belongsPlace = collectGoodsPlace;
                break;
            }
            if(belongsPlace == null){
                return null;
            }
        }
        //最后获取最大扫描运单数
        for(CollectGoodsPlaceType collectGoodsPlaceType : allPlaceTypes){
            if(collectGoodsPlaceType.getCollectGoodsPlaceType().equals(belongsPlace.getCollectGoodsPlaceType())){
                maxScanWaybillNum = collectGoodsPlaceType.getMaxWaybillNum();
            }
        }

        //更新推荐储位状态
        //变更货位状态
        //当前货位已扫运单数
        int nowPlaceScanWaybill;
        if(isScanWaybill){
            //如果该单已扫过 则 数量不去+1
            nowPlaceScanWaybill = belongsPlace.getScanWaybillNum();
        }else{
            nowPlaceScanWaybill = belongsPlace.getScanWaybillNum() + 1;
        }
        if(isCollectAll){
            //已集齐
            nowPlaceScanWaybill = belongsPlace.getScanWaybillNum()-1;
        }

        if(nowPlaceScanWaybill == 0){
            //重置空闲状态
            belongsPlace.setCollectGoodsPlaceStatus(Integer.valueOf(CollectGoodsPlaceStatusEnum.FREE_0.getCode()));
        }else if(maxScanWaybillNum > nowPlaceScanWaybill){
            belongsPlace.setCollectGoodsPlaceStatus(Integer.valueOf(CollectGoodsPlaceStatusEnum.NOT_FREE_1.getCode()));
        }else{
            belongsPlace.setCollectGoodsPlaceStatus(Integer.valueOf(CollectGoodsPlaceStatusEnum.FULL_2.getCode()));
        }
        belongsPlace.setScanWaybillNum(nowPlaceScanWaybill);
        if(!updateStatus(belongsPlace)){
            throw new CollectException("变更货位状态失败！");
        }

        return belongsPlace;
    }

    /**
     * 获取顺序货位
     * @param belongsType
     * @param allPlaces
     * @return
     */
    private List<CollectGoodsPlace> getOrderMappingPlace(CollectGoodsPlaceTypeEnum belongsType,List<CollectGoodsPlace> allPlaces){
        List<CollectGoodsPlace> result = new ArrayList<>();

        //将 所有集货位按类型转换成MAP 方便计算推荐货位使用
        Map<CollectGoodsPlaceTypeEnum,List<CollectGoodsPlace>> placeMap = new HashMap<>();
        for(CollectGoodsPlace collectGoodsPlace : allPlaces){
            CollectGoodsPlaceTypeEnum mapKey =
                    CollectGoodsPlaceTypeEnum.getEnumByKey(collectGoodsPlace.getCollectGoodsPlaceType().toString());
            if(!placeMap.containsKey(mapKey)){
                placeMap.put(mapKey,
                        new ArrayList<CollectGoodsPlace>());
            }
            placeMap.get(mapKey).add(collectGoodsPlace);
        }
        //按规则获取集货位
        //	小件包裹---小单>中单>大单
        //	中件包裹---中单>大单>小单
        //	大件包裹---大单>中单
        if(belongsType.equals(CollectGoodsPlaceTypeEnum.SMALL_TYPE_1)){
            if(placeMap.get(CollectGoodsPlaceTypeEnum.SMALL_TYPE_1)!=null){
                result.addAll(placeMap.get(CollectGoodsPlaceTypeEnum.SMALL_TYPE_1));
            }
            if(placeMap.get(CollectGoodsPlaceTypeEnum.MIDDLE_TYPE_2)!=null){
                result.addAll(placeMap.get(CollectGoodsPlaceTypeEnum.MIDDLE_TYPE_2));
            }
            if(placeMap.get(CollectGoodsPlaceTypeEnum.BIG_TYPE_3)!=null){
                result.addAll(placeMap.get(CollectGoodsPlaceTypeEnum.BIG_TYPE_3));
            }
        }else if(belongsType.equals(CollectGoodsPlaceTypeEnum.MIDDLE_TYPE_2)){
            if(placeMap.get(CollectGoodsPlaceTypeEnum.MIDDLE_TYPE_2)!=null){
                result.addAll(placeMap.get(CollectGoodsPlaceTypeEnum.MIDDLE_TYPE_2));
            }
            if(placeMap.get(CollectGoodsPlaceTypeEnum.BIG_TYPE_3)!=null){
                result.addAll(placeMap.get(CollectGoodsPlaceTypeEnum.BIG_TYPE_3));
            }
            if(placeMap.get(CollectGoodsPlaceTypeEnum.SMALL_TYPE_1)!=null){
                result.addAll(placeMap.get(CollectGoodsPlaceTypeEnum.SMALL_TYPE_1));
            }
        }else if(belongsType.equals(CollectGoodsPlaceTypeEnum.BIG_TYPE_3)){
            if(placeMap.get(CollectGoodsPlaceTypeEnum.BIG_TYPE_3)!=null){
                result.addAll(placeMap.get(CollectGoodsPlaceTypeEnum.BIG_TYPE_3));
            }
            if(placeMap.get(CollectGoodsPlaceTypeEnum.MIDDLE_TYPE_2)!=null){
                result.addAll(placeMap.get(CollectGoodsPlaceTypeEnum.MIDDLE_TYPE_2));
            }
        }

        return result;
    }




    /**
     * 获取异常货位
     *
     * @param createSiteCode
     * @param areaCode
     * @return
     */
    @Override
    public CollectGoodsPlace findAbnormalPlace(Integer createSiteCode, String areaCode) {
        CollectGoodsPlace param = new CollectGoodsPlace();
        param.setCreateSiteCode(createSiteCode);
        param.setCollectGoodsAreaCode(areaCode);
        param.setCollectGoodsPlaceType(Integer.valueOf(CollectGoodsPlaceTypeEnum.BIG_TYPE_4.getCode()));
        return collectGoodsPlaceDao.findAbnormalPlace(param);
    }

    /**
     * 获取货位
     *
     * @param createSiteCode
     * @param placeCode
     * @return
     */
    @Override
    public CollectGoodsPlace findPlaceByCode(Integer createSiteCode, String placeCode) {
        CollectGoodsPlace param = new CollectGoodsPlace();
        param.setCreateSiteCode(createSiteCode);
        param.setCollectGoodsPlaceCode(placeCode);
        return collectGoodsPlaceDao.findPlaceByCode(param);
    }

    @Override
    public boolean deleteByAreaCode(List<String> codes) {
        return collectGoodsPlaceDao.deleteByAreaCode(codes) > 0;
    }

    @Override
    public List<CollectGoodsPlace> findPlaceByAreaCode(CollectGoodsPlace collectGoodsPlace) {
        return collectGoodsPlaceDao.findPlaceByAreaCode(collectGoodsPlace);
    }


}
