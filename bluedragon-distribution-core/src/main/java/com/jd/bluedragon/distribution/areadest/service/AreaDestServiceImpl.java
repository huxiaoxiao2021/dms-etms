package com.jd.bluedragon.distribution.areadest.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.AreaDestRequest;
import com.jd.bluedragon.distribution.areadest.dao.AreaDestDao;
import com.jd.bluedragon.distribution.areadest.domain.AreaDest;
import com.jd.bluedragon.utils.RouteType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 区域批次目的地
 * <p>
 * Created by lixin39 on 2016/12/7.
 */
@Service("areaDestService")
public class AreaDestServiceImpl implements AreaDestService {

    private final Logger logger = Logger.getLogger(AreaDestServiceImpl.class);

    @Autowired
    private AreaDestDao areaDestDao;

    @Override
    public boolean add(AreaDest areaDest) {
        try {
            if (areaDestDao.add(areaDest) == 1) {
                return true;
            }
        } catch (Exception e) {
            logger.error("龙门架发货路线关系新增失败！", e);
        }
        return false;
    }

    @Override
    public Integer addBatch(List<AreaDest> areaDests) {
        try {
            return areaDestDao.addBatch(areaDests);
        } catch (Exception e) {
            logger.error("龙门架发货路线关系批量新增失败！", e);
        }
        return 0;
    }

    @Override
    public Integer addBatch(AreaDestRequest request, String user, Integer userCode) {
        try {
            List<AreaDest> areaDestList = new ArrayList<AreaDest>();
            for (String codeName : request.getReceiveSiteList()) {
                if (codeName != null && !"".equals(codeName)) {
                    String[] arr = codeName.split(",");
                    if (arr.length > 1) {
                        AreaDest areaDest = new AreaDest();
                        areaDest.setPlanId(request.getPlanId());
                        areaDest.setRouteType(request.getRouteType());
                        areaDest.setCreateSiteCode(request.getCreateSiteCode());
                        areaDest.setCreateSiteName(request.getCreateSiteName());
                        if (request.getRouteType() == RouteType.DIRECT_SITE.getType()) {
                            areaDest.setTransferSiteCode(0);
                            areaDest.setTransferSiteName("");
                        } else {
                            areaDest.setTransferSiteCode(request.getTransferSiteCode());
                            areaDest.setTransferSiteName(request.getTransferSiteName());
                        }
                        areaDest.setReceiveSiteCode(Integer.valueOf(arr[0]));
                        areaDest.setReceiveSiteName(arr[1]);
                        areaDest.setCreateUser(user);
                        areaDest.setCreateUserCode(userCode);
                        areaDestList.add(areaDest);
                    }
                }
            }
            if (areaDestList.size() > 0) {
                return addBatch(areaDestList);
            }
        } catch (Exception e) {
            logger.error("龙门架发货路线关系批量新增时发生异常！", e);
        }
        return 0;
    }

    @Override
    public boolean update(AreaDest areaDest) {
        try {
            areaDestDao.update(areaDest);
            return true;
        } catch (Exception e) {
            logger.error("龙门架发货路线关系更新时发生异常！", e);
        }
        return false;
    }

    @Override
    public boolean disable(Integer planId, String updateUser, Integer updateUserCode) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("planId", planId);
            params.put("updateUser", updateUser);
            params.put("updateUserCode", updateUserCode);
            areaDestDao.disableByPlanId(params);
            return true;
        } catch (Exception e) {
            logger.error("龙门架发货路线关系批量更新无效时发生异常！", e);
        }
        return false;
    }

    @Override
    public boolean disable(AreaDestRequest request, String updateUser, Integer updateUserCode) {
        try {
            List<String> siteList = request.getReceiveSiteList();
            if (siteList != null && siteList.size() > 0) {
                List<Integer> siteCodeList = new ArrayList<Integer>();
                for (String code : siteList) {
                    siteCodeList.add(Integer.valueOf(code));
                }
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("planId", request.getPlanId());
                params.put("createSiteCode", request.getCreateSiteCode());
                params.put("transferSiteCode", request.getTransferSiteCode());
                params.put("receiveSiteCodeList", siteCodeList);
                params.put("updateUser", updateUser);
                params.put("updateUserCode", updateUserCode);
                areaDestDao.disableByParams(params);
                return true;
            }
        } catch (Exception e) {
            logger.error("龙门架发货路线关系更新无效时发生异常！", e);
        }
        return false;
    }

    @Override
    public boolean enable(Integer id, String updateUser, Integer updateUserCode) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("updateUser", updateUser);
            params.put("updateUserCode", updateUserCode);
            params.put("id", id);
            areaDestDao.enableById(params);
            return true;
        } catch (Exception e) {
            logger.error("龙门架发货路线关系设置为有效失败！", e);
        }
        return false;
    }

    @Override
    public List<AreaDest> getList(Integer planId, RouteType type, Pager pager) {
        try {
            Map<String, Object> parameter = new HashMap<String, Object>();
            if (planId != null && planId > 0) {
                parameter.put("planId", planId);
            }
            if (type != null) {
                parameter.put("routeType", type.getType());
            }
            int total = areaDestDao.getCount(parameter);

            if (pager == null) {
                pager = new Pager();
            }

            if (total > 0) {
                pager.setTotalSize(total);
                pager.init();
                parameter.put("startIndex", pager.getStartIndex());
                parameter.put("pageSize", pager.getPageSize());
                return areaDestDao.getList(parameter);
            }
        } catch (Exception e) {
            logger.error("获取龙门架发货路线关系列表异常！", e);
        }
        return null;
    }

    @Override
    public List<AreaDest> getList(Integer planId, RouteType type) {
        try {
            Map<String, Object> parameter = new HashMap<String, Object>();
            if (planId != null && planId > 0) {
                parameter.put("planId", planId);
            }
            if (type != null) {
                parameter.put("routeType", type.getType());
            }
            return areaDestDao.getList(parameter);
        } catch (Exception e) {
            logger.error("获取龙门架发货路线关系列表异常！", e);
        }
        return null;
    }

    @Override
    public Integer getCount(Integer planId, RouteType type) {
        try {
            Map<String, Object> parameter = new HashMap<String, Object>();
            if (planId != null && planId > 0) {
                parameter.put("planId", planId);
            }
            if (type != null) {
                parameter.put("routeType", type.getType());
            }
            return areaDestDao.getCount(parameter);
        } catch (Exception e) {
            logger.error("根据方案编号、线路类型获取龙门架发货关系数量异常！", e);
        }
        return null;
    }

    @Override
    public Integer getCount(AreaDestRequest request) {
        try {
            Map<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("planId", request.getPlanId());
            parameter.put("routeType", request.getRouteType());
            parameter.put("createSiteCode", request.getCreateSiteCode());
            parameter.put("transferSiteCode", request.getTransferSiteCode());
            parameter.put("receiveSiteCode", request.getReceiveSiteCode());
            return areaDestDao.getCount(parameter);
        } catch (Exception e) {
            logger.error("获取龙门架发货关系数量时发生异常！", e);
        }
        return null;
    }


}
