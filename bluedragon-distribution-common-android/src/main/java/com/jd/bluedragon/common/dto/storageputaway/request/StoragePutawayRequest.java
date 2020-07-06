package com.jd.bluedragon.common.dto.storageputaway.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * 暂存上架请求参数
 *
 * @author jiaowenqiang
 * @date 2019/7/11
 */
public class StoragePutawayRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 条码
     * 运单号/包裹号
     */
    private String barCode;

    /**
     * 储位号
     */
    private String storageCode;

    /**
     * 操作人
     */
    private User user;

    /**
     * 操作人ERP编号
     */
    private String erp;

    /**
     * 当前站点
     */
    private CurrentOperate currentOperate;

    /**
     * 暂存来源
     * */
    private Integer storageSource;

    /**
     * 是否强制暂存
     * */
    private Boolean forceStorage;

    @Override
    public String toString() {
        return "StoragePutawayRequest{" +
                "barCode='" + barCode + '\'' +
                ", storageCode='" + storageCode + '\'' +
                ", user=" + user +
                ", erp='" + erp + '\'' +
                ", currentOperate=" + currentOperate +
                '}';
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getStorageCode() {
        return storageCode;
    }

    public void setStorageCode(String storageCode) {
        this.storageCode = storageCode;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getErp() {
        return erp;
    }

    public void setErp(String erp) {
        this.erp = erp;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public Integer getStorageSource() {
        return storageSource;
    }

    public void setStorageSource(Integer storageSource) {
        this.storageSource = storageSource;
    }

    public Boolean getForceStorage() {
        return forceStorage;
    }

    public void setForceStorage(Boolean forceStorage) {
        this.forceStorage = forceStorage;
    }
}
