package com.jd.bluedragon.distribution.gantry.service.impl;

import com.jd.bluedragon.core.redis.service.impl.RedisCommonUtil;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.gantry.dao.GantryDeviceDao;
import com.jd.bluedragon.distribution.gantry.domain.GantryBatchSendResult;
import com.jd.bluedragon.distribution.gantry.domain.GantryDevice;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.GantryPackageUtil;
import com.jd.etms.waybill.api.WaybillPackageApi;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.PackOpeFlowDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Autowired
    private BoxService boxService;

    @Autowired
    private WaybillPackageApi waybillPackageApi;

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
    public GantryBatchSendResult getSummaryVolumeBySendCode(String sendCode) {
        GantryBatchSendResult sendCodeSum = new GantryBatchSendResult();
        List<SendDetail> sendDetailList = this.queryBoxCodeBySendCode(sendCode);
        sendCodeSum.setSendCode(sendCode);
        sendCodeSum.setPackageSum(sendDetailList.size());
        sendCodeSum.setVolumeSum(0.0);
        List<String> boxCodeList = this.boxCodeListFilter(sendDetailList);
        this.buildBoxVolume(boxCodeList, sendCodeSum);
        Map<String, List<String>> waybillPackageMap = this.getWaybillPackageMap(sendDetailList);
        this.buildPackageVolume(waybillPackageMap, sendCodeSum);
        return sendCodeSum;
    }

    private void buildPackageVolume(Map<String, List<String>> waybillPackageMap, GantryBatchSendResult sendCodeSum) {
        for (Map.Entry<String, List<String>> entry : waybillPackageMap.entrySet()) {
            BaseEntity<List<PackOpeFlowDto>> dtoList = waybillPackageApi.getPackOpeByWaybillCode(entry.getKey());
            Double volumeSum = 0.0;
            if (dtoList != null && dtoList.getResultCode() == 1) {
                List<PackOpeFlowDto> dto = dtoList.getData();
                if (dto != null && !dto.isEmpty()) {
                    for (PackOpeFlowDto pack : dto) {
                        if (entry.getValue().contains(pack.getPackageCode())) {
                            double volume = null == pack.getpLength() || null == pack.getpWidth() || null == pack.getpHigh() ?
                                    0.00 : pack.getpLength() * pack.getpWidth() * pack.getpHigh();
                            volumeSum += volume;
                        }
                    }
                }
            }
            //四舍五入;保留两位有效数字
            BigDecimal bg = new BigDecimal(volumeSum).setScale(2, RoundingMode.UP);
            sendCodeSum.setVolumeSum(sendCodeSum.getVolumeSum() + bg.doubleValue());
        }
    }

    private void buildBoxVolume(List<String> boxCodeList, GantryBatchSendResult sendCodeSum) {
        if (boxCodeList != null && boxCodeList.size() > 0) {
            Double volumeSum = 0.0;
            for (String boxCode : boxCodeList) {
                Box box = boxService.findBoxByCode(boxCode);
                if (box == null) {
                    continue;
                }
                double length = box.getLength() == null ? 0 : box.getLength();
                double width = box.getWidth() == null ? 0 : box.getWidth();
                double height = box.getHeight() == null ? 0 : box.getWidth();
                volumeSum += length * width * height;
            }
            //四舍五入;保留两位有效数字
            BigDecimal bg = new BigDecimal(volumeSum).setScale(2, RoundingMode.UP);
            sendCodeSum.setVolumeSum(bg.doubleValue());
        }
    }

    /**
     * 过滤出按箱号发货的列表
     *
     * @param sendDetailList
     * @return
     */
    private List<String> boxCodeListFilter(List<SendDetail> sendDetailList) {
        Set<String> boxCodeSet = new HashSet<>();
        if (sendDetailList.size() > 0) {
            Iterator<SendDetail> iterator = sendDetailList.iterator();
            while (iterator.hasNext()) {
                SendDetail sendDetail = iterator.next();
                if (BusinessHelper.isBoxcode(sendDetail.getBoxCode())) {
                    boxCodeSet.add(sendDetail.getBoxCode());
                    iterator.remove();
                }
            }
        }
        return new ArrayList<>(boxCodeSet);
    }

    /**
     * 运单包裹关系列表
     *
     * @param sendDetailList
     * @return
     */
    private Map<String, List<String>> getWaybillPackageMap(List<SendDetail> sendDetailList) {
        Collections.sort(sendDetailList, new Comparator<SendDetail>() {
            @Override
            public int compare(SendDetail o1, SendDetail o2) {
                if (o1.getPackageBarcode() == null && o2.getPackageBarcode() == null) {
                    return 0;
                }
                if (o1.getPackageBarcode() == null && o2.getPackageBarcode() != null) {
                    return -1;
                }
                if (o1.getPackageBarcode() != null && o2.getPackageBarcode() == null) {
                    return 1;
                }
                return o1.getPackageBarcode().compareTo(o2.getPackageBarcode());
            }
        });

        Map<String, List<String>> resultMap = new HashMap<>();
        for (SendDetail sendDetail : sendDetailList) {
            if (resultMap.containsKey(sendDetail.getWaybillCode())) {
                resultMap.get(sendDetail.getWaybillCode()).add(sendDetail.getPackageBarcode());
            } else {
                List<String> packageBarCodeList = new ArrayList<>();
                packageBarCodeList.add(sendDetail.getPackageBarcode());
                resultMap.put(sendDetail.getWaybillCode(), packageBarCodeList);
            }
        }
        return resultMap;
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
