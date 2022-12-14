package com.jd.bluedragon.openplateform;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.request.SortingRequest;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.inspection.InspectionBizSourceEnum;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.send.utils.SendBizSourceEnum;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.openplateform.entity.JYCargoOperateEntity;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.openplateform
 * @ClassName: JYOpenCargoOperateServiceImpl
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/7 01:00
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Service
@Slf4j
public class JYOpenCargoOperateServiceImpl implements IJYOpenCargoOperate {

    @Autowired
    private TaskService taskService;

    @Autowired
    private DeliveryService deliveryService;

    @Override
    public InvokeResult<Boolean> inspection(JYCargoOperateEntity entity) {
        Integer businessType = Constants.BUSSINESS_TYPE_POSITIVE;
        InspectionRequest inspection = new InspectionRequest();
        inspection.setUserCode(entity.getOperatorInfo().getOperateUserId());
        inspection.setUserName(entity.getOperatorInfo().getOperateUserName());
        inspection.setSiteCode(entity.getOperatorInfo().getOperateSiteId());
        inspection.setSiteName(entity.getOperatorInfo().getOperateSiteName());
        inspection.setOperateTime(DateFormatUtils.format(entity.getOperatorInfo().getOperateTime(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
        inspection.setBusinessType(businessType);
        inspection.setPackageBarOrWaybillCode(entity.getBarcode());
        inspection.setBizSource(InspectionBizSourceEnum.valueOf(entity.getRequestProfile().getSysSource()).getCode());

        TaskRequest request = new TaskRequest();
        request.setBusinessType(businessType);
        request.setKeyword1(String.valueOf(entity.getOperatorInfo().getOperateSiteId()));
        request.setKeyword2(entity.getBarcode());
        request.setType(Task.TASK_TYPE_INSPECTION);
        request.setOperateTime(DateFormatUtils.format(entity.getOperatorInfo().getOperateTime(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
        request.setSiteCode(entity.getCreateSiteId());
        request.setSiteName(entity.getCreateSiteName());
        request.setUserCode(entity.getOperatorInfo().getOperateUserId());
        request.setUserName(entity.getOperatorInfo().getOperateUserName());
        //request.setBody();
        String eachJson = Constants.PUNCTUATION_OPEN_BRACKET
                + JsonHelper.toJson(inspection)
                + Constants.PUNCTUATION_CLOSE_BRACKET;
        Task task = this.taskService.toTask(request, eachJson);
        if (log.isDebugEnabled()) {
            log.debug("验货任务插入{}", JsonHelper.toJson(task));
        }
        this.taskService.add(task, true);
        return new InvokeResult<>();
    }

    @Override
    public InvokeResult<Boolean> sorting(JYCargoOperateEntity entity) {

        Task task=new Task();
        task.setKeyword1(entity.getBoxCode());
        task.setKeyword2(entity.getPackageCode());
        task.setCreateSiteCode(entity.getCreateSiteId());
        task.setReceiveSiteCode(entity.getReceiveSiteId());
        task.setCreateTime(new Date(entity.getOperatorInfo().getOperateTime()));
        task.setType(Task.TASK_TYPE_SORTING);
        task.setBoxCode(entity.getBoxCode());
        task.setTableName(Task.getTableName(task.getType()));
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        StringBuilder fingerprint = new StringBuilder("");
        fingerprint.append(task.getCreateSiteCode()).append("_")
                .append(task.getReceiveSiteCode()).append("_").append(task.getBusinessType())
                .append("_").append(task.getBoxCode()).append("_").append(task.getKeyword2())
                .append("_").append(DateHelper.formatDateTimeMs(task.getOperateTime()));
        task.setFingerprint(Md5Helper.encode(fingerprint.toString()));
        List<SortingRequest> list=new ArrayList<SortingRequest>(1);

        SortingRequest request=new SortingRequest();
        request.setBoxCode(entity.getBoxCode());
        request.setFeatureType(0);
        request.setIsCancel(0);
        request.setIsLoss(0);
        request.setPackageCode(entity.getPackageCode());
        request.setReceiveSiteCode(entity.getReceiveSiteId());
        request.setReceiveSiteName(entity.getReceiveSiteName());
        request.setWaybillCode(entity.getWaybillCode());
        request.setPackageCode(entity.getPackageCode());
        request.setBusinessType(10);
        request.setOperateTime(DateFormatUtils.format(entity.getOperatorInfo().getOperateTime(), DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));
        request.setSiteCode(entity.getCreateSiteId());
        request.setSiteName(entity.getCreateSiteName());
        request.setUserCode(entity.getOperatorInfo().getOperateUserId());
        request.setUserName(entity.getOperatorInfo().getOperateUserName());
        list.add(request);
        task.setOwnSign(BusinessHelper.getOwnSign()); //fix 增加ownsign避免直接写库没有ownsign问题
        task.setBody(JsonHelper.toJson(list));
        log.info("插入分拣任务为：{}",task.toString());
        taskService.add(task,true);
        return new InvokeResult<>();
    }

    @Override
    public InvokeResult<Boolean> send(JYCargoOperateEntity entity) {

        SendBizSourceEnum bizSource = SendBizSourceEnum.valueOf(entity.getRequestProfile().getSysSource());
        SendM domain = new SendM();
        domain.setReceiveSiteCode(entity.getReceiveSiteId());
        domain.setCreateSiteCode(entity.getCreateSiteId());
        domain.setSendCode(entity.getSendCode());
        domain.setBoxCode(entity.getBarcode());//包裹号+箱号
        domain.setCreateUser(entity.getOperatorInfo().getOperateUserName());
        domain.setCreateUserCode(entity.getOperatorInfo().getOperateUserId());

        domain.setSendType(Constants.BUSSINESS_TYPE_POSITIVE);
        domain.setYn(Constants.YN_YES);

        if (entity.getOperatorInfo().getOperateTime() != null) {
            domain.setCreateTime(new Date(entity.getOperatorInfo().getOperateTime()));
            domain.setOperateTime(new Date(entity.getOperatorInfo().getOperateTime()));
        } else {
            domain.setCreateTime(new Date());
            domain.setOperateTime(new Date());
        }

        if (log.isDebugEnabled()) {
            log.debug("拣运开放发货--begin--参数【{}】", JsonHelper.toJson(domain));
        }
        //调用一车一单发货接口
        deliveryService.packageSend(bizSource, domain);
        if (log.isDebugEnabled()) {
            log.debug("拣运开放发货--end--参数【{}】", JsonHelper.toJson(domain));
        }
        return new InvokeResult<>();
    }
}
