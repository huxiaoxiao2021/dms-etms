package com.jd.bluedragon.distribution.auto.service;

import com.jd.bluedragon.distribution.auto.domain.UploadData;
import com.jd.bluedragon.distribution.box.dao.BoxDao;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.gantry.domain.GantryDeviceConfig;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.weight.domain.OpeEntity;
import com.jd.bluedragon.distribution.weight.domain.OpeObject;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

/**
 * 龙门架测量体积消费
 * Created by wangtingwei on 2016/3/14.
 */
@Service("scannerFrameMeasureConsume")
public class ScannerFrameMeasureConsume implements ScannerFrameConsume {

    private static final Log logger= LogFactory.getLog(ScannerFrameMeasureConsume.class);

    @Autowired
    private TaskService taskService;


    @Autowired
    private BoxService boxService;

    @Override
    public boolean onMessage(UploadData uploadData, GantryDeviceConfig config) {
        if(SerialRuleUtil.isMatchAllPackageNo(uploadData.getBarCode())) { // 如果是包裹号，添加称重任务
            OpeEntity opeEntity = new OpeEntity();
            opeEntity.setOpeType(1);//分拣中心称重、长宽高
            opeEntity.setWaybillCode(SerialRuleUtil.getWaybillCode(uploadData.getBarCode()));
            opeEntity.setOpeDetails(new ArrayList<OpeObject>());

            OpeObject obj = new OpeObject();
            obj.setOpeSiteId(config.getCreateSiteCode());
            obj.setOpeSiteName(config.getCreateSiteName());
            obj.setpWidth(uploadData.getWidth());
            obj.setpLength(uploadData.getLength());
            obj.setpHigh(uploadData.getHeight());
            obj.setPackageCode(uploadData.getBarCode());
            obj.setOpeUserId(config.getOperateUserId());
            obj.setOpeUserName(config.getUpdateUserName());
            obj.setOpeTime(DateHelper.formatDateTime(uploadData.getScannerTime()));
            obj.setpWeight(uploadData.getWeight());//增加称重  2016年12月12日10:54:53  by guoyongzhi
            opeEntity.getOpeDetails().add(obj);
            String body = "[" + JsonHelper.toJson(opeEntity) + "]";
            Task task = new Task();
            task.setBody(body);
            task.setType(Task.TASK_TYPE_WEIGHT);
            task.setTableName(Task.getTableName(Task.TASK_TYPE_WEIGHT));
            task.setCreateSiteCode(opeEntity.getOpeDetails().get(0).getOpeSiteId());
            task.setKeyword1(String.valueOf(opeEntity.getOpeDetails().get(0).getOpeSiteId()));
            task.setKeyword2("上传长宽高、重量");
            task.setBody(body);
            task.setBoxCode("");
            task.setSequenceName(Task.getSequenceName(task.getTableName()));
            task.setReceiveSiteCode(opeEntity.getOpeDetails().get(0).getOpeSiteId());
            task.setOwnSign(BusinessHelper.getOwnSign());
            int result=taskService.add(task);
            return result>0;
        } else if (SerialRuleUtil.isMatchBoxCode(uploadData.getBarCode())) {  //如果是箱号，更新箱号的长宽高信息
            //return boxDao.updateVolumeByCode(toBoxDto(uploadData)) > 0;
            return boxService.updateVolumeByCode(toBoxDto(uploadData))>0;
        }

        return Boolean.TRUE; //不是箱号，也不是包裹，直接返回成功，不处理
    }

    private Box toBoxDto(UploadData uploadData) {
        Box box = new Box();
        box.setCode(uploadData.getBarCode());
        box.setLength(uploadData.getLength());
        box.setWidth(uploadData.getWidth());
        box.setHeight(uploadData.getHeight());
        return box;
    }

    public static void main(String[] args) {
        Box box = new Box();
        box.setCode("BC010F002010Y04200061079");
        box.setLength(4f);
        box.setWidth(10f);
        box.setHeight(7f);
        System.out.println(com.jd.bluedragon.distribution.api.utils.JsonHelper.toJson(box));
    }
}
