package com.jd.bluedragon.distribution.ucc;

/**
 * @ClassName UccConfigService
 * @Description
 * @Author wyh
 * @Date 2020/12/28 17:18
 **/
public interface UccConfigService {

    /**
     * 站点开启文件箱号逻辑
     * @param siteCode
     * @return
     */
    boolean siteEnableFilePackageCheck(Integer siteCode);
}
