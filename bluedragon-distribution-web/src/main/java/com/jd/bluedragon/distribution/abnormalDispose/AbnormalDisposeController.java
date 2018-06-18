package com.jd.bluedragon.distribution.abnormalDispose;

import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年06月13日 14时:25分
 */
@Controller
@RequestMapping("abnormalDispose/abnormalDispose")
public class AbnormalDisposeController extends DmsBaseController {
    /**
     * 返回主页面
     *
     * @return
     */
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
        return "/abnormalDispose/abnormalDispose";
    }

    /**
     * 根据条件分页查询数据信息
     *
     * @param abnormalDisposeCondition
     * @return
     */
    @RequestMapping(value = "/listData")
    public @ResponseBody
    PagerResult<AbnormalDisposeMain> listData(@RequestBody AbnormalDisposeCondition abnormalDisposeCondition) {
        JdResponse<PagerResult<AbnormalDisposeMain>> rest = new JdResponse<PagerResult<AbnormalDisposeMain>>();
        return rest.getData();
    }

    /**
     * 根据条件分页查询数据信息
     *
     * @param abnormalDisposeCondition
     * @return
     */
    @RequestMapping(value = "/inspection/listData", method = RequestMethod.POST)
    public @ResponseBody
    PagerResult<AbnormalDisposeMain> inspectionListData(@RequestBody AbnormalDisposeCondition abnormalDisposeCondition) {
        JdResponse<PagerResult<AbnormalDisposeMain>> rest = new JdResponse<PagerResult<AbnormalDisposeMain>>();
        return rest.getData();
    }

    /**
     * 根据条件分页查询数据信息
     *
     * @param abnormalDisposeCondition
     * @return
     */
    @RequestMapping(value = "/send/listData")
    public @ResponseBody
    PagerResult<AbnormalDisposeMain> sendListData(@RequestBody AbnormalDisposeCondition abnormalDisposeCondition) {
        JdResponse<PagerResult<AbnormalDisposeMain>> rest = new JdResponse<PagerResult<AbnormalDisposeMain>>();
        return rest.getData();
    }

}
