package com.jd.bluedragon.distribution.inspection.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName InspectionPackProgress
 * @Description 运单包裹验货情况
 * @Author wangyinhui3
 * @Date 2019/12/11
 **/
public class InspectionPackProgress implements Serializable {

    private static final long serialVersionUID = 2162958112016542717L;

    private String waybillCode;

    private List<CheckPack> checkedPackNos;

    private List<CheckPack> unCheckedPackNos;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public List<CheckPack> getCheckedPackNos() {
        return checkedPackNos;
    }

    public void setCheckedPackNos(List<CheckPack> checkedPackNos) {
        this.checkedPackNos = checkedPackNos;
    }

    public List<CheckPack> getUnCheckedPackNos() {
        return unCheckedPackNos;
    }

    public void setUnCheckedPackNos(List<CheckPack> unCheckedPackNos) {
        this.unCheckedPackNos = unCheckedPackNos;
    }

    public static class CheckPack implements Serializable {

        private String packNo;

        public String getPackNo() {
            return packNo;
        }

        public void setPackNo(String packNo) {
            this.packNo = packNo;
        }
    }
}
