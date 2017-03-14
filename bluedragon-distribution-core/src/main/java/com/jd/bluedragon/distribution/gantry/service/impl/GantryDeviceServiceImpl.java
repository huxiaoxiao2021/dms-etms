package com.jd.bluedragon.distribution.gantry.service.impl;

import com.jd.bluedragon.core.redis.service.impl.RedisCommonUtil;
import com.jd.bluedragon.distribution.gantry.dao.GantryDeviceDao;
import com.jd.bluedragon.distribution.gantry.domain.GantryDevice;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.GantryPackageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/3/10
 */
@Service("gantryDeviceService")
public class GantryDeviceServiceImpl implements GantryDeviceService{

    @Autowired
    private GantryDeviceDao gantryDeviceDao;

    @Autowired
    private SendDatailDao sendDatailDao;

    @Autowired
    private RedisCommonUtil redisCommonUtil;

    @Override
    public List<GantryDevice> getGantryByDmsCode(Integer dmsCode) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("siteCode", dmsCode);
        return gantryDeviceDao.getGantry(param);
    }

    @Override
    public List<GantryDevice> getGantryByDmsCode(Integer dmsCode,byte version) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("siteCode", dmsCode);
        param.put("version",version);
        return gantryDeviceDao.getGantry(param);
    }

    @Override
    public Integer getGantryCount(Map<String, Object> param) {
        return gantryDeviceDao.getGantryCount(param);
    }


    @Override
    public List<GantryDevice> getGantry(Map<String, Object> param) {
        return gantryDeviceDao.getGantry(param);
    }

    @Override
    public List<GantryDevice> getGantryPage(Map<String, Object> param) {
        return gantryDeviceDao.getGantryPage(param);
    }

    @Override
    public int addGantry(GantryDevice device) {
        return gantryDeviceDao.addGantry(device);
    }

    @Override
    public int delGantryById(Integer id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("machineId", id);
        return gantryDeviceDao.delGantry(map);
    }

    @Override
    public int updateGantryById(GantryDevice device) {
        return gantryDeviceDao.updateGantryById(device);
    }

    @Override
    public List<SendDetail> queryWaybillsBySendCode(String sendCode) {
        List<SendDetail> list = sendDatailDao.queryWaybillsBySendCode(sendCode);
        return list;
    }

    @Override
    public List<SendDetail> queryBoxCodeBySendCode(String sendCode) {
        List<SendDetail> list = sendDatailDao.queryBoxCodeBySendCode(sendCode);
        return list;
    }

    /**
     * gantry velocity
     * */

    @Override
    public Integer getGantryVelocity(Integer dmsCode, String gantryNumber, Date startTime, Date endTime) {
        //
        String startKey = GantryPackageUtil.getDateRegion(gantryNumber,startTime);
        String endKey = GantryPackageUtil.getDateRegion(gantryNumber,endTime);

        if(startKey.equals(endKey)){
            return 0;
        }else{
            int startTimeHour = GantryPackageUtil.getDateHour(startTime);
            int endTimeHour = GantryPackageUtil.getDateHour(endTime);

            int startTimeRegion = GantryPackageUtil.getDateMinuteRegion(startTime);
            //
//            int curTimeRegion = GantryPackageUtil.getDateMinuteRegion(curDate);

            String startTimePrefix = GantryPackageUtil.getDatePrefix(gantryNumber,startTime);

            int packageCount = 0;
            int regionCount = 0;


            for(int i = startTimeHour;i <= endTimeHour;i++){
                while(startTimeRegion <= 12){

                    String key = startTimePrefix+GantryPackageUtil.fixIntLenth(i)+GantryPackageUtil.fixIntLenth(startTimeRegion);

                    if(key.equals(endKey)){
                        break;
                    }

                    int regionPackageNum = redisCommonUtil.getData(key);

                    if(regionPackageNum != 0){
                        packageCount += regionPackageNum;
                        regionCount ++;
                    }
                    startTimeRegion ++;
                }

                startTimeRegion = 1;
            }

            if(regionCount == 0){
                return 0;
            }else{
                return packageCount/regionCount;
            }
        }
    }
}
