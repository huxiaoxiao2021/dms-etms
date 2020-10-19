package com.jd.bluedragon.distribution.goodsLoadScan.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.goodsLoadingScanning.request.GoodsLoadingReq;
import com.jd.bluedragon.distribution.goodsLoadScan.GoodsLoadScanConstants;
import com.jd.bluedragon.distribution.goodsLoadScan.domain.GoodsLoadScan;
import com.jd.bluedragon.distribution.goodsLoadScan.service.LoadScanService;
import com.jd.bluedragon.distribution.loadAndUnload.LoadCar;
import com.jd.bluedragon.distribution.loadAndUnload.dao.LoadCarDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;

public class LoadScanServiceImpl implements LoadScanService {
    private final static Logger log = LoggerFactory.getLogger(LoadScanServiceImpl.class);

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private LoadCarDao loadCarDao;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Override
    public JdCResponse goodsLoadingDeliver(GoodsLoadingReq req) {
        JdCResponse response = new JdCResponse();

        SendBizSourceEnum bizSource = SendBizSourceEnum.ANDROID_PDA_LOAD_SEND;
        SendM domain = new SendM();
        domain.setReceiveSiteCode(req.getReceiveSiteCode());
        domain.setCreateSiteCode(req.getCurrentOperate().getSiteCode());
        domain.setSendCode(req.getSendCode());
        domain.setCreateUser(req.getUser().getUserName());
        domain.setCreateUserCode(req.getUser().getUserCode());
        domain.setSendType(Constants.BUSSINESS_TYPE_POSITIVE);
        domain.setYn(GoodsLoadScanConstants.YN_Y);
        domain.setCreateTime(new Date());
        domain.setOperateTime(new Date());

        log.info("LoadScanServiceImpl#goodsLoadingDeliver--begin 装车调用发货:来源【" + bizSource +  "】参数" + JsonHelper.toJson(domain) + "】");
        deliveryService.packageSend(bizSource, domain);
        log.info("LoadScanServiceImpl#goodsLoadingDeliver--end 装车调用发货:来源【" + bizSource +  "】参数" + JsonHelper.toJson(domain) + "】");

        LoadCar loadCar = new LoadCar();
        loadCar.setId(req.getTaskId());
        loadCar.setStatus(GoodsLoadScanConstants.GOODS_LOAD_TASK_STATUS_END);
        boolean flagRes = loadCarDao.updateLoadCarById(loadCar);
        if(flagRes == false) {
            log.info("LoadScanServiceImpl#goodsLoadingDeliver--发货完成后修改任务状态失败，发货信息【" + JsonHelper.toJson(domain) + "】");
            response.toSucceed("发货状态修改失败");
            return response;
        }
        log.info("LoadScanServiceImpl#goodsLoadingDeliver--发货成功，发货信息【" + JsonHelper.toJson(domain) + "】");
        response.toSucceed("发货成功");
        return response;
    }

    @Override
    public Integer findTaskStatus(Long taskId) {

        LoadCar lc = loadCarDao.findLoadCarById(taskId);
        log.info("LoadScanServiceImpl#findTaskStatus--根据任务号【" + JsonHelper.toJson(taskId) + "】查询任务信息,出参【" + JsonHelper.toJson(lc) +"】");

        if(lc != null && lc.getStatus() != null) {
            return lc.getStatus();
        }
        return null;
    }

    @Override
    public GoodsLoadScan queryByWaybillCodeAndTaskId(String waybillCode, Long taskId) {
        String key = taskId + "_" + waybillCode;
        return jimdbCacheService.get(key, GoodsLoadScan.class);
    }

    @Override
    public boolean updateGoodsLoadScanAmount(GoodsLoadScan param) {
        /*
        加锁
        查缓存
        更改已装 未装数量（缓存中库存-已装）
        缓存记录变更
        库中数据变更
         */

                return false;
    }

}
