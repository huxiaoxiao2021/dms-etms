package com.jd.bluedragon.common.dto.box.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.List;

/**
 * 集包异常举报请求
 *
 * @author fanggang7
 * @time 2020-09-23 19:52:19 周三
 */
public class BoxCollectionReportRequest implements Serializable {

    private static final long serialVersionUID = 6998827330972775777L;

    /**
     * packageCode : JD42134231-1-1
     * reportType : 1
     * reportImgUrls : []
     * currentOperate : {}
     * user : {"operatorErp":"teacherBi","operatorName":"毕老师"}
     */

    private String packageCode;
    private Integer reportType;
    private CurrentOperate currentOperate;
    private User user;
    private List<String> reportImgUrls;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public Integer getReportType() {
        return reportType;
    }

    public void setReportType(Integer reportType) {
        this.reportType = reportType;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<String> getReportImgUrls() {
        return reportImgUrls;
    }

    public void setReportImgUrls(List<String> reportImgUrls) {
        this.reportImgUrls = reportImgUrls;
    }

    @Override
    public String toString() {
        return "BoxCollectionReportRequest{" +
                "packageCode='" + packageCode + '\'' +
                ", reportType=" + reportType +
                ", currentOperate=" + currentOperate +
                ", user=" + user +
                ", reportImgUrls=" + reportImgUrls +
                '}';
    }
}
