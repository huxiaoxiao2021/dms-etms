package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.domain.BoxSystemTypeEnum;

import java.util.List;

/**
 *
 * 中台容器相关服务
 *
 * @author shipeilin
 * @date 2019年05月14日 14时:54分
 */
public interface ContainerManager {

    /**
     * 组合
     */
    String tenantCode = "cn";

    /**
     * 预计发货时间
     */
    String predictSendTimeKey = "predictSendTime";

    /**
     * 系统默认erp
     */
    String defaultUserErp = "systemErp";

    /**
     * 系统默认name
     */
    String defaultUserName = "systemName";


    /**
     *
     * 通过中台批量创建容器
     *
     * @param param 箱子信息
     * @param systemType 箱子系统来源
     * @return 箱子集合
     * @throws Exception
     */
    List<Box> createContainers(Box param, BoxSystemTypeEnum systemType) throws Exception;

    /**
     * 更新箱号的体积：单位厘米
     * @param boxCode 箱号
     * @param userErp 更新人erp
     * @param userName 更新人name
     * @param length 长厘米
     * @param width 宽厘米
     * @param height 高厘米
     * @return 是否成功
     * @throws Exception
     */
    Boolean updateVolumeByContainerCode(String boxCode,String userErp, String userName, Integer createSiteCode,
                                        Double length, Double width, Double height) throws Exception;

    /**
     * 更新箱子状态为已发货
     * @param boxCode 箱号
     * @param userErp 用户erp 不可为空
     * @param userName 用户名称
     * @param createSiteCode 所属场地
     * @return 是否成功
     * @throws Exception
     */
    Boolean updateBoxSend(String boxCode, String userErp, String userName, Integer createSiteCode) throws Exception;

    /**
     * 更新箱子状态为已发货
     * @param boxCode 箱号
     * @param userErp 用户erp 不可为空
     * @param userName 用户名称
     * @param createSiteCode 所属场地
     * @return 是否成功
     * @throws Exception
     */
    Boolean updateBoxCancelSend(String boxCode, String userErp, String userName, Integer createSiteCode) throws Exception;

    /**
     * 根据箱号查询箱子信息
     * @param boxCode 箱号
     * @return 箱子
     * @throws Exception
     */
    Box findBoxByCode(String boxCode) throws Exception;

}
