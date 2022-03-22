package com.jd.bluedragon.common.dto.board.request;


import com.jd.bluedragon.common.dto.base.request.OperatorInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 组板请求
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-08-15 17:44:33 周日
 */
public class BindToVirtualBoardPo implements Serializable {

    private static final long serialVersionUID = -376483646169084509L;

    /**
     * 操作人信息
     */
    private OperatorInfo operatorInfo;

    private String barCode;

    /**
     * 目标板号
     */
    private List<String> boardCodeList;

    /**
     * 最多扫入个数
     */
    private Integer maxItemCount;

    /**
     * 操作来源 1: 分拣pda 2:分拣机
     * @return
     */
    private Integer bizSource;

    public OperatorInfo getOperatorInfo() {
        return operatorInfo;
    }

    public BindToVirtualBoardPo setOperatorInfo(OperatorInfo operatorInfo) {
        this.operatorInfo = operatorInfo;
        return this;
    }

    public String getBarCode() {
        return barCode;
    }

    public BindToVirtualBoardPo setBarCode(String barCode) {
        this.barCode = barCode;
        return this;
    }

    public List<String> getBoardCodeList() {
        return boardCodeList;
    }

    public BindToVirtualBoardPo setBoardCodeList(List<String> boardCodeList) {
        this.boardCodeList = boardCodeList;
        return this;
    }

    public Integer getMaxItemCount() {
        return maxItemCount;
    }

    public BindToVirtualBoardPo setMaxItemCount(Integer maxItemCount) {
        this.maxItemCount = maxItemCount;
        return this;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }
}
