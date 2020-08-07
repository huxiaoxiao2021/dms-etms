package com.jd.bluedragon.distribution.sealVehicle.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicle;
import com.jd.bluedragon.distribution.newseal.domain.PreSealVehicleSourceEnum;
import com.jd.bluedragon.distribution.newseal.service.PreSealVehicleService;
import com.jd.bluedragon.distribution.newseal.service.SealVehiclesService;
import com.jd.bluedragon.distribution.seal.service.NewSealVehicleService;
import com.jd.bluedragon.distribution.sealVehicle.DmsSealVehicleService;
import com.jd.bluedragon.distribution.sealVehicle.domain.*;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("dmsSealVehicleService")
public class DmsSealVehicleServiceImpl implements DmsSealVehicleService {

    private static final Logger log = LoggerFactory.getLogger(DmsSealVehicleServiceImpl.class);

    @Autowired
    private PreSealVehicleService preSealVehicleService;

    @Autowired
    private SendMService sendMService;

    @Autowired
    private NewSealVehicleService newSealVehicleService;

    @Autowired
    private SealVehiclesService sealVehiclesService;

    /*
     * 获取未封车信息列表
     *
     * */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.DmsSealVehicleServiceImpl.getUnSealVehicleInfo", mState = JProEnum.TP)
    public JdResponse<List<UnSealVehicleInfo>> getUnSealVehicleInfo(Integer createSiteCode, Integer hourRange) {
        JdResponse<List<UnSealVehicleInfo>> jdResponse = new JdResponse<>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
        try {
            //获取该场地所有预封车信息
            List<PreSealVehicle> preSealVehicleList = preSealVehicleService.queryBySiteCode(createSiteCode);
            Map<String, UnSealVehicleInfo> unSealVehicleInfoMap = new HashMap<>();
            if (preSealVehicleList != null) {
                //遍历所有预封车记录
                for (PreSealVehicle preSealVehicle : preSealVehicleList) {
                    //运力编码维度记录信息
                    String transportCode = preSealVehicle.getTransportCode();
                    if (unSealVehicleInfoMap.containsKey(transportCode)) {
                        UnSealVehicleInfo unSealVehicleInfo = unSealVehicleInfoMap.get(transportCode);
                        //存在多条运力编码信息，说明该运力下有多个车牌，存在多个车牌时设置未就绪
                        unSealVehicleInfo.setReady(false);
                    } else {
                        //不存在运力编码，进行信息初始化
                        UnSealVehicleInfo unSealVehicleInfo = this.convert2UnSealVehicleInfo(preSealVehicle);
                        //判断是否需要进行体积判断
                        if (this.isNeedCheckVolume(preSealVehicle.getPreSealSource(), preSealVehicle.getTransWay())) {
                            unSealVehicleInfo.setReady(NumberHelper.gt0(preSealVehicle.getVolume()));
                        } else {
                            unSealVehicleInfo.setReady(true);
                        }
                        //设置批次数量
                        unSealVehicleInfo.setSendCodeCount(this.getUnSealSendCodeCount(createSiteCode, preSealVehicle.getReceiveSiteCode(), hourRange));
                        unSealVehicleInfoMap.put(transportCode, unSealVehicleInfo);
                    }
                }
            }
            jdResponse.setData(new ArrayList<>(unSealVehicleInfoMap.values()));
        } catch (Exception e) {
            jdResponse.setCode(JdResponse.CODE_ERROR);
            jdResponse.setMessage(JdResponse.MESSAGE_ERROR);
            log.error("获取未封车信息列表失败，操作单位：{}", createSiteCode, e);
        }
        return jdResponse;
    }

    /*
     * 获取未封车信息明细
     *
     * */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.DmsSealVehicleServiceImpl.getUnSealSendCodeDetail", mState = JProEnum.TP)
    public JdResponse<UnSealVehicleDetail> getUnSealSendCodeDetail(Integer createSiteCode, String transportCode, Integer hourRange) {
        JdResponse<UnSealVehicleDetail> jdResponse = new JdResponse<>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);

        try {
            //按运力编码获取预封车记录
            List<PreSealVehicle> preSealVehicleList = preSealVehicleService.getPreSealInfoByParams(transportCode);

            if (preSealVehicleList == null || preSealVehicleList.isEmpty()) {
                jdResponse.setCode(JdResponse.CODE_FAIL);
                jdResponse.setMessage("运力编码【" + transportCode + "】没有预封车信息，无法获取一键封车明细！");
                return jdResponse;
            }

            Integer receiveSiteCode = preSealVehicleList.get(0).getReceiveSiteCode();
            List<String> unSealSendCodeList = this.getUnSealSendCodeList(createSiteCode, receiveSiteCode, hourRange);

            if (unSealSendCodeList == null || unSealSendCodeList.isEmpty()) {
                jdResponse.setCode(JdResponse.CODE_FAIL);
                jdResponse.setMessage("运力编码【" + transportCode + "】没有需要封车的批次信息！");
                return jdResponse;
            }

            boolean isAllReady = false;
            String vehicleNumber = null;
            String sealCode = null;
            //如果只有一个预封车记录，说明只有一个车牌，进一步判断体积是否大于0
            if (preSealVehicleList.size() == 1) {
                PreSealVehicle preSealVehicle = preSealVehicleList.get(0);
                //进行体积判断
                if (! this.isNeedCheckVolume(preSealVehicle.getPreSealSource(), preSealVehicle.getTransWay()) || NumberHelper.gt0(preSealVehicle.getVolume())) {
                    vehicleNumber = preSealVehicle.getVehicleNumber();
                    sealCode = preSealVehicle.getSealCodes();
                    isAllReady = true;
                }
            }

            UnSealVehicleDetail unSealVehicleDetail = new UnSealVehicleDetail();
            unSealVehicleDetail.setCreateSiteCode(createSiteCode);
            unSealVehicleDetail.setTransportCode(transportCode);
            List<SealVehicleSendCodeInfo> sealVehicleSendCodeInfoList = new ArrayList<>();
            //遍历批次号，组装批次明细
            for (String unSealSendCode : unSealSendCodeList) {
                SealVehicleSendCodeInfo sealVehicleSendCodeInfo = new SealVehicleSendCodeInfo();
                sealVehicleSendCodeInfo.setSendCode(unSealSendCode);
                sealVehicleSendCodeInfo.setReady(isAllReady);
                sealVehicleSendCodeInfo.setVehicleNumber(vehicleNumber);
                sealVehicleSendCodeInfo.setSealCode(sealCode);
                sealVehicleSendCodeInfoList.add(sealVehicleSendCodeInfo);
            }
            unSealVehicleDetail.setSendCodeInfoList(sealVehicleSendCodeInfoList);
            jdResponse.setData(unSealVehicleDetail);
        } catch (Exception e) {
            jdResponse.setCode(JdResponse.CODE_ERROR);
            jdResponse.setMessage(JdResponse.MESSAGE_ERROR);
            log.error("获取未封车批次信息失败，运力编码：{}", transportCode, e);
        }

        return jdResponse;
    }

    /*
     * 修改运力编码中的车辆基础信息
     * */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.DmsSealVehicleServiceImpl.getVehicleBaseInfoList", mState = JProEnum.TP)
    public JdResponse<List<VehicleBaseInfo>> getVehicleBaseInfoList(String transportCode) {

        JdResponse<List<VehicleBaseInfo>> jdResponse = new JdResponse<>(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
        try {
            //按运力编码获取预封车记录
            List<PreSealVehicle> preSealVehicleList = preSealVehicleService.getPreSealInfoByParams(transportCode);

            if (preSealVehicleList == null || preSealVehicleList.isEmpty()) {
                jdResponse.setCode(JdResponse.CODE_FAIL);
                jdResponse.setMessage("运力编码【" + transportCode + "】没有预封车信息，无法车牌相关信息！");
                return jdResponse;
            }

            List<VehicleBaseInfo> vehicleNumberInfoList = new ArrayList<>();
            //遍历预封车记录，组装车牌信息列表并判断车牌是够处于就绪状态
            for (PreSealVehicle preSealVehicle : preSealVehicleList) {
                if (StringHelper.isNotEmpty(preSealVehicle.getVehicleNumber())) {
                    VehicleBaseInfo vehicleBaseInfo = new VehicleBaseInfo();
                    vehicleBaseInfo.setVehicleNumber(preSealVehicle.getVehicleNumber());
                    vehicleBaseInfo.setSealCode(preSealVehicle.getSealCodes());
                    vehicleBaseInfo.setVolume(preSealVehicle.getVolume());
                    vehicleBaseInfo.setWeight(preSealVehicle.getWeight());
                    //预封车来源为传摆，且运力类型为零担，需要判断体积是否大于0，大于0则就绪
                    if (this.isNeedCheckVolume(preSealVehicle.getPreSealSource(), preSealVehicle.getTransWay())) {
                        vehicleBaseInfo.setReady(NumberHelper.gt0(preSealVehicle.getVolume()));
                    } else {
                        vehicleBaseInfo.setReady(true);
                    }
                    vehicleNumberInfoList.add(vehicleBaseInfo);
                } else {
                    log.warn("预封车信息中车牌号为空：{}", JsonHelper.toJson(preSealVehicle));
                }
            }

            jdResponse.setData(vehicleNumberInfoList);
        } catch (Exception e) {
            jdResponse.setCode(JdResponse.CODE_ERROR);
            jdResponse.setMessage(JdResponse.MESSAGE_ERROR);
            log.error("获取车牌信息失败！，运力编码：{}", transportCode, e);
    }
        return jdResponse;
    }

    /*
     * 校验数据提交
     * */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.DmsSealVehicleServiceImpl.modifyVehicleBaseInfo", mState = JProEnum.TP)
    public JdResponse modifyVehicleBaseInfo(String transportCode, VehicleBaseInfo vehicleBaseInfo) {
        JdResponse jdResponse = new JdResponse(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
        if (transportCode == null || vehicleBaseInfo == null || vehicleBaseInfo.getVehicleNumber() == null) {
            jdResponse.setCode(JdResponse.CODE_FAIL);
            jdResponse.setMessage("运力编码和车牌号不能为空！");
            return jdResponse;
        }

        if (vehicleBaseInfo.getVolume() == null || ! NumberHelper.gt0(vehicleBaseInfo.getVolume())) {
            jdResponse.setCode(JdResponse.CODE_FAIL);
            jdResponse.setMessage("体积不能为空且必须大于0！");
            return jdResponse;
        }
        try {
            PreSealVehicle preSealVehicle = new PreSealVehicle();
            preSealVehicle.setTransportCode(transportCode);
            preSealVehicle.setVehicleNumber(vehicleBaseInfo.getVehicleNumber());
            preSealVehicle.setVolume(vehicleBaseInfo.getVolume());
            if (NumberHelper.gt0(vehicleBaseInfo.getWeight())) {
                preSealVehicle.setWeight(vehicleBaseInfo.getWeight());
            }
            preSealVehicleService.updatePreSealVehicleMeasureInfo(preSealVehicle);

        } catch (Exception e) {
            jdResponse.setCode(JdResponse.CODE_ERROR);
            jdResponse.setMessage(JdResponse.MESSAGE_ERROR);
            log.error("更新预封车重量体积信息失败，运力编码：{}，车牌信息：{}", transportCode, JsonHelper.toJson(vehicleBaseInfo), e);
        }
        return jdResponse;
    }


    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB, jKey = "DMS.WEB.DmsSealVehicleServiceImpl.checkTransportVehicleSubmit", mState = JProEnum.TP)
    public JdResponse checkTransportVehicleSubmit(String transportCode, List<String> vehicleNumberList, Boolean transportReady) {
        JdResponse jdResponse = new JdResponse(JdResponse.CODE_SUCCESS, JdResponse.MESSAGE_SUCCESS);
        if (StringHelper.isEmpty(transportCode) || vehicleNumberList == null || vehicleNumberList.isEmpty()) {
            jdResponse.setCode(JdResponse.CODE_FAIL);
            jdResponse.setMessage("运力编码和车牌信息不能为空！");
            return jdResponse;
        }
        try {
            for (String vehicleNumber : vehicleNumberList) {
                List<PreSealVehicle> preSealVehicleList = preSealVehicleService.getPreSealInfoByParams(transportCode, vehicleNumber);
                if (preSealVehicleList == null || preSealVehicleList.isEmpty()) {
                    jdResponse.setCode(JdResponse.CODE_FAIL);
                    jdResponse.setMessage("不存在车牌号【" + vehicleNumber + "】的预封车信息！");
                    return jdResponse;
                } else {
                    PreSealVehicle preSealVehicle = preSealVehicleList.get(0);

                    //预封车来源为传摆，且运力类型为零担，需要判断体积是否大于0
                    if (this.isNeedCheckVolume(preSealVehicle.getPreSealSource(), preSealVehicle.getTransWay())) {
                        //不大于0，返回有误
                        if (! NumberHelper.gt0(preSealVehicle.getVolume())) {
                            jdResponse.setCode(JdResponse.CODE_FAIL);
                            jdResponse.setMessage("由于运力编码【"+ transportCode +"】为【" + preSealVehicle.getTransWayName()+"】，车牌号【" + vehicleNumber + "】的体积不能为0，请重新录入！");
                        }
                    }
                }

            }
        } catch (Exception e) {
            jdResponse.setCode(JdResponse.CODE_ERROR);
            jdResponse.setMessage(JdResponse.MESSAGE_ERROR);
            log.error("校验提交运力车牌信息失败，运力编码：{}，车牌信息：{}", transportCode, JsonHelper.toJson(vehicleNumberList), e);
        }
        return jdResponse;
    }

    /**
     * 查询全部的未封车批次号
     */
    private List<String> getUnSealSendCodeList(Integer createSiteCode, Integer receiveSiteCode, Integer hourRange) {

        Date date = DateHelper.newTimeRangeHoursAgo(new Date(), hourRange);
        Set<String> sendCodeSet = new HashSet<>();
        //sendM获取所有批次信息
        List<SendM> sendMList = sendMService.findAllSendCodesWithStartTime(createSiteCode, receiveSiteCode, date);
        if (sendMList != null && ! sendMList.isEmpty()) {
            for(SendM sendM : sendMList) {
                //封车批次缓存中找出未封车的批次数据
                if(! newSealVehicleService.checkSendCodeIsSealed(sendM.getSendCode())) {
                    sendCodeSet.add(sendM.getSendCode());
                }
            }
        } else {
            return null;
        }

        List<String> result = new ArrayList<>(sendCodeSet);
        //利用封车记录表再次筛选sendCode
        List<String> sealedSendCodeList = sealVehiclesService.findBySealDataCodes(sendCodeSet);
        if (sealedSendCodeList != null && ! sealedSendCodeList.isEmpty()) {
            result.removeAll(sealedSendCodeList);
        }
        return result;
    }

    /**
     * 查询全部的未封车批次号
     */
    private Integer getUnSealSendCodeCount(Integer createSiteCode, Integer receiveSiteCode, Integer hourRange) {
        List<String> result = getUnSealSendCodeList(createSiteCode, receiveSiteCode, hourRange);
        return result == null ? 0 : result.size();
    }

    /**
     * 查询全部的未封车批次号
     */
    private boolean isNeedCheckVolume(Integer preSealSource, Integer transWay) {
        return preSealSource == PreSealVehicleSourceEnum.FERRY_PRE_SEAL.getCode() && transWay == TransTypeEnum.ROAD_LINGDAN.getType();
    }

    /*
    * 对象转换
    * */
    private UnSealVehicleInfo convert2UnSealVehicleInfo(PreSealVehicle preSealVehicle) {
        UnSealVehicleInfo unSealVehicleInfo = new UnSealVehicleInfo();
        unSealVehicleInfo.setTransportCode(preSealVehicle.getTransportCode());
        unSealVehicleInfo.setCreateSiteCode(preSealVehicle.getCreateSiteCode());
        unSealVehicleInfo.setCreateSiteName(preSealVehicle.getCreateSiteName());
        unSealVehicleInfo.setReceiveSiteCode(preSealVehicle.getReceiveSiteCode());
        unSealVehicleInfo.setReceiveSiteName(preSealVehicle.getReceiveSiteName());
        unSealVehicleInfo.setTransWay(preSealVehicle.getTransWay());
        unSealVehicleInfo.setTransWayName(preSealVehicle.getTransWayName());
        unSealVehicleInfo.setSendCarTime(preSealVehicle.getSendCarTime());
        unSealVehicleInfo.setPreSealSource(preSealVehicle.getPreSealSource());
        unSealVehicleInfo.setSource(preSealVehicle.getSource());
        return unSealVehicleInfo;
    }
}
