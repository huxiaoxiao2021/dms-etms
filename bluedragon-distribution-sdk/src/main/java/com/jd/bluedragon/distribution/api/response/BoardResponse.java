package com.jd.bluedragon.distribution.api.response;

import com.jd.ql.dms.common.domain.JdResponseStatusInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xumei3 on 2018/3/27.
 */
public class BoardResponse implements Serializable{

    private static final long serialVersionUID = 1L;

    public static final Integer CODE_BOARD_NOT_IRREGULAR = 20100;
    public static final String  MESSAGE_BOARD_NOT_IRREGULAR = "板号正则校验不通过.";

    public static final Integer CODE_BOARD_NOT_FOUND = 20101;
    public static final String MESSAGE_BOARD_NOT_FOUND = "无板号信息.";

    public static final Integer CODE_BOARD_CLOSED = 20104;
    public static final String MESSAGE_BOARD_CLOSED = "此板号已经完结.";

    public static final Integer CODE_BOX_PACKAGE_SENDED = 20105;
    public static final String MESSAGE_BOX_PACKAGE_SENDED = "箱号/包裹号已经发货.";

    public static final Integer CODE_BOX_PACKAGE_BINDINGED = 20106;
    public static final String MESSAGE_BOX_PACKAGE_BINDINGED = "箱号/包裹号已经绑定到其他板号.";

    public static final Integer CODE_BOXORPACKAGE_REACH_LIMIT = 20107;
    public static final String MESSAGE_BOXORPACKAGE_REACH_LIMIT = "板号绑定的箱号/包裹号已达上限.";

    public static final Integer CODE_BOX_NO_SORTING = 20108;
    public static final String MESSAGE_BOX_NO_SORTING = "无该箱号分拣记录,是否强制组板？";

    public static final Integer CODE_BOX_NOT_EXIST = 20109;
    public static final String MESSAGE_BOX_NOT_EXIST = "无该箱号信息!";

    public static final Integer CODE_BOX_PACKAGECODE_ERROR = 20110;
    public static final String MESSAGE_BOX_PACKAGECODE_ERROR = "请扫描正确的包裹号或箱号!";

    public static final Integer CODE_WAYBILL_PACKAGE_NUM_LIMIT = 20111;
    public static final String MESSAGE_WAYBILL_PACKAGE_NUM_LIMIT = "板上剩余位置不足以装下该运单下所有包裹，不能组板!";

    public static final Integer CODE_WAYBILL_NO_PACKAGE_INFO = 20112;
    public static final String MESSAGE_WAYBILL_NO_PACKAGE_INFO = "该运单包裹信息不存在，不能组板!";

    public static final Integer CODE_BOARD_CHANGE = 39999;
    public static final String Message_BOARD_CHANGE = "  确定绑到新板上？";

    /**
     * 一单多件不齐
     */
    public static final Integer CODE_PACAGES_NOT_ENOUGH=39001;


    /** 板号 */
    private String boardCode;

    /** 箱号 **/
    private String boxCode;

    /** 包裹号 **/
    private String packageCode;

    /** 接收站点编号 */
    private Integer receiveSiteCode;

    /** 接收站点名称 */
    private String receiveSiteName;

    /** 组板明细 */
    private List<String> boardDetails;

    /** 组板明细箱子数量 */
    private Integer boxNum;

    /** 组板明细包裹数量 */
    private Integer packageNum;



    /**
     * 提示信息
     */
    private List<JdResponseStatusInfo> statusInfo;


    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public List<JdResponseStatusInfo> getStatusInfo() {
        return statusInfo;
    }

    public void setStatusInfo(List<JdResponseStatusInfo> statusInfo) {
        this.statusInfo = statusInfo;
    }

    /**
     * 添加一种提示信息
     * @param code
     * @param message
     */
    public void addStatusInfo(Integer code,String message){
        if(statusInfo == null){
            statusInfo = new ArrayList<JdResponseStatusInfo>();
        }
        statusInfo.add(new JdResponseStatusInfo(code,message));
    }


    /**
     * 组装状态信息
     * @return
     */
    public String buildStatusMessages(){
        String statusMessage = "";
        for(JdResponseStatusInfo status : statusInfo){
            statusMessage += status.getStatusMessage();
        }
        return statusMessage;
    }

    public List<String> getBoardDetails() {
        return boardDetails;
    }

    public void setBoardDetails(List<String> boardDetails) {
        this.boardDetails = boardDetails;
    }

    public Integer getBoxNum() {
        return boxNum;
    }

    public void setBoxNum(Integer boxNum) {
        this.boxNum = boxNum;
    }

    public Integer getPackageNum() {
        return packageNum;
    }

    public void setPackageNum(Integer packageNum) {
        this.packageNum = packageNum;
    }
}
