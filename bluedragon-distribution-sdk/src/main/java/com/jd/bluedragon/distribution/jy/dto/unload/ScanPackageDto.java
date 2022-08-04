package com.jd.bluedragon.distribution.jy.dto.unload;



import java.io.Serializable;

public class ScanPackageDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = 1954736364227854974L;
    /**
     * 任务主键
     */
    private String taskId;
    /**
     * 卸车任务
     */
    private String bizId;
    /**
     * 封车编码
     */
    private String sealCarCode;
    /**
     * 扫描的包裹/运单号
     */
    private String scanCode;

    /**
     * 扫包裹 1按包裹 2按运单
     */
    private Integer type;

    /**
     * 作业类型  0-流水线  1-人工
     */
    private Integer workType;

    /**
     * 进行中的板，基于这个板进行组板
     */
    private String boardCode;

    /**
     * 货区编码
     */
    private String goodsAreaCode;

    /**
     * 下游场地编码
     * */
    private Integer nextSiteCode;
    /**
     * 下游场地名称
     * */
    private String nextSiteName;

    /**
     * 上下游场地编码
     * */
    private Integer prevSiteCode;

    /**
     * 上下游场地名称
     * */
    private String prevSiteName;

    /**
     * 补扫标识
     */
    private boolean supplementary;

    /**
     * 任务组号
     */
    private String groupCode;

    /**
     * 是否强制组板
     * **/
    private boolean isForceCombination = false;

    /**
     * 1:组板转移标识
     */
    private Integer isCombinationTransfer;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getScanCode() {
        return scanCode;
    }

    public void setScanCode(String scanCode) {
        this.scanCode = scanCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getGoodsAreaCode() {
        return goodsAreaCode;
    }

    public void setGoodsAreaCode(String goodsAreaCode) {
        this.goodsAreaCode = goodsAreaCode;
    }

    public Integer getNextSiteCode() {
        return nextSiteCode;
    }

    public void setNextSiteCode(Integer nextSiteCode) {
        this.nextSiteCode = nextSiteCode;
    }

    public boolean isSupplementary() {
        return supplementary;
    }

    public void setSupplementary(boolean supplementary) {
        this.supplementary = supplementary;
    }

    public boolean getIsForceCombination() {
        return isForceCombination;
    }

    public boolean isForceCombination() {
        return isForceCombination;
    }

    public void setForceCombination(boolean forceCombination) {
        isForceCombination = forceCombination;
    }

    public Integer getIsCombinationTransfer() {
        return isCombinationTransfer;
    }

    public void setIsCombinationTransfer(Integer isCombinationTransfer) {
        this.isCombinationTransfer = isCombinationTransfer;
    }

    public String getNextSiteName() {
        return nextSiteName;
    }

    public void setNextSiteName(String nextSiteName) {
        this.nextSiteName = nextSiteName;
    }

    public Integer getPrevSiteCode() {
        return prevSiteCode;
    }

    public void setPrevSiteCode(Integer prevSiteCode) {
        this.prevSiteCode = prevSiteCode;
    }

    public Integer getWorkType() {
        return workType;
    }

    public void setWorkType(Integer workType) {
        this.workType = workType;
    }

    public String getPrevSiteName() {
        return prevSiteName;
    }

    public void setPrevSiteName(String prevSiteName) {
        this.prevSiteName = prevSiteName;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }
}
