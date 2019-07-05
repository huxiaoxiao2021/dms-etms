package com.jd.bluedragon.common.dto.waybillnocollection.response;

import java.io.Serializable;
import java.util.List;

/**
 * DifferentialQueryResultDto
 * 一车一单发货、组板、建箱差异查询返回结果
 *
 * @author jiaowenqiang
 * @date 2019/7/5
 */
public class DifferentialQueryResultDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<String> packageCodeList;

    @Override
    public String toString() {
        return "DifferentialQueryResultDto{" +
                "packageCodeList=" + packageCodeList +
                '}';
    }

    public List<String> getPackageCodeList() {
        return packageCodeList;
    }

    public void setPackageCodeList(List<String> packageCodeList) {
        this.packageCodeList = packageCodeList;
    }
}
