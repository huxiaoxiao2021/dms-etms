package com.jd.bluedragon.distribution.whiteList.service.Impl;

import com.jd.bluedragon.distribution.whiteList.dao.WhiteListDao;
import com.jd.bluedragon.distribution.whiteList.service.WhiteListService;
import com.jd.bluedragon.distribution.whitelist.DimensionEnum;
import com.jd.bluedragon.distribution.whitelist.MenuEnum;
import com.jd.bluedragon.distribution.whitelist.WhiteList;
import com.jd.bluedragon.distribution.whitelist.WhiteListCondition;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lijie
 * @date 2020/3/10 16:31
 */
@Service
public class WhiteListServiceImpl implements WhiteListService {

    private static final Logger log = LoggerFactory.getLogger(WhiteListServiceImpl.class);

    @Autowired
    private WhiteListDao whiteListDao;

    @Override
    public PagerResult<WhiteList> queryByCondition(WhiteListCondition condition) {

        PagerResult<WhiteList> result = new PagerResult<>();
        List<WhiteList> whiteLists = new ArrayList<>();
        try{
            whiteLists = whiteListDao.queryByCondition(condition);

            result.setRows(whiteLists);
            result.setTotal(whiteLists.size());
        }catch (RuntimeException e){
            log.error("查询失败!",e);
            result.setRows(new ArrayList<WhiteList>());
            result.setTotal(0);
        }
        return result;
    }

    @Override
    public JdResponse<Boolean> save(WhiteList whiteList) {

        JdResponse<Boolean> rest = new JdResponse<>();
        try{
            //校验数据是否已存在
            int count = whiteListDao.query(whiteList);
            if(count>0){
                log.warn("该名单已经存在！场地:{}，erp:{}",whiteList.getSiteCode(),whiteList.getErp());
                rest.toError("该名单已存在！");
                return rest;
            }

            whiteList.setMenuName(MenuEnum.getEnumByKey(whiteList.getMenuId()).getName());
            whiteList.setDimensionName(DimensionEnum.getEnumByKey(whiteList.getDimensionId()).getName());
            int res = whiteListDao.save(whiteList);
            if(res>0){
                rest.setData(true);
            }
        }catch (RuntimeException e){
            log.error("新增白名单信息保存失败：{}", JsonHelper.toJson(whiteList), e);
            rest.toError("保存失败，服务异常！");
        }

        return rest;
    }

    @Override
    public JdResponse<Integer> deleteByIds(List<Long> ids) {
        JdResponse<Integer> rest = new JdResponse<>();
        try{
            rest.setData(whiteListDao.deleteByIds(ids));
        }catch(RuntimeException e){
            log.error("删除白名单失败：{}", JsonHelper.toJson(ids), e);
            rest.toError("删除白名单失败，服务异常！");
        }
        return rest;
    }
}
