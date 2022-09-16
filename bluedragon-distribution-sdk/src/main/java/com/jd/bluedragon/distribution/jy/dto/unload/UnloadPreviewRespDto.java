package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;
import java.util.List;

/**
 * 卸车扫描完成预览数据
 **/
public class UnloadPreviewRespDto implements Serializable {

    private static final long serialVersionUID = -546411604277439598L;

    /**
     * 是否异常；0-否 1-是
     */
    private Byte abnormalFlag;

    /**
     * 待扫描数量
     */
    private Integer toScanCount;

    /**
     * 本场地多扫数量
     */
    private Integer moreScanLocalCount;

    /**
     * 非本场地多扫数量
     */
    private Integer moreScanOutCount;

    /**
     * 异常扫描类型统计数据
     */
    private List<ExcepScanDto> exceptScanDtoList;

    /**
     * 运单列表
     */
    private List<UnloadWaybillDto> unloadWaybillDtoList;

    public Byte getAbnormalFlag() {
        return abnormalFlag;
    }

    public void setAbnormalFlag(Byte abnormalFlag) {
        this.abnormalFlag = abnormalFlag;
    }

    public Integer getToScanCount() {
        return toScanCount;
    }

    public void setToScanCount(Integer toScanCount) {
        this.toScanCount = toScanCount;
    }

    public Integer getMoreScanLocalCount() {
        return moreScanLocalCount;
    }

    public void setMoreScanLocalCount(Integer moreScanLocalCount) {
        this.moreScanLocalCount = moreScanLocalCount;
    }

    public Integer getMoreScanOutCount() {
        return moreScanOutCount;
    }

    public void setMoreScanOutCount(Integer moreScanOutCount) {
        this.moreScanOutCount = moreScanOutCount;
    }

    public List<ExcepScanDto> getExceptScanDtoList() {
        return exceptScanDtoList;
    }

    public void setExceptScanDtoList(List<ExcepScanDto> exceptScanDtoList) {
        this.exceptScanDtoList = exceptScanDtoList;
    }

    public List<UnloadWaybillDto> getUnloadWaybillDtoList() {
        return unloadWaybillDtoList;
    }

    public void setUnloadWaybillDtoList(List<UnloadWaybillDto> unloadWaybillDtoList) {
        this.unloadWaybillDtoList = unloadWaybillDtoList;
    }
}
