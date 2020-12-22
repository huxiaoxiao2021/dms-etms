package com.jd.bluedragon.distribution.box.domain;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.box.BoxRelationRequest;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.web.mvc.api.DbEntity;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.Date;

public class BoxRelation extends DbEntity implements Serializable {

    private static final long serialVersionUID = 2421784228793092268L;

    /**
    * 箱号
    */
    private String boxCode;

    /**
    * 关联箱号
    */
    private String relationBoxCode;

    /**
    * 操作机构ID
    */
    private Long createSiteCode;

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
    * 修改人姓名
    */
    private String updateUserName;

    /**
    * 删除标识，1:未删除；0：已删除
    */
    private Integer yn;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode == null ? null : boxCode.trim();
    }

    public String getRelationBoxCode() {
        return relationBoxCode;
    }

    public void setRelationBoxCode(String relationBoxCode) {
        this.relationBoxCode = relationBoxCode == null ? null : relationBoxCode.trim();
    }

    public Long getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Long createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp == null ? null : createUserErp.trim();
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName == null ? null : createUserName.trim();
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp == null ? null : updateUserErp.trim();
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName == null ? null : updateUserName.trim();
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }

    public BoxRelation() {}

    public BoxRelation(String boxCode, Long createSiteCode) {
        this.boxCode = boxCode;
        this.createSiteCode = createSiteCode;
    }

    public BoxRelation(String boxCode, Long createSiteCode, String relationBoxCode) {
        this.boxCode = boxCode;
        this.createSiteCode = createSiteCode;
        this.relationBoxCode = relationBoxCode;
    }

    public static BoxRelation genEntity(BoxRelationRequest request) {
        BoxRelation relation = new BoxRelation();
        relation.setBoxCode(request.getBoxCode());
        relation.setRelationBoxCode(request.getRelationBoxCode());
        relation.setCreateSiteCode(Long.valueOf(request.getSiteCode()));
        relation.setCreateUserErp(request.getUserErp());
        relation.setCreateUserName(request.getUserName());
        relation.setUpdateUserErp(request.getUserErp());
        relation.setUpdateUserName(request.getUserName());
        relation.setYn(Constants.YN_YES);

        Date operateTime = StringUtils.isBlank(request.getOperateTime()) ? new Date(): DateHelper.getSeverTime(request.getOperateTime());
        Date createTime = new Date();
        relation.setCreateTime(createTime);
        relation.setUpdateTime(operateTime);

        return relation;
    }
}