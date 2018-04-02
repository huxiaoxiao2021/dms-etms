package com.jd.bluedragon.distribution.base.service;

import com.jd.bluedragon.distribution.base.domain.DmsStorageArea;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: DmsStorageAreaService
 * @Description: 分拣中心库位--Service接口
 * @author wuyoude
 * @date 2018年03月13日 16:25:45
 *
 */
public interface DmsStorageAreaService extends Service<DmsStorageArea> {
	/**
	 * 判断是否存在
	 * @param dmsStorageArea
	 * @return
	 */
	boolean isExist(DmsStorageArea dmsStorageArea);
	/**
	 * 根据当前操作的分拣中心id和运单收货地址省市获取库位配置
	 * @param dmsSiteCode
	 * @param dmsProvinceCode
	 * @param dmsCityCode
	 * @return
	 */
    DmsStorageArea findByDmsSiteAndWaybillAdress(Integer dmsSiteCode,Integer dmsProvinceCode,Integer dmsCityCode);

    Boolean importExcel(List<DmsStorageArea>  dataList,String createUserCode,String createUserName,Date createTime);

    String checkExportData(List<DmsStorageArea> dataList,Integer dmsSiteCode,String dmsSiteName);
}
