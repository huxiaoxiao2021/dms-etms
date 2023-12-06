package com.jd.bluedragon.distribution.api.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class SingleSiteWaveQuery implements Serializable {

    static final long serialVersionUID = 1L;
    /**
     * 波次日期
     */
    @JsonProperty("wave_date")
    @JsonAlias("wave_date")
    private String waveDate;
    /**
     * 分拣网点编码
     */
    @JsonProperty("sorting_site_code")
    @JsonAlias("sorting_site_code")
    private Long sortingSiteCode;
    /**
     * 波次开始时间
     */
    @JsonProperty("wave_begin_tm")
    @JsonAlias("wave_begin_tm")
    private String waveBeginTm;
    /**
     * 波次结束时间
     */
    @JsonProperty("wave_end_tm")
    @JsonAlias("wave_end_tm")
    private String waveEndTm;

    public String getWaveDate() {
        return waveDate;
    }

    public void setWaveDate(String waveDate) {
        this.waveDate = waveDate;
    }

    public Long getSortingSiteCode() {
        return sortingSiteCode;
    }

    public void setSortingSiteCode(Long sortingSiteCode) {
        this.sortingSiteCode = sortingSiteCode;
    }

    public String getWaveBeginTm() {
        return waveBeginTm;
    }

    public void setWaveBeginTm(String waveBeginTm) {
        this.waveBeginTm = waveBeginTm;
    }

    public String getWaveEndTm() {
        return waveEndTm;
    }

    public void setWaveEndTm(String waveEndTm) {
        this.waveEndTm = waveEndTm;
    }
}
