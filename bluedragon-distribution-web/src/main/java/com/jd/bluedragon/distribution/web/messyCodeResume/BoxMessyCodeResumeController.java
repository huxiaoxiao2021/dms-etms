package com.jd.bluedragon.distribution.web.messyCodeResume;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.box.dao.BoxDao;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.uim.annotation.Authorization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 工具类 用于还原因jproxy故障造成的箱号乱码
 *
 * @author luyue5
 * time: 2017-01-11
 */
@RequestMapping("/messyCodeResume/box/")
@Controller
public class BoxMessyCodeResumeController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    BoxDao dao;

    /**
     * 根据boxCode 更新create_site_name，receive_site_name 用于还原因jproxy故障造成的箱号站点乱码
     * @param condition 条件:code  更改值:createSiteCode receiveSiteCode
     * @return
     */
    @Authorization(Constants.DMS_WEB_DEVELOP_TOOLS_SUMMARY_R)
    @RequestMapping("/updateBoxMessyCode")
    @ResponseBody
    public InvokeResult<Integer> updateBoxMessyCode(Box condition) {
        Box box = new Box();

        box.setCode(condition.getCode());
        box.setCreateSiteName(condition.getCreateSiteName());
        box.setReceiveSiteName(condition.getReceiveSiteName());

        try {
            int excuteResult = dao.updateMessySiteNameByBoxCode(box);
            if (excuteResult >= 1) {
                InvokeResult<Integer> result = new InvokeResult<Integer>();
                result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
                result.setMessage(InvokeResult.RESULT_SUCCESS_MESSAGE);
                result.setData(excuteResult);
                return result;
            } else {
                InvokeResult<Integer> result = new InvokeResult<Integer>();
                result.setCode(InvokeResult.RESULT_PARAMETER_ERROR_CODE);
                result.setMessage("未更新任何箱号数据！请检查箱号！");
                result.setData(excuteResult);
                return result;
            }

        } catch (Exception e) {
            log.error("箱号乱码处理失败", e);
            InvokeResult<Integer> result = new InvokeResult<Integer>();
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
            return result;
        }

    }
}
