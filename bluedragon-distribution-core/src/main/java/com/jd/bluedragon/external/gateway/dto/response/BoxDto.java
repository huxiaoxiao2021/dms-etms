package com.jd.bluedragon.external.gateway.dto.response;

import java.io.Serializable;
import java.util.List;

/**
 * 箱号信息 返回对象
 * @author : xumigen
 * @date : 2020/1/2
 */
public class BoxDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> boxCodes;
    private String startSiteName;
    private String endSiteName;
    private List<String> router;
    private String routerNum;
    private String routerText;

    public List<String> getBoxCodes() {
        return boxCodes;
    }

    public void setBoxCodes(List<String> boxCodes) {
        this.boxCodes = boxCodes;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public List<String> getRouter() {
        return router;
    }

    public void setRouter(List<String> router) {
        this.router = router;
    }

    public String getRouterNum() {
        return routerNum;
    }

    public void setRouterNum(String routerNum) {
        this.routerNum = routerNum;
    }

    public String getRouterText() {
        return routerText;
    }

    public void setRouterText(String routerText) {
        this.routerText = routerText;
    }
}
