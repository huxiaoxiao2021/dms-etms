package com.jd.bluedragon.distribution.express.service.impl;


import com.jd.bluedragon.common.domain.Pack;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.express.domain.ExpressBoxDetail;
import com.jd.bluedragon.distribution.express.domain.ExpressBoxDetailsResponse;
import com.jd.bluedragon.distribution.express.domain.ExpressPackageDetailsResponse;
import com.jd.bluedragon.distribution.express.enums.ExpressStatusTypeEnum;
import com.jd.bluedragon.distribution.express.service.ExpressCollectionService;
import com.jd.bluedragon.distribution.inspection.dao.InspectionDao;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sorting.dao.SortingDao;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author zhangleqi
 * @comments 快运到齐查询
 * @date 2017/11/14
 */
@Service
public class ExpressCollectionServiceImpl implements ExpressCollectionService {



    @Resource
    private WaybillCommonService waybillCommonService;

    @Resource
    private InspectionDao inspectionDao;

    @Resource
    private SortingDao sortingDao;
    @Resource
    private SendDatailDao sendDatailDao;

    /**
     * 展示未扫描包裹个数
     */
    public static final int SHOW_UNSCAN_PACKAGE_NUM = 100;

    /**
     * 根据运单号和扫描状态获取快运到齐查询信息
     *
     * @param waybillCode     运单号
     * @param statusQueryCode 查询状态
     * @return 查询明细
     */
    @Override
    public ExpressPackageDetailsResponse findExpressPackageDetails(ExpressPackageDetailsResponse expressPackageDetailsResponse,Integer createSiteCode, String waybillCode, String statusQueryCode) {

        //获取运单信息
        Waybill waybill = waybillCommonService.findWaybillAndPack(waybillCode);

        //判断运单信息是否为空，为空则提示运单号不存在
        if (null == waybill) {
            expressPackageDetailsResponse.setCode(JdResponse.CODE_NOT_EXIST_WAYBILL);
            expressPackageDetailsResponse.setMessage(JdResponse.MESSAGE_NOT_EXIST_WAYBILL + ",运单号：" + waybillCode);
            return expressPackageDetailsResponse;
        }

        //获取全部包裹
        List<Pack> packList = waybill.getPackList();

        //获取扫描箱以及包裹明细数据
        Map<String, List<String>> boxCodesMap = new HashMap<String, List<String>>();
        if (null != packList && packList.size() > 0) {
            //获取已扫描箱号以及包裹号
            findScanPackagesByWaybillAndStatus( createSiteCode,waybillCode, statusQueryCode, boxCodesMap);
        }


        return getExpressPackageDetailsResponse(expressPackageDetailsResponse, waybill, packList, boxCodesMap);
    }

    /**
     * 根据运单号和扫描状态获取快运到齐箱号明细信息
     *
     * @param waybillCode     运单号
     * @param statusQueryCode 查询状态
     * @return 查询明细
     */
    @Override
    public ExpressBoxDetailsResponse findExpressBoxDetails(ExpressBoxDetailsResponse expressBoxDetailsResponse, Integer createSiteCode ,String waybillCode, String statusQueryCode) {
        //获取运单信息
        Waybill waybill = waybillCommonService.findByWaybillCode(waybillCode);

        //判断运单信息是否为空，为空则提示运单号不存在
        if (null == waybill) {
            expressBoxDetailsResponse.setCode(JdResponse.CODE_NOT_EXIST_WAYBILL);
            expressBoxDetailsResponse.setMessage(JdResponse.MESSAGE_NOT_EXIST_WAYBILL + ",运单号：" + waybillCode);
            return expressBoxDetailsResponse;
        }

        //获取全部包裹
        Integer quantity = waybill.getQuantity();

        Map<String, List<String>> boxCodesMap = new HashMap<String, List<String>>();

        if (null != quantity && quantity > 0) {
            //获取已扫描箱号以及包裹号
            findScanPackagesByWaybillAndStatus(createSiteCode,waybillCode, statusQueryCode, boxCodesMap);
        }
        return getExpressBoxDetailsResponse(expressBoxDetailsResponse, boxCodesMap);
    }

    /**
     * 根据运单和状态获取包裹数据
     *
     * @param waybillCode     运单号
     * @param statusQueryCode 状态码
     */
    Map<String, List<String>> findScanPackagesByWaybillAndStatus(Integer createSiteCode ,String waybillCode, String statusQueryCode, Map<String, List<String>> boxCodesMap) {

            if (ExpressStatusTypeEnum.HAS_INSPECTION.getCode().equals(statusQueryCode)) {
                findScanPackagesByInspection(createSiteCode,waybillCode, boxCodesMap);
            } else if (ExpressStatusTypeEnum.HAS_SORTING.getCode().equals(statusQueryCode)) {
                findScanPackagesBySorting(createSiteCode,waybillCode, boxCodesMap);
            } else if (ExpressStatusTypeEnum.HAS_SEND.getCode().equals(statusQueryCode)) {
                findScanPackagesBySendD(createSiteCode,waybillCode, boxCodesMap);
            }else if(ExpressStatusTypeEnum.HAS_INSPECTION_OR_HAS_SORTING.getCode().equals(statusQueryCode)||ExpressStatusTypeEnum.HAS_SORTING_OR_HAS_INSPECTION.getCode().equals(statusQueryCode)){
                //先获取分拣状态，如果没有分拣记录，则在再查询验货记录
                findScanPackagesBySorting(createSiteCode,waybillCode, boxCodesMap);
                if(boxCodesMap.size()==0){
                    findScanPackagesByInspection(createSiteCode,waybillCode, boxCodesMap);
                }
        }
        return boxCodesMap;
    }


    /**
     * 获取验货已扫描信息
     *
     * @param waybillCode 运单号
     */
    void findScanPackagesByInspection(Integer createSiteCode,String waybillCode, Map<String, List<String>> boxCodesMap) {
        Inspection inspectionQuery = new Inspection();
        inspectionQuery.setWaybillCode(waybillCode);
        inspectionQuery.setCreateSiteCode(createSiteCode);
        List<Inspection> inspectionList = inspectionDao.findPackageBoxCodesByWaybillCode(inspectionQuery);
        for (Inspection inspection : inspectionList) {
            getBoxCodesMap(boxCodesMap, inspection.getBoxCode(), inspection.getPackageBarcode());
        }
    }


    /**
     * 获取分拣已扫描信息
     *
     * @param waybillCode 运单号
     */
    void findScanPackagesBySorting(Integer createSiteCode,String waybillCode, Map<String, List<String>> boxCodesMap) {
        Sorting sortingQuery = new Sorting();
        sortingQuery.setCreateSiteCode(createSiteCode);
        sortingQuery.setWaybillCode(waybillCode);
        List<Sorting> sortingList = sortingDao.findPackageCodesByWaybillCode(sortingQuery);
        for (Sorting sorting : sortingList) {
            getBoxCodesMap(boxCodesMap, sorting.getBoxCode(), sorting.getPackageCode());
        }
    }

    /**
     * 获取发货已扫描信息
     *
     * @param waybillCode 运单号
     */
    void findScanPackagesBySendD(Integer createSiteCode,String waybillCode, Map<String, List<String>> boxCodesMap) {
        SendDetail sendDetailQuery = new SendDetail();
        sendDetailQuery.setCreateSiteCode(createSiteCode);
        sendDetailQuery.setWaybillCode(waybillCode);
        List<SendDetail> sendDetailList = sendDatailDao.findPackageBoxCodesByWaybillCode(sendDetailQuery);
        for (SendDetail sendDetail : sendDetailList) {
            getBoxCodesMap(boxCodesMap, sendDetail.getBoxCode(), sendDetail.getPackageBarcode());
        }
    }

    /**
     * 构造箱号map 如Map< 箱号,List<包裹号>>
     *
     * @param boxCode     箱号
     * @param packageCode 包裹号
     * @return 箱号map
     */
    Map<String, List<String>> getBoxCodesMap(Map<String, List<String>> boxCodesMap, String boxCode, String packageCode) {
        if (null == boxCodesMap) {
            boxCodesMap = new HashMap<String, List<String>>();
        }

        if (boxCodesMap.containsKey(boxCode)) {
            if (!boxCodesMap.get(boxCode).contains(packageCode)) {
                boxCodesMap.get(boxCode).add(packageCode);
            }
        } else {
            List packageCodeList = new ArrayList();
            packageCodeList.add(packageCode);
            boxCodesMap.put(boxCode, packageCodeList);
        }
        return boxCodesMap;
    }


    /**
     * 汇总快运查询包裹明细
     *
     * @return
     */
    ExpressPackageDetailsResponse getExpressPackageDetailsResponse(ExpressPackageDetailsResponse expressPackageDetailsResponse, Waybill waybill, List<Pack> packList, Map<String, List<String>> boxCodesMap) {
        if (null == expressPackageDetailsResponse) {
            expressPackageDetailsResponse = new ExpressPackageDetailsResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        }
        //设置箱总数
        expressPackageDetailsResponse.setBoxSize(boxCodesMap.size());

        //设置站点名称
        expressPackageDetailsResponse.setPreSortingSite(waybill.getSiteName());

        //设置包裹总数
        if (null == packList || packList.size() == 0) {
            expressPackageDetailsResponse.setPackageSize(0);
        } else {
            expressPackageDetailsResponse.setPackageSize(packList.size());
        }

        //获取已扫描包裹号
        List<String> scanPackageCodes = new ArrayList<String>();
        for (List<String> packageCodesTemp : boxCodesMap.values()) {
            scanPackageCodes.addAll(packageCodesTemp);
        }

        //获取未扫描包裹号
        List<String> unScanPackageCodes = getUnScanPackages(packList,  scanPackageCodes);

        //设置已扫描数量
        expressPackageDetailsResponse.setHasScanPackageSize(scanPackageCodes.size());

        //设置未扫描数量
        expressPackageDetailsResponse.setUnScanPackageSize(unScanPackageCodes.size());

        //设置显示的扫描包裹明细，最多100条
        List<String> showUnScanPackageCodes;

        //pda只展示100条包裹信息
        if(SHOW_UNSCAN_PACKAGE_NUM < unScanPackageCodes.size() ){
            showUnScanPackageCodes = new ArrayList<String>(SHOW_UNSCAN_PACKAGE_NUM);

            for(int i=0;i<SHOW_UNSCAN_PACKAGE_NUM;i++){
                showUnScanPackageCodes.add(unScanPackageCodes.get(i));
            }

         }else{
            showUnScanPackageCodes = unScanPackageCodes;
        }
        //设置未扫描包裹明细
        expressPackageDetailsResponse.setUnScanPackageCodes(showUnScanPackageCodes);




        return expressPackageDetailsResponse;
    }

    private List<String> getUnScanPackages(List<Pack> packList, List<String> scanPackageCodes) {
        List<String> unScanPackages = new ArrayList<String>();
        if (null != packList && !packList.isEmpty()) {
            for (Pack pack : packList) {
                if (!scanPackageCodes.contains(pack.getPackCode())) {
                    unScanPackages.add(pack.getPackCode());
                }
            }
        }
        return unScanPackages;
    }

    /**
     * 汇总快运查询箱明细
     *
     * @return
     */
    ExpressBoxDetailsResponse getExpressBoxDetailsResponse(ExpressBoxDetailsResponse expressBoxDetailsResponse, Map<String, List<String>> boxCodesMap) {
        if (null == expressBoxDetailsResponse) {
            expressBoxDetailsResponse = new ExpressBoxDetailsResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        }
        List<ExpressBoxDetail> expressBoxDetailList = new ArrayList<ExpressBoxDetail>();
        for (String boxCode : boxCodesMap.keySet()) {
            expressBoxDetailList.add(new ExpressBoxDetail(boxCode, boxCodesMap.get(boxCode).size()));
        }
        expressBoxDetailsResponse.setBoxSize(expressBoxDetailList.size());
        expressBoxDetailsResponse.setBoxDetails(expressBoxDetailList);
        return expressBoxDetailsResponse;
    }
}
