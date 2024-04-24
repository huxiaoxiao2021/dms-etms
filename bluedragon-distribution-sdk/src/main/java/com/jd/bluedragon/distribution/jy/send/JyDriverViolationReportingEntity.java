package com.jd.bluedragon.distribution.jy.send;

import java.io.Serializable;
import java.util.Date;

/**
 * @author pengchong28
 * @description 司机违规举报实体
 * @date 2024/4/12
 */
public class JyDriverViolationReportingEntity implements Serializable {
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 业务主键
     */
    private String bizId;
    /**
     * 图片url，多个用,分割
     */
    private String imgUrl;
    /**
     * 视频url
     */
    private String videoUrl;
    /**
     * 操作场地编码
     */
    private Long siteCode;
    /**
     * 创建人ERP
     */
    private String createUserErp;
    /**
     * 创建人姓名
     */
    private String createUserName;
    /**
     * 修改人ERP
     */
    private String updateUserErp;
    /**
     * 更新人姓名
     */
    private String updateUserName;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 更新时间
     */
    private Date updateTime;
    /**
     * 数据变更时间戳
     */
    private Date ts;
    /**
     * 是否删除：1-有效，0-删除
     */
    private Integer yn;

    public Long setId(Long id){
        return this.id = id;
    }

    public Long getId(){
        return this.id;
    }
    public String setBizId(String bizId){
        return this.bizId = bizId;
    }

    public String getBizId(){
        return this.bizId;
    }
    public String setImgUrl(String imgUrl){
        return this.imgUrl = imgUrl;
    }

    public String getImgUrl(){
        return this.imgUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Long getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Long siteCode) {
        this.siteCode = siteCode;
    }

    public String setCreateUserErp(String createUserErp){
        return this.createUserErp = createUserErp;
    }

    public String getCreateUserErp(){
        return this.createUserErp;
    }
    public String setCreateUserName(String createUserName){
        return this.createUserName = createUserName;
    }

    public String getCreateUserName(){
        return this.createUserName;
    }
    public String setUpdateUserErp(String updateUserErp){
        return this.updateUserErp = updateUserErp;
    }

    public String getUpdateUserErp(){
        return this.updateUserErp;
    }
    public String setUpdateUserName(String updateUserName){
        return this.updateUserName = updateUserName;
    }

    public String getUpdateUserName(){
        return this.updateUserName;
    }
    public Date setCreateTime(Date createTime){
        return this.createTime = createTime;
    }

    public Date getCreateTime(){
        return this.createTime;
    }
    public Date setUpdateTime(Date updateTime){
        return this.updateTime = updateTime;
    }

    public Date getUpdateTime(){
        return this.updateTime;
    }
    public Date setTs(Date ts){
        return this.ts = ts;
    }

    public Date getTs(){
        return this.ts;
    }
    public Integer setYn(Integer yn){
        return this.yn = yn;
    }

    public Integer getYn(){
        return this.yn;
    }

}
