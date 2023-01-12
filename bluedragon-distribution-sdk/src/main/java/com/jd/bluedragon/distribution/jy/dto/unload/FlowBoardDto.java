package com.jd.bluedragon.distribution.jy.dto.unload;

import java.io.Serializable;

/**
 * @Author zhengchengfa
 * @Description //TODO
 * @date
 **/
public class FlowBoardDto extends UnloadBaseDto implements Serializable {
    private static final long serialVersionUID = 7431307375891856971L;


    private String bizId;

    private String packageCode;


    private UnloadTaskFlowDto unloadTaskFlowDto;

    private ComBoardAggDto comBoardAggDto;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public UnloadTaskFlowDto getUnloadTaskFlowDto() {
        return unloadTaskFlowDto;
    }

    public void setUnloadTaskFlowDto(UnloadTaskFlowDto unloadTaskFlowDto) {
        this.unloadTaskFlowDto = unloadTaskFlowDto;
    }

    public ComBoardAggDto getComBoardAggDto() {
        return comBoardAggDto;
    }

    public void setComBoardAggDto(ComBoardAggDto comBoardAggDto) {
        this.comBoardAggDto = comBoardAggDto;
    }
}
