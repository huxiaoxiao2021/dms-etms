package com.jd.bluedragon.distribution.web.funcSwitchConfig;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.funcSwitchConfig.domain.FuncSwitchConfigCondition;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigDto;
import com.jd.bluedragon.distribution.funcSwitchConfig.FuncSwitchConfigEnum;
import com.jd.bluedragon.distribution.funcSwitchConfig.service.FuncSwitchConfigService;
import com.jd.bluedragon.distribution.whitelist.DimensionEnum;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 功能开关配置
 *
 * @author: hujiping
 * @date: 2020/9/16 15:32
 */
@Controller
@RequestMapping("/funcSwitchConfig")
public class FuncSwitchConfigController extends DmsBaseController {

    private static final Logger logger = LoggerFactory.getLogger(FuncSwitchConfigController.class);

    @Autowired
    private FuncSwitchConfigService funcSwitchConfigService;

    /**
     * 主页面
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_INTERCEPT_CONFIG_R)
    @RequestMapping("/toIndex")
    public String toIndex(){
        return "funcSwitchConfig/funcSwitchConfig";
    }

    /**
     * 获取功能
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_INTERCEPT_CONFIG_R)
    @RequestMapping("/getMenu")
    @ResponseBody
    public Map<Integer,String> getMenu(){
        return FuncSwitchConfigEnum.interceptMenuEnumMap;
    }

    /**
     * 获取维度
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_INTERCEPT_CONFIG_R)
    @RequestMapping("/getDimension")
    @ResponseBody
    public Map<Integer,String> getDimension(){
        return DimensionEnum.dimensionEnumMap;
    }

    /**
     * 分页查询
     * @param condition
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_INTERCEPT_CONFIG_R)
    @RequestMapping("/listData")
    @ResponseBody
    public PagerResult<FuncSwitchConfigDto> listData(@RequestBody FuncSwitchConfigCondition condition){
        return funcSwitchConfigService.queryByCondition(condition);
    }

    /**
     * 保存数据
     * @param funcSwitchConfigDto
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_INTERCEPT_CONFIG_R)
    @RequestMapping(value = "/save")
    public @ResponseBody
    JdResponse save(@RequestBody FuncSwitchConfigDto funcSwitchConfigDto) {
        LoginUser loginUser = getLoginUser();
        funcSwitchConfigDto.setCreateErp(loginUser.getUserErp());
        funcSwitchConfigDto.setCreateUser(loginUser.getUserName());
        return funcSwitchConfigService.insert(funcSwitchConfigDto);
    }

    /**
     * 变更数据
     * @param funcSwitchConfigDto
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_INTERCEPT_CONFIG_R)
    @RequestMapping(value = "/update")
    public @ResponseBody
    JdResponse update(@RequestBody FuncSwitchConfigDto funcSwitchConfigDto) {
        LoginUser loginUser = getLoginUser();
        funcSwitchConfigDto.setCreateErp(loginUser.getUserErp());
        funcSwitchConfigDto.setCreateUser(loginUser.getUserName());
        return funcSwitchConfigService.update(funcSwitchConfigDto);
    }

    /**
     * 逻辑删除
     * @param dtos
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_INTERCEPT_CONFIG_R)
    @RequestMapping(value = "/deleteByIds", method = RequestMethod.POST)
    public @ResponseBody
    JdResponse deleteByIds(@RequestBody List<FuncSwitchConfigDto> dtos) {
        return funcSwitchConfigService.logicalDelete(dtos,getLoginUser().getUserErp());
    }

    /**
     * 导入excel表格
     *
     * @param file
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_INTERCEPT_CONFIG_R)
    @RequestMapping(value = "/uploadExcel", method = RequestMethod.POST)
    @ResponseBody
    public JdResponse uploadExcel(@RequestParam("importExcelFile") MultipartFile file) {
        JdResponse response = new JdResponse();
        try {
            String fileName = file.getOriginalFilename();
            if (!fileName.endsWith("xls"))  {
                response.toFail("文件格式不正确!");
                return response;
            }
            DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(ExcelDataResolverFactory.EXCEL_2003);
            List<FuncSwitchConfigDto> dataList = dataResolver.resolver(file.getInputStream(), FuncSwitchConfigDto.class,
                    new PropertiesMetaDataFactory("/excel/funcSwitchConfig.properties"));
            //对导入的数据进行校验
            String loginUser = getLoginUser().getUserErp();
            funcSwitchConfigService.checkExportData(dataList,loginUser,response);
            if(!JdResponse.CODE_SUCCESS.equals(response.getCode())){
                return response;
            }
            //批量插入数据
            funcSwitchConfigService.importExcel(dataList, getLoginUser());
        } catch (Exception e) {
            logger.error("导入数据异常",e);
            response.toFail("导入数据异常!");
        }
        return response;
    }


    /**
     * 批量添加
     *  -用于初始化数据
     * @param list
     * @return
     */
    @Authorization(Constants.DMS_WEB_TOOL_INTERCEPT_CONFIG_R)
    @RequestMapping(value = "/batchInsert", method = RequestMethod.POST)
    public @ResponseBody
    JdResponse batchInsert(@RequestBody List<FuncSwitchConfigDto> list) {

        return funcSwitchConfigService.batchInsert(list);
    }

}
