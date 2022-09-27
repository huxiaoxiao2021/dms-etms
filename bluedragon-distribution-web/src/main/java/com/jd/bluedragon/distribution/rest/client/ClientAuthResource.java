package com.jd.bluedragon.distribution.rest.client;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.client.domain.ClientAuthRequest;
import com.jd.bluedragon.distribution.client.domain.ClientMenuDto;
import com.jd.bluedragon.distribution.client.domain.ClientMenuFuncConfig;
import com.jd.bluedragon.distribution.client.domain.ClientNoAuthMenuCodeConfig;
import com.jd.bluedragon.distribution.client.enums.ClientMenuEnum;
import com.jd.bluedragon.distribution.client.enums.DeskClientMenuEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

/**
 * 客户端权限控制
 *
 * @author hujiping
 * @date 2021/10/9 5:28 下午
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class ClientAuthResource {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Autowired
    private UccPropertyConfiguration uccPropertyConfiguration;

    /**
     * 获取无权限的菜单编码
     *
     * @param clientAuthRequest
     * @return
     */
    @POST
    @Path("/client/getNoAuthMenuCode")
    @JProfiler(jKey = "dms.ClientAuthorityResource.getNoAuthMenuCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<String>> getNoAuthMenuCode(ClientAuthRequest clientAuthRequest){
        InvokeResult<List<String>> result = new InvokeResult<List<String>>();
        // 参数校验
        if(clientAuthRequest == null || clientAuthRequest.getSiteCode() == null){
            result.parameterError(InvokeResult.PARAM_ERROR);
            return result;
        }
        BaseStaffSiteOrgDto baseSite = null;
        try {
            baseSite = baseMajorManager.getBaseSiteBySiteId(clientAuthRequest.getSiteCode());
            if(baseSite == null){
                logger.warn("登录人所属站点不存在!");
                result.parameterError("登录人所属站点不存在!");
                return result;
            }
        }catch (Exception e){
            logger.error("根据站点:{}查询站点信息异常!", clientAuthRequest.getSiteCode(), e);
            // 防止查询基础资料异常影响现场正常使用，不限制菜单展示
            return result;
        }
        result.setData(obtainNoAuthMenu(baseSite));
        return result;
    }

    @POST
    @Path("/client/getNoAuthMenuCodeNew")
    @JProfiler(jKey = "dms.ClientAuthorityResource.getNoAuthMenuCodeNew", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<List<String>> getNoAuthMenuCodeNew(ClientAuthRequest clientAuthRequest){
        InvokeResult<List<String>> result = new InvokeResult<List<String>>();
        // 参数校验
        if(clientAuthRequest == null || clientAuthRequest.getSiteCode() == null){
            result.parameterError(InvokeResult.PARAM_ERROR);
            return result;
        }
        BaseStaffSiteOrgDto baseSite = null;
        try {
            baseSite = baseMajorManager.getBaseSiteBySiteId(clientAuthRequest.getSiteCode());
            if(baseSite == null){
                logger.warn("登录人所属站点不存在!");
                result.parameterError("登录人所属站点不存在!");
                return result;
            }
        }catch (Exception e){
            logger.error("根据站点:{}查询站点信息异常!", clientAuthRequest.getSiteCode(), e);
            // 防止查询基础资料异常影响现场正常使用，不限制菜单展示
            return result;
        }
        result.setData(obtainNoAuthMenuNew(baseSite));
        return result;
    }

    private List<String> obtainNoAuthMenuNew(BaseStaffSiteOrgDto baseSite) {
        List<String> noAuthMenuCodeList = new ArrayList<String>();
        Integer siteType = baseSite.getSiteType();
        Integer subType = baseSite.getSubType();
        // 打印客户端无权限的菜单编码
        String noAuthMenuConfig = uccPropertyConfiguration.getNoAuthMenuConfigNew();
        List<ClientNoAuthMenuCodeConfig> list = JsonHelper.jsonToList(noAuthMenuConfig, ClientNoAuthMenuCodeConfig.class);
        if(CollectionUtils.isEmpty(list)){
            return noAuthMenuCodeList;
        }
        for (ClientNoAuthMenuCodeConfig clientNoAuthMenuCodeConfig : list) {
            if(Objects.equals(siteType, clientNoAuthMenuCodeConfig.getSiteType())){
                if(clientNoAuthMenuCodeConfig.getSubType() == null || Objects.equals(clientNoAuthMenuCodeConfig.getSubType(), subType)){
                    return clientNoAuthMenuCodeConfig.getNoAuthMenuCodes();
                }
                break;
            }
        }
        return noAuthMenuCodeList;
    }

    /**
     * 获取站点无权限操作的菜单编码
     *  1、营业部限制：站点类型 4
     *      1)、称重量方 2)、包裹称重 3)、批量分拣称重 4)、快运称重打印
     *  2、自提点限制：站点类型 8
     *      1)、称重量方 2)、包裹称重 3)、批量分拣称重 4)、快运称重打印
     *  3、三方站点限制：站点类型 16
     *      1)、称重量方 2)、包裹称重 3)、批量分拣称重 4)、快运称重打印
     *  4、配送运输-集配站限制：站点类型：96、子站点类型：9605
     *      1)、称重量方 2)、包裹称重 3)、批量分拣称重 4)、快运称重打印
     *  5、虚拟站点-B2B2C虚拟站限制：站点类型：100、子站点类型：10003
     *      1)、称重量方 2)、包裹称重 3)、批量分拣称重 4)、快运称重打印
     *  6、快运终端限制：站点类型：101
     *      1)、称重量方 2)、包裹称重 3)、批量分拣称重 4)、快运称重打印
     * @param baseSite
     * @return
     */
    private List<String> obtainNoAuthMenu(BaseStaffSiteOrgDto baseSite) {
        List<String> noAuthMenuCodeList = new ArrayList<String>();
        Integer siteType = baseSite.getSiteType();
        Integer subType = baseSite.getSubType();
        // 打印客户端无权限的菜单编码
        String noAuthMenuConfig = uccPropertyConfiguration.getNoAuthMenuConfigUat();
        List<ClientNoAuthMenuCodeConfig> list = JsonHelper.jsonToList(noAuthMenuConfig, ClientNoAuthMenuCodeConfig.class);
        if(CollectionUtils.isEmpty(list)){
            return noAuthMenuCodeList;
        }
        for (ClientNoAuthMenuCodeConfig clientNoAuthMenuCodeConfig : list) {
            if(Objects.equals(siteType, clientNoAuthMenuCodeConfig.getSiteType())){
                if(clientNoAuthMenuCodeConfig.getSubType() == null || Objects.equals(clientNoAuthMenuCodeConfig.getSubType(), subType)){
                    return clientNoAuthMenuCodeConfig.getNoAuthMenuCodes();
                }
                break;
            }
        }
        return noAuthMenuCodeList;
    }

    /**
     * 获取某菜单功能权限
     *
     * @param clientAuthRequest
     * @return
     */
    @POST
    @Path("/client/getMenuAuthFunc")
    @JProfiler(jKey = "dms.ClientAuthorityResource.getMenuAuthFunc", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<ClientMenuDto> getMenuAuthFunc(ClientAuthRequest clientAuthRequest){
        InvokeResult<ClientMenuDto> result = new InvokeResult<ClientMenuDto>();
        // 参数校验
        if(clientAuthRequest == null || clientAuthRequest.getSiteCode() == null || clientAuthRequest.getMenuCode() == null){
            result.parameterError(InvokeResult.PARAM_ERROR);
            return result;
        }
        ClientMenuDto clientMenuDto = new ClientMenuDto();
        clientMenuDto.setMenuCode(clientAuthRequest.getMenuCode());
        clientMenuDto.setMenuName(ClientMenuEnum.getEnumName(Integer.parseInt(clientAuthRequest.getMenuCode())));
        // 判断菜单是否拥有某功能
        checkMenuFuncAuth(clientAuthRequest, clientMenuDto);
        result.setData(clientMenuDto);
        return result;
    }

    @POST
    @Path("/client/getMenuAuthFuncNew")
    @JProfiler(jKey = "dms.ClientAuthorityResource.getMenuAuthFuncNew", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<ClientMenuDto> getMenuAuthFuncNew(ClientAuthRequest clientAuthRequest){
        InvokeResult<ClientMenuDto> result = new InvokeResult<ClientMenuDto>();
        // 参数校验
        if(clientAuthRequest == null || clientAuthRequest.getSiteCode() == null || StringUtils.isEmpty(clientAuthRequest.getMenuCode())){
            result.parameterError(InvokeResult.PARAM_ERROR);
            return result;
        }
        ClientMenuDto clientMenuDto = new ClientMenuDto();
        clientMenuDto.setMenuCode(clientAuthRequest.getMenuCode());
        clientMenuDto.setMenuName(DeskClientMenuEnum.getEnumName(clientAuthRequest.getMenuCode()));
        // 判断菜单是否拥有某功能
        checkMenuFuncAuthNew(clientAuthRequest, clientMenuDto);
        result.setData(clientMenuDto);
        return result;
    }

    /**
     * 校验菜单功能权限
     *
     * @param clientAuthRequest
     * @param clientMenuDto
     */
    private void checkMenuFuncAuthNew(ClientAuthRequest clientAuthRequest, ClientMenuDto clientMenuDto) {
        // 站点平台打印是否校验开关
        if(Objects.equals(clientAuthRequest.getMenuCode(), DeskClientMenuEnum.SITE_CLIENT_PRINT.getCode())
                && !uccPropertyConfiguration.getSitePlateIsCheckFunc()){
            return;
        }
        Integer siteType = null;
        Integer subType = null;
        try {
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(clientAuthRequest.getSiteCode());
            if(baseSite == null){
                logger.warn("登录人所属站点不存在!");
                return;
            }
            siteType = baseSite.getSiteType();
            subType = baseSite.getSubType();
        }catch (Exception e){
            logger.error("根据站点:{}查询站点信息异常!", clientAuthRequest.getSiteCode(), e);
            return;
        }
        String menuCodeFuncConfig = uccPropertyConfiguration.getMenuCodeFuncConfigNew();
        List<ClientMenuFuncConfig> list = JsonHelper.jsonToList(menuCodeFuncConfig, ClientMenuFuncConfig.class);
        if(CollectionUtils.isEmpty(list)){
            // 无配置，均可操作
            return;
        }
        for (ClientMenuFuncConfig clientMenuFuncConfig : list) {
            // 匹配菜单编码、站点类型
            if(Objects.equals(clientMenuFuncConfig.getMenuCode(), clientAuthRequest.getMenuCode())){
                // 匹配站点类型
                if(Objects.equals(clientMenuFuncConfig.getSiteType(), siteType)){
                    // 站点子类型判断
                    if(clientMenuFuncConfig.getSubType() == null || Objects.equals(clientMenuFuncConfig.getSubType(), subType)){
                        clientMenuDto.setIsCanWeight(clientMenuFuncConfig.getIsCanWeight());
                        clientMenuDto.setIsCanPrint(clientMenuFuncConfig.getIsCanPrint());
                        return;
                    }
                }
            }
        }
    }

    /**
     * 校验菜单权限
     *
     * @param clientAuthRequest
     * @param clientMenuDto
     */
    private void checkMenuFuncAuth(ClientAuthRequest clientAuthRequest, ClientMenuDto clientMenuDto) {
        // 站点平台打印是否校验开关
        if(Objects.equals(clientAuthRequest.getMenuCode(), ClientMenuEnum.SITE_CLIENT_PRINT.getCode()) && !uccPropertyConfiguration.getSitePlateIsCheckFunc()){
            return;
        }
        Integer siteType = null;
        Integer subType = null;
        try {
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(clientAuthRequest.getSiteCode());
            if(baseSite == null){
                logger.warn("登录人所属站点不存在!");
                return;
            }
            siteType = baseSite.getSiteType();
            subType = baseSite.getSubType();
        }catch (Exception e){
            logger.error("根据站点:{}查询站点信息异常!", clientAuthRequest.getSiteCode(), e);
            return;
        }
        String menuCodeFuncConfig = uccPropertyConfiguration.getMenuCodeFuncConfigUat();
        List<ClientMenuFuncConfig> list = JsonHelper.jsonToList(menuCodeFuncConfig, ClientMenuFuncConfig.class);
        if(CollectionUtils.isEmpty(list)){
            // 无配置，均可操作
            return;
        }
        for (ClientMenuFuncConfig clientMenuFuncConfig : list) {
            // 匹配菜单编码、站点类型
            if(Objects.equals(clientMenuFuncConfig.getMenuCode(), clientAuthRequest.getMenuCode())){
                // 匹配站点类型
                if(Objects.equals(clientMenuFuncConfig.getSiteType(), siteType)){
                    // 站点子类型判断
                    if(clientMenuFuncConfig.getSubType() == null || Objects.equals(clientMenuFuncConfig.getSubType(), subType)){
                        clientMenuDto.setIsCanWeight(clientMenuFuncConfig.getIsCanWeight());
                        clientMenuDto.setIsCanPrint(clientMenuFuncConfig.getIsCanPrint());
                        return;
                    }
                }
            }
        }
    }
}