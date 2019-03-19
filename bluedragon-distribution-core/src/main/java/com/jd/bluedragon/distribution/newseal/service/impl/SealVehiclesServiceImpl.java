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

import java.util.*;

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


    private static final Integer SQL_IN_EXPRESS_LIMIT = 999;

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
        calendar.setTime(new Date());
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),0,0,0);

        return sealVehiclesDao.findUsedTransports(createSiteCode, calendar.getTime());
    }

    @Override
    public List<String> findBySealDataCodes(Set<String> sendCodeSet) {
        List<String> sendCodes = new ArrayList<>(sendCodeSet);
        List<String> result = new ArrayList<>();
        int total = sendCodes.size();
        for(int index = 0; index < total; index += SQL_IN_EXPRESS_LIMIT){
            List<String> temp;
            int end = index + SQL_IN_EXPRESS_LIMIT;
            if(end > total ){
                temp = sendCodes.subList(index, total);
            }else{
                temp = sendCodes.subList(index, end);
            }
            if (temp != null && !temp.isEmpty()) {
                List<String> sealedList = sealVehiclesDao.findSealDataBySealDataCodes(temp);
                if(sealedList != null && !sealedList.isEmpty()){
                    result.addAll(sealedList);
                }
            }
        }

        return result;
    }
}
