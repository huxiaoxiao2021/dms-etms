package com.jd.bluedragon.distribution.api.response.box;

import java.util.List;

/**
 * 创建箱号返回数据
 * @author fanggang7
 * @time 2023-10-25 10:20:44 周三
 */
public class CreateBoxInfo extends BoxPrintBaseInfo{

    private static final long serialVersionUID = -6878935547968590430L;

    /**
     * 箱号类型不合法值
     */
    public static Integer Code_boxTypeIllegal = 600;

    private List<String> boxCodes;

    public List<String> getBoxCodes() {
        return boxCodes;
    }

    public void setBoxCodes(List<String> boxCodes) {
        this.boxCodes = boxCodes;
    }
}
