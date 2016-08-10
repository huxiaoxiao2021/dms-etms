package com.jd.bluedragon.distribution.send.domain;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by guoyongzhi on 2016/7/19.
 */
public class SendDifference  implements Serializable {
    private static final long serialVersionUID = -5706252377345301775L;
    /** 分拣发件数 **/
    private Long dmsSendNums;

    /** 运输手件数**/
    private Long tmsReceiveNums;

    /** 发货批次号**/
    private String sendCode;

    /** 差异明细**/


    private Set<String> packageDifferences;

    public Set<String> getPackageDifferences() {
        return packageDifferences;
    }

    public void setPackageDifferences(Set<String> packageDifferences) {
        this.packageDifferences = packageDifferences;
    }

    public SendDifference() {
    }



    public Long getDmsSendNums() {
        return dmsSendNums;
    }

    public void setDmsSendNums(Long dmsSendNums) {
        this.dmsSendNums = dmsSendNums;
    }

    public Long getTmsReceiveNums() {
        return tmsReceiveNums;
    }

    public void setTmsReceiveNums(Long tmsReceiveNums) {
        this.tmsReceiveNums = tmsReceiveNums;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    /** 相应状态码 */
    private Integer code;

    /** 响应消息 */
    private String message;

    public SendDifference(Integer code, String message) {
        super();
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


        public String toString() {
            return "SendDifference[dmsSendNums=" + dmsSendNums + ",tmsReceiveNums=" + tmsReceiveNums + "," +
                    "sendCode=" + sendCode + ",packageDifference=" + packageDifferences.toString() + "]";
        }


}
