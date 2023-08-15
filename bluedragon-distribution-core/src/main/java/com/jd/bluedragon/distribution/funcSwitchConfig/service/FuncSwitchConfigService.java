package com.jd.bluedragon.distribution.funcSwitchConfig.service;

import com.jd.bluedragon.common.domain.WaybillCache;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.external.domain.DmsFuncSwitchDto;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigDto;
import com.jd.bluedragon.distribution.funcSwitchConfig.domain.FuncSwitchConfigAllPureDto;
import com.jd.bluedragon.distribution.funcSwitchConfig.domain.FuncSwitchConfigCondition;
import com.jd.bluedragon.distribution.funcSwitchConfig.domain.FuncSwitchConfigResponse;
import com.jd.bluedragon.distribution.ver.exception.SortingCheckException;
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
     * 校验是否配置功能(带缓存)
     * @param menuCode 菜单编码
     * @param siteCode 站点编码
     * @param dimensionCode 维度编码
     * @param operateErp 操作人erp
     */
    boolean checkIsConfiguredWithCache(Integer menuCode, Integer siteCode, Integer dimensionCode, String operateErp);

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
     * 查询站点的功能拦截状态
     * @param siteCode
     * @return
     */
    FuncSwitchConfigResponse<List<DmsFuncSwitchDto>> getSiteFilterStatus(Integer siteCode);


    /**
     * 纯配重量判断
     * @param waybillCache
     * @param waybillCode
     * @param packageCode
     * @throws SortingCheckException
     */
    JdResponse<Void> checkAllPureWeight(WaybillCache waybillCache, String waybillCode, String packageCode) ;

    /**
     * 判断是否满足 纯配外单拦截标识
     * @param funcSwitchConfigAllPureDto
     * true 需要判断重量   false 不需要判断重量
     * @return
     */
    boolean isAllPureValidateWeight(FuncSwitchConfigAllPureDto funcSwitchConfigAllPureDto);

    /**
     * 按照全国->场地->个人的顺序，返回功能开关的有效状态
     * @param funcCode 开关编码
     * @param siteCode 场地
     * @param userErp null则不按个人维度查询
     * @return 有效返回true，记录不存在或者失效返回false
     */
    boolean getFuncStatusByAllDimension(Integer funcCode, Integer siteCode, String userErp);

    /**
     * 获取BC箱号是否强制绑定循环集包袋校验
     * @param menuCode
     * @param siteCode
     * @return  false 不拦截校验   true 拦截校验
     */
    boolean  getBcBoxFilterStatus(Integer menuCode,Integer siteCode);
}
