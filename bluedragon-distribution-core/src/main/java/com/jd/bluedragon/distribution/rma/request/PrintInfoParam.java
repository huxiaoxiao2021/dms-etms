package com.jd.bluedragon.distribution.rma.request;

import com.jd.bluedragon.distribution.rma.PrintStatusEnum;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * Created by lixin39 on 2018/9/21.
 */
public class PrintInfoParam {

    /**
     * 打印对象Id
     */
    private List<Long> ids;

    /**
     * 打印时间
     */
    private Date printTime;

    /**
     * 打印操作人名称
     */
    private String printUserName;

    /**
     * 打印操作人编号
     */
    private Integer printUserCode;

    /**
     * 打印状态
     */
    private PrintStatusEnum printStatus;

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }

    public Date getPrintTime() {
        return printTime;
    }

    public void setPrintTime(Date printTime) {
        this.printTime = printTime;
    }

    public String getPrintUserName() {
        return printUserName;
    }

    public void setPrintUserName(String printUserName) {
        this.printUserName = printUserName;
    }

    public Integer getPrintUserCode() {
        return printUserCode;
    }

    public void setPrintUserCode(Integer printUserCode) {
        this.printUserCode = printUserCode;
    }

    public PrintStatusEnum getPrintStatus() {
        return printStatus;
    }

    public void setPrintStatus(PrintStatusEnum printStatus) {
        this.printStatus = printStatus;
    }
}
