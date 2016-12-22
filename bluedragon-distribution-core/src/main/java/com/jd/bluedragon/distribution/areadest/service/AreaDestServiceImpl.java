package com.jd.bluedragon.distribution.areadest.service;

import com.jd.bluedragon.distribution.api.request.AreaDestRequest;
import com.jd.bluedragon.distribution.api.response.AreaDestTree;
import com.jd.bluedragon.distribution.areadest.dao.AreaDestDao;
import com.jd.bluedragon.distribution.areadest.domain.AreaDest;
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
    public List<AreaDest> getList(Integer createSiteCode, Integer transferSiteCode, Integer receiveSiteCode) {
        try {
            Map<String, Object> params = new HashMap<String, Object>();
            if (createSiteCode != null && createSiteCode > 0) {
                params.put("createSiteCode", createSiteCode);
            }
            if (transferSiteCode != null && transferSiteCode > 0) {
                params.put("transferSiteCode", transferSiteCode);
            }
            if (receiveSiteCode != null && receiveSiteCode > 0) {
                params.put("receiveSiteCode", receiveSiteCode);
            }
            return areaDestDao.getList(params);
        } catch (Exception e) {
            logger.error("区域批次目的地配置信息获取失败！", e);
        }
        return null;
    }

    @Override
    public List<AreaDestTree> getTree(Integer createSiteCode, Integer transferSiteCode, Integer receiveSiteCode) {
        List<AreaDest> areaDestList = this.getList(createSiteCode, transferSiteCode, receiveSiteCode);
        if (areaDestList != null && areaDestList.size() > 0) {
            return this.doGetTree(areaDestList);
        }
        return null;
    }

    /**
     * 获取结构树方法，该方法仅针对固定一级的三级结构树，无法获取无限级结构
     *
     * @param areaDestList
     * @return
     */
    private List<AreaDestTree> doGetTree(List<AreaDest> areaDestList) {
        AreaDestTree areaDest = this.initResponseTree(areaDestList);
        for (int i = 1, len = areaDestList.size(); i < len; i++) {
            boolean flag = false;
            List<AreaDestTree> transferNodes = areaDest.getNodes();
            for (AreaDestTree transfer : transferNodes) {
                // 判断是否为已存在的中转分拣站中心
                if (transfer.getId() == areaDestList.get(i).getTransferSiteCode()) {
                    AreaDestTree dest = new AreaDestTree();
                    dest.setId(areaDestList.get(i).getReceiveSiteCode());
                    dest.setText(areaDestList.get(i).getReceiveSiteName());
                    transfer.getNodes().add(dest);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                // 目的分拣中心
                List<AreaDestTree> destNodes = new ArrayList<AreaDestTree>();
                AreaDestTree dest = new AreaDestTree();
                dest.setId(areaDestList.get(i).getReceiveSiteCode());
                dest.setText(areaDestList.get(i).getReceiveSiteName());
                destNodes.add(dest);

                // 中转分拣中心
                AreaDestTree transfer = new AreaDestTree();
                transfer.setId(areaDestList.get(i).getTransferSiteCode());
                transfer.setText(areaDestList.get(i).getTransferSiteName());
                transfer.setNodes(destNodes);
                areaDest.getNodes().add(transfer);
            }
        }
        List<AreaDestTree> tree = new ArrayList<AreaDestTree>();
        tree.add(areaDest);
        return tree;
    }

    /**
     * 初始化结构树第一个节点,减少在循环内判断空值
     *
     * @param areaDestList
     * @return
     */
    private AreaDestTree initResponseTree(List<AreaDest> areaDestList) {
        // 目的分拣中心
        List<AreaDestTree> destNodes = new ArrayList<AreaDestTree>();
        AreaDestTree dest = new AreaDestTree();
        dest.setId(areaDestList.get(0).getReceiveSiteCode());
        dest.setText(areaDestList.get(0).getReceiveSiteName());
        destNodes.add(dest);

        // 中转分拣中心
        List<AreaDestTree> transferNodes = new ArrayList<AreaDestTree>();
        AreaDestTree transfer = new AreaDestTree();
        transfer.setId(areaDestList.get(0).getTransferSiteCode());
        transfer.setText(areaDestList.get(0).getTransferSiteName());
        transfer.setNodes(destNodes);
        transferNodes.add(transfer);

        // 始发分拣中心
        AreaDestTree tree = new AreaDestTree();
        tree.setId(areaDestList.get(0).getCreateSiteCode());
        tree.setText(areaDestList.get(0).getCreateSiteName());
        tree.setNodes(transferNodes);
        return tree;
    }

}
