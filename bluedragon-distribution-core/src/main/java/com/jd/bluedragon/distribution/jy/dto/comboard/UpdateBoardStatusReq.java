package com.jd.bluedragon.distribution.jy.dto.comboard;

import java.util.List;

/**
 * @author liwenji
 * @date 2022-12-26 14:32
 */
public class UpdateBoardStatusReq {

    /**
     * 操作人ERP
     */
    private String updateUserErp;
    /**
     * 操作人名字
     */
    private String updateUserName;

    private Integer boardStatus;
    
    private List<Long> ids;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Integer getBoardStatus() {
        return boardStatus;
    }

    public void setBoardStatus(Integer boardStatus) {
        this.boardStatus = boardStatus;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }
}
