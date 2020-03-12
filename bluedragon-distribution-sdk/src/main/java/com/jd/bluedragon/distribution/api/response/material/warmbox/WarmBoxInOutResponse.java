package com.jd.bluedragon.distribution.api.response.material.warmbox;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName WarmBoxInOutResponse
 * @Description
 * @Author wyh
 * @Date 2020/2/26 15:42
 **/
public class WarmBoxInOutResponse implements Serializable {

    private static final long serialVersionUID = -1915641002927243567L;
    /**
     * 板号
     */
    private String boardCode;

    /**
     * 保温箱列表
     */
    private List<String> boxes;

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public List<String> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<String> boxes) {
        this.boxes = boxes;
    }

}
