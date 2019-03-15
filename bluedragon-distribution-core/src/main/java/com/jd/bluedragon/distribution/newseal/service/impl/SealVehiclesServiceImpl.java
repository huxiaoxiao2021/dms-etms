package com.jd.bluedragon.distribution.newseal.service.impl;

import com.jd.bluedragon.distribution.newseal.domain.SealVehicleEnum;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.newseal.domain.SealVehicles;
import com.jd.bluedragon.distribution.newseal.dao.SealVehiclesDao;
import com.jd.bluedragon.distribution.newseal.service.SealVehiclesService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: SealVehiclesServiceImpl
 * @Description: 封车数据表--Service接口实现
 * @author wuyoude
 * @date 2019年03月12日 15:00:58
 *
 */
@Service("sealVehiclesService")
public class SealVehiclesServiceImpl extends BaseService<SealVehicles> implements SealVehiclesService {

	@Autowired
	@Qualifier("sealVehiclesDao")
	private SealVehiclesDao sealVehiclesDao;

	@Override
	public Dao<SealVehicles> getDao() {
		return this.sealVehiclesDao;
	}

	@Transactional
	@Override
	public boolean updateDeSealBySealDataCode(List<SealVehicles> sealVehiclesList) {
		for (SealVehicles sealVehicles : sealVehiclesList){
			sealVehicles.setStatus(SealVehicleEnum.DE_SEAL.getCode());
			sealVehiclesDao.updateBySealDataCode(sealVehicles);
		}
		return true;
	}

    @Override
    public List<String> findTodayUsedTransports(Integer createSiteCode) {
        //获取当天零点时刻
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),0,0,0);

        calendar.set(Calendar.MILLISECOND, 0);

        return sealVehiclesDao.findUsedTransports(createSiteCode, calendar.getTime());
    }
}
