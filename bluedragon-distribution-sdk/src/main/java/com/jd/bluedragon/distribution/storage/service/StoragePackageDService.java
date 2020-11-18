package com.jd.bluedragon.distribution.storage.service;

import com.jd.bluedragon.distribution.storage.domain.StoragePackageD;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

/**
 *
 * @ClassName: StoragePackageDService
 * @Description: 储位包裹明细表--Service接口
 * @author wuyoude
 * @date 2018年08月15日 18:27:23
 *
 */
public interface StoragePackageDService extends Service<StoragePackageD> {


    List<StoragePackageD> queryByWaybill(String waybillCode);


    int cancelPutaway(String waybillCode);
}
