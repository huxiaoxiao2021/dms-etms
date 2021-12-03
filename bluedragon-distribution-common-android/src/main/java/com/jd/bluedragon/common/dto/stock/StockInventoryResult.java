package com.jd.bluedragon.common.dto.stock;

import java.io.Serializable;
import java.util.Date;

/**
 * 库存盘点结果
 *
 * @author hujiping
 * @date 2021/6/4 2:12 下午
 */
public class StockInventoryResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 波次编码
     */
    private String waveCode;

    /**
     * 波次开始时间
     */
    private Date waveBeginTime;

    /**
     * 波次结束时间
     */
    private Date waveEndTime;

    /**
     * 待盘点数量
     */
    private String todoInventoryNum;

    /**
     * 已盘点数量
     */
    private String inventoryNum;
    
    /**
     * 包裹状态
     */
    private String packageStatusDesc;

    public String getWaveCode() {
        return waveCode;
    }

    public void setWaveCode(String waveCode) {
        this.waveCode = waveCode;
    }

    public Date getWaveBeginTime() {
        return waveBeginTime;
    }

    public void setWaveBeginTime(Date waveBeginTime) {
        this.waveBeginTime = waveBeginTime;
    }

    public Date getWaveEndTime() {
        return waveEndTime;
    }

    public void setWaveEndTime(Date waveEndTime) {
        this.waveEndTime = waveEndTime;
    }

    public String getTodoInventoryNum() {
        return todoInventoryNum;
    }

    public void setTodoInventoryNum(String todoInventoryNum) {
        this.todoInventoryNum = todoInventoryNum;
    }

    public String getInventoryNum() {
        return inventoryNum;
    }

    public void setInventoryNum(String inventoryNum) {
        this.inventoryNum = inventoryNum;
    }

	public String getPackageStatusDesc() {
		return packageStatusDesc;
	}

	public void setPackageStatusDesc(String packageStatusDesc) {
		this.packageStatusDesc = packageStatusDesc;
	}
}
