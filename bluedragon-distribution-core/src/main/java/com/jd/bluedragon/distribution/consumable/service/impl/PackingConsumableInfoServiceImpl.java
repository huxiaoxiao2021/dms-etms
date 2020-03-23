package com.jd.bluedragon.distribution.consumable.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.packingconsumable.domain.PackingConsumableBaseInfo;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfo;
import com.jd.bluedragon.distribution.consumable.dao.PackingConsumableInfoDao;
import com.jd.bluedragon.distribution.consumable.service.PackingConsumableInfoService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: PackingConsumableInfoServiceImpl
 * @Description: 包装耗材信息表--Service接口实现
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
@Service("packingConsumableInfoService")
public class PackingConsumableInfoServiceImpl extends BaseService<PackingConsumableInfo> implements PackingConsumableInfoService {

	@Autowired
	@Qualifier("packingConsumableInfoDao")
	private PackingConsumableInfoDao packingConsumableInfoDao;

	@Override
	public Dao<PackingConsumableInfo> getDao() {
		return this.packingConsumableInfoDao;
	}

	@Override
	public boolean saveOrUpdate(PackingConsumableInfo packingConsumableInfo) {
		PackingConsumableInfo oldData = this.find(packingConsumableInfo);
		Date date = new Date();
		packingConsumableInfo.setUpdateTime(date);
		packingConsumableInfo.setOperateTime(date);

		if(oldData != null){
			packingConsumableInfo.setId(oldData.getId());
			return this.getDao().update(packingConsumableInfo);
		}else{
			boolean result = this.getDao().insert(packingConsumableInfo);

			//根据插入返回的id，更新包装耗材编码
			Long id = packingConsumableInfo.getId();
			//根据前缀和占位符生产耗材编号
			String code = String.format(Constants.PACKING_PRE_CODE + Constants.PACKING_PLACEHOLDER, id);

			packingConsumableInfo.setCode(code);
			packingConsumableInfo.setCreateTime(date);
			return result && this.getDao().update(packingConsumableInfo);
		}
	}

	@Override
	public PackingConsumableBaseInfo getPackingConsumableInfoByCode(String code) {
		return packingConsumableInfoDao.getPackingConsumableInfoByCode(code);
	}

    @Override
    public List<PackingConsumableInfo> listPackingConsumableInfoByCodes(List<String> codes) {
	    if (!CollectionUtils.isEmpty(codes)) {
	        return packingConsumableInfoDao.listPackingConsumableInfoByCodes(codes);
        }
        return new ArrayList<>();
    }

    @Override
    public List<PackingConsumableInfo> listByTypeCode(String typeCode) {
	    if (StringUtils.isBlank(typeCode)) {
	        return new ArrayList<>();
        }
        return packingConsumableInfoDao.listByTypeCode(typeCode);
    }
}
