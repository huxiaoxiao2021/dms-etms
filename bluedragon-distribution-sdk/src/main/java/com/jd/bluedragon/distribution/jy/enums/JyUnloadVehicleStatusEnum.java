package com.jd.bluedragon.distribution.jy.enums;

/**
 * @ClassName JyUnloadVehicleStatusEnum
 * @Description 拣运卸车任务状态
 * @Author wyh
 * @Date 2022/5/10 12:16
 **/
public enum JyUnloadVehicleStatusEnum {

    /**
     * 待卸
     */
    WAIT_UN_LOAD(JyBizTaskUnloadStatusEnum.WAIT_UN_LOAD.getCode(), JyBizTaskUnloadStatusEnum.WAIT_UN_LOAD.getName(), 1),

    /**
     * 卸车
     */
    UN_LOADING(JyBizTaskUnloadStatusEnum.UN_LOADING.getCode(), JyBizTaskUnloadStatusEnum.UN_LOADING.getName(), 2),

    /**
     * 已完成
     */
    UN_LOAD_DONE(JyBizTaskUnloadStatusEnum.UN_LOAD_DONE.getCode(), JyBizTaskUnloadStatusEnum.UN_LOAD_DONE.getName(), 3)
    ;

    private Integer code;
    private String name;
    private Integer order;

    JyUnloadVehicleStatusEnum(Integer code, String name, Integer order) {
        this.code = code;
        this.name = name;
        this.order = order;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public Integer getOrder() {
        return this.order;
    }
}
