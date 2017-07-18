package com.jd.bluedragon.distribution.sortscheme.service;

import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.SortSchemeDetailRequest;
import com.jd.bluedragon.distribution.api.response.SortSchemeDetailResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.sortscheme.domain.SortSchemeDetail;
import com.jd.bluedragon.utils.*;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.*;
import java.util.zip.DataFormatException;

/**
 * Created by yangbo7 on 2016/6/22.
 */
@Service("sortSchemeDetailService")
public class SortSchemeDetailServiceImpl implements SortSchemeDetailService {

    private static final String EXP = "EXP";

    @Autowired
    private SiteService siteService;

    @Override
    public SortSchemeDetailResponse<Pager<List<SortSchemeDetail>>> pageQuerySortSchemeDetail(SortSchemeDetailRequest request, String url) {
        return RestHelper.jsonPostForEntity(url, request, //
                new TypeReference<SortSchemeDetailResponse<Pager<List<SortSchemeDetail>>>>() {
                });
    }

    @Override
    public SortSchemeDetailResponse<List<String>> findMixSiteBySchemeId2(SortSchemeDetailRequest request, String url) {
        return RestHelper.jsonPostForEntity(url, request, //
                new TypeReference<SortSchemeDetailResponse<List<String>>>() {
                });
    }

    @Override
    public SortSchemeDetailResponse<List<String>> findChuteCodeBySchemeId2(SortSchemeDetailRequest request, String url) {
        return RestHelper.jsonPostForEntity(url, request, //
                new TypeReference<SortSchemeDetailResponse<List<String>>>() {
                });
    }

    @Override
    public SortSchemeDetailResponse<List<SortSchemeDetail>> findBySchemeId2(SortSchemeDetailRequest request, String url) {
        return RestHelper.jsonPostForEntity(url, request, //
                new TypeReference<SortSchemeDetailResponse<List<SortSchemeDetail>>>() {
                });
    }

    @Override
    public HSSFWorkbook createWorkbook(List<SortSchemeDetail> sortSchemeDetailList) {
        Multimap<String, SortSchemeDetail> sortSchemeDetailTreeMap = TreeMultimap.create();
        if (sortSchemeDetailList != null) {
            for (SortSchemeDetail sortSchemeDetail : sortSchemeDetailList) {
                sortSchemeDetailTreeMap.put(sortSchemeDetail.getChuteCode1(), sortSchemeDetail);
            }
        }
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet("Sheet1");
        // 组装第一行
        HSSFRow firstRow = sheet.createRow(0);
        ExportByPOIUtil.createHSSFCell(firstRow, 0, "物理滑槽");
        ExportByPOIUtil.createHSSFCell(firstRow, 1, "格口箱号目的地代码");
        ExportByPOIUtil.createHSSFCell(firstRow, 2, "格口箱号目的地名称");
        ExportByPOIUtil.createHSSFCell(firstRow, 3, "发货目的地代码");
        ExportByPOIUtil.createHSSFCell(firstRow, 4, "发货目的地名称");
        ExportByPOIUtil.createHSSFCell(firstRow, 5, "当前使用滑槽");

        int rowIndex = 1; // 从第一行开始,每遍历一个Entry,则加1
        int maxSiteNum = 0;
        Set<Map.Entry<String, Collection<SortSchemeDetail>>> set = sortSchemeDetailTreeMap.asMap().entrySet();
        for (Map.Entry entry : set) {
            HSSFRow currentIndexRow = sheet.createRow(rowIndex);
            Collection<SortSchemeDetail> sortSchemeDetails = (Collection<SortSchemeDetail>) entry.getValue();
            // 更新最大的站点数量值
            int size = sortSchemeDetails.size();
            maxSiteNum = maxSiteNum > size ? maxSiteNum : size;
            // 将多条数据组装成Excel中的一条数据
            int siteIndex = 5;
            boolean isFirst = true;
            for (SortSchemeDetail sortSchemeDetail : sortSchemeDetails) {
                if (isFirst) {
                    ExportByPOIUtil.createHSSFCell(currentIndexRow, 0, sortSchemeDetail.getChuteCode1());
                    ExportByPOIUtil.createHSSFCell(currentIndexRow, 1, sortSchemeDetail.getBoxSiteCode());
                    ExportByPOIUtil.createHSSFCell(currentIndexRow, 2, sortSchemeDetail.getPkgLabelName());
                    ExportByPOIUtil.createHSSFCell(currentIndexRow, 3, String.valueOf(sortSchemeDetail.getSendSiteCode()));
                    ExportByPOIUtil.createHSSFCell(currentIndexRow, 4, sortSchemeDetail.getSendSiteName());
                    ExportByPOIUtil.createHSSFCell(currentIndexRow, 5, sortSchemeDetail.getCurrChuteCode());
                    ExportByPOIUtil.createHSSFCell(currentIndexRow, 6, sortSchemeDetail.getSiteCode());
                    isFirst = false;
                } else {
                    ExportByPOIUtil.createHSSFCell(currentIndexRow, siteIndex, sortSchemeDetail.getSiteCode());
                    siteIndex++;
                }
            }
            rowIndex++;
        }

        // 补全第一行的站点标题
        for (int i = 0; i < maxSiteNum; i++) {
            ExportByPOIUtil.createHSSFCell(firstRow, i + 6, "目的地代码");
        }
        return wb;
    }

    @Override
    public List<SortSchemeDetail> parseSortSchemeDetail2(Sheet sheet0) throws Exception {
        // 计算最大列数, 确定横向遍历的最大索引值
        Row firstRow = sheet0.getRow(0);
        int maxColumnNum = firstRow.getLastCellNum();
        int effectiveColumns = 0;
        for (int i = 0; i < maxColumnNum; i++) {
            if (StringUtils.isBlank(ExportByPOIUtil.getCellValue(firstRow.getCell(i)))) {
                break;
            } else {
                effectiveColumns++;
            }
        }
        if (effectiveColumns < 7) {
            throw new DataFormatException("分拣计划明细Excel不满足最少7列的要求!!");
        }
        // 确定最大行数,同时统计重复的错误(只做第一列,物理滑槽)
        int maxRowNum = sheet0.getLastRowNum();
        int effectiveRowNum = 1;
        List<String> repeatChuteErrorList = new ArrayList<String>();
        Map<String, String> chuteCodeMap = new HashedMap<String, String>();
        for (int i = 1; i <= maxRowNum; i++) {
            String chuteCode = ExportByPOIUtil.getCellValue(sheet0.getRow(i).getCell(0));
            if (StringUtils.isBlank(chuteCode)) {
                break;
            } else {
                //兼容.
//                chuteCode = StringHelper.prefixStr(chuteCode, ".");
                if (StringUtils.isNotBlank(chuteCodeMap.put(chuteCode, chuteCode))) {
                    repeatChuteErrorList.add(MessageFormat.format("第{0}行的物理格口{1}重复", i + 1, chuteCode));
                }
                effectiveRowNum++;
            }
        }
        List<SortSchemeDetail> sortSchemeDetailList = new ArrayList<SortSchemeDetail>();
        List<String> repeatSiteErrorList = new ArrayList<String>();
        List<String> emptyErrorList = new ArrayList<String>(); //包含某条数据,目的地代码均为空
        List<String> notExsitErrorList = new ArrayList<String>();  // 校验站点不存在
        Map<String, BaseStaffSiteOrgDto> siteMap = new HashedMap<String, BaseStaffSiteOrgDto>();
        for (int j = 1; j < effectiveRowNum; j++) {
            validateAndParseSingleCell(j, effectiveColumns, sortSchemeDetailList, repeatSiteErrorList, emptyErrorList, repeatChuteErrorList, notExsitErrorList, sheet0.getRow(j), siteMap);
        }
        if (repeatChuteErrorList.size() > 0 || repeatSiteErrorList.size() > 0 || emptyErrorList.size() > 0 || notExsitErrorList.size() > 0) {
            StringBuilder sb = new StringBuilder();
            if (repeatChuteErrorList.size() > 0) {
                sb.append("物理滑槽重复:").append(repeatChuteErrorList).append("; ");
            }
            if (repeatSiteErrorList.size() > 0) {
                sb.append("同一格口不可维护相同目的地:").append(JsonHelper.toJson(repeatSiteErrorList)).append("; ");
            }
            if (emptyErrorList.size() > 0) {
                sb.append("目的地代码不存在:").append(JsonHelper.toJson(emptyErrorList)).append("; ");
            }
            if (notExsitErrorList.size() > 0) {
                sb.append("数据为空:").append(JsonHelper.toJson(notExsitErrorList));
            }
            throw new DataFormatException(sb.toString());
        }
        if (sortSchemeDetailList.size() < 1) {
            throw new DataFormatException("Excel数据为空,请检查!!");
        }
        return sortSchemeDetailList;
    }

    private void validateAndParseSingleCell(int rowIndex, // 当前行号
                                            int effectiveColumns, // 最大列数
                                            List<SortSchemeDetail> sortSchemeDetailList, //
                                            List<String> repeatSiteErrorList, //
                                            List<String> emptyErrorList, //
                                            List<String> repeatChuteErrorList, //
                                            List<String> notExsitErrorList, //
                                            Row currentRow,// 当前行对象
                                            Map<String, BaseStaffSiteOrgDto> siteMap//
    ) {
        String chuteCode1 = "";  // 物理滑槽滑槽
        String boxSiteCode = ""; // 格口箱号目的地代码
        String pkgLabelName = ""; // 格口箱号目的地名称
        String currChuteCode = "";// 当前使用滑槽
        Integer sendSiteCode = null; // 发货目的地代码
        String sendSiteName = ""; // 发货目的地名称
        String siteStrs = ""; // 站点字符串
        List<String> siteList = new ArrayList<String>();


        //------------如果一行里面有一个目的地代码不为空,就不要校验其他站点为空-------------
        boolean needValiSiteEmpty = true;
        for (int k = 6; k < effectiveColumns; k++) {
            if (StringUtils.isNotBlank(StringHelper.prefixStr(ExportByPOIUtil.getCellValue(currentRow.getCell(k)), "."))) {
                needValiSiteEmpty = false;
                break;
            }
        }
        //------------如果一行里面有一个目的地代码不为空,就不要校验其他站点为空-------------

        for (int i = 0; i < effectiveColumns; i++) {
            String cellValue = "";
            if (i == 2) {
                cellValue = ExportByPOIUtil.getCellValue(currentRow.getCell(i));
                if (StringUtils.isBlank(cellValue)) {
                    emptyErrorList.add(MessageFormat.format("第{0}行第{1}列的值{2}为空", rowIndex + 1, i + 1, cellValue));
                }
            } else if (i == 5) {
//                cellValue = StringHelper.prefixStr(ExportByPOIUtil.getCellValue(currentRow.getCell(i)), ".");
                /**
                 * 格口号获取为string类型
                 */
                cellValue = ExportByPOIUtil.getStringCellValue(currentRow.getCell(i));
                if (StringUtils.isBlank(cellValue)) {
                    emptyErrorList.add(MessageFormat.format("第{0}行第{1}列的值{2}为空", rowIndex + 1, i + 1, cellValue));
                }
            } else if (i == 0) {
//                cellValue = StringHelper.prefixStr(ExportByPOIUtil.getCellValue(currentRow.getCell(i)), ".");
                /**
                 * 格口号获取为string类型
                 */
                cellValue = ExportByPOIUtil.getStringCellValue(currentRow.getCell(i));
                //这里去掉对物理滑槽非数值的判断 可以导入带字母 = .的
//                || !NumberHelper.isNumberUpZero(cellValue)
                if (StringUtils.isBlank(cellValue)) {
                    emptyErrorList.add(MessageFormat.format("第{0}行第{1}列的值{2}为空", rowIndex + 1, i + 1, cellValue));
                }
            } else if (i == 1) {
                cellValue = StringHelper.prefixStr(ExportByPOIUtil.getCellValue(currentRow.getCell(i)), ".");
                if (StringUtils.isBlank(cellValue)) {
                    emptyErrorList.add(MessageFormat.format("第{0}行第{1}列的值{2}为空", rowIndex + 1, i + 1, cellValue));
                } else {
                    if (!cellValue.startsWith(EXP) && !NumberHelper.isNumberUpZero(cellValue)) {
                        emptyErrorList.add(MessageFormat.format("第{0}行第{1}列的值{2}不符合规则", rowIndex + 1, i + 1, cellValue));
                    }
                }
            } else if (i == 3) {
                cellValue = StringHelper.prefixStr(ExportByPOIUtil.getCellValue(currentRow.getCell(i)), ".");
                if (!StringUtils.isBlank(cellValue)) {
                    if (!NumberHelper.isNumberUpZero(cellValue)) {
                        emptyErrorList.add(MessageFormat.format("第{0}行第{1}列的值{2}不符合规则", rowIndex + 1, i + 1, cellValue));
                    }
                }
            } else {
                cellValue = StringHelper.prefixStr(ExportByPOIUtil.getCellValue(currentRow.getCell(i)), ".");
                if (needValiSiteEmpty && !cellValue.startsWith(EXP)) {
                    if (StringUtils.isBlank(cellValue) || !NumberHelper.isNumberUpZero(cellValue)) {
                        emptyErrorList.add(MessageFormat.format("第{0}行第{1}列的值{2}为空", rowIndex + 1, i + 1, cellValue));
                    }
                }
            }

            // 校验目的地代码重复
            if (i == 0) {
                chuteCode1 = cellValue;
            } else if (i == 1) {
                boxSiteCode = cellValue;
                validateSite(siteMap, cellValue, notExsitErrorList, rowIndex, i);
            } else if (i == 2) {
                pkgLabelName = cellValue;
            } else if (i == 3) {
                if (StringUtils.isNotEmpty(cellValue)){
                    sendSiteCode = Integer.parseInt(cellValue);
                    validateSite(siteMap, cellValue, notExsitErrorList, rowIndex, i);
                    BaseStaffSiteOrgDto site = siteMap.get(cellValue);
                    if (site != null) {
                        sendSiteName = site.getSiteName();
                    }
                }
            } else if (i == 4) {
                if (StringUtils.isEmpty(sendSiteName.trim())) {
                    sendSiteName = cellValue;
                }
            } else if (i == 5) {
                currChuteCode = cellValue;
            } else {
                if (StringUtils.isNotBlank(cellValue)) {
                    if (siteList.contains(cellValue)) {
                        repeatSiteErrorList.add(MessageFormat.format("第{0}行第{1}列的目的地代码的值{2}重复", rowIndex + 1, i + 1, cellValue));
                    } else {
                        validateSite(siteMap, cellValue, notExsitErrorList, rowIndex, i);
                        BaseStaffSiteOrgDto site = siteMap.get(cellValue);
                        if (site != null) {
                            siteStrs += cellValue + ":" + site.getSiteType() + ",";
                            siteList.add(cellValue);
                        }
                    }
                }
            }
        }
        // 均没有错误,才能创建domain对象
        if (repeatChuteErrorList.size() == 0 && repeatSiteErrorList.size() == 0 && emptyErrorList.size() == 0 && notExsitErrorList.size() == 0) {
            sortSchemeDetailList.add(new SortSchemeDetail(chuteCode1, currChuteCode, boxSiteCode, pkgLabelName, sendSiteCode, sendSiteName, siteStrs));
        }

    }

    private void validateSite(Map<String, BaseStaffSiteOrgDto> siteMap, String cellValue, List<String> notExsitErrorList, int rowIndex, int columnIndex) {
        // 不包含则远程获取,然后校验是否存在
        if (StringUtils.isNotBlank(cellValue) && !siteMap.containsKey(cellValue)) {
            BaseStaffSiteOrgDto site = null;
            if (cellValue.startsWith(EXP)) {
                BaseStaffSiteOrgDto virtualSite = new BaseStaffSiteOrgDto();
                virtualSite.setSiteType(8);
                site = virtualSite;
            } else {
                try {
                    site = siteService.getSite(Integer.parseInt(cellValue));
                } catch (Exception e) {

                }
            }
            if (site == null) {
                notExsitErrorList.add(MessageFormat.format("第{0}行第{1}列的站点{2}不存在", rowIndex + 1, columnIndex + 1, cellValue));
            } else {
                siteMap.put(cellValue, site);
            }
        }
    }


    public static void main(String[] args) throws Exception {

    }


}






































