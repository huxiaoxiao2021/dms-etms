package com.jd.bluedragon.distribution.goodsLoadScan.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.service.LoadScanService;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
import com.jd.bluedragon.distribution.loadAndUnload.dao.LoadCarDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

public class LoadScanServiceImpl implements LoadScanService {
    private final static Logger log = LoggerFactory.getLogger(LoadScanServiceImpl.class);

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private LoadCarDao loadCarDao;

    @Override
    public JdCResponse goodsLoadingDeliver(GoodsLoadingReq req) {
        JdCResponse response = new JdCResponse();

        SendBizSourceEnum bizSource = SendBizSourceEnum.ANDROID_PDA_SEND;
        SendM domain = new SendM();
        domain.setReceiveSiteCode(req.getReceiveSiteCode());
        domain.setCreateSiteCode(req.getCreateSiteCode());
        domain.setSendCode(req.getSendCode());
        domain.setCreateUser(req.getCreateUser());
        domain.setCreateUserCode(req.getCreateUserCode());
        domain.setSendType(Constants.BUSSINESS_TYPE_POSITIVE);
        domain.setYn(1);
        domain.setCreateTime(new Date(System.currentTimeMillis() + Constants.DELIVERY_DELAY_TIME));
        domain.setOperateTime(new Date(System.currentTimeMillis() + Constants.DELIVERY_DELAY_TIME));

        log.info("LoadScanServiceImpl#goodsLoadingDeliver--begin 装车调用发货:来源【" + bizSource +  "】参数" + JsonHelper.toJson(domain) + "】");
        deliveryService.packageSend(bizSource, domain);
        log.info("LoadScanServiceImpl#goodsLoadingDeliver--end 装车调用发货:来源【" + bizSource +  "】参数" + JsonHelper.toJson(domain) + "】");

        LoadCar loadCar = new LoadCar();
        loadCar.setId(req.getTaskId());
        loadCar.setStatus(GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END);
        boolean flagRes = loadCarDao.updateLoadCarById(loadCar);
        if(flagRes == false) {
            response.toSucceed("发货状态修改失败");
            return response;
        }

        response.toSucceed("发货成功");
        return response;
    }
}
