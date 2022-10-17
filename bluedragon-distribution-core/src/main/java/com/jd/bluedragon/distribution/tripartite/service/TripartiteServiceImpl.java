package com.jd.bluedragon.distribution.tripartite.service;

import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jsf.gd.config.annotation.AutoActive;
import com.jd.ump.annotation.JProfiler;
import com.jdl.basic.api.domain.sendHandover.SendTripartiteHandoverDetail;
import com.jdl.basic.api.service.sendHandover.SendHandoverJsfService;
import com.jdl.basic.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.tripartite.service
 * @ClassName: TripartiteServiceImpl
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/9/13 18:18
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Service
@Slf4j
public class TripartiteServiceImpl implements TripartiteService{

    @Autowired
    private SendHandoverJsfService sendHandoverJsfService;

    @Override
    @JProfiler(jKey = "DMS.WEB.TripartiteService.getTripartiteListBySiteCode")
    public List<SendTripartiteHandoverDetail> getTripartiteListBySiteCode(Integer siteCode) {
        try {
            Result<List<SendTripartiteHandoverDetail>> result = sendHandoverJsfService.getListOrderByOperateTimeLimit(siteCode);
            if (result != null && result.isSuccess()) {
                return result.getData();
            } else {
                log.warn("调用拣运基础资料查询三方邮件列表服务失败，参数为：{}，返回值为：{}", siteCode, JsonHelper.toJson(result));
            }
        } catch (RuntimeException e) {
            log.error("调用拣运基础资料查询三方邮件列表服务异常，参数：{}", siteCode, e);
        }
        return Collections.emptyList();
    }

    @Override
    @JProfiler(jKey = "DMS.WEB.TripartiteService.getTripartiteDetail")
    public SendTripartiteHandoverDetail getTripartiteDetail(Long id) {
        try {
            Result<SendTripartiteHandoverDetail> result = sendHandoverJsfService.getInfoById(id);
            if (result != null && result.isSuccess()) {
                return result.getData();
            } else {
                log.warn("调用拣运基础资料查询三方邮件详情服务失败，参数为：{}，返回值为：{}", id, JsonHelper.toJson(result));
            }
        } catch (RuntimeException e) {
            log.error("调用拣运基础资料查询三方邮件详情服务异常，参数：{}", id, e);
        }
        return null;
    }

    @Override
    @JProfiler(jKey = "DMS.WEB.TripartiteService.updateTripartiteDetail")
    public Boolean updateTripartiteDetail(Long id) {
        try {
            Result<Boolean> result = sendHandoverJsfService.updateLastOperateTimeById(id);
            if (result != null && result.isSuccess()) {
                return result.getData();
            } else {
                log.warn("调用拣运基础资料更新三方服务列表热点值失败，参数为：{}，返回值为：{}", id, JsonHelper.toJson(result));
            }
        } catch (RuntimeException e) {
            log.error("调用拣运基础资料更新三方服务列表服务异常，参数：{}", id, e);
        }
        return Boolean.FALSE;
    }
}
