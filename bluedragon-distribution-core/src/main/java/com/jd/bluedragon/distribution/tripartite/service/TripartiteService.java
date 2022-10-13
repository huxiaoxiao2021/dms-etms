package com.jd.bluedragon.distribution.tripartite.service;

import com.jdl.basic.api.domain.sendHandover.SendTripartiteHandoverDetail;

import java.util.List;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.tripartite.service
 * @ClassName: TripartiteService
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/9/13 18:17
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public interface TripartiteService {

    /**
     * 查询前15个三方发送邮件列表
     * @param siteCode 站点编号
     * @return
     */
    List<SendTripartiteHandoverDetail> getTripartiteListBySiteCode(Integer siteCode);

    /**
     * 根据ID查询当前的邮件发送服务明细
     * @param id 数据库ID
     * @return
     */
    SendTripartiteHandoverDetail getTripartiteDetail(Long id);

    /**
     * 置最新的热点时间
     * @param id 数据库ID
     * @return
     */
    Boolean updateTripartiteDetail(Long id);

}
