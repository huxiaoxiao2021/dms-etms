package com.jd.bluedragon.distribution.print.domain;

import java.util.List;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/11/28 20:26
 * @Description:  站点类型配置
 */
public class JdSiteTypeConfig {

    /**
     * 站点三级类型
     */
    private List<Integer> sortTypes;

    /**
     * 站点四级类型
     */
    private List<Integer> sortSubTypes;

    /**
     * 拦截开关：true 拦截  false：不拦截
     */
    private boolean checkSwitch = false;

    /**
     * 标准岗位编码
     */
    private List<String> positionCodes;

    public List<String> getPositionCodes() {
        return positionCodes;
    }

    public void setPositionCodes(List<String> positionCodes) {
        this.positionCodes = positionCodes;
    }


    public List<Integer> getSortTypes() {
        return sortTypes;
    }

    public void setSortTypes(List<Integer> sortTypes) {
        this.sortTypes = sortTypes;
    }

    public List<Integer> getSortSubTypes() {
        return sortSubTypes;
    }

    public void setSortSubTypes(List<Integer> sortSubTypes) {
        this.sortSubTypes = sortSubTypes;
    }

    public boolean isCheckSwitch() {
        return checkSwitch;
    }

    public void setCheckSwitch(boolean checkSwitch) {
        this.checkSwitch = checkSwitch;
    }
}
