package com.jd.bluedragon.common.dto.board.response;

import java.util.List;

/**
 * BoardDetailDto
 * 组板包裹/箱号明细实体类
 *
 * @author jiaowenqiang
 * @date 2019/7/8
 */
public class BoardDetailDto {

    /**
     * 包裹/箱号明细
     */
    private List<String> boardDetails;

    /**
     * 箱号数量
     */
    private int boxNum;

    /**
     * 包裹数量
     */
    private int packageNum;

    @Override
    public String toString() {
        return "BoardDetailDto{" +
                "boardDetails=" + boardDetails +
                ", boxNum=" + boxNum +
                ", packageNum=" + packageNum +
                '}';
    }

    public List<String> getBoardDetails() {
        return boardDetails;
    }

    public void setBoardDetails(List<String> boardDetails) {
        this.boardDetails = boardDetails;
    }

    public int getBoxNum() {
        return boxNum;
    }

    public void setBoxNum(int boxNum) {
        this.boxNum = boxNum;
    }

    public int getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(int packageNum) {
        this.packageNum = packageNum;
    }
}
