package com.jd.bluedragon.distribution.jy.service.picking.template;

import com.jd.bluedragon.distribution.jy.dto.pickinggood.PickingGoodTaskInitDto;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodEntity;
import com.jd.bluedragon.distribution.jy.pickinggood.JyBizTaskPickingGoodSubsidiaryEntity;

import java.util.List;
import java.util.Map;

/**
 * 提货任务抽象化
 * @Author zhengchengfa
 * @Date 2023/12/7 21:29
 * @Description
 */
public abstract class PickingGoodTaskInit {

    private Map<JyBizTaskPickingGoodEntity, List<JyBizTaskPickingGoodSubsidiaryEntity>> taskMap;

    public Map<JyBizTaskPickingGoodEntity, List<JyBizTaskPickingGoodSubsidiaryEntity>> getTaskMap() {
        return taskMap;
    }

    public void setTaskMap(Map<JyBizTaskPickingGoodEntity, List<JyBizTaskPickingGoodSubsidiaryEntity>> taskMap) {
        this.taskMap = taskMap;
    }

    /**
     * 初始化模板方法
     * @param initDto
     * @return
     */
    public boolean initTaskTemplate(PickingGoodTaskInitDto initDto) {
        this.generatePickingGoodTask(initDto);

        if(this.initDetailSwitch(initDto)) {
            this.pickingGoodDetailInit(initDto);
        }
        return true;
    }


    /**
     * 生成提货任务
     * @param obj
     */
    protected abstract boolean generatePickingGoodTask(PickingGoodTaskInitDto pickingGoodTaskInitDto);

    /**
     * 勾子方法
     * 是否初始化明细由子类确定，默认不初始化
     * @param pickingGoodTaskInitDto
     * @return
     */
    protected boolean initDetailSwitch(PickingGoodTaskInitDto pickingGoodTaskInitDto) {
        return false;
    }

    /**
     * 提货明细初始化服务
     * @param initDto
     */
    protected boolean pickingGoodDetailInit(PickingGoodTaskInitDto initDto){
        return true;
    }
}
