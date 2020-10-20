package com.jd.bluedragon.common.dto.box.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

/**
 * 查询
 *
 * @author fanggang7
 * @time 2020-09-23 19:46:31 周三
 */
public class QueryBoxCollectionReportRequest {

    /**
     * packageCode : JD42134231-1-1
     * currentOperate : {}
     * user : {"operatorErp":"teacherBi","operatorName":"毕老师"}
     */

    private String packageCode;
    private CurrentOperate currentOperate;
    private User user;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
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

    @Override
    public String toString() {
        return "QueryBoxCollectionReportRequest{" +
                "packageCode='" + packageCode + '\'' +
                ", currentOperate=" + currentOperate +
                ", user=" + user +
                '}';
    }
}
