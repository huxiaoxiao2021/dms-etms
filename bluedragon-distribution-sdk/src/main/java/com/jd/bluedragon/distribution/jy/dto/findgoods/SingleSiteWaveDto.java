package com.jd.bluedragon.distribution.jy.dto.findgoods;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SingleSiteWaveDto implements Serializable {
    
    static final long serialVersionUID = 1L;
    /**
     * 波次日期
     */
    @JsonProperty(value = "wave_date")
    private String waveDate;
    /**
     * 分拣网点编码
     */
    @JsonProperty(value = "sorting_site_code")
    private String sortingSiteCode;

    /**
     * 分拣网点名称
     */
    @JsonProperty(value = "sorting_site_name")
    private String sortingSiteName;
   
    /**
     * 待找货量
     */
    @JsonProperty(value = "wait_find_count")
    private Integer waitFindCount;
    /**
     * 已找货量
     */
    @JsonProperty(value = "have_find_count")
    private Integer haveFindCount;
    /**
     * 未找货量
     */
    @JsonProperty(value = "not_find_count")
    private Integer notFindCount;
    /**
     * 未找货量-高值货量
     */
    @JsonProperty(value = "not_find_high_value_count")
    private Integer notFindHighValueCount;
    /**
     * 未找货量-特快货量
     */
    @JsonProperty(value = "not_find_express_count")
    private Integer notFindExpressCount;
    /**
     * 未找货量-生鲜货量
     */
    @JsonProperty(value = "not_find_fresh_count")
    private Integer notFindFreshCount;
    /**
     * 未找货量-其他量
     */
    @JsonProperty(value = "not_find_other_count")
    private Integer notFindOtherCount;

    public String getWaveDate() {
        return waveDate;
    }

    public void setWaveDate(String waveDate) {
        this.waveDate = waveDate;
    }

    public String getSortingSiteCode() {
        return sortingSiteCode;
    }

    public void setSortingSiteCode(String sortingSiteCode) {
        this.sortingSiteCode = sortingSiteCode;
    }

    public String getSortingSiteName() {
        return sortingSiteName;
    }

    public void setSortingSiteName(String sortingSiteName) {
        this.sortingSiteName = sortingSiteName;
    }

    public Integer getWaitFindCount() {
        return waitFindCount;
    }

    public void setWaitFindCount(Integer waitFindCount) {
        this.waitFindCount = waitFindCount;
    }

    public Integer getHaveFindCount() {
        return haveFindCount;
    }

    public void setHaveFindCount(Integer haveFindCount) {
        this.haveFindCount = haveFindCount;
    }

    public Integer getNotFindCount() {
        return notFindCount;
    }

    public void setNotFindCount(Integer notFindCount) {
        this.notFindCount = notFindCount;
    }

    public Integer getNotFindHighValueCount() {
        return notFindHighValueCount;
    }

    public void setNotFindHighValueCount(Integer notFindHighValueCount) {
        this.notFindHighValueCount = notFindHighValueCount;
    }

    public Integer getNotFindExpressCount() {
        return notFindExpressCount;
    }

    public void setNotFindExpressCount(Integer notFindExpressCount) {
        this.notFindExpressCount = notFindExpressCount;
    }

    public Integer getNotFindFreshCount() {
        return notFindFreshCount;
    }

    public void setNotFindFreshCount(Integer notFindFreshCount) {
        this.notFindFreshCount = notFindFreshCount;
    }

    public Integer getNotFindOtherCount() {
        return notFindOtherCount;
    }

    public void setNotFindOtherCount(Integer notFindOtherCount) {
        this.notFindOtherCount = notFindOtherCount;
    }
}
