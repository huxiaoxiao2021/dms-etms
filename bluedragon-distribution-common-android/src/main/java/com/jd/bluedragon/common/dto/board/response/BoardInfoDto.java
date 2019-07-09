package com.jd.bluedragon.common.dto.board.response;

/**
 * BoardInfoDto
 * 组板信息
 *
 * @author jiaowenqiang
 * @date 2019/7/8
 */
public class BoardInfoDto {

    /**
     * 板号
     */
    private String code;

    /**
     * 目的地名称
     */
    private String destination;

    @Override
    public String toString() {
        return "BoardInfoDto{" +
                "code='" + code + '\'' +
                ", destination='" + destination + '\'' +
                '}';
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}
