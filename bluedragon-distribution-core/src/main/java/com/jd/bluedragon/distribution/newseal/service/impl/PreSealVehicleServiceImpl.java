package com.jd.bluedragon.distribution.newseal.service.impl;

import com.jd.bluedragon.distribution.newseal.domain.SealVehicleEnum;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicle;
import com.jd.bluedragon.distribution.newseal.dao.PreSealVehicleDao;
import com.jd.bluedragon.distribution.newseal.service.PreSealVehicleService;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @ClassName: PreSealVehicleServiceImpl
 * @Description: 预封车数据表--Service接口实现
 * @author wuyoude
 * @date 2019年03月12日 15:00:58
 *
 */
@Service("preSealVehicleService")
public class PreSealVehicleServiceImpl extends BaseService<PreSealVehicle> implements PreSealVehicleService {

	@Autowired
	@Qualifier("preSealVehicleDao")
	private PreSealVehicleDao preSealVehicleDao;

	@Override
	public Dao<PreSealVehicle> getDao() {
		return this.preSealVehicleDao;
	}

    @Transactional
	@Override
	public boolean insert(PreSealVehicle preSealVehicle) {
		return preSealVehicleDao.insert(preSealVehicle);
	}

    @Transactional
	@Override
	public boolean cancelPreSealBeforeInsert(PreSealVehicle preSealVehicle) {
		List<PreSealVehicle> exists = findByCreateAndReceive(preSealVehicle.getCreateSiteCode(), preSealVehicle.getReceiveSiteCode());
		if(exists != null && !exists.isEmpty()){
			List<Long> ids = new ArrayList<>(exists.size());
			for (PreSealVehicle vo : exists){
				ids.add(vo.getId());
			}
			updateStatusByIds(ids, preSealVehicle.getCreateUserErp(), preSealVehicle.getCreateUserName(), SealVehicleEnum.CANCEL_PRE_SEAL);
		}
		return insert(preSealVehicle);
	}

    @Override
    public boolean updateById(PreSealVehicle preSealVehicle) {
        return preSealVehicleDao.update(preSealVehicle);
    }

    @Transactional
	@Override
	public boolean updateStatusById(Long id, String updateUserErp, String updateUserName, SealVehicleEnum status) {
		PreSealVehicle preSealVehicle = new PreSealVehicle();

		preSealVehicle.setId(id);
		preSealVehicle.setStatus(status.getCode());
		preSealVehicle.setUpdateUserErp(updateUserErp);
		preSealVehicle.setUpdateUserName(updateUserName);
		preSealVehicle.setUpdateTime(new Date());

		return preSealVehicleDao.update(preSealVehicle);
	}

    @Transactional
	@Override
	public int updateStatusByIds(List<Long> ids, String updateUserErp, String updateUserName, SealVehicleEnum status) {
		return preSealVehicleDao.updateStatusByIds(ids, updateUserErp, updateUserName, status.getCode());
	}

	@Override
	public List<PreSealVehicle> findByCreateAndReceive(Integer createSiteCode, Integer receiveSiteCode) {
		return preSealVehicleDao.findByCreateAndReceive(createSiteCode, receiveSiteCode);
	}
}
