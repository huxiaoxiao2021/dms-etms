package com.jd.bluedragon.common.dto.operation.workbench.strand;

import java.io.Serializable;
import java.util.List;

/**
 * 拣运app-滞留上报扫描请求体
 *
 * @author hujiping
 * @date 2023/3/27 4:33 PM
 */
public class JyStrandReportScanResp implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务主键
     */
    private String bizId;

    /**
     * 已扫明细
     */
    private List<JyStrandReportScanVO> scanVOList;

    /**
     * 扫描容器内的数量
     */
    private Integer scanNum;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public List<JyStrandReportScanVO> getScanVOList() {
        return scanVOList;
    }

    public void setScanVOList(List<JyStrandReportScanVO> scanVOList) {
        this.scanVOList = scanVOList;
    }

    public Integer getScanNum() {
        return scanNum;
    }

    public void setScanNum(Integer scanNum) {
        this.scanNum = scanNum;
    }
}
