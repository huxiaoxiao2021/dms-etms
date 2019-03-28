package com.jd.bluedragon.distribution.weight.domain;

import com.jd.bluedragon.distribution.kuaiyun.weight.domain.WaybillWeightVO;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 包裹称重 rest接口 交互实体
 */
public class PackWeightVO extends WaybillWeightVO{

    private Double length;

    private Double width;

    private Double high;

    private String erpCode;

    private Integer organizationCode;

    private String organizationName;

    public String getErpCode() {
        return erpCode;
    }

    public void setErpCode(String erpCode) {
        this.erpCode = erpCode;
    }

    public Integer getOrganizationCode() {
        return organizationCode;
    }

    public void setOrganizationCode(Integer organizationCode) {
        this.organizationCode = organizationCode;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    /**
     * 包裹称重转换异步任务实体
     * @param remark
     * @return
     */
    public Task convertToTask(String remark){
        Task task = new Task();
        String waybillCode;
        String packageCode;
        if(WaybillUtil.isPackageCode(getCodeStr())){
            //现阶段只支持包裹称重转任务
            waybillCode = WaybillUtil.getWaybillCode(getCodeStr());
            packageCode = getCodeStr();
        }else{
            throw new RuntimeException("称重对象转换任务失败，不支持非包裹称重");
        }
        task.setType(Task.TASK_TYPE_WEIGHT);
        task.setTableName(Task.getTableName(Task.TASK_TYPE_WEIGHT));
        task.setSequenceName(Task.getSequenceName(task.getTableName()));
        task.setOwnSign(BusinessHelper.getOwnSign());
        task.setKeyword1(waybillCode);
        task.setKeyword2(remark);
        task.setCreateSiteCode(getOperatorSiteCode());
        task.setReceiveSiteCode(0);
        /*[{
              "waybillCode" : "JDVE00000030939",
              "opeType" : 1,
              "opeDetails" : [ {
                "packageCode" : "JDVE00000030939-1-20-",
                "pWeight" : 2.33,
                "pLength" : 0.0,
                "pWidth" : 0.0,
                "pHigh" : 0.0,
                "opeUserId" : 10053,
                "opeUserName" : "邢松",
                "opeSiteId" : 910,
                "opeSiteName" : "北京马驹桥分拣中心",
                "opeTime" : "2019-01-11 17:15:15"
              } ]
        }]*/
        OpeEntity opeEntity = new OpeEntity();
        opeEntity.setOpeType(1);
        opeEntity.setWaybillCode(waybillCode);
        List<OpeObject> opeObjects = new ArrayList<OpeObject>();
        //以后有需求可扩展成多个包裹
        OpeObject opeObject = new OpeObject();
        opeObjects.add(opeObject);
        opeEntity.setOpeDetails(opeObjects);


        opeObject.setPackageCode(packageCode);
        if(!((getLength()==null || getLength().equals(new Double(0)))
                && (getWidth() == null || getWidth().equals(new Double(0)))
                && (getHigh()==null || getHigh().equals(new Double(0))))){
            //长宽高默认三个同时输入
            opeObject.setpLength(Float.valueOf(getLength().toString()));
            opeObject.setpWidth(Float.valueOf(getWidth().toString()));
            opeObject.setpHigh(Float.valueOf(getHigh().toString()));
        }
        opeObject.setpWeight(Float.valueOf(getWeight().toString()));
        opeObject.setOpeUserId(getOperatorId());
        opeObject.setOpeUserName(getOperatorName());
        opeObject.setOpeSiteId(getOperatorSiteCode());
        opeObject.setOpeSiteName(getOperatorSiteName());
        opeObject.setOpeTime(DateHelper.formatDate(new Date(getOperateTimeMillis()),DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2));

        task.setBody("["+JsonHelper.toJson(opeEntity)+"]");


        return task;
    }

    /**
     * 校验称重入参
     * @param message 提示语
     * @return
     */
    public boolean checkParam(StringBuilder message){

        if(!WaybillUtil.isPackageCode(getCodeStr())){
            message.append("包裹号格式不正确");
            return false;
        }

        if(!(((getLength()==null || getLength().equals(new Double(0)))
                && (getWidth() == null || getWidth().equals(new Double(0)))
                && (getHigh()==null || getHigh().equals(new Double(0))))
                || (((getLength()!=null && !getLength().equals(new Double(0)))
                && (getWidth() != null && !getWidth().equals(new Double(0)))
                && (getHigh()!=null && !getHigh().equals(new Double(0)))))
                )){
            //长宽高必须同时录入
            message.append("长宽高必须同时录入");
            return false;
        }

        if(getWeight() == null){
            message.append("重量必须录入");
            return false;
        }

        return true;
    }
}
