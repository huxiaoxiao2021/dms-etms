package com.jd.bluedragon.distribution.loadAndUnload;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

import java.util.Date;

/**
 * 卸车任务与板关系DTO
 *
 * @author: hujiping
 * @date: 2020/6/23 10:43
 */
public class UnloadCarTransBoard extends DbEntity {

    /**
     * 卸车任务板关系主键ID
     * */
    private Long unloadBoardId;
    /**
     * 封车编码
     * */
    private String sealCarCode;
    /**
     * 组板号
     * */
    private String boardCode;
    /**
     * 已扫包裹数
     * */
    private Integer packageScanCount;
    /**
     * 多货包裹数
     *
     * */
    private Integer surplusPackageScanCount;
    /**
     * 操作时间
     * */
    private Date operateTime;

    public Long getUnloadBoardId() {
        return unloadBoardId;
    }

    public void setUnloadBoardId(Long unloadBoardId) {
        this.unloadBoardId = unloadBoardId;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public Integer getPackageScanCount() {
        return packageScanCount;
    }

    public void setPackageScanCount(Integer packageScanCount) {
        this.packageScanCount = packageScanCount;
    }

    public Integer getSurplusPackageScanCount() {
        return surplusPackageScanCount;
    }

    public void setSurplusPackageScanCount(Integer surplusPackageScanCount) {
        this.surplusPackageScanCount = surplusPackageScanCount;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
}
