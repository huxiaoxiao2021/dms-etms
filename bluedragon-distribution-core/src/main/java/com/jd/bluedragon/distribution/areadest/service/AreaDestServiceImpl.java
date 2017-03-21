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
            logger.error("区域批次目的地配置新增失败！", e);
        }
        return false;
    }

    @Override
    public boolean add(List<AreaDest> areaDests) {
        try {
            areaDestDao.addBatch(areaDests);
            return true;
        } catch (Exception e) {
            logger.error("区域批次目的地配置批量新增失败！", e);
        }
        return false;
    }

    @Override
    public boolean saveOrUpdate(AreaDestRequest request, String user, Integer userCode) {
        try {
            if (request.getReceiveSiteCodeName() != null && request.getReceiveSiteCodeName().size() > 0) {
                Integer createSiteCode = request.getCreateSiteCode();
                Integer transferSiteCode = request.getTransferSiteCode();
                List<AreaDest> areaDestList = new ArrayList<AreaDest>();

                for (String codeName : request.getReceiveSiteCodeName()) {
                    if (codeName != null && !"".equals(codeName)) {
                        String[] arr = codeName.split(",");
                        if (arr.length > 1) {
                            Integer receiveSiteCode = Integer.valueOf(arr[0]);
                            int result = this.doEnable(createSiteCode, transferSiteCode, receiveSiteCode, user, userCode);
                            if (result <= 0) {
                                AreaDest area = new AreaDest();
                                area.setCreateSiteCode(createSiteCode);
                                area.setCreateSiteName(request.getCreateSiteName());
                                area.setTransferSiteCode(transferSiteCode);
                                area.setTransferSiteName(request.getTransferSiteName());
                                area.setReceiveSiteCode(Integer.valueOf(arr[0]));
                                area.setReceiveSiteName(arr[1]);
                                area.setCreateUser(user);
                                area.setCreateUserCode(userCode);
                                areaDestList.add(area);
                            }
                        }
                    }
                }
                if (areaDestList.size() > 0) {
                    return add(areaDestList);
                }
                return true;
            }
        } catch (Exception e) {
            logger.error("区域批次目的地配置保存更新失败！", e);
        }
        return false;
    }

    @Override
    public boolean update(AreaDest areaDest) {
        try {
            areaDestDao.update(areaDest);
            return true;
        } catch (Exception e) {
            logger.error("区域批次目的地配置更新失败！", e);
        }
        return false;
    }

    @Override
    public boolean disable(Integer id, String updateUser, Integer updateUserCode) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("updateUser", updateUser);
            params.put("updateUserCode", updateUserCode);
            params.put("id", id);
            areaDestDao.disableById(params);
            return true;
        } catch (Exception e) {
            logger.error("区域批次目的地配置逻辑删除失败！", e);
        }
        return false;
    }

    @Override
    public boolean disable(Integer createSiteCode, Integer transferSiteCode, List<Integer> receiveSiteCode, String updateUser, Integer updateUserCode) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("createSiteCode", createSiteCode);
            params.put("transferSiteCode", transferSiteCode);
            params.put("receiveSiteCode", receiveSiteCode);
            params.put("updateUser", updateUser);
            params.put("updateUserCode", updateUserCode);
            areaDestDao.disableByParams(params);
            return true;
        } catch (Exception e) {
            logger.error("区域批次目的地配置逻辑删除失败！", e);
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
            logger.error("区域批次目的地配置设置为有效失败！", e);
        }
        return false;
    }

    @Override
    public boolean enable(Integer createSiteCode, Integer transferSiteCode, Integer receiveSiteCode, String updateUser, Integer updateUserCode) {
        try {
            doEnable(createSiteCode, transferSiteCode, receiveSiteCode, updateUser, updateUserCode);
            return true;
        } catch (Exception e) {
            logger.error("区域批次目的地配置设置为有效失败！", e);
        }
        return false;
    }

    private int doEnable(Integer createSiteCode, Integer transferSiteCode, Integer receiveSiteCode, String updateUser, Integer updateUserCode) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("createSiteCode", createSiteCode);
        params.put("transferSiteCode", transferSiteCode);
        params.put("receiveSiteCode", receiveSiteCode);
        params.put("updateUser", updateUser);
        params.put("updateUserCode", updateUserCode);
        return areaDestDao.enableByParams(params);
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
            logger.error("获取龙门架发货关系列表异常！", e);
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


}
