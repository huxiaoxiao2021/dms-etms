package com.jd.bluedragon.distribution.web.boxlimit.jsf;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.basic.DataResolver;
import com.jd.bluedragon.distribution.basic.ExcelDataResolverFactory;
import com.jd.bluedragon.distribution.basic.PropertiesMetaDataFactory;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitQueryDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitTemplateVO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitVO;
import com.jd.bluedragon.distribution.boxlimit.service.BoxLimitJsfService;
import com.jd.bluedragon.distribution.boxlimit.service.BoxLimitService;
import com.jd.bluedragon.distribution.web.boxlimit.BoxLimitController;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenyaguo
 * @date 2022/3/31 19:54
 */
public class BoxLimitJsfServiceImpl extends DmsBaseController implements BoxLimitJsfService {

    private static final Logger log = LoggerFactory.getLogger(BoxLimitController.class);

    @Autowired
    private BoxLimitService boxLimitService;


    /**
     * 获取列表
     */

    public JdResponse<PagerResult<BoxLimitVO>> listData(BoxLimitQueryDTO dto) {
        JdResponse response = new JdResponse();
        response.setCode(JdResponse.CODE_FAIL);
        response.setMessage(JdResponse.MESSAGE_FAIL);
        PagerResult<BoxLimitVO> result = boxLimitService.listData(dto);
        if(result != null){
            response.setData(result);
            response.setCode(JdResponse.CODE_SUCCESS);
            response.setMessage(JdResponse.MESSAGE_SUCCESS);
        }
        return response;
    }

    /**
     * 获取站点名称
     */
    public JdResponse getSiteNameById(Integer siteId) {

        return boxLimitService.querySiteNameById(siteId);
    }

    /**
     * 新建/修改
     */
    public JdResponse save(BoxLimitDTO dto) {
        if (dto.getId() == null) {
            return boxLimitService.create(dto, getLoginUser());
        } else {
            return boxLimitService.update(dto, getLoginUser());
        }
    }

    /**
     * 删除
     */
    public JdResponse delete(ArrayList<Long> ids) {
        return boxLimitService.delete(ids, getLoginUser().getUserErp());
    }

    /**
     * 导入
     */
    public JdResponse toImport(List<BoxLimitTemplateVO> dataList) {
        try {
            return boxLimitService.importData(dataList, getLoginUser());
        } catch (Exception e) {
            this.log.error("导入异常!", e);
            return new JdResponse(JdResponse.CODE_FAIL, e.getMessage());
        }
    }
}
