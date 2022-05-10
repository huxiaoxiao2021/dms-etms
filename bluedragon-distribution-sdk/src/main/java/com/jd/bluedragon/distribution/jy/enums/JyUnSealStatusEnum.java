package com.jd.bluedragon.distribution.jy.enums;

/**
 * @ClassName JyUnSealStatusEnum
 * @Description 拣运待解任务状态
 * @Author wyh
 * @Date 2022/5/10 12:09
 **/
public enum JyUnSealStatusEnum {

    /**
     * 待解
     */
    WAIT_UN_SEAL(JyBizTaskUnloadStatusEnum.WAIT_UN_SEAL.getCode(), JyBizTaskUnloadStatusEnum.WAIT_UN_SEAL.getName(), 1),

    /**
     * 待卸
     */
    WAIT_UN_LOAD(JyBizTaskUnloadStatusEnum.WAIT_UN_LOAD.getCode(), JyBizTaskUnloadStatusEnum.WAIT_UN_LOAD.getName(), 2),

    /**
     * 卸车
     */
    UN_LOADING(JyBizTaskUnloadStatusEnum.UN_LOADING.getCode(), JyBizTaskUnloadStatusEnum.UN_LOADING.getName(), 3),

    /**
     * 在途
     */
    ON_WAY(JyBizTaskUnloadStatusEnum.ON_WAY.getCode(), JyBizTaskUnloadStatusEnum.ON_WAY.getName(), 4);

    private Integer code;
    private String name;
    private Integer order;

    JyUnSealStatusEnum(Integer code, String name, Integer order) {
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
