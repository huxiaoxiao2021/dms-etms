package com.jd.bluedragon.distribution.carSchedule.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.carSchedule.domain.CancelScheduleTo;
import com.jd.bluedragon.distribution.carSchedule.domain.CarScheduleTo;
import org.apache.commons.collections.map.HashedMap;

import java.util.Map;

/**
 * Created by wuzuxiang on 2017/3/3.
 */
public class CarScheduleDao extends BaseDao<CarScheduleTo>{
    private static final String NAMESPACE = CarScheduleDao.class.getName();

    public Integer addSchedule(CarScheduleTo entity) {
        return super.add(NAMESPACE, entity);
    }

    public CarScheduleTo getByVehicleNoAndSiteCode (String vehicleNumber,Integer siteCode){
        Map<String,Object> params = new HashedMap();
        params.put("vehicleNumber",vehicleNumber);
        params.put("siteCode",siteCode);
        return super.getSqlSession().selectOne(CarScheduleDao.NAMESPACE + ".getByVehicleNoAndSiteCode" ,params);
    }

    public Boolean cancelSend(CancelScheduleTo cancelScheduleTo){
        Boolean bool = Boolean.FALSE;
        int  i = super.getSqlSession().update(CarScheduleDao.NAMESPACE+".cancelSchedule",cancelScheduleTo);
        if(i >= 1){
            bool = Boolean.TRUE;
        }
        return bool;
    }

    public Integer routeTypeByVehicleNo(String vehicleNumer){
        return super.getSqlSession().selectOne(CarScheduleDao.NAMESPACE + ".routeTypeByVehicleNo",vehicleNumer);
    }

    public Integer routeTypeByVehicleNoAndSiteCode(String vehicleNumber,Integer siteCode){
        Map<String,Object> params = new HashedMap();
        params.put("vehicleNumber",vehicleNumber);
        params.put("siteCode",siteCode);
        return super.getSqlSession().selectOne(CarScheduleDao.NAMESPACE + ".routeTypeByVehicleNoAndSiteCode",params);
    }

    public Integer packageNumByVehicleNo(String vehicleNumber){
        return super.getSqlSession().selectOne(CarScheduleDao.NAMESPACE + ".packageNumByVehicleNo",vehicleNumber);
    }

    public Integer packageNumByVehicleNoAndSiteCode(String vehicleNumber,Integer siteCode){
        Map<String,Object> params = new HashedMap();
        params.put("vehicleNumber",vehicleNumber);
        params.put("siteCode",siteCode);
        return super.getSqlSession().selectOne(CarScheduleDao.NAMESPACE + ".packageNumByVehicleNoAndSiteCode",params);
    }

    public String querySendCodesByVehicleNo(String vehicleNumber){
        return super.getSqlSession().selectOne(CarScheduleDao.NAMESPACE + ".querySendCodesByVehicleNo",vehicleNumber);
    }

    public String sendCarCodeByVehicleNumberAndSiteCode(String vehicleNumber ,Integer siteCode){
        Map<String,Object> params = new HashedMap();
        params.put("vehicleNumber",vehicleNumber);
        params.put("siteCode",siteCode);
        return super.getSqlSession().selectOne(CarScheduleDao.NAMESPACE + ".sendCarCodeByVehicleNumberAndSiteCode",params);
    }
}
