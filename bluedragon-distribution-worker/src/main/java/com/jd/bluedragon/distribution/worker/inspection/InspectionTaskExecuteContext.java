package com.jd.bluedragon.distribution.worker.inspection;

import com.jd.bluedragon.distribution.framework.TaskExecuteContext;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.etms.waybill.dto.BigWaybillDto;

import java.util.List;

/**
 * Created by wangtingwei on 2017/1/16.
 */
public class InspectionTaskExecuteContext extends TaskExecuteContext {
    /**
     * 验货数据列表
     */
    private List<Inspection> inspectionList;

    /**
     * 收货确认列表
     */
    private List<CenConfirm> cenConfirmList;
    /**
     * 运单对象
     */
    private BigWaybillDto bigWaybillDto;

    /**
     * 业务操作关键字
     */
    private String      businessKey;

    public List<Inspection> getInspectionList() {
        return inspectionList;
    }

    public void setInspectionList(List<Inspection> inspectionList) {
        this.inspectionList = inspectionList;
    }

    public BigWaybillDto getBigWaybillDto() {
        return bigWaybillDto;
    }

    public void setBigWaybillDto(BigWaybillDto bigWaybillDto) {
        this.bigWaybillDto = bigWaybillDto;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public List<CenConfirm> getCenConfirmList() {
        return cenConfirmList;
    }

    public void setCenConfirmList(List<CenConfirm> cenConfirmList) {
        this.cenConfirmList = cenConfirmList;
    }
}
