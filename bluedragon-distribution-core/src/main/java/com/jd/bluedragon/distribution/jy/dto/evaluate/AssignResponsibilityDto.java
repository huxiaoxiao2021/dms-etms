package com.jd.bluedragon.distribution.jy.dto.evaluate;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class AssignResponsibilityDto implements Serializable {
    private static final long serialVersionUID = 8012183473104976411L;

    /**
     * 单次评价唯一id
     */
    private String sourceSystemId;
    /**
     * 小哥app   1
     * 京象app   2
     * 分拣app   3
     */
    private Integer sourceSystem;
    /**
     * 装车 1
     * 卸车 2
     */
    private Integer link;
    /**
     * SC编码
     * 若link=2，必填
     * 若link=1，不必填，batchCodes必填
     */
    private String scCode;
    /**
     * 装车场地id
     */
    private String loadSiteCode;
    /**
     * 装车场地名称
     */
    private String loadSiteName;
    /**
     * 卸车场地id
     */
    private String unloadSiteCode;
    /**
     * 卸车场地名称
     */
    private String unloadSiteName;
    /**
     * 图片信息
     */
    private List<ImgInfo> imgInfos;
    /**
     * 评价人erp
     */
    private String evaluatorErp;
    /**
     * 评价时间
     */
    private Date evaluateTime;
    /**
     * 批次号
     */
    private List<String> batchCodes;

    public String getSourceSystemId() {
        return sourceSystemId;
    }

    public void setSourceSystemId(String sourceSystemId) {
        this.sourceSystemId = sourceSystemId;
    }

    public Integer getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(Integer sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public Integer getLink() {
        return link;
    }

    public void setLink(Integer link) {
        this.link = link;
    }

    public String getScCode() {
        return scCode;
    }

    public void setScCode(String scCode) {
        this.scCode = scCode;
    }

    public String getLoadSiteCode() {
        return loadSiteCode;
    }

    public void setLoadSiteCode(String loadSiteCode) {
        this.loadSiteCode = loadSiteCode;
    }

    public String getLoadSiteName() {
        return loadSiteName;
    }

    public void setLoadSiteName(String loadSiteName) {
        this.loadSiteName = loadSiteName;
    }

    public String getUnloadSiteCode() {
        return unloadSiteCode;
    }

    public void setUnloadSiteCode(String unloadSiteCode) {
        this.unloadSiteCode = unloadSiteCode;
    }

    public String getUnloadSiteName() {
        return unloadSiteName;
    }

    public void setUnloadSiteName(String unloadSiteName) {
        this.unloadSiteName = unloadSiteName;
    }

    public List<ImgInfo> getImgInfos() {
        return imgInfos;
    }

    public void setImgInfos(List<ImgInfo> imgInfos) {
        this.imgInfos = imgInfos;
    }

    public String getEvaluatorErp() {
        return evaluatorErp;
    }

    public void setEvaluatorErp(String evaluatorErp) {
        this.evaluatorErp = evaluatorErp;
    }

    public Date getEvaluateTime() {
        return evaluateTime;
    }

    public void setEvaluateTime(Date evaluateTime) {
        this.evaluateTime = evaluateTime;
    }

    public List<String> getBatchCodes() {
        return batchCodes;
    }

    public void setBatchCodes(List<String> batchCodes) {
        this.batchCodes = batchCodes;
    }
}
