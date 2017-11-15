package com.jd.bluedragon.distribution.express.domain;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.List;

/**
 * 快运到齐包裹明细实体
 *
 * @author zhangleqi
 * @date 2017/11/14
 */
public class ExpressPackageDetailsResponse extends JdResponse {
    private static final long serialVersionUID = 9015640463647589704L;
    public static final String STATUS_SPLIT_CHAR = ",";

    /**
     * 未扫描包裹号列表
     */
    private List<String> unScanPackageCodes;

    /**
     * 箱子总数
     */
    private int boxSize;

    /**
     * 预分拣站点名称
     */
    private String preSortingSite;

    /**
     * 包裹数
     */
    private int packageSize;

    /**
     * 已扫描包裹数
     */
    private int hasScanPackageSize;

    /**
     * 未扫描包裹数
     */
    private int unScanPackageSize;

    public ExpressPackageDetailsResponse() {
    }

    public ExpressPackageDetailsResponse(Integer code, String message) {
        super(code, message);
    }

    public List<String> getUnScanPackageCodes() {
        return unScanPackageCodes;
    }

    public void setUnScanPackageCodes(List<String> unScanPackageCodes) {
        this.unScanPackageCodes = unScanPackageCodes;
    }

    public int getBoxSize() {
        return boxSize;
    }

    public void setBoxSize(int boxSize) {
        this.boxSize = boxSize;
    }

    public String getPreSortingSite() {
        return preSortingSite;
    }

    public void setPreSortingSite(String preSortingSite) {
        this.preSortingSite = preSortingSite;
    }

    public int getPackageSize() {
        return packageSize;
    }

    public void setPackageSize(int packageSize) {
        this.packageSize = packageSize;
    }

    public int getHasScanPackageSize() {
        return hasScanPackageSize;
    }

    public void setHasScanPackageSize(int hasScanPackageSize) {
        this.hasScanPackageSize = hasScanPackageSize;
    }

    public int getUnScanPackageSize() {
        return unScanPackageSize;
    }

    public void setUnScanPackageSize(int unScanPackageSize) {
        this.unScanPackageSize = unScanPackageSize;
    }
}
