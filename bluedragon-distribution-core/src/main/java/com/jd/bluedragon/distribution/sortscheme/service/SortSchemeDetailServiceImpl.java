package com.jd.bluedragon.distribution.sortscheme.service;

import com.alibaba.fastjson.TypeReference;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.request.SortSchemeDetailRequest;
import com.jd.bluedragon.distribution.api.response.SortSchemeDetailResponse;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.sortscheme.domain.SortSchemeDetail;
import com.jd.bluedragon.utils.*;
import com.jd.jsf.gd.util.StringUtils;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.DataFormatException;

/**
 * Created by yangbo7 on 2016/6/22.
 */
@Service("sortSchemeDetailService")
public class SortSchemeDetailServiceImpl implements SortSchemeDetailService {

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
        if (effectiveColumns < 5) {
            throw new DataFormatException("分拣计划明细Excel不满足最少5列的要求!!");
        }
        // 确定最大行数,同时统计重复的错误(只做第一列,物理滑槽)
        int maxRowNum = sheet0.getLastRowNum();
        int effectiveRowNum = 1;
        List<String> repeatChuteErrorList = new ArrayList<String>();
        Map<String, String> chuteCodeMap = new HashedMap<String, String>();
        for (int i = 1; i < maxRowNum; i++) {
            String chuteCode = ExportByPOIUtil.getCellValue(sheet0.getRow(i).getCell(0));
            if (StringUtils.isBlank(chuteCode)) {
                break;
            } else {
                chuteCode = StringHelper.prefixStr(chuteCode, ".");
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
            throw new DataFormatException(new StringBuilder()//
                    .append("物理滑槽重复:")//
                    .append(repeatChuteErrorList)//
                    .append("; ")//
                    .append("同一格口不可维护相同目的地:")//
                    .append(JsonHelper.toJson(repeatSiteErrorList))//
                    .append("; ")//
                    .append("目的地代码不存在:")//
                    .append(JsonHelper.toJson(notExsitErrorList))//
                    .append("; ")//
                    .append("数据为空:")//
                    .append(JsonHelper.toJson(emptyErrorList)).toString()//
            );
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
        String chuteCode = "";  // 物理滑槽滑槽
        String boxSiteCode = ""; // 格口箱号目的地代码
        String pkgLabelName = ""; // 格口箱号目的地名称
        String currChuteCode = "";// 当前使用滑槽
        String siteStrs = ""; // 站点字符串
        List<String> siteList = new ArrayList<String>();
        for (int i = 0; i < effectiveColumns; i++) {
            String cellValue = "";
            if (i == 2) {
                cellValue = ExportByPOIUtil.getCellValue(currentRow.getCell(i));
                if (StringUtils.isBlank(cellValue)) {
                    emptyErrorList.add(MessageFormat.format("第{0}行第{1}列的值{2}为空", rowIndex + 1, i + 1, cellValue));
                }
            } else if(i == 3) {
                cellValue = StringHelper.prefixStr(ExportByPOIUtil.getCellValue(currentRow.getCell(i)), ".");
                if (StringUtils.isBlank(cellValue)) {
                    emptyErrorList.add(MessageFormat.format("第{0}行第{1}列的值{2}为空", rowIndex + 1, i + 1, cellValue));
                }
            } else {
                cellValue = StringHelper.prefixStr(ExportByPOIUtil.getCellValue(currentRow.getCell(i)), ".");
                if (StringUtils.isBlank(cellValue) || !NumberHelper.isNumberUpZero(cellValue)) {
                    emptyErrorList.add(MessageFormat.format("第{0}行第{1}列的值{2}为空", rowIndex + 1, i + 1, cellValue));
                }
            }

            // 校验目的地代码重复
            if (i == 0) {
                chuteCode = cellValue;
            } else if (i == 1) {
                boxSiteCode = cellValue;
                validateSite(siteMap, cellValue, notExsitErrorList, rowIndex, i);
            } else if (i == 2) {
                pkgLabelName = cellValue;
            } else if (i == 3) {
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
            sortSchemeDetailList.add(new SortSchemeDetail(chuteCode, currChuteCode, boxSiteCode, pkgLabelName, siteStrs));
        }

    }

    private void validateSite(Map<String, BaseStaffSiteOrgDto> siteMap, String cellValue, List<String> notExsitErrorList, int rowIndex, int columnIndex) {
        if (StringUtils.isNotBlank(cellValue) && NumberHelper.isNumberUpZero(cellValue)) {
            // 不包含则远程获取,然后校验是否存在
            if (!siteMap.containsKey(cellValue)) {
                BaseStaffSiteOrgDto site = siteService.getSite(Integer.parseInt(cellValue));
                if (site == null) {
                    notExsitErrorList.add(MessageFormat.format("第{0}行第{1}列的站点{2}不存在", rowIndex + 1, columnIndex + 1, cellValue));
                } else {
                    siteMap.put(cellValue, site);
                }
            }
        }
    }


    public static void main(String[] args) throws Exception {
        File file = new File("E:\\dms_work_file\\项目_20160708_分拣计划2.0\\正式分拣计划批量导入.xls");
        String fileName = file.getName();
        Sheet sheet0 = null;
        if (fileName.toLowerCase().endsWith("xlsx")) {
            sheet0 = new XSSFWorkbook(new FileInputStream(file)).getSheetAt(0);
        } else if (fileName.toLowerCase().endsWith("xls")) {
            sheet0 = new HSSFWorkbook(new FileInputStream(file)).getSheetAt(0);
        }
//        System.out.println(sheet0.getLastRowNum());
//        for (int i = 0, len = sheet0.getLastRowNum(); i < len; i++) {
//            Row row = sheet0.getRow(i);
//            int cells = row.getLastCellNum();
//            for (int j = 0; j < cells; j++) {
//                System.out.print(row.getCell(j) + " ");
//            }
//            System.out.println("\n");
//        }
        // 计算最大列数, 确定横向遍历的最大索引值
        Row firstRow = sheet0.getRow(0);
        int maxColumns = firstRow.getLastCellNum();
        int effectiveColumns = 0;
        for (int i = 0; i < maxColumns; i++) {
            if (StringUtils.isBlank(firstRow.getCell(i).getStringCellValue())) {
                break;
            } else {
                System.out.print(firstRow.getCell(i).getStringCellValue() + " ");
                effectiveColumns++;
            }
        }
        System.out.println("\n");
        System.out.println("effectiveColumns " + effectiveColumns);

        // 确定最大行数,同时统计重复的错误(只做第一列,物理滑槽)
        int maxRowNum = sheet0.getLastRowNum();
        int effectiveRowNum = 1;
        List<String> repeatChuteErrorList = new ArrayList<String>();
        Map<String, String> chuteCodeMap = new HashedMap<String, String>();
        for (int i = 1; i < maxRowNum; i++) {
            String chuteCode = ExportByPOIUtil.getCellValue(sheet0.getRow(i).getCell(0));
            if (StringUtils.isBlank(chuteCode)) {
                break;
            } else {
                chuteCode = StringHelper.prefixStr(chuteCode, ".");
                if (StringUtils.isNotBlank(chuteCodeMap.put(chuteCode, chuteCode))) {
                    repeatChuteErrorList.add(MessageFormat.format("第{0}行的物理格口{1}重复", i, chuteCode));
                }
                effectiveRowNum++;
            }
        }
        System.out.println("effectiveRowNum : " + effectiveRowNum);
        System.out.println("物理滑槽重复" + repeatChuteErrorList);
        List<SortSchemeDetail> sortSchemeDetailList = new ArrayList<SortSchemeDetail>();
        List<String> repeatSiteErrorList = new ArrayList<String>();
        List<String> emptyErrorList = new ArrayList<String>(); //包含某条数据,目的地代码均为空
        for (int j = 1; j < effectiveRowNum; j++) {
            //validateAndParseSingleCell(j, effectiveColumns, sortSchemeDetailList, repeatSiteErrorList, emptyErrorList, repeatChuteErrorList, sheet0.getRow(j), siteMap);
        }
        if (repeatChuteErrorList.size() == 0 || repeatSiteErrorList.size() == 0 || emptyErrorList.size() == 0) {
            System.out.println(new StringBuilder()//
                    .append("物理滑槽重复:")//
                    .append(repeatChuteErrorList)//
                    .append("; ")//
                    .append("同一格口不可维护相同目的地:")//
                    .append(JsonHelper.toJson(repeatSiteErrorList))//
                    .append("; ")//
                    .append("数据为空:")//
                    .append(JsonHelper.toJson(emptyErrorList)).toString()//
            );
        }
        System.out.println("sortSchemeDetailList" + sortSchemeDetailList);

    }


}






































