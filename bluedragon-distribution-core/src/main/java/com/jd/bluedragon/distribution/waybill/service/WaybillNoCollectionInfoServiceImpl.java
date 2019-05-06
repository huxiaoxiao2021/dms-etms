package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.board.service.BoardCombinationService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.waybill.domain.*;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.transboard.api.dto.Response;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class WaybillNoCollectionInfoServiceImpl implements WaybillNoCollectionInfoService {

    @Value("${no.collection.package.max.count:50}")
    private int noCollectionPackageMaxCount;

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private SendDatailDao sendDatailDao;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private BoardCombinationService boardCombinationService;


    /*
    *
    * 根据批次号或者箱号查询包裹不齐的信息
    *
    * */
    @Override
    @JProfiler(jKey = "DMSWEB.WaybillNoCollectionInfoServiceImpl.getWaybillNoCollectionInfo", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public WaybillNoCollectionResult getWaybillNoCollectionInfo(WaybillNoCollectionCondition waybillNoCollectionCondition) {

        WaybillNoCollectionResult waybillNoCollectionResult = new WaybillNoCollectionResult();

        //根据查询条件统计不齐的订单
        List<WaybillNoCollectionInfo> waybillNoCollectionInfoList = sendDatailDao.getWaybillNoCollectionInfo(waybillNoCollectionCondition);
        //如果没有查询到不齐的运单，直接返回
        if (waybillNoCollectionInfoList == null || waybillNoCollectionInfoList.size() == 0) {
            waybillNoCollectionResult.setTotal(0);
            waybillNoCollectionResult.setPackageCodeList(new ArrayList<String>());
            return waybillNoCollectionResult;
        }

        waybillNoCollectionResult = this.getNoCollectionPackageResult(waybillNoCollectionInfoList, waybillNoCollectionCondition);

        return waybillNoCollectionResult;
    }

    /*
     * 根据板号查询包裹不齐的信息
     * 基本逻辑为：
     * 1.调用TC接口获取板号下所有箱号或包裹号信息
     * 2.如果是包裹号存入扫描的列表
     * 3.如果是箱号，去计算出箱号下不齐的运单，再根据运单取出不齐运单下已经扫过的包裹号，存入扫描的列表
     * 4.通过算法扫描出不齐的包裹，算法不适用于上海亚一带N带S的包裹，需要剔除
     * 5.对上海亚一特殊的包裹好进行单独处理，处理逻辑同批次号和箱号查询不齐的算法逻辑相同
     * 6.最后汇总结果返回
     *
     * */
    @Override
    @JProfiler(jKey = "DMSWEB.WaybillNoCollectionInfoServiceImpl.getWaybillNoCollectionInfoByBoardCode", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public WaybillNoCollectionResult getWaybillNoCollectionInfoByBoardCode(WaybillNoCollectionCondition waybillNoCollectionCondition) throws WaybillNoCollectionException {

        WaybillNoCollectionResult waybillNoCollectionResult = new WaybillNoCollectionResult();

        //获取板号
        String boardCode = waybillNoCollectionCondition.getBoardCode();
        //从TC取出板号下所有包裹和箱号的关系
        Response<List<String>> tcResponse = null;
        try {
            tcResponse = boardCombinationService.getBoxesByBoardCode(boardCode);
        } catch (Exception e) {
            logger.error("通过TC的getBoxesByBoardCode接口获取板号【" + boardCode +"】的信息失败！", e);
        }
        List<String> scannedPackageMoreThanOneList = new ArrayList<>();

        if (tcResponse != null && JdResponse.CODE_SUCCESS.equals(tcResponse.getCode()) && tcResponse.getData() != null) {
            for (String barCode : tcResponse.getData()) {
                if (WaybillUtil.isPackageCode(barCode)) {
                    String waybillCode = WaybillUtil.getWaybillCode(barCode);
                    //根据查询范围确定是否计算C网以外的运单
                    if (queryAllWaybillType(waybillNoCollectionCondition) && isBWaybill(waybillCode)) {
                        continue;
                    }

                    int packageNum = WaybillUtil.getPackNumByPackCode(barCode);
                    //剔除一单一件的包裹
                    if (packageNum > 1) {
                        scannedPackageMoreThanOneList.add(barCode);
                    }
                }
                else if (BusinessHelper.isBoxcode(barCode)) {
                    waybillNoCollectionCondition.setBoxCode(barCode);

                    //根据查询条件统计不齐的订单
                    List<WaybillNoCollectionInfo> waybillNoCollectionInfoList = sendDatailDao.getWaybillNoCollectionInfo(waybillNoCollectionCondition);

                    List<String> waybillCodeListTemp = new ArrayList<>();
                    //如果没有查询到不齐的运单，直接返回
                    if (waybillNoCollectionInfoList != null && waybillNoCollectionInfoList.size() > 0) {
                        int size = waybillNoCollectionInfoList.size();
                        for (int i = 0; i < size; i++) {
                            WaybillNoCollectionInfo waybillNoCollectionInfo = waybillNoCollectionInfoList.get(i);
                            String waybillCode = waybillNoCollectionInfo.getWaybillCode();
                            if (! WaybillUtil.isWaybillCode(waybillCode)) {
                                continue;
                            }

                            //根据查询范围确定是否计算C网以外的运单
                            if (queryAllWaybillType(waybillNoCollectionCondition) && isBWaybill(waybillCode)) {
                                continue;
                            }
                            waybillCodeListTemp.add(waybillCode);

                            //每次计算十个不齐的运单，遍历到最后，把剩下不够十个的进行计算
                            if ((i > 0 && i % 10 == 0) || (i == size - 1)) {
                                //取一次数据库的包裹扫描列表
                                List<String> scannedPackageList = this.getScannedInfoPackageNumMoreThanOne(waybillNoCollectionCondition, waybillCodeListTemp);
                                if (scannedPackageList != null && scannedPackageList.size() > 0) {
                                    scannedPackageMoreThanOneList.addAll(scannedPackageList);
                                }
                                waybillCodeListTemp.clear();
                            }
                        }
                    }

                } else {
                    //有可能扫的是运单号，扫运单号认为是齐的，同时也排除脏数据干扰
                    logger.warn("遍历板号：" + barCode + "上的数据，" + barCode + "既不是包裹号，也不是箱号！");
                }
            }
        }else {
            //查询失败
            String message = "通过TC系统的接口获取板号【" + boardCode +"】的信息为空！";
            logger.error(message);
            throw new WaybillNoCollectionException(message);
        }

        //如果存在一单多件的包裹数
        if (scannedPackageMoreThanOneList.size() > 0) {
            Map<String, String> specialWaybillMap = new HashMap<>();
            Set<String> specialPackageSet = new HashSet<>();
            //计算并生成不齐的结果，该方法不适用于特殊包裹号（上海亚一带N和S的包裹）
            waybillNoCollectionResult = this.getNoCollectionPackageResultWithoutSpecial(scannedPackageMoreThanOneList, specialWaybillMap, specialPackageSet);

            int total = waybillNoCollectionResult.getTotal();
            List<String> packageCodeList = waybillNoCollectionResult.getPackageCodeList();
            //处理特殊包裹号
            for (Map.Entry<String, String> entry : specialWaybillMap.entrySet()) {
                String packageCode = entry.getValue();
                int packageNum = WaybillUtil.getPackNumByPackCode(packageCode);
                if (packageNum < 0) {
                    logger.warn("无法计算出包裹数，请检查包裹号【" + packageCode + "】是否有误！");
                    continue;
                }
                for (int i = 1; i <= packageNum; i++) {
                    String newPackageCode = getNewPackageByIndex(packageCode, i);
                    if (newPackageCode != null && ! specialPackageSet.contains(newPackageCode)) {
                        //如果当前结果大于阈值，不添加到结果，继续计算不齐数量
                        if (packageCodeList.size() < this.noCollectionPackageMaxCount) {
                            packageCodeList.add(newPackageCode);
                        }
                        total++;
                    }
                }
            }
            waybillNoCollectionResult.setTotal(total);
        } else {
            //初始化一个空列表
            waybillNoCollectionResult.setPackageCodeList(new ArrayList<String>());
        }

        return waybillNoCollectionResult;
    }

    /*
     * 根据不齐运单的运单信息计算包裹不齐的结果
     *
     * */
    @JProfiler(jKey = "DMSWEB.WaybillNoCollectionInfoServiceImpl.getNoCollectionPackageResult", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public WaybillNoCollectionResult getNoCollectionPackageResult(List<WaybillNoCollectionInfo> waybillNoCollectionInfoList, WaybillNoCollectionCondition waybillNoCollectionCondition) {
        WaybillNoCollectionResult waybillNoCollectionResult = new WaybillNoCollectionResult();

        //存放缺少的包裹号
        List<String> packageCodeResultList = new ArrayList<>();

        //临时变量，用于分批查询运单下扫描过的包裹记录
        List<WaybillNoCollectionInfo> waybillNoCollectionInfoListTemp = new ArrayList<>();
        List<String> waybillCodeListTemp = new ArrayList<>();

        //初始化总数
        int total = 0;
        int size = waybillNoCollectionInfoList.size();

        for (int i = 0; i < size; i++) {
            WaybillNoCollectionInfo waybillNoCollectionInfo = waybillNoCollectionInfoList.get(i);
            String waybillCode = waybillNoCollectionInfo.getWaybillCode();

            //如果不是运单号，跳过计算
            if (! WaybillUtil.isWaybillCode(waybillCode)) {
                continue;
            }
            //运单的包裹数
            int packageNum = waybillNoCollectionInfo.getPackageNum();
            //该运单下，数据库中记录的扫描包裹数
            int scanCount = waybillNoCollectionInfo.getScanCount();
            //如果当前缺少的包裹结果小于阈值，进行计算添加
            if (packageCodeResultList.size() < this.noCollectionPackageMaxCount) {

                //根据查询范围确定是否计算C网以外的运单
                if (this.queryAllWaybillType(waybillNoCollectionCondition) && isBWaybill(waybillCode)) {
                    continue;
                }
                waybillCodeListTemp.add(waybillCode);
                waybillNoCollectionInfoListTemp.add(waybillNoCollectionInfo);

                //每次计算十个不齐的运单，遍历到最后，把剩下不够十个的进行计算
                if ((i > 0 && i % 10 == 0) || (i == size - 1)) {
                    //取一次数据库的包裹扫描列表，特点为运单都是一单多件的
                    List<String> scannedPackageList = this.getScannedInfoPackageNumMoreThanOne(waybillNoCollectionCondition, waybillCodeListTemp);
                    //由于实时计算的原因，两条SQL中间存在时间，当此处查库时有可能不齐的包裹被扫描上了，此时运单就是齐的，影响后面的总数计算，这种极端情况先不考虑了
                    if (scannedPackageList != null && scannedPackageList.size() > 0) {
                        //计算这一批不齐的运单，packageCodeResultList作为入参，方法结束后内容可能会被改变
                        generateNoCollectionPackageList(scannedPackageList, waybillNoCollectionInfoListTemp, packageCodeResultList);
                    } else {
                        logger.warn("运单信息：" + JsonHelper.toJson(waybillNoCollectionInfo) + "，此时已补齐！");
                    }

                    waybillNoCollectionInfoListTemp.clear();
                    waybillCodeListTemp.clear();
                }
            }
            //计算差值
            total += (packageNum - scanCount);
        }

        waybillNoCollectionResult.setTotal(total);
        waybillNoCollectionResult.setPackageCodeList(packageCodeResultList);
        return waybillNoCollectionResult;
    }

    /*
     *
     * 根据运单号生成包裹并找出不齐运单的包裹信息
     *
     * */
    @JProfiler(jKey = "DMSWEB.WaybillNoCollectionInfoServiceImpl.generateNoCollectionPackageList", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public void generateNoCollectionPackageList(List<String> scannedPackageList, List<WaybillNoCollectionInfo> computeWaybillList, List<String> packageCodeResultList) {
        HashSet<String> scannedPackageSet = new HashSet<>(scannedPackageList);

        for (WaybillNoCollectionInfo waybillNoCollectionInfo : computeWaybillList) {
            String packageCode = waybillNoCollectionInfo.getPackageCodeSomeone();
            int packageNum = waybillNoCollectionInfo.getPackageNum();

            for (int i = 1; i <= packageNum; i++) {
                String newPackageCode = getNewPackageByIndex(packageCode, i);
                if (newPackageCode != null && ! scannedPackageSet.contains(newPackageCode)) {
                    packageCodeResultList.add(newPackageCode);
                    //如果当前结果大于阈值，直接返回
                    if (packageCodeResultList.size() >= this.noCollectionPackageMaxCount) {
                        return;
                    }
                }
            }
        }
    }

    /*
     *
     * 根据扫描的包裹号列表计算不齐的订单，不适用于特殊包裹号（上海亚一带N和S的包裹号）
     *
     * */
    @JProfiler(jKey = "DMSWEB.WaybillNoCollectionInfoServiceImpl.getNoCollectionPackageResultWithoutSpecial", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public WaybillNoCollectionResult getNoCollectionPackageResultWithoutSpecial(List<String> scannedPackageList, Map<String, String> specialWaybillMap, Set<String> specialPackageSet) {

        WaybillNoCollectionResult waybillNoCollectionResult = new WaybillNoCollectionResult();

        int total = 0;

        //不齐的包裹号结果
        List<String> packageCodeResultList = new ArrayList<>();
        //字典序排序
        Collections.sort(scannedPackageList);

        //包裹当前索引值
        HashMap<String, Integer> packageIndexMap = new HashMap<>();
        List<Integer> lexicalOrder = null;
        int scannedPackageListSize = scannedPackageList.size();

        for (int i = 0; i < scannedPackageListSize; i++) {

            String currPackageCode = scannedPackageList.get(i);
            String currWaybillCode = WaybillUtil.getWaybillCode(currPackageCode);
            //带“-”的可以进行计算
            if (! currPackageCode.contains("-")) {
                if (! specialWaybillMap.containsKey(currWaybillCode)) {
                    specialWaybillMap.put(currWaybillCode, currPackageCode);
                }
                specialPackageSet.add(currPackageCode);
                continue;
            }

            //处理当前包裹
            int packageNum = WaybillUtil.getPackNumByPackCode(currPackageCode);

            if (packageNum < 0) {
                logger.warn("无法计算出包裹数，请检查包裹号【" + currPackageCode + "】是否有误！");
                continue;
            }

            //遍历到了新运单，初始化期望索引，初始化字典序列表
            if (! packageIndexMap.containsKey(currWaybillCode)) {
                packageIndexMap.put(currWaybillCode, 0);
                lexicalOrder = this.getLexicalOrder(packageNum);
            }

            //该索引是字典序数组中的索引
            int desireIndex = packageIndexMap.get(currWaybillCode);
            //判断并生成不齐的包裹号，返回更新后的索引值
            int currIndex = this.generateNoCollectionCommonPackage(packageCodeResultList, lexicalOrder, packageNum, desireIndex, currPackageCode);
            //说明计算过程中存在问题，跳过该包裹号
            if (currIndex < 0) {
                continue;
            }
            //此处已经找到当前包裹数字在字典序的位置，则更新期望值为当前值的在字典序的下一个
            packageIndexMap.put(currWaybillCode, currIndex + 1);

            //遍历结束条件
            total += (currIndex - desireIndex);

            //判断当前包裹号是不是该运单的最后一个
            boolean isNeedHandleTail = false;

            //如果可以取到下一个包裹号
            if (i + 1 < scannedPackageListSize) {
                //取出下一个运单号和包裹号
                String nextPackageCode = scannedPackageList.get(i + 1);
                String nextWaybillCode = WaybillUtil.getWaybillCode(nextPackageCode);
                //下一单是新运单，要对上一个运单进行收尾工作，避免丢失[x, 包裹最大值]这个区间的值
                if (! currWaybillCode.equals(nextWaybillCode)) {
                    isNeedHandleTail = true;
                }
            } else if (i + 1 == scannedPackageListSize) {
                //同理，如果包裹号是整个集合的最后一个，也要处理该包裹是不是当前运单的最后一个
                isNeedHandleTail = true;
            }
            //处理尾巴
            if (isNeedHandleTail) {
                //该索引上一运单期望的字典序数组中的索引
                int lastDesireIndex = currIndex + 1;
                this.generateNoCollectionCommonPackageUntilEnd(packageCodeResultList, lexicalOrder, packageNum, lastDesireIndex, currPackageCode);
                if (lastDesireIndex < packageNum) {
                    total += (packageNum - lastDesireIndex);
                }
            }
        }

        waybillNoCollectionResult.setTotal(total);
        waybillNoCollectionResult.setPackageCodeList(packageCodeResultList);

        return waybillNoCollectionResult;
    }

    /*
     * 根据字典序顺序，找出缺少的包裹号，针对JD000000000-1-2-2这种包裹号有效
     *
     * */
    public int generateNoCollectionCommonPackage(List<String> packageCodeResultList, List<Integer> lexicalOrder, int packageNum, int desireIndex, String currPackageCode) {

        //从字典序数组中取出真正的字典序下的值，是一个期望值
        int desireLexicalOrderIndex = lexicalOrder.get(desireIndex);
        //当前包裹号的包裹数字，应该在字典序中找到该数字的位置
        int currLexicalOrderIndex = WaybillUtil.getCurrentPackageNum(currPackageCode);
        //如果该包裹的序号比包裹数还大，说明包裹号有问题，返回-1
        if (currLexicalOrderIndex > packageNum) {
            logger.warn("计算出包裹序号大于包裹数【" + packageNum + "】，请检查包裹号【" + currPackageCode + "】是否有误！");
            return -1;
        }
        //如果当前包裹数字与期望值不同，期望值对应的包裹到当前包裹数字之间的包裹，都是没有被扫描到的包裹
        while (currLexicalOrderIndex != desireLexicalOrderIndex) {
            //如果当前结果大于返回的最大值，则返回false，外层跳出循环
            String newPackageCode = getNewPackageByIndex(currPackageCode, desireLexicalOrderIndex);
            if (StringHelper.isNotEmpty(newPackageCode)) {
                if (packageCodeResultList.size() < this.noCollectionPackageMaxCount) {
                    packageCodeResultList.add(newPackageCode);
                }
            }
            //防止脏数据使数组索引越界
            if (desireIndex == packageNum - 1) {
                break;
            }
            desireLexicalOrderIndex = lexicalOrder.get(++desireIndex);
        }

        return desireIndex;
    }

    /*
     * 根据字典序顺序，生成从开始索引一直到最后一个，针对JD000000000-1-2-2这种包裹号有效
     *
     * */
    public void generateNoCollectionCommonPackageUntilEnd(List<String> packageCodeResultList, List<Integer> lexicalOrder, int packageNum, int startIndex, String lastPackageCode) {

        //遍历直到当前值等于最大包裹数
        for (int i = startIndex; i < packageNum; i++) {
            //如果当前结果大于返回的最大值，则返回true，外层跳出循环
            String newPackageCode = getNewPackageByIndex(lastPackageCode, lexicalOrder.get(i));
            if (packageCodeResultList.size() < this.noCollectionPackageMaxCount && StringHelper.isNotEmpty(newPackageCode)) {
                packageCodeResultList.add(newPackageCode);
            }
        }
    }

    public boolean queryAllWaybillType(WaybillNoCollectionCondition waybillNoCollectionCondition) {
        int queryRange = waybillNoCollectionCondition.getQueryRange();
        return queryRange == WaybillNoCollectionRangeEnum.B_RANGE.getType();
    }

    /*
     *
     * 判断是否是B网运单
     *
     * */
    @JProfiler(jKey = "DMSWEB.WaybillNoCollectionInfoServiceImpl.isBWaybill", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public boolean isBWaybill(String waybillCode) {
        BigWaybillDto bigWaybillDto = findWaybillAndPack(waybillCode);
        if (bigWaybillDto.getWaybill() != null && StringHelper.isNotEmpty(bigWaybillDto.getWaybill().getWaybillSign())) {
            String waybillSign = bigWaybillDto.getWaybill().getWaybillSign();
            return ! BusinessUtil.isSignInChars(waybillSign, 40, '2', '3') && ! BusinessUtil.isSignY(waybillSign, 36);
        } else {
            logger.warn("获取【" + waybillCode + "】的运单信息失败，按B网运单处理");
        }
        return true;
    }

    /*
     *
     * 批量查询运单下，扫描过的一单多件的包裹号信息
     *
     * */
    public List<String> getScannedInfoPackageNumMoreThanOne(WaybillNoCollectionCondition waybillNoCollectionCondition, List<String> waybillCodeList) {
        waybillNoCollectionCondition.setWaybillCodeList(waybillCodeList);
        return sendDatailDao.getScannedInfoPackageNumMoreThanOne(waybillNoCollectionCondition);
    }

    /*
    *
    * 查询运单接口，判断是否是B网面单
    *
    * */
    public BigWaybillDto findWaybillAndPack(String waybillCode) {
        BigWaybillDto bigWaybillDto = null;

        try {
            WChoice wChoice = new WChoice();
            wChoice.setQueryWaybillC(true);
//            wChoice.setQueryPackList(true);
            BaseEntity<BigWaybillDto> baseEntity = this.waybillQueryManager.getDataByChoice(waybillCode, wChoice);
            if (baseEntity != null) {
                bigWaybillDto = baseEntity.getData();
            }

        } catch (Exception e) {
            this.logger.error("运单号【 " + waybillCode + "】调用运单WSS异常：", e);
        }

        return bigWaybillDto;
    }

    /*
     *
     * 生成1到n的字典序数组
     *
     * */
    @JProfiler(jKey = "DMSWEB.WaybillNoCollectionInfoServiceImpl.getLexicalOrder", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public List<Integer> getLexicalOrder(int n) {
        List<Integer> res = new ArrayList<>();
        int cur = 1;
        for (int i = 1; i <= n; i++) {
            res.add(cur);
            if (cur * 10 <= n) {
                cur = cur * 10;
            } else if (cur % 10 != 9 && cur + 1 <= n) {
                cur++;
            } else {
                while ((cur / 10) % 10 == 9) {
                    cur = cur / 10;
                }
                cur = cur / 10 + 1;
            }
        }
        return res;
    }

    /*
     *
     * 根据一个包裹号模板，生产运单中第index个包裹号
     *
     * */
    @JProfiler(jKey = "DMSWEB.WaybillNoCollectionInfoServiceImpl.getNewPackageByIndex", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public String getNewPackageByIndex(String packageCodeTemplate, int index) {

        String newPackageCode = null;
        //上海亚一包裹号处理
        if (packageCodeTemplate.contains("N") && packageCodeTemplate.contains("S") && packageCodeTemplate.contains("H")) {
            newPackageCode = packageCodeTemplate.replaceFirst("N\\d+S", "N" + index + "S");
        } else if (packageCodeTemplate.contains("-")) {
            newPackageCode = packageCodeTemplate.replaceFirst("-\\d+-", "-" + index + "-");
        } else {
            logger.warn("根据包裹号【" + packageCodeTemplate + "】，无法计算出新包裹，请检查包裹号是否符合包裹号标准！");
        }
        return newPackageCode;
    }
}
