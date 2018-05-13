package com.jd.bluedragon.distribution.web.operatelog;


import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclp;
import com.jd.bluedragon.distribution.operateMonitor.domain.OperateMonitor;
import com.jd.bluedragon.distribution.operateMonitor.service.OperateMonitorService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 分拣中心实操监控
 * 2018年5月10日16:01:01
 * author：shipeilin
 */
@Controller
@RequestMapping("/dmsOperateMonitor")
public class DmsOperateMonitorController {

    @Autowired
    private OperateMonitorService operateMonitorService;

    private static final Log logger = LogFactory.getLog(DmsOperateMonitorController.class);

    /**
     * 跳转到主界面
     * @return
     */
    @RequestMapping("/index")
    public String index() {
        return "operateMonitor/operateMonitor";
    }

    /**
     * 根据包裹号查询实操数据
     * @return
     */
    @RequestMapping(value = "/listData")
    public @ResponseBody PagerResult<OperateMonitor> listData(@RequestBody OperateMonitor operateMonitor) {
        PagerResult<OperateMonitor> rest = new PagerResult<OperateMonitor>();
        if(BusinessHelper.isPackageCode(operateMonitor.getPackageCode())){
            List<OperateMonitor> data = operateMonitorService.queryOperateMonitorByPackageCode(operateMonitor.getPackageCode());
            rest.setRows(data);
            rest.setTotal(data.size());
        }
        return rest;
    }

}
