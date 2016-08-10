package com.jd.bluedragon.distribution.departure.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.api.request.DeparturePrintRequest;
import com.jd.bluedragon.distribution.api.response.DeparturePrintResponse;
import com.jd.bluedragon.distribution.departure.domain.DepartureCar;

public class DepartureCarDao extends BaseDao<DepartureCar> {

	private static final String namespace = DepartureCarDao.class.getName();

	public int insert(DepartureCar record) {
		return this.getSqlSession().insert(namespace + ".insert", record);
	}
	
	@SuppressWarnings("unchecked")
	public List<DepartureCar> findDepartureCarByFingerprint(DepartureCar departureCar) {
	   return this.getSqlSession().selectList(namespace + ".findDepartureCarByFingerprint",
	    		   departureCar);
	   }
	
	@SuppressWarnings("unchecked")
	public List<DepartureCar> findDepartureList(DeparturePrintRequest departurPrintRequest){
		return this.getSqlSession().selectList(namespace + ".findDepartureList", departurPrintRequest);
	}
	
	public boolean updatePrintTime(long departureCarId){
		return this.getSqlSession().update(namespace + ".updatePrintTime", departureCarId)>0;
	}

    public List<DeparturePrintResponse> queryDeliveryInfoByOrderCode(String orderCode){
        return this.getSqlSession().selectList(namespace + ".queryDeliveryInfoByOrderCode", orderCode);
    }

	public List<DeparturePrintResponse> queryDepartureInfoBySendCode(List<String> sendCodes){
		return this.getSqlSession().selectList(namespace + ".queryDepartureInfoBySendCode", sendCodes);
	}

    public DeparturePrintResponse queryArteryBillingInfo(long carCode){
        return (DeparturePrintResponse)this.getSqlSession().selectOne(namespace + ".queryArteryBillingInfo", carCode);
    }

    public DeparturePrintResponse queryArteryBillingInfoByBoxCode(String boxCode){
        return (DeparturePrintResponse) this.getSqlSession().selectOne(namespace + ".queryArteryBillingInfoByBoxCode", boxCode);
    }
    
    public Long getSeqNextVal() {
		return (Long) this.getSqlSession().selectOne(namespace + ".getSeqNextVal");
	}
}
