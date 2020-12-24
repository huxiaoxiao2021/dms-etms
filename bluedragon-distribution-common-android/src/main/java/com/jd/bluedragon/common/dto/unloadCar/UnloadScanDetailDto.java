package com.jd.bluedragon.common.dto.unloadCar;

import java.io.Serializable;
import java.util.List;

/**
 * 卸车任务扫描结果
 * @author lvyuan21
 * @date 2020-12-24 15:34
 */
public class UnloadScanDetailDto implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 总单数
     */
    private Integer totalWaybillNum;

    /**
     * 总件数
     */
    private Integer totalPackageNum;

    /**
     * 已卸单数
     */
    private Integer loadWaybillAmount;

    /**
     * 已卸件数
     */
    private Integer loadPackageAmount;

    /**
     * 未卸单数
     */
    private Integer unloadWaybillAmount;

    /**
     * 未卸件数
     */
    private Integer unloadPackageAmount;

    /**
     * 包裹号转运单 权限标识
     */
    private Integer waybillAuthority;

    /**
     * 板号
     */
    private String boardCode;

    /**
     * 下一流向
     */
    private String nextSiteName;

    /**
     * 未卸件数
     */
    private List<UnloadScanDto> unloadScanDtoList;




}
