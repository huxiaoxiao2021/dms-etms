package com.jd.bluedragon.distribution.areadest.service;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.api.request.AreaDestRequest;
import com.jd.bluedragon.distribution.areadest.dao.AreaDestDao;
import com.jd.bluedragon.distribution.areadest.domain.AreaDest;
import com.jd.bluedragon.utils.RouteType;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.zip.DataFormatException;

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

    @Autowired
    private BaseMajorManager baseMajorManager;

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
    public Integer addBatch(AreaDestRequest request, String user, Integer userCode) throws Exception{
        try {
            List<AreaDest> areaDestList = new ArrayList<AreaDest>();
            for (String codeName : request.getReceiveSiteList()) {
                if (codeName != null && !"".equals(codeName)) {
                    String[] arr = codeName.split(",");
                    if (arr.length > 1) {
                        Integer count = this.getCount(request.getPlanId(), request.getCreateSiteCode(), Integer.valueOf(arr[0]));
                        if (count != null && count >= 1){
                            throw new Exception("操作失败，已存在目的站点为“"+ arr[1] + "”的路线关系！");
                        }
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
            throw e;
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
    public AreaDest get(Integer planId, Integer createSiteCode, Integer receiveSiteCode) {
        Map<String, Object> parameter = new HashMap<String, Object>();
        parameter.put("planId", planId);
        parameter.put("createSiteCode", createSiteCode);
        parameter.put("receiveSiteCode", receiveSiteCode);
        return areaDestDao.get(parameter);
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
    public List<AreaDest> getList(Integer planId, Integer createSiteCode, Integer receiveSiteCode) {
        try {
            Map<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("planId", planId);
            parameter.put("createSiteCode", createSiteCode);
            parameter.put("receiveSiteCode", receiveSiteCode);
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
    public Integer getCount(Integer planId, Integer createSiteCode, Integer receiveSiteCode) {
        try {
            Map<String, Object> parameter = new HashMap<String, Object>();
            parameter.put("planId", planId);
            parameter.put("createSiteCode", createSiteCode);
            parameter.put("receiveSiteCode", receiveSiteCode);
            return areaDestDao.getCount(parameter);
        } catch (Exception e) {
            logger.error("获取龙门架发货关系数量时发生异常！", e);
        }
        return null;
    }

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void importForExcel(Map<RouteType, Sheet> sheets, AreaDestRequest request, String userName, Integer userCode) throws Exception {
        Set<AreaDest> importData = new HashSet<AreaDest>();
        for (Map.Entry<RouteType, Sheet> entry : sheets.entrySet()) {
            Set<AreaDest> areaDestSet = doImportBySheet(entry.getKey(), entry.getValue(), request, userName, userCode);
            for (AreaDest areaDest : areaDestSet) {
                if (!importData.add(areaDest)) {
                    throw new DataFormatException("【" + RouteType.getEnum(areaDest.getRouteType()).getName() + "】预分拣站点/末级分拣中心：" + areaDest.getReceiveSiteName() + "，站点编号：" + areaDest.getReceiveSiteCode() + "，在其他页签中已存在发货路线关系");
                }
            }
        }
        if (!importData.isEmpty()) {
            disable(request.getPlanId(), userName, userCode);
            areaDestDao.addBatch(new ArrayList<AreaDest>(importData));
        }
    }

    private Set<AreaDest> doImportBySheet(RouteType routeType, Sheet sheet, AreaDestRequest request, String userName, Integer userCode) throws Exception {
        Set<AreaDest> insertSet = new HashSet<AreaDest>();
        for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            List<BaseStaffSiteOrgDto> baseStaffSites = baseMajorManager.getDmsSiteAll();
            AreaDest areaDest = new AreaDest();
            // 初始化中转站点信息，避免无中转站点时插入报错
            areaDest.setTransferSiteCode(0);
            areaDest.setTransferSiteName("");
            // 检查单元格是否符合列对应的要求
            checkCellFormat(row, routeType);
            // 检查输入的站点code是否存在
            checkSiteCodeValid(row, routeType, baseStaffSites, areaDest);
            areaDest.setPlanId(request.getPlanId());
            areaDest.setRouteType(routeType.getType());
            areaDest.setCreateSiteCode(request.getCreateSiteCode());
            areaDest.setCreateSiteName(request.getCreateSiteName());
            areaDest.setCreateUser(userName);
            areaDest.setCreateUserCode(userCode);
            if (!insertSet.add(areaDest)) {
                throw new DataFormatException("【" + routeType.getName() + "】第" + (rowIndex + 1) + "行在该页签中存在重复关系");
            }
        }
        return insertSet;
    }

    /**
     * 检查指定的单元格格式是否是规定的格式
     *
     * @param row
     * @param type
     * @throws Exception
     */
    private void checkCellFormat(Row row, RouteType type) throws Exception {
        int rowIndex = row.getRowNum();
        Cell cell0 = row.getCell(0);
        Cell cell2 = row.getCell(2);
        switch (type) {
            case DIRECT_SITE:
                if (null == cell0 || cell0.getCellType() == Cell.CELL_TYPE_BLANK) {
                    throw new DataFormatException(type.getName() + "(" + (rowIndex + 1) + "行," + (1) + "列) 此处为必填项");
                }
                if (cell0.getCellType() != Cell.CELL_TYPE_NUMERIC) {
                    throw new DataFormatException(type.getName() + "(" + (rowIndex + 1) + "行," + (1) + "列) 数据格式不正确");
                }
                break;
            case DIRECT_DMS:
                if (null == cell0 || cell0.getCellType() == Cell.CELL_TYPE_BLANK) {
                    throw new DataFormatException(type.getName() + "(" + (rowIndex + 1) + "行," + (1) + "列) 此处为必填项");
                }
                if (cell0.getCellType() != Cell.CELL_TYPE_NUMERIC) {
                    throw new DataFormatException(type.getName() + "(" + (rowIndex + 1) + "行," + (1) + "列) 数据格式不正确");
                }

                if (null == cell2 || cell2.getCellType() == Cell.CELL_TYPE_BLANK) {
                    throw new DataFormatException(type.getName() + "(" + (rowIndex + 1) + "行," + (3) + "列) 此处为必填项");
                }
                if (cell2.getCellType() != Cell.CELL_TYPE_NUMERIC) {
                    throw new DataFormatException(type.getName() + "(" + (rowIndex + 1) + "行," + (3) + "列) 数据格式不正确");
                }
                break;
            case MULTIPLE_DMS:
                if (null != cell0 && cell0.getCellType() != Cell.CELL_TYPE_NUMERIC) {
                    throw new DataFormatException(type.getName() + "(" + (rowIndex + 1) + "行," + (1) + "列) 数据格式不正确");
                }
                if (null == cell2 || cell2.getCellType() == Cell.CELL_TYPE_BLANK) {
                    throw new DataFormatException(type.getName() + "(" + (rowIndex + 1) + "行," + (3) + "列) 此处为必填项");
                }
                if (cell2.getCellType() != Cell.CELL_TYPE_NUMERIC) {
                    throw new DataFormatException(type.getName() + "(" + (rowIndex + 1) + "行," + (3) + "列) 数据格式不正确");
                }
                break;
        }
    }

    /**
     * 检查站点是否存在
     *
     * @param row
     * @param type
     * @param siteCodes
     * @param areaDest
     * @throws DataFormatException
     */
    private void checkSiteCodeValid(Row row, RouteType type, List<BaseStaffSiteOrgDto> siteCodes, AreaDest areaDest) throws DataFormatException {
        int rowIndex = row.getRowNum();
        BaseStaffSiteOrgDto siteOrgDto;
        Cell cell0 = row.getCell(0);
        Cell cell2 = row.getCell(2);
        switch (type) {
            case DIRECT_SITE:
                siteOrgDto = getSiteByCode(siteCodes, cell0.getNumericCellValue());
                if (null == siteOrgDto) {
                    throw new DataFormatException(type.getName() + "(" + (rowIndex + 1) + "行," + (1) + "列) 没有找到对应分拣中心");
                }
                areaDest.setReceiveSiteCode(siteOrgDto.getSiteCode());
                areaDest.setReceiveSiteName(siteOrgDto.getSiteName());
                break;
            case DIRECT_DMS:
                siteOrgDto = getSiteByCode(siteCodes, cell0.getNumericCellValue());
                if (null == siteOrgDto) {
                    throw new DataFormatException(type.getName() + "(" + (rowIndex + 1) + "行," + (1) + "列) 没有找到对应分拣中心");
                }
                areaDest.setTransferSiteCode(siteOrgDto.getSiteCode());
                areaDest.setTransferSiteName(siteOrgDto.getSiteName());

                siteOrgDto = getSiteByCode(siteCodes, cell2.getNumericCellValue());
                if (null == siteOrgDto) {
                    throw new DataFormatException(type.getName() + "(" + (rowIndex + 1) + "行," + (3) + "列) 没有找到对应分拣中心");
                }
                areaDest.setReceiveSiteCode(siteOrgDto.getSiteCode());
                areaDest.setReceiveSiteName(siteOrgDto.getSiteName());
                break;
            case MULTIPLE_DMS:
                if (null != cell0) {
                    siteOrgDto = getSiteByCode(siteCodes, cell0.getNumericCellValue());
                    if (null == siteOrgDto) {
                        throw new DataFormatException(type.getName() + "(" + (rowIndex + 1) + "行," + (1) + "列) 没有找到对应分拣中心");
                    }
                    areaDest.setTransferSiteCode(siteOrgDto.getSiteCode());
                    areaDest.setTransferSiteName(siteOrgDto.getSiteName());
                }
                siteOrgDto = getSiteByCode(siteCodes, cell2.getNumericCellValue());
                if (null == siteOrgDto) {
                    throw new DataFormatException(type.getName() + "(" + (rowIndex + 1) + "行," + (3) + "列) 没有找到对应分拣中心");
                }
                areaDest.setReceiveSiteCode(siteOrgDto.getSiteCode());
                areaDest.setReceiveSiteName(siteOrgDto.getSiteName());
                break;
        }
    }

    /**
     * 根据配置的站点编码获取完整的站点信息
     *
     * @param siteOrgDtos 所有的分拣中心
     * @param siteCode    配置的站点编码
     * @return
     */
    private BaseStaffSiteOrgDto getSiteByCode(List<BaseStaffSiteOrgDto> siteOrgDtos, double siteCode) {
        for (BaseStaffSiteOrgDto dto : siteOrgDtos) {
            if (dto.getSiteCode().equals(Integer.valueOf((int) siteCode))) {
                return dto;
            }
        }
        return null;
    }

}
