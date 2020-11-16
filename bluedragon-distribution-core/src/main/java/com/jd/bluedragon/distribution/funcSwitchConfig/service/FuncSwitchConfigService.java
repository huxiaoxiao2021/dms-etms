package com.jd.bluedragon.distribution.funcSwitchConfig.service;

import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.funcSwitchConfig.domain.FuncSwitchConfigCondition;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;

/**
 * 功能开关配置
 *
 * @author: hujiping
 * @date: 2020/9/17 10:22
 */
public interface FuncSwitchConfigService {

    /**
     * 根据条件分页查询
     * @param condition
     * @return
     */
    PagerResult<FuncSwitchConfigDto> queryByCondition(FuncSwitchConfigCondition condition);

    /**
     * 新增
     * @param funcSwitchConfigDto
     * @return
     */
    JdResponse insert(FuncSwitchConfigDto funcSwitchConfigDto);

    /**
     * 批量新增
     * @param list
     * @return
     */
    JdResponse batchInsert(List<FuncSwitchConfigDto> list);

    /**
     * 变更
     * @param funcSwitchConfigDto
     * @return
     */
    JdResponse update(FuncSwitchConfigDto funcSwitchConfigDto);

    /**
     * 逻辑删除
     *   置为无效
     * @param funcSwitchConfigDtos
     * @param loginUser
     * @return
     */
    JdResponse logicalDelete(List<FuncSwitchConfigDto> funcSwitchConfigDtos,String loginUser);

    /**
     * 校验表格数据
     * @param dataList
     * @param loginUser
     * @param jdResponse
     * @return
     */
    void checkExportData(List<FuncSwitchConfigDto> dataList, String loginUser, JdResponse jdResponse);

    /**
     * 批量插入
     * @param dataList
     * @param loginUser
     */
    void importExcel(List<FuncSwitchConfigDto> dataList, LoginUser loginUser) throws Exception;

    /**
     * 根据条件查询
     * @param funcSwitchConfigDto
     * @return
     */
    List<FuncSwitchConfigDto> getFuncSwitchConfigs(FuncSwitchConfigDto funcSwitchConfigDto);

    /**
     * 校验是否配置功能
     * @param funcSwitchConfigDto
     * @return
     */
    boolean checkIsConfigured(FuncSwitchConfigDto funcSwitchConfigDto);

    /**
     * 从缓存或数据库中查询拦截的状态
     * @param menuCode   功能编码
     * @return
     */
    boolean getAllCountryFromCacheOrDb( Integer menuCode);

    /**
     * 站点维度查询缓存或数据库拦截状态
     * @param menuCode   功能编码
     * @param siteCode   站点编码
     * @return
     */
    boolean getSiteFlagFromCacheOrDb(Integer menuCode,Integer siteCode);

    /**
     * 个人维度从缓存或数据库中查询拦截的状态
     * @param menuCode  功能编码
     * @param operateErp 操作人erp
     * @return
     */
    boolean getErpFlagFromCacheOrDb(Integer menuCode,String operateErp);

    /**
     * 拿运单号获取商家青龙业主号-通过青龙业务号判断是否是内部商家
     * @param waybillCode
     * @return
     */
    boolean isInsideMode(String waybillCode);
}
