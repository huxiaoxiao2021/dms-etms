package com.jd.bluedragon.distribution.batchForward.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.request.BatchForwardRequest;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.batchForward.service.BatchForwardService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.domain.SendResult;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


/**
 * Created by hujiping on 2018/8/1.
 */
@Service("batchForwardService")
public class BatchForwardServiceImpl implements BatchForwardService {

    private final Logger logger = Logger.getLogger(BatchForwardServiceImpl.class);

    @Autowired
    private RedisManager redisManager;
    @Autowired
    private TaskService taskService;
    @Autowired
    private SendMDao sendMDao;
    @Autowired
    private DeliveryService deliveryService;
    @Autowired
    private SiteService siteService;
    @Autowired
    private SendDatailDao sendDatailDao;

    @Override
    public SendResult batchSend(BatchForwardRequest request) {

        //批次是否封车校验
        if(checkSendCodeIsSealed(request.getNewSendCode())){
            return new SendResult(SendResult.CODE_SENDED, "新批次号已操作封车，请换批次！");
        }
        //插入批次转发的任务
        insertBatchForwardTask(request);

        return null;
    }

    @Override
    public boolean dealBatchForwardTask(Task task) {
        this.logger.info("批次转发开始：" + JsonHelper.toJson(task));
        BatchForwardRequest request = JsonHelper.fromJson(task.getBody(), BatchForwardRequest.class);
        //获得旧批次号下的所有sendM
        String oldSendCode = request.getOldSendCode();
        Integer oldCreateSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(oldSendCode);
        Integer oldReceiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(oldSendCode);
        List<SendM> oldSendMList = deliveryService.getSendMBySendCodeAndSiteCode(oldSendCode, oldCreateSiteCode, oldReceiveSiteCode);
        //新批次
        String newSendCode = request.getNewSendCode();
        Integer newCreateSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(oldSendCode);
        Integer newReceiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(oldSendCode);
        SendM domain = new SendM();
        domain.setCreateSiteCode(newCreateSiteCode);
        domain.setReceiveSiteCode(newReceiveSiteCode);
        domain.setSendCode(newSendCode);
        domain.setCreateUser(request.getUserName());
        domain.setCreateUserCode(request.getUserCode());
        domain.setSendType(request.getBusinessType());
        domain.setYn(1);
        domain.setCreateTime(new Date(System.currentTimeMillis() + Constants.DELIVERY_DELAY_TIME));
        domain.setOperateTime(new Date(System.currentTimeMillis() + Constants.DELIVERY_DELAY_TIME));
        for(SendM oldSendM : oldSendMList){
            domain.setBoxCode(oldSendM.getBoxCode());
            packageSend(domain);
        }

        return true;
    }

    /**
     * 批次转发数据落库，写相关的异步任务
     * @param domain
     */
    private void packageSend(SendM domain){
        //插入SEND_M
        this.sendMDao.insertSendM(domain);
        // 判断是按箱发货还是包裹发货
        if (!SerialRuleUtil.isMatchBoxCode(domain.getBoxCode())) {
            // 按包裹 补分拣任务
            pushSorting(domain);//大件写TASK_SORTING
        } else {
            // 按箱
            SendDetail tSendDatail = new SendDetail();
            tSendDatail.setBoxCode(domain.getBoxCode());
            tSendDatail.setCreateSiteCode(domain.getCreateSiteCode());
            tSendDatail.setReceiveSiteCode(domain.getReceiveSiteCode());
            //更新SEND_D状态
            sendDatailDao.updateCancel(tSendDatail);
        }

        // 判断是否是中转发货
        deliveryService.transitSend(domain);
        deliveryService.pushStatusTask(domain);
    }

    /**
     * 推分拣任务
     * @param domain
     */
    private void pushSorting(SendM domain) {
        BaseStaffSiteOrgDto create = siteService.getSite(domain.getCreateSiteCode());
        String createSiteName = null != create ? create.getSiteName() : null;
        BaseStaffSiteOrgDto receive = siteService.getSite(domain.getReceiveSiteCode());
        String receiveSiteName = null != receive ? receive.getSiteName() : null;
        Task task = new Task();
        task.setBoxCode(domain.getBoxCode());
        task.setCreateSiteCode(domain.getCreateSiteCode());
        task.setReceiveSiteCode(domain.getReceiveSiteCode());
        task.setBusinessType(10);
        task.setType(Task.TASK_TYPE_SORTING);
        task.setTableName(Task.getTableName(Task.TASK_TYPE_SORTING));
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(domain.getCreateSiteCode().toString());
        task.setKeyword2(domain.getBoxCode());
        task.setOperateTime(new Date(domain.getOperateTime().getTime()-Constants.DELIVERY_DELAY_TIME));
        taskService.initFingerPrint(task);
        task.setOwnSign(BusinessHelper.getOwnSign());
        SortingRequest sortDomain = new SortingRequest();
        sortDomain.setOperateTime(DateHelper.formatDateTimeMs(new Date(domain.getOperateTime().getTime()-Constants.DELIVERY_DELAY_TIME)));
        sortDomain.setBoxCode(domain.getBoxCode());
        sortDomain.setUserCode(domain.getCreateUserCode());
        sortDomain.setUserName(domain.getCreateUser());
        sortDomain.setPackageCode(domain.getBoxCode());
        sortDomain.setSiteName(createSiteName);
        sortDomain.setIsCancel(0);
        sortDomain.setSiteCode(domain.getCreateSiteCode());
        sortDomain.setBsendCode("");
        sortDomain.setIsLoss(0);
        sortDomain.setFeatureType(0);
        sortDomain.setUserName(domain.getCreateUser());
        sortDomain.setBusinessType(10);
        sortDomain.setWaybillCode(SerialRuleUtil.getWaybillCode(domain.getBoxCode()));
        sortDomain.setReceiveSiteCode(domain.getReceiveSiteCode());
        sortDomain.setReceiveSiteName(receiveSiteName);
        task.setBody(JsonHelper.toJson(new SortingRequest[]{sortDomain}));
        taskService.add(task, true);
        logger.info("批次转发插入task_sorting" + JsonHelper.toJson(task));
    }

    private void insertBatchForwardTask(BatchForwardRequest request) {

        Task task = new Task();

        Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(request.getNewSendCode());
        Integer receiveSiteCode = SerialRuleUtil.getReceiveSiteCodeFromSendCode(request.getNewSendCode());

        task.setCreateSiteCode(createSiteCode);
        task.setReceiveSiteCode(receiveSiteCode);
        task.setBusinessType(10);
        task.setType(Task.TASK_TYPE_SEND_BATCHFORWARD);
        task.setTableName(Task.getTableName(Task.TASK_TYPE_SEND_BATCHFORWARD));
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setKeyword1(createSiteCode.toString());
        task.setKeyword2(request.getNewSendCode());
        task.setOperateTime(DateHelper.parseDate(request.getOperateTime()));
        taskService.initFingerPrint(task);
        task.setOwnSign(BusinessHelper.getOwnSign());

        task.setBody(JsonHelper.toJson(request));
        taskService.add(task, true);
        logger.info("批次转发插入task_send" + JsonHelper.toJson(task));
    }

    /**
     * 校验批次号是否封车:默认返回false
     * @param sendCode
     * @return
     */
    private boolean checkSendCodeIsSealed(String sendCode) {
        boolean result = false;
        try {
            String isSeal = redisManager.getCache(Constants.CACHE_KEY_PRE_SEAL_SENDCODE+sendCode);
            logger.info("redis取封车批次号"+sendCode+"结果："+isSeal);
            if(StringUtils.isNotBlank(isSeal) && Constants.STRING_FLG_TRUE.equals(isSeal)){
                result = true;
            }
        }catch (Throwable e){
            logger.warn("redis取封车批次号失败："+e.getMessage());
        }
        return result;
    }



}
