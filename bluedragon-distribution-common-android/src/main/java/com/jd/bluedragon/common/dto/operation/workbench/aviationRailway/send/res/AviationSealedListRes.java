package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.res;

import java.io.Serializable;
import java.util.List;

/**
 * @Author zhengchengfa
 * @Date 2023/8/8 15:33
 * @Description
 */
public class AviationSealedListRes implements Serializable {

    private static final long serialVersionUID = -9147679847630229665L;

    /**
     * 封车列表数据（待封车、已封车）
     */
    private List<AviationSealListDto> aviationSealListDtoList;


    public List<AviationSealListDto> getAviationSealListDtoList() {
        return aviationSealListDtoList;
    }

    public void setAviationSealListDtoList(List<AviationSealListDto> aviationSealListDtoList) {
        this.aviationSealListDtoList = aviationSealListDtoList;
    }
}
