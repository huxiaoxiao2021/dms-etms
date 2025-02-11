package com.jd.bluedragon.distribution.web.boxlimit;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitQueryDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitTemplateVO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitVO;
import com.jd.bluedragon.distribution.boxlimit.service.BoxLimitService;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * PDA建箱包裹数配置表
 * TODO 接口权限修改
 */
@Controller
@RequestMapping("/boxlimit")
public class BoxLimitController extends DmsBaseController {

    private static final Logger log = LoggerFactory.getLogger(BoxLimitController.class);

    @Autowired
    private BoxLimitService boxLimitService;

    /**
     * 返回主页面
     */
    @Authorization(Constants.DMS_WEB_TOOL_BOXLMIT_R)
    @RequestMapping("/toIndex")
    public String toIndex(Model model){

        return "/boxlimit/boxLimitIndex";
    }

    /**
     * 获取列表
     */
    @Authorization(Constants.DMS_WEB_TOOL_BOXLMIT_R)
    @RequestMapping("/listData")
    @ResponseBody
    public PagerResult<BoxLimitVO> listData(@RequestBody BoxLimitQueryDTO dto){

        return boxLimitService.listData(dto);
    }
    /**
     * 获取站点名称
     */
    @Authorization(Constants.DMS_WEB_TOOL_BOXLMIT_R)
    @RequestMapping("/getSiteNameById")
    @ResponseBody
    public JdResponse getSiteNameById(Integer siteId){

        return boxLimitService.querySiteNameById(siteId);
    }
    /**
     * 新建/修改
     */
    @Authorization(Constants.DMS_WEB_TOOL_BOXLMIT_R)
    @RequestMapping("/save")
    @ResponseBody
    public JdResponse save(@RequestBody BoxLimitDTO dto){
        if (dto.getId() == null) {
            return boxLimitService.create(dto, getLoginUser());
        } else {
            return boxLimitService.update(dto, getLoginUser());
        }
    }
    /**
     * 删除
     */
    @Authorization(Constants.DMS_WEB_TOOL_BOXLMIT_R)
    @RequestMapping("/delete")
    @ResponseBody
    public JdResponse delete(@RequestBody ArrayList<Long> ids){

        return boxLimitService.delete(ids, getLoginUser().getUserErp());
    }
    /**
     * 导入
     */
    @Authorization(Constants.DMS_WEB_TOOL_BOXLMIT_R)
    @RequestMapping(value = "/toImport", method = RequestMethod.POST)
    @ResponseBody
    public JdResponse toImport(@RequestParam("importExcelFile") MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            if (!fileName.endsWith("xlsx")) {
                return new JdResponse(JdResponse.CODE_FAIL,"文件格式不对!");
            }
            DataResolver dataResolver = ExcelDataResolverFactory.getDataResolver(2);
            List<BoxLimitTemplateVO> dataList = dataResolver.resolver(file.getInputStream(), BoxLimitTemplateVO.class, new PropertiesMetaDataFactory("/excel/boxLimitCheck.properties"));
            return boxLimitService.importData(dataList,getLoginUser());
        } catch (Exception e) {
            this.log.error("导入异常!",e);
            return new JdResponse(JdResponse.CODE_FAIL, e.getMessage());
        }
    }
}
