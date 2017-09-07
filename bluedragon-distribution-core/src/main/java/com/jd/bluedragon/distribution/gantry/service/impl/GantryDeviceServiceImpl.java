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

    @Override
    public Integer querySendDCountBySendCode(String sendCode) {
        return sendDatailDao.querySendDCountBySendCode(sendCode);
    }

    /**
     * gantry velocity
     * added by zhanglei
     *
     * 龙门架流速计算
     *
     *
     * */

    @Override
    public Integer getGantryVelocity(Integer dmsCode, String gantryNumber, Date startTime, Date endTime) {
        //计算出开始时间对应的redis key
        String startKey = GantryPackageUtil.getDateRegion(gantryNumber,startTime);

        //计算出结束时间 redis key
        String endKey = GantryPackageUtil.getDateRegion(gantryNumber,endTime);

        //如果开始时间与结束时间是在同一个区间 返回0  这时候前台处理可以使用默认值  3600件/h 龙门架的平均流速
        if(startKey.equals(endKey)){
            return 0;
        }else{
            //开始时间对应的小时段
            int startTimeHour = GantryPackageUtil.getDateHour(startTime);
            //结束时间对应的小时段
            int endTimeHour = GantryPackageUtil.getDateHour(endTime);

            //开始分钟对应的12个小分区
            int startTimeRegion = GantryPackageUtil.getDateMinuteRegion(startTime);
            //
//            int curTimeRegion = GantryPackageUtil.getDateMinuteRegion(curDate);

            //前缀  默认是车辆调度不会垮天进行 最多垮小时  所以前缀是龙门架序列号+日期
            String startTimePrefix = GantryPackageUtil.getDatePrefix(gantryNumber,startTime);

            int packageCount = 0;
            int regionCount = 0;


            /**
             * 简单逻辑介绍：1、外层循环从开始时间小时数循环到结束时间小时数
             *             2、内层循环从开始时间region循环到12  一小时12个区域 超过12 小时数+1 从0开始继续循环  直到与结束时间key相同
             *
             * 可用递归？
             * */
            for(int i = startTimeHour;i <= endTimeHour;i++){
                while(startTimeRegion <= 12){

                    //i不足两位补零
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
                //返回每分钟多少个
                return packageCount/regionCount/5 ;
            }
        }
    }
}
