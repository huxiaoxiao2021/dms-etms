package com.jd.bluedragon.distribution.web.boxlimit.jsf;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitQueryDTO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitTemplateVO;
import com.jd.bluedragon.distribution.boxlimit.BoxLimitVO;
import com.jd.bluedragon.distribution.boxlimit.service.BoxLimitJsfService;
import com.jd.bluedragon.distribution.boxlimit.service.BoxLimitService;
import com.jd.bluedragon.distribution.web.boxlimit.BoxLimitController;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenyaguo
 * @date 2022/3/31 19:54
 */
@Service("boxLimitJsfService")
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
        log.info("新增或者修改集箱包裹限制-入参-{}", JSON.toJSONString(dto));
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
            this.log.error("导入异常!-{}", e.getMessage(),e);
            return new JdResponse(JdResponse.CODE_FAIL, e.getMessage());
        }
    }

    @Override
    public JdResponse<Integer> countByCondition(BoxLimitQueryDTO dto) {
        return boxLimitService.countByCondition(dto);
    }
}
