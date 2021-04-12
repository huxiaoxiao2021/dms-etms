package com.jd.bluedragon.distribution.collect.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDetailCondition;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDetailExportDto;
import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlaceTypeEnum;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.common.util.StringUtils;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDetail;
import com.jd.bluedragon.distribution.collect.dao.CollectGoodsDetailDao;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsDetailService;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

	@Autowired
	private ExportConcurrencyLimitService exportConcurrencyLimitService;

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
	 * @param bufferedWriter
	 * @return
	 */
	@Override
	public void getExportData(CollectGoodsDetailCondition collectGoodsDetailCondition, BufferedWriter bufferedWriter) {
		try {
			// 报表头
			Map<String, String> headerMap = getHeaderMap();
			//设置最大导出数量
			Integer MaxSize  =  exportConcurrencyLimitService.uccSpotCheckMaxSize();
			//设置单次导出数量
			collectGoodsDetailCondition.setLimit(exportConcurrencyLimitService.getOneQuerySizeLimit());
			CsvExporterUtils.writeTitleOfCsv(headerMap, bufferedWriter, headerMap.values().size());
			int queryTotal = 0;
			int index = 1;
			while (index++ <= 100){
				collectGoodsDetailCondition.setOffset((index-1) * exportConcurrencyLimitService.getOneQuerySizeLimit());
				PagerResult<CollectGoodsDetail> result = queryByPagerCondition(collectGoodsDetailCondition);
				if(CollectionUtils.isEmpty(result.getRows())){
					break;
				}
				List<CollectGoodsDetailExportDto>  dataList =  transForm(result.getRows());
				// 输出至csv文件中
				CsvExporterUtils.writeCsvByPage(bufferedWriter, headerMap, dataList);
				// 限制导出数量
				queryTotal += dataList.size();
				if(queryTotal > MaxSize ){
					break;
				}
			}
		}catch (Exception e){
			log.error("集货报表 export error",e);
		}
	}

	private List<CollectGoodsDetailExportDto> transForm(List<CollectGoodsDetail> list) {
		List<CollectGoodsDetailExportDto> dataList = new ArrayList<>();
		for(CollectGoodsDetail row : list){
			CollectGoodsDetailExportDto body = new CollectGoodsDetailExportDto();
			body.setCollectGoodsAreaCode(row.getCollectGoodsAreaCode());
			body.setCollectGoodsPlaceType(row.getCollectGoodsPlaceType()==null?"": CollectGoodsPlaceTypeEnum.getEnumByKey(row.getCollectGoodsPlaceType().toString()).getName());
			body.setCollectGoodsPlaceCode(row.getCollectGoodsPlaceCode());
			body.setWaybillCode(row.getWaybillCode());
			body.setPackageCount(row.getPackageCount());
			body.setScanPackageCount(row.getScanPackageCount());
			body.setCreateTime(DateHelper.formatDate(row.getCreateTime(), Constants.DATE_TIME_FORMAT));
			body.setCreateUser(row.getCreateUser());
			dataList.add(body);
		}
		return dataList;
	}

	private Map<String, String> getHeaderMap() {
		Map<String,String> headerMap = new LinkedHashMap<>();
		headerMap.put("collectGoodsAreaCode","集货区");
		headerMap.put("collectGoodsPlaceType","货位类型");
		headerMap.put("collectGoodsPlaceCode","集货位");
		headerMap.put("waybillCode","运单号");
		headerMap.put("packageCount","总包裹数");
		headerMap.put("scanPackageCount","实际扫描包裹数");
		headerMap.put("createTime","集货时间");
		headerMap.put("createUser","集货人");
		return headerMap;
	}

	@Override
	public CollectGoodsDetail findCollectGoodsDetailByPackageCode(String packageCode){
		return collectGoodsDetailDao.findCollectGoodsDetailByPackageCode(packageCode);
	}

}
