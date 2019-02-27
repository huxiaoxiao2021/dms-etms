package com.jd.bluedragon.distribution.receive;

import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.receive.domain.ReceiveWeightCheckCondition;
import com.jd.bluedragon.distribution.receive.domain.ReceiveWeightCheckResult;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName: ReceiveWeightCheck
 * @Description: 揽收重量校验统计--Controller实现
 * @author: hujiping
 * @date: 2019/2/26 20:58
 */
@Controller
@RequestMapping("receive")
public class ReceiveWeightCheck extends DmsBaseController {


    /**
     * 返回主页面
     * @return
     */
    @RequestMapping("/toIndex")
    public String toIndex(){
        return "/receive/receiveWeightCheck";
    }

    @RequestMapping("/listData")
    @ResponseBody
    public PagerResult<ReceiveWeightCheckResult> listData(@RequestBody ReceiveWeightCheckCondition receiveWeightCheckCondition){

        return null;
    }

}
