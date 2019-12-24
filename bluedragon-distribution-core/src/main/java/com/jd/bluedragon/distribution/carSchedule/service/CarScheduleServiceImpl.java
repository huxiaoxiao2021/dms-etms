package com.jd.bluedragon.distribution.carSchedule.service;

import com.google.gson.Gson;
import com.jd.bluedragon.distribution.areadest.dao.AreaDestDao;
import com.jd.bluedragon.distribution.areadest.domain.AreaDest;
import com.jd.bluedragon.distribution.carSchedule.dao.CarScheduleDao;
import com.jd.bluedragon.distribution.carSchedule.dao.SendCodeToCarNoDao;
import com.jd.bluedragon.distribution.carSchedule.domain.CancelScheduleTo;
import com.jd.bluedragon.distribution.carSchedule.domain.CarScheduleTo;
import com.jd.bluedragon.distribution.carSchedule.domain.SendCodeToCarCode;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by wuzuxiang on 2017/3/6.
 */
@Service("carScheduleService")
public class CarScheduleServiceImpl implements CarScheduleService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Resource
    private CarScheduleDao carScheduleDao;

    @Resource
    private SendCodeToCarNoDao sendCodeToCarNoDao;

    @Resource
    private SendDatailDao sendDatailDao;

    @Autowired
    private BasicPrimaryWS basicPrimaryWS;

    Gson gson = new Gson();

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void persistData(CarScheduleTo carScheduleTo) {
        this.log.info("CarScheduleService-->persistData方法begin...");
        if(null == carScheduleTo || null == carScheduleTo.getSendCarCode() || "".equals(carScheduleTo.getSendCarCode())){
            this.log.info("参数错误，参数的基本信息为空,本次方法退出。");
            return;
        }
        completeDomain(carScheduleTo);
        int i = carScheduleDao.addSchedule(carScheduleTo);
        List<String> sendCodes = carScheduleTo.getSendCodeList();
        if(null != sendCodes && sendCodes.size() > 0 && i > 0){
            for(String sendCode : sendCodes){
                SendCodeToCarCode sendCodeToCarNo = new SendCodeToCarCode();
                sendCodeToCarNo.setSendCarCode(carScheduleTo.getSendCarCode());//发车条码
                sendCodeToCarNo.setSendCode(sendCode);//批次号
                sendCodeToCarNo.setYn(1);
                sendCodeToCarNoDao.add(sendCodeToCarNo);
            }
        }
        return;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Boolean cancelSchedule(CancelScheduleTo cancelScheduleTo) {
        Boolean bool = Boolean.FALSE;
        if(null != cancelScheduleTo && null != cancelScheduleTo.getSendCarCode() && !"".equals(cancelScheduleTo.getSendCarCode())) {
            Boolean bool1 = carScheduleDao.cancelSend(cancelScheduleTo);
            SendCodeToCarCode sendCodeToCarNo = new SendCodeToCarCode();
            sendCodeToCarNo.setSendCarCode(cancelScheduleTo.getSendCarCode());//发车条码
            sendCodeToCarNo.setYn(0);
            Boolean bool2 = sendCodeToCarNoDao.cancelSendCar(sendCodeToCarNo)>0;
            bool = bool1 & bool2;
        }
        return bool;
    }

    @Override
    public Integer routeTypeByVehicleNo(String vehicleNo) {
        Integer routeType = null;
        if(null != vehicleNo || !"".equals(vehicleNo)){
            routeType = carScheduleDao.routeTypeByVehicleNo(vehicleNo);
        }
        return routeType;
    }

    @Override
    public Integer routeTypeByVehicleNoAndSiteCode(String vehicleNo, Integer siteCode) {
        Integer routeType = null;
        if(null != vehicleNo && !"".equals(vehicleNo) && null != siteCode){
            routeType = carScheduleDao.routeTypeByVehicleNoAndSiteCode(vehicleNo,siteCode);
        }
        return routeType;
    }

    @Override
    public Integer packageNumByVehicleNo(String vehicleNo) {
        // 批次与分拣中心是多对多关系（不考虑现场的运营实际情况）
        Integer routeType = null;
        if(null != vehicleNo || !"".equals(vehicleNo)){
            routeType = carScheduleDao.packageNumByVehicleNo(vehicleNo);
        }
        return routeType;
    }

    @Override
    public Integer packageNumByVehicleNoAndSiteCode(String vehicleNo, Integer siteCode) {
        Integer packageNum = null;
        if(null != vehicleNo && !"".equals(vehicleNo)){
            packageNum = carScheduleDao.packageNumByVehicleNoAndSiteCode(vehicleNo,siteCode);
        }
        return packageNum;
    }

    @Override
    public List<SendDetail> sendDetailByCarAndSiteCode(String vehicleNo ,Integer siteCode) {
        List<SendDetail> sendD = new ArrayList<SendDetail>();
        if(null != vehicleNo && !"".equals(vehicleNo)){
            String sendCarCode = carScheduleDao.sendCarCodeByVehicleNumberAndSiteCode(vehicleNo,siteCode);
            List<String> sendCodes= sendCodeToCarNoDao.sendCodeBySendCarCode(sendCarCode);
            if(null != sendCodes && sendCodes.size() > 0){
                for (String sendCode : sendCodes){
                    if(!sendCode.substring(sendCode.indexOf("-")+1,sendCode.lastIndexOf("-")).equals(String.valueOf(siteCode)) ){
                        //排除非本分拣中心的批次
                        continue;
                    }
                    List<SendDetail> items = new ArrayList<SendDetail>();
                    SendDetail queryDetail = new SendDetail();
                    queryDetail.setCreateSiteCode(Integer.valueOf(sendCode.substring(0,sendCode.indexOf("-"))));
                    queryDetail.setSendCode(sendCode);
                    items = sendDatailDao.queryBySiteCodeAndSendCode(queryDetail);
                    sendD .addAll(items);
                }
            }
        }
        return sendD;
    }

    @Override
    public Integer localPackageNumByVehicleNo(String vehicleNo , Integer siteCode) {
        Integer localPackageNum = 0;
        Integer temporaryNum = 0;//临时变量
        if(null != vehicleNo && !"".equals(vehicleNo) && null != siteCode){
            CarScheduleTo carScheduleTo = carScheduleDao.getByVehicleNoAndSiteCode(vehicleNo,siteCode);
            if(carScheduleTo != null && StringUtils.isNotBlank(carScheduleTo.getSendCarCode())){
                localPackageNum = carScheduleTo.getPackageNum();//默认是车载的总量就是本分拣中心的总量
                temporaryNum = localPackageNum;
                List<String> sendCodes = sendCodeToCarNoDao.sendCodeBySendCarCode(carScheduleTo.getSendCarCode());
                Set<String> sendCodeOutside = new HashSet<String>();//非本分拣中心的批次号
                Set<String> sendCodeInside = new HashSet<String>();//本分拣中心的批次号
                if(null != sendCodes && sendCodes.size() > 0){
                    for(String sendCode : sendCodes){
                        if(!sendCode.substring(sendCode.indexOf("-")+1,sendCode.lastIndexOf("-")).equals(String.valueOf(siteCode)) ){
                            //排除非本分拣中心的批次
                            sendCodeOutside.add(sendCode);
                            continue;
                        }
                        sendCodeInside.add(sendCode);
                    }
                }
//                if(sendCodeOutside.size() > 0){
                    if(sendCodeInside.size() > 0){
                        for(Iterator it = sendCodeInside.iterator();it.hasNext();){
                            String sendCode1 = String.valueOf(it.next());
                            SendDetail queryDetail = new SendDetail();
                            queryDetail.setCreateSiteCode(Integer.valueOf(sendCode1.substring(0,sendCode1.indexOf("-"))));
                            queryDetail.setSendCode(sendCode1);
                            temporaryNum += sendDatailDao.queryBySiteCodeAndSendCode(queryDetail).size();//当前分拣中心的包裹总量
                        }
                    }
//                }
            }
        }
//        if(temporaryNum < localPackageNum){
            localPackageNum = temporaryNum;
//        }
        return localPackageNum;
    }

    @Override
    public Integer isSameOrg(String vehicleNumber, Integer siteCode) {
        CarScheduleTo carScheduleTo = carScheduleDao.getByVehicleNoAndSiteCode(vehicleNumber,siteCode);
        if (null == carScheduleTo ) {
            log.warn("车辆调度查询该车辆信息失败，车牌号为:{},站点为：{}" ,vehicleNumber, siteCode);
            return null;
        }
        Integer createSiteCode = carScheduleTo.getCreateSiteCode();
        Integer receiveSiteCode = carScheduleTo.getReceiveSiteCode();
        Integer result = null;
        if(createSiteCode != null && receiveSiteCode != null){
            BaseStaffSiteOrgDto createSite = basicPrimaryWS.getBaseSiteBySiteId(createSiteCode);
            BaseStaffSiteOrgDto receiveSite = basicPrimaryWS.getBaseSiteBySiteId(receiveSiteCode);
            Integer createOrg = new Integer(0);
            Integer receiveOrg = new Integer(-1);
            if(createSite != null ){
                createOrg = createSite.getOrgId();
            }
            if(receiveSite != null){
                receiveOrg = receiveSite.getOrgId();
            }
            if(createOrg.equals(receiveOrg)){
                result = 1;//同区
            }else {
                result = 2;//跨区
            }
        }
        return result;
    }

    @Override
    public Date expectArriveTimeByVehicleNo(String vehicleNo) {
        return null;
    }

    /**
     * 对实体信息进行补全
     * @param carScheduleTo
     * @return
     */
    private void completeDomain(CarScheduleTo carScheduleTo){
        /** 处理sendCode为%%,%%,%%逗号隔开的形式 **/
//        if(null != carScheduleTo.getSendCodeList() && !"".equals(carScheduleTo.getSendCodeList())
//                && !"[]".equals(carScheduleTo.getSendCodeList())){
//            String sendCodes = carScheduleTo.getSendCodeList();
//            List<String> sendList = gson.fromJson(sendCodes, ArrayList.class);
//            StringBuffer sendCodeList = new StringBuffer();
//            for(int i = 0 ; i < sendList.size();i++){
//                sendCodeList.append(sendList.get(i));
//                if(i != sendList.size()-1){
//                    sendCodeList.append(",");
//                }
//            }
//            carScheduleTo.setSendCodeList(sendCodeList.toString());
//        }
        /**根据七位站点编码，获取站点ID**/
        if(null != carScheduleTo.getCreateDmsCode() && !"".equals(carScheduleTo.getCreateDmsCode())){
            BaseStaffSiteOrgDto createSiteDto = basicPrimaryWS.getBaseSiteByDmsCode(carScheduleTo.getCreateDmsCode());
            if(null != createSiteDto){
                carScheduleTo.setCreateSiteCode(createSiteDto.getSiteCode());
                carScheduleTo.setCreateSiteName(createSiteDto.getSiteName());
            }
        }
        if(null != carScheduleTo.getReceiveDmsCode() && !"".equals(carScheduleTo.getReceiveDmsCode())){
            BaseStaffSiteOrgDto receiveSiteDto = basicPrimaryWS.getBaseSiteByDmsCode(carScheduleTo.getReceiveDmsCode());
            if(null != receiveSiteDto){
                carScheduleTo.setReceiveSiteCode(receiveSiteDto.getSiteCode());
                carScheduleTo.setReceiveSiteName(receiveSiteDto.getSiteName());
            }
        }
        if(null != carScheduleTo.getRouteType()){
            carScheduleTo.setRouteTypeMark(routeTypeByType(carScheduleTo.getRouteType()));
        }
        if(null != carScheduleTo.getTransportWay()){
            carScheduleTo.setTransportWayMark(transportWayByTpye(carScheduleTo.getTransportWay()));
        }
    }

    /**
     * 运输方式
     * @param type
     * @return
     */
    private String transportWayByTpye(Integer type){
        String result = "";
        if(null != type){
            switch (type){
                case 1: result = "公路零担";break;
                case 2: result = "公路整车";break;
                case 3: result = "航空";break;
                case 4: result = "铁路";break;
                case 5: result = "快递";break;
                case 6: result = "冷链整车";break;
                case 7: result = "冷链零担";break;
                case 8: result = "公路整车平板";break;
                case 9: result = "公路零担平板";break;
                default: break;
            }
        }
        return result;
    }

    /**
     * 线路类型
     * @param type
     * @return
     */
    private String routeTypeByType(Integer type){
        String result = "";
        if(null != type){
            switch (type){
                case 1: result = "干线";break;
                case 2: result = "支线";break;
                case 3: result = "短驳";break;
                case 4: result = "上门提送";break;
                case 5: result = "摆渡";break;
                case 6: result = "司机接货";break;
                case 8: result = "站点接货";break;
                case 9: result = "传站返回";break;
                case 10: result = "长途传站";break;
                case 11: result = "室内传站";break;
                case 16: result = "摆渡返回";break;
                default: break;
            }
        }
        return result;
    }

    public static void main(String[] args){
        CarScheduleDao dao = new CarScheduleDao();
        AreaDestDao aDao = new AreaDestDao();
        int i = aDao.add(new AreaDest());
//        CarScheduleTo i = dao.getByVehicleNoAndSiteCode("京A-8888",910);
        System.out.println( i );

    }
}
