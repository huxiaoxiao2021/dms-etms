package com.jd.bluedragon.distribution.ver.service.impl;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.SortingResponse;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.client.domain.*;
import com.jd.bluedragon.distribution.internal.service.DmsInternalService;
import com.jd.bluedragon.distribution.jsf.domain.BlockResponse;
import com.jd.bluedragon.distribution.mixedPackageConfig.enums.YNEnum;
import com.jd.bluedragon.distribution.ver.domain.CancelWaybillDto;
import com.jd.bluedragon.distribution.ver.domain.JdCancelWaybillResponse;
import com.jd.bluedragon.distribution.ver.enums.WaybillCancelBusinessTypeEnum;
import com.jd.bluedragon.distribution.ver.service.CancelWaybillService;
import com.jd.bluedragon.distribution.ver.service.domain.SortingReturnRequest;
import com.jd.bluedragon.distribution.ver.util.RedisStringUtil;
import com.jd.bluedragon.distribution.waybill.dao.CancelWaybillDao;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;
import com.jd.bluedragon.distribution.waybill.domain.WaybillCancelInterceptModeEnum;
import com.jd.bluedragon.distribution.waybill.domain.WaybillCancelInterceptTypeEnum;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.bluedragon.utils.PropertiesHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.MessageFormat;
import java.util.*;

import static com.jd.bluedragon.distribution.jsf.domain.BlockResponse.*;
import static com.jd.bluedragon.distribution.waybill.domain.CancelWaybill.*;

@Service
public class CancelWaybillServiceImpl implements CancelWaybillService {

    private final Log logger = LogFactory.getLog(this.getClass());
    private static final Integer WAYBILL_STATUS_CANCEL = 0;
    //拦截的包裹号初始化保存时每批次保存的条数 根据网上的资料每次（50 ~ 100）总效率最高
    private static final Integer CANCLE_PACKAGECODE_BATCH_INSERT_COUNT = 100;

    /**
     * 记录拦截订单操作数据
     */
    public final static Integer INTERCEPT_RECORD_TYPE = -1;

    /**
     * 重置包裹拦截记录的key前缀
     */
    public final static String CANCEL_WAYBILL_PACKAGE_CODE_RESET_REDIS_KEY_PREFIX = "CANCEL_WAYBILL_PACKAGE_CODE_RESET_";
    /**
     * 重置包裹拦截记录 redis 缓存过期时间
     */
    public final static Long CANCEL_WAYBILL_PACKAGE_CODE_RESET_REDIS_EXPIRE_TIME = 5 * 60 * 1000L;

    @Autowired
    private CancelWaybillDao cancelWaybillMapper;

    @Autowired
    private WaybillService waybillService;

    @Autowired(required = false)
    RedisStringUtil redisStringUtil;

    @Autowired
    private DmsInternalService dmsInternalService;


    @Override
    public CancelWaybill get(String waybillCode) {
        //调用 病单优先级方法。与其处理保持一致
        Integer featureType = getFeatureTypeByWaybillCode(waybillCode);
        if (featureType == null) {
            return null;
        } else {
            CancelWaybill cancelWaybill = new CancelWaybill();
            cancelWaybill.setWaybillCode(waybillCode);
            cancelWaybill.setFeatureType(featureType);
            return cancelWaybill;
        }
    }

    @Override
    public Boolean hasCancel(String waybillCode) {
        return this.get(waybillCode) != null ? Boolean.TRUE : Boolean.FALSE;
    }

    /**
     * 查询waybill_cancel表最近一段时间内是否有存活数据
     *
     * @return
     */
    @Override
    public Integer countWaybillCancelLatest() {
        int viableLimit = 3;
        try {
            String viableLimitStr = PropertiesHelper.newInstance().getValue("viableLimit");
            viableLimit = Integer.parseInt(viableLimitStr);
        } catch (NumberFormatException e) {
            logger.error(e);
        }

        return this.cancelWaybillMapper.countWaybillCancelLatest(viableLimit);
    }

    /**
     * 1.从本地分拣中心获取WaybillCancel数据
     * 2.如果Waybillcancel为null,直接返回OK
     * 3.如果不为null,则从订单中间件获取Order数据
     * 4.结合WaybillCancel.featureType和Order的数据,联合判断
     * 4.1.订单取消和订单删除,这两种情况,订单中间件对应的字段是yn,且yn = 0,而featureType分别是-1和-2
     * 4.2.订单锁定,这种情况,订单中间件对应的字段是state (注意不是state2),且state > 100, 而featureType = -3
     * 4.3.退款100分订单和订单拦截,这两种情况,不用加订单中间件校验 (订单拦截是外单业务范畴)
     *
     * @param waybillCode
     * @return
     */
    @Override
    public JdCancelWaybillResponse dealCancelWaybill(String waybillCode) {
        CancelWaybill cancelWaybill = this.getCancelWaybillByWaybillCode(waybillCode);
        if (cancelWaybill == null) {
            return new JdCancelWaybillResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        }

        Integer featureType = cancelWaybill.getFeatureType();
        Integer interceptType = cancelWaybill.getInterceptType();

        JdCancelWaybillResponse response;
        if (interceptType != null) {
            // 走新逻辑
            response = this.getResponseByInterceptType(interceptType, cancelWaybill.getInterceptMode());
        } else {
            //走旧逻辑
            response = this.getResponseByFeatureType(featureType);
        }
        response.setFeatureType(featureType);
        response.setInterceptType(interceptType);
        return response;
    }

    /**
     * 根据FeatureType获取拦截结果
     *
     * @param featureType
     * @return
     */
    private JdCancelWaybillResponse getResponseByFeatureType(Integer featureType) {
        Integer code = JdResponse.CODE_OK;
        String message = JdResponse.MESSAGE_OK;
        if (featureType != null) {
            if (CancelWaybill.FEATURE_TYPE_CANCELED.equals(featureType)) {
                code = SortingResponse.CODE_29302;
                message = SortingResponse.MESSAGE_29302;
            } else if (CancelWaybill.FEATURE_TYPE_DELETED.equals(featureType)) {
                code = SortingResponse.CODE_29302;
                message = SortingResponse.MESSAGE_29302;
            } else if (CancelWaybill.FEATURE_TYPE_REFUND100.equals(featureType)) {
                code = SortingResponse.CODE_29303;
                message = SortingResponse.MESSAGE_29303;
            } else if (CancelWaybill.FEATURE_TYPE_INTERCEPT.equals(featureType)) {
                code = SortingResponse.CODE_29305;
                message = SortingResponse.MESSAGE_29305;
            } else if (CancelWaybill.FEATURE_TYPE_INTERCEPT_BUSINESS.equals(featureType)) {
                code = SortingResponse.CODE_29306;
                message = SortingResponse.MESSAGE_29306;
            } else if (CancelWaybill.FEATURE_TYPE_SICK.equals(featureType)) {
                code = SortingResponse.CODE_29307;
                message = SortingResponse.MESSAGE_29307;
            } else if (CancelWaybill.FEATURE_TYPE_INTERCEPT_LP.equals(featureType)) {
                code = SortingResponse.CODE_29308;
                message = SortingResponse.MESSAGE_29308;
            }
        }
        return new JdCancelWaybillResponse(code, message);
    }

    private JdCancelWaybillResponse getResponseByInterceptType(Integer interceptType, Integer interceptMode) {
        Integer code = JdResponse.CODE_OK;
        String message = JdResponse.MESSAGE_OK;
        if (WaybillCancelInterceptTypeEnum.CANCEL.getCode() == interceptType) {
            if (interceptMode == WaybillCancelInterceptModeEnum.NOTICE.getCode()) {
                code = SortingResponse.CODE_39006;
                message = SortingResponse.MESSAGE_39006;
            }
            if (interceptMode == WaybillCancelInterceptModeEnum.INTERCEPT.getCode()) {
                code = SortingResponse.CODE_29311;
                message = SortingResponse.MESSAGE_29311;
            }
            return new JdCancelWaybillResponse(code, message);
        }

        if (WaybillCancelInterceptTypeEnum.REFUSE.getCode() == interceptType) {
            code = SortingResponse.CODE_29312;
            message = SortingResponse.MESSAGE_29312;
            return new JdCancelWaybillResponse(code, message);
        }

        if (WaybillCancelInterceptTypeEnum.MALICE.getCode() == interceptType) {
            code = SortingResponse.CODE_29313;
            message = SortingResponse.MESSAGE_29313;
            return new JdCancelWaybillResponse(code, message);
        }

        if (WaybillCancelInterceptTypeEnum.WHITE.getCode() == interceptType) {
            code = SortingResponse.CODE_29316;
            message = SortingResponse.MESSAGE_29316;
            return new JdCancelWaybillResponse(code, message);
        }

        return new JdCancelWaybillResponse(code, message);
    }

    @Override
    public JdCancelWaybillResponse dealCancelWaybill(PdaOperateRequest pdaOperate) {
        String waybillCode = WaybillUtil.getWaybillCode(pdaOperate.getPackageCode());

        JdCancelWaybillResponse jdResponse = dealCancelWaybill(waybillCode);
        if (!jdResponse.getCode().equals(JdResponse.CODE_OK)) {
            this.pushSortingInterceptByFeatureType(pdaOperate, jdResponse.getFeatureType());
        }
        return jdResponse;
    }

    private void pushSortingInterceptByFeatureType(PdaOperateRequest pdaOperate, Integer featureType) {
        /*分拣信息链接*/
        String record_msg = "";
        if (CancelWaybill.FEATURE_TYPE_LOCKED.equals(featureType)) {
            record_msg = CancelWaybill.FEATURE_MSG_LOCKED;
        } else if (CancelWaybill.FEATURE_TYPE_CANCELED.equals(featureType)) {
            record_msg = CancelWaybill.FEATURE_MSG_CANCELED;
        } else if (CancelWaybill.FEATURE_TYPE_DELETED.equals(featureType)) {
            record_msg = CancelWaybill.FEATURE_MSG_CANCELED;
        } else if (CancelWaybill.FEATURE_TYPE_REFUND100.equals(featureType)) {
            record_msg = CancelWaybill.FEATURE_MSG_REFUND100;
        } else if (CancelWaybill.FEATURE_TYPE_INTERCEPT.equals(featureType)) {
            record_msg = CancelWaybill.FEATURE_MSG_INTERCEPT;
        } else if (CancelWaybill.FEATURE_TYPE_INTERCEPT_BUSINESS.equals(featureType)) {
            record_msg = CancelWaybill.FEATURE_MSG_INTERCEPT_BUSINESS;
        } else if (CancelWaybill.FEATURE_TYPE_SICK.equals(featureType)) {
            record_msg = CancelWaybill.FEATURE_MSG_SICKCANCEL;
        } else if (CancelWaybill.FEATURE_TYPE_INTERCEPT_LP.equals(featureType)) {
            record_msg = CancelWaybill.FEATURE_MSG_INTERCEPT_LP;
        }
        pushSortingInterceptRecord(pdaOperate, record_msg);
    }

    @Override
    public void pushSortingInterceptRecord(PdaOperateRequest pdaOperate, String errorMsg) {
        this.post(pdaOperate.getBusinessType(), pdaOperate.getCreateSiteCode()
                , pdaOperate.getCreateSiteName(), pdaOperate.getOperateUserCode()
                , pdaOperate.getOperateUserName(), errorMsg
                , pdaOperate.getOperateTime(), pdaOperate.getPackageCode());
    }

    @Override
    public TaskResponse postSortingCached(TaskRequest request) {
        com.jd.bluedragon.distribution.api.response.TaskResponse result = null;
        try{
            com.jd.bluedragon.distribution.api.request.TaskRequest taskRequest = toServerTaskRequest(request);
            if(null == taskRequest) return null;
            result = dmsInternalService.addTask(taskRequest);
        }catch (Exception e){
            logger.error("调用任务接口添加任务失败，原因", e);
            return null;
        }
        return toClientTaskResponse(result);

    }
    private TaskResponse toClientTaskResponse(com.jd.bluedragon.distribution.api.response.TaskResponse result){
        if(null == result){
            return null;
        }
        TaskResponse response = new TaskResponse();
        response.setRequest(result.getRequest());
        response.setCode(result.getCode());
        response.setMessage(result.getMessage());
        response.setId(result.getId());
        response.setCreateTime(result.getCreateTime());
        return response;
    }

    private com.jd.bluedragon.distribution.api.request.TaskRequest toServerTaskRequest(TaskRequest result){
        if(null == result){
            return null;
        }
        com.jd.bluedragon.distribution.api.request.TaskRequest response = new com.jd.bluedragon.distribution.api.request.TaskRequest();
        response.setUserCode(result.getUserCode());
        response.setUserName(result.getUserName());
        response.setSiteCode(result.getSiteCode());
        response.setSiteName(result.getSiteName());
        response.setBusinessType(result.getBusinessType());
        response.setId(result.getId());
        response.setOperateTime(result.getOperateTime());
        response.setTaskId(result.getTaskId());
        response.setType(result.getType());
        response.setKeyword1(result.getKeyword1());
        response.setKeyword2(result.getKeyword2());
        response.setBody(result.getBody());
        response.setBoxCode(result.getBoxCode());
        response.setReceiveSiteCode(result.getReceiveSiteCode());
        return  response;
    }


    public TaskResponse post(
            Integer businessType,//分拣业务类型
            Integer siteCode, //站点编号
            String siteName,//站点名称
            Integer userCode,//用户编码
            String userName,//用户名称
            String shieldsError, //错误类型
            String operateTime,
            String packageCode) {
        try {
            SortingReturnRequest[] request = new SortingReturnRequest[1];
            request[0] = new SortingReturnRequest();
            request[0].setBusinessType(INTERCEPT_RECORD_TYPE);//分拣业务类型
            request[0].setSiteCode(siteCode);//站点编号
            request[0].setSiteName(siteName);//站点名称
            request[0].setUserCode(userCode);//用户编码
            request[0].setUserName(userName);//用户名称
            request[0].setShieldsError(shieldsError);//错误类型
            request[0].setOperateTime(operateTime);
            request[0].setPackageCode(packageCode);

            TaskRequest task = new TaskRequest();
            task.setOperateTime(operateTime);
            task.setKeyword1(String.valueOf(siteCode));
            task.setKeyword2(packageCode);
            task.setSiteCode(siteCode);
            task.setReceiveSiteCode(siteCode);
            task.setType(1220);/*分拣退货类型*/

            task.setBody(JsonHelper.toJson(request));
            return postSortingCached(task);

        } catch (Exception e) {
            logger.error("拦截订单记录失败，异常为：", e);
        }
        return null;
    }

    /**
     * 写入Waybill_Cancel数据
     *
     * @param cancelWaybill
     */
    @Override
    public void insertWaybillCancel(CancelWaybill cancelWaybill) {
        cancelWaybillMapper.insertWaybillCancel(cancelWaybill);
    }

    private CancelWaybill getCancelWaybillByWaybillCode(String waybillCode) {
        List<CancelWaybill> cancelWaybills = this.cancelWaybillMapper.getByWaybillCode(waybillCode);
        if (cancelWaybills == null || cancelWaybills.isEmpty()) {
            return null;
        }

        // 获取病单拦截
        CancelWaybill cancelWaybill = this.getSickCancelWaybill(cancelWaybills);
        if (cancelWaybill != null) {
            return cancelWaybill;
        }

        return cancelWaybills.get(0);
    }

    /**
     * 获取病单，有病单则优先返回病单 30病单 31 取消病单
     *
     * @param cancelWaybills
     * @return
     */
    private CancelWaybill getSickCancelWaybill(List<CancelWaybill> cancelWaybills) {
        for (CancelWaybill cancelWaybill : cancelWaybills) {
            if (CancelWaybill.FEATURE_TYPE_SICK.equals(cancelWaybill.getFeatureType())) {
                return cancelWaybill;
            }
            if (CancelWaybill.FEATURE_TYPE_SICK_CANCEL.equals(cancelWaybill.getFeatureType())) {
                return null;
            }
        }
        return null;
    }

    /**
     * 获取 拦截标识
     *
     * 病单优先级最高
     */
    @Override
    public Integer getFeatureTypeByWaybillCode(String waybillCode) {
        List<Integer> featureTypes = this.cancelWaybillMapper.getFeatureTypeByWaybillCode(waybillCode);
        if (featureTypes == null || featureTypes.size() == 0) {
            return null;
        }
        //有病单则优先返回病单  30病单 31 取消病单
        if (featureTypes.contains(CancelWaybill.FEATURE_TYPE_SICK)) {
            List<Integer> needFeatureTypes = featureTypes.subList(0, featureTypes.indexOf(CancelWaybill.FEATURE_TYPE_SICK));
            if (!needFeatureTypes.contains(CancelWaybill.FEATURE_TYPE_SICK_CANCEL)) {
                return CancelWaybill.FEATURE_TYPE_SICK;
            }
        }
        return featureTypes.get(0);
    }

    @Override
    public Integer[] upsertWaybillCancel(CancelWaybillDto cancelWaybill) {
        if (null == cancelWaybill || StringHelper.isEmpty(cancelWaybill.getWaybillCode())) {
            logger.error("CancelWaybillServiceImpl.upsertWaybillCancel--参数不全:" + JsonHelper.toJson(cancelWaybill));
            return null;
        }
        /*拦截数据初始化业务类型*/
        Integer featureType = cancelWaybill.getFeatureType();
        /*解锁（比如补打面单）是对应的业务类型数组*/
        Integer[] featureTypes = cancelWaybill.getFeatureTypes();
        if (featureType == null && ArrayUtils.isEmpty(featureTypes)) {
            logger.error("upsertWaybillCancel时featureType 和 featureTypes不能同时为空");
            return null;
        }
        String waybillCode = cancelWaybill.getWaybillCode();
        String packageCode = cancelWaybill.getPackageCode();
        String businessType = cancelWaybill.getBusinessType();
        //验证业务类型
        if (StringUtils.isBlank(businessType)) {
            logger.error(MessageFormat.format("cancelWaybill的businessType为空，运单号：{0}", waybillCode));
            return null;
        }


        boolean packageCodeIllegal = StringUtils.isNotBlank(packageCode) && !WaybillUtil.isPackageCode(packageCode);
        if (!WaybillUtil.isWaybillCode(waybillCode) || packageCodeIllegal) {
            logger.error(MessageFormat.format("waybillCode:{0}或packageCode{1}格式非法", waybillCode, packageCode));
            return null;
        }
        if (BUSINESS_TYPE_UNLOCK.equals(businessType) && ArrayUtils.isNotEmpty(featureTypes)) {
            //需要后后续操作的数组进行单独更新，以便记录是否需要后续操作，避免重复的后续操作
            Integer[] needAfterDealFeatureTypes = unBlockNeedAfterDealFeatureTypes(waybillCode, packageCode, featureTypes,
                    cancelWaybill.getUpdateTime(), cancelWaybill.getTs(), businessType);
            Integer[] noNeedAfterDealFeatureTypes = unBlockNoNeedAfterDealFeatureTypes(waybillCode, packageCode, featureTypes,
                    cancelWaybill.getUpdateTime(), cancelWaybill.getTs(), businessType);
            //更新运单拦截状态
            if (StringUtils.isNotBlank(cancelWaybill.getPackageCode())) {
                unBlockWaybillIfPackageAllUnBlock(needAfterDealFeatureTypes, noNeedAfterDealFeatureTypes, cancelWaybill);
            }
            return needAfterDealFeatureTypes;
        }



        /* 获取拦截记录 waybill_cancel */
        CancelWaybill oldCancleWaybill = findWaybillCancelByWaybillCodeAndFeatureType(waybillCode, featureType);
        //按运单插入
        if (null == oldCancleWaybill) {
            //业务类型需要包裹拦截
            if (featureType != null && FEATURE_TYPES_NEED_PACKAGE_DEAL.contains(featureType)) {
                boolean result = insertBatchPackageCodeLock(cancelWaybill);
                if (result) {
                    logger.info(MessageFormat.format("运单号：{0}的包裹拦截数据插入完成", waybillCode));
                } else {
                    //插入包裹拦截记录失败 运单记录也不插入
                    return null;
                }

            }
            /* 插入运单拦截记录 */
            insertWaybillCancel(cancelWaybill);
        } else {
            //只有订单改配送方式的需求 需要更新
            if (FEATURE_TYPE_ORDER_MODIFY.equals(featureType)) {
                /* 存在记录的话，则进行更新 运单记录和包裹记录都更新*/
                cancelWaybillMapper.updatePackageBusinessTypeByWaybillCode(waybillCode, new Integer[]{featureType},
                        cancelWaybill.getUpdateTime(), cancelWaybill.getTs(), cancelWaybill.getBusinessType());
                logger.info(MessageFormat.format("更新cancelWaybill：{0}完成", JsonHelper.toJson(cancelWaybill)));
            }

        }
        return null;
    }

    private void unBlockWaybillIfPackageAllUnBlock(Integer[] needAfterDealFeatureTypes, Integer[] noNeedAfterDealFeatureTypes,
                                                   CancelWaybill cancelWaybill) {
        Integer[] mergeFeatureTypes = (Integer[]) ArrayUtils.addAll(needAfterDealFeatureTypes, noNeedAfterDealFeatureTypes);
        if (ArrayUtils.isEmpty(mergeFeatureTypes)) {
            return;
        }
        Collection<Integer> intersectionElems = CollectionUtils.intersection(Arrays.asList(mergeFeatureTypes),
                FEATURE_TYPES_NEED_PACKAGE_DEAL);
        if (CollectionUtils.isEmpty(intersectionElems)) {
            logger.info("无需要后续处理的交集featureType类型");
            return;
        }
        Integer[] packageNeedDealFeatureTypes = new Integer[intersectionElems.size()];
        packageNeedDealFeatureTypes = intersectionElems.toArray(packageNeedDealFeatureTypes);
        for (Integer featureType : packageNeedDealFeatureTypes) {
            //包裹数量
            Integer packageCount = WaybillUtil.getPackNumByPackCode(cancelWaybill.getPackageCode());
            if (packageCount == null || packageCount < 1) {
                logger.error(MessageFormat.format("根据包裹号{0}获取包裹数量失败", cancelWaybill.getPackageCode()));
                return;
            }
            Long unBlockPackageCount = findPackageCodeCountByFeatureTypeAndWaybillCode(cancelWaybill.getWaybillCode(),
                    featureType, String.valueOf(WaybillCancelBusinessTypeEnum.UNLOCK.getCode()));
            if (unBlockPackageCount >= packageCount) {
                cancelWaybill.setFeatureType(featureType);
                Integer count = updatWaybillbusinessType(cancelWaybill);
                logger.info(MessageFormat.format("cancelWaybill:{0},运单所有包裹均已解除拦截状态，运单维度的拦截更新状态，更新数量count:{1}",
                        JsonHelper.toJson(cancelWaybill), count));
            }
        }
    }

    private Integer[] unBlockNoNeedAfterDealFeatureTypes(String waybillCode, String packageCode, Integer[] featureTypes,
                                                         String updateTime, Long ts, String businessType) {
        featureTypes = noNeedAfterDealFeatureTypes(featureTypes);
        if(featureTypes == null || featureTypes.length == 0){
            return null;
        }
        List<Integer> featureTypeList = new ArrayList<Integer>(featureTypes.length);
        for (Integer featureType : featureTypes) {
            Integer count = unBlockWaybillAndPackage(waybillCode, packageCode, new Integer[]{featureType}, updateTime,
                    ts, businessType);
            logger.info(MessageFormat.format("根据运单号：{0},或包裹号：{1}更新业务类型：{2}拦截记录的状态为：{3}，更新数量{4}",
                    waybillCode, packageCode, featureType, businessType, count));
            if (count != null && count > 0) {
                featureTypeList.add(featureType);
            }

        }
        if (CollectionUtils.isEmpty(featureTypeList)) {
            return ArrayUtils.EMPTY_INTEGER_OBJECT_ARRAY;
        }
        featureTypes = new Integer[featureTypeList.size()];
        featureTypes = featureTypeList.toArray(featureTypes);
        return featureTypes;

    }

    /**
     * @param featureTypes
     * @return
     */
    private Integer[] noNeedAfterDealFeatureTypes(Integer[] featureTypes) {
        if (ArrayUtils.isEmpty(featureTypes)) {
            return null;
        }
        //featureTypes 里刨除 NEED_AFTER_DEAL_FEATURE_TYPES的元素 ，因为上面已经处理过了。
        Collection<Integer> subtractList = CollectionUtils.subtract(Arrays.asList(featureTypes),
                Arrays.asList(NEED_AFTER_DEAL_FEATURE_TYPES));
        if (CollectionUtils.isEmpty(subtractList)) {
            return null;
        }
        Integer[] noDeeds = new Integer[subtractList.size()];
        return subtractList.toArray(noDeeds);

    }

    /**
     * 后续需要处理类型 从featureTypes 挑处理处理，
     * 并记录更新数量，更新数量未0的，说明是重复解锁，无需再进行后续操作，以免重复操作。
     *
     * @param waybillCode
     * @param packageCode
     * @param featureTypes
     * @param updateTime
     * @param ts
     * @param businessType
     * @return
     */
    private Integer[] unBlockNeedAfterDealFeatureTypes(String waybillCode, String packageCode, Integer[] featureTypes,
                                                       String updateTime, Long ts, String businessType) {
        //需要后续操作的数组进行单独更新，以便记录是否需要后续操作，避免重复的后续操作
        Integer[] needAfterDealFeatureTypes = getNeedAfterDealFeatureTypes(featureTypes);
        if (ArrayUtils.isEmpty(needAfterDealFeatureTypes)) {
            logger.info("无需要后续操作的业务类型数组为空");
            return null;
        }
        List<Integer> needAfterDealFeatureTypeList = new ArrayList<Integer>();
        for (Integer featureType : needAfterDealFeatureTypes) {
            //解除拦截状态
            Integer count = unBlockWaybillAndPackage(waybillCode, packageCode, new Integer[]{featureType}, updateTime,
                    ts, businessType);
            if (count != null && count > 0) {
                needAfterDealFeatureTypeList.add(featureType);
            }
            logger.info(MessageFormat.format("根据运单号：{0},或包裹号：{1}更新业务类型：{2}拦截记录的状态为：{3}，更新数量{4}",
                    waybillCode, packageCode, featureType, businessType, count));

        }
        if (needAfterDealFeatureTypeList.isEmpty()) {
            logger.info("无更新，无需要后续操作的业务类型");
            return null;
        }
        //list to List
        needAfterDealFeatureTypes = new Integer[needAfterDealFeatureTypeList.size()];
        return needAfterDealFeatureTypeList.toArray(needAfterDealFeatureTypes);
    }

    private Integer unBlockWaybillAndPackage(String waybillCode, String packageCode, Integer[] featureTypes,
                                             String updateTime, Long ts, String businessType) {
        //更新未解锁的为已解锁状态，重复解锁不更新
        if (StringUtils.isNotBlank(packageCode)) {
            Integer count = cancelWaybillMapper.updateByPackageCodeAndFeatureType(packageCode, featureTypes,
                    updateTime, ts, businessType);
            logger.info(MessageFormat.format("按包裹:{0}解锁，更新包裹号的拦截状态，更新数量{1},featureTypes:{2}",
                    packageCode, count, ArrayUtils.toString(featureTypes)));
            return count;
        }

        //按运单解锁 所有的包裹拦截记录都更新一下
        if (BUSINESS_TYPE_UNLOCK.equals(businessType)) {
            Integer count = cancelWaybillMapper.updatePackageBusinessTypeByWaybillCode(waybillCode, featureTypes,
                    updateTime, ts, businessType);
            logger.info(MessageFormat.format("按运单{0}操作解锁，所有的包裹记录也都更新，更新数量{1},featureTypes:{2}",
                    waybillCode, count, ArrayUtils.toString(featureTypes)));
            return count;
        }
        return null;
    }

    /**
     * 计算需要后续处理的featureType交集
     *
     * @param featureTypes 本次操作解锁的业务数组
     * @return
     */
    private Integer[] getNeedAfterDealFeatureTypes(Integer[] featureTypes) {
        if (ArrayUtils.isEmpty(featureTypes) || ArrayUtils.isEmpty(NEED_AFTER_DEAL_FEATURE_TYPES)) {
            return null;
        }
        Collection<Integer> intersectionElems = CollectionUtils.intersection(Arrays.asList(featureTypes),
                Arrays.asList(NEED_AFTER_DEAL_FEATURE_TYPES));
        if (CollectionUtils.isEmpty(intersectionElems)) {
            logger.info("无需要后续处理的交集featureType类型");
            return null;
        }
        Integer[] needAfterDealFeatureTypes = new Integer[intersectionElems.size()];
        needAfterDealFeatureTypes = intersectionElems.toArray(needAfterDealFeatureTypes);
        logger.info(MessageFormat.format("需要后续处理的交集featureType类型为：{0}", Arrays.toString(needAfterDealFeatureTypes)));
        return needAfterDealFeatureTypes;
    }

    @Override
    public CancelWaybill findWaybillCancelByWaybillCodeAndFeatureType(String waybillCode, Integer featureType) {
        return cancelWaybillMapper.findWaybillCancelByCodeAndFeatureType(waybillCode, featureType);
    }

    /**
     * 批量插入包裹号拦截记录
     *
     * @param cancelWaybill
     */
    @Override
    public boolean insertBatchPackageCodeLock(CancelWaybillDto cancelWaybill) {
        String waybillCode = cancelWaybill.getWaybillCode();
        //查询运单下的所有包裹记录
        List<DeliveryPackageD> deliveryPackageDS = waybillService.getPackageByWaybillCode(waybillCode);
        if (deliveryPackageDS == null || deliveryPackageDS.isEmpty()) {
            logger.error(MessageFormat.format("消费运单拦截消息，插入包裹拦截记录时，根据运单号：{0}获取所有包裹失败", waybillCode));
            return Boolean.FALSE;
        }
        //数量小于批量数量直接 插入
        if (deliveryPackageDS.size() <= CANCLE_PACKAGECODE_BATCH_INSERT_COUNT) {
            List<CancelWaybill> cancelWaybills = convertCancelWaybillList(deliveryPackageDS, cancelWaybill);
            cancelWaybillMapper.insertBatchWaybillCancel(cancelWaybills);
            return Boolean.TRUE;
        }
        for (int fromIndex = 0; fromIndex < deliveryPackageDS.size(); fromIndex = fromIndex + CANCLE_PACKAGECODE_BATCH_INSERT_COUNT) {
            Integer toIndex = fromIndex + CANCLE_PACKAGECODE_BATCH_INSERT_COUNT;
            //防止toIndex溢出
            if (toIndex > deliveryPackageDS.size()) {
                toIndex = deliveryPackageDS.size();
            }
            List<DeliveryPackageD> subPackages = deliveryPackageDS.subList(fromIndex, toIndex);
            List<CancelWaybill> cancelWaybills = convertCancelWaybillList(subPackages, cancelWaybill);
            cancelWaybillMapper.insertBatchWaybillCancel(cancelWaybills);
        }
        return Boolean.TRUE;
    }

    /**
     * 根据包裹列表 和cancelWaybill 生成List<CancelWaybill>
     *
     * @param deliveryPackageDs
     * @param cancelWaybill
     * @return
     */
    private List<CancelWaybill> convertCancelWaybillList(List<DeliveryPackageD> deliveryPackageDs, CancelWaybillDto cancelWaybill) {
        if (deliveryPackageDs == null || deliveryPackageDs.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<CancelWaybill> cancelWaybills = new ArrayList<CancelWaybill>(deliveryPackageDs.size());
        for (DeliveryPackageD deliveryPackage : deliveryPackageDs) {
            CancelWaybill cancelWaybillCopy = (CancelWaybill) ObjectHelper.copyBeans(cancelWaybill);
            cancelWaybillCopy.setPackageCode(deliveryPackage.getPackageBarcode());
            cancelWaybills.add(cancelWaybillCopy);
        }
        return cancelWaybills;
    }


    @Override
    @JProfiler(jKey = "DMSVER.CancelWaybillServiceImpl.checkWaybillBlock", mState = {JProEnum.TP})
    public BlockResponse checkWaybillBlock(String waybillCode, Integer featureType) {
        BlockResponse cancelResponse = new BlockResponse();
        //参数错误
        if (featureType == null) {
            cancelResponse.setMessage("入参的业务类型featureType不能为空");
            cancelResponse.setCode(ERROR_PARAM);
            logger.error(MessageFormat.format("按运单号{0}查询拦截,featureType为空", waybillCode));
            return cancelResponse;
        }
        //参数错误
        if (StringUtils.isBlank(waybillCode) || !WaybillUtil.isWaybillCode(waybillCode)) {
            cancelResponse.setMessage("运单号为空或格式非法");
            cancelResponse.setCode(ERROR_PARAM);
            logger.error(MessageFormat.format("按包裹号{0}查询拦截,waybillCode为空或格式非法", waybillCode));
            return cancelResponse;
        }
        CancelWaybill cancelWaybill = findWaybillCancelByWaybillCodeAndFeatureType(waybillCode, featureType);
        //无需拦截
        if (cancelWaybill == null) {
            cancelResponse.setMessage("没有拦截记录无需拦截");
            cancelResponse.setCode(NO_NEED_BLOCK);
            logger.info(MessageFormat.format("根据运单号：{0}未查到拦截记录", waybillCode));
            return cancelResponse;
        }
        //有拦截 锁定状态
        if (BUSINESS_TYPE_LOCK.equals(cancelWaybill.getBusinessType())) {
            //如果是包裹维度也需要拦截的业务类型
            if (FEATURE_TYPES_NEED_PACKAGE_DEAL.contains(featureType)) {
                logger.info(MessageFormat.format("运单{0}拦截未完成，有包裹未处理。", waybillCode));
                List<CancelWaybill> cancelWaybills = cancelWaybillMapper.findPackageCodesByFeatureTypeAndWaybillCode(
                        waybillCode, featureType, BUSINESS_TYPE_LOCK, BLOCK_PACKAGE_QUERY_NUMBER);
                List<String> packageCodes = getPackageCodes(cancelWaybills);
                Long PackageCount = cancelWaybillMapper.findPackageCodeCountByFeatureTypeAndWaybillCode(waybillCode,
                        featureType, BUSINESS_TYPE_LOCK);
                cancelResponse.setBlockPackageCount(PackageCount);
                cancelResponse.setBlockPackages(packageCodes);
            }
            cancelResponse.setMessage("该运单拦截待处理");
            cancelResponse.setCode(BLOCK);
            logger.info(MessageFormat.format("根据运单号：{0}查询到该包裹未拦截状态", waybillCode));
            return cancelResponse;
        }
        //有拦截 解锁状态
        cancelResponse.setMessage("该运单拦截已解除");
        cancelResponse.setCode(UNBLOCK);
        logger.info(MessageFormat.format("根据包裹号：{0}该包裹拦截已解除", waybillCode));
        return cancelResponse;
    }

    @Override
    @JProfiler(jKey = "DMSVER.CancelWaybillServiceImpl.checkPackageBlock", mState = {JProEnum.TP})
    public BlockResponse checkPackageBlock(String packageCode, Integer featureType) {
        BlockResponse cancelResponse = new BlockResponse();
        if (featureType == null) {
            cancelResponse.setMessage("入参的业务类型featureType不能为空");
            cancelResponse.setCode(ERROR_PARAM);
            logger.error(MessageFormat.format("按包裹号{0}查询拦截,featureType为空", packageCode));
            return cancelResponse;
        }
        if (StringUtils.isBlank(packageCode) || !WaybillUtil.isPackageCode(packageCode)) {
            cancelResponse.setMessage("包裹号为空或格式非法");
            cancelResponse.setCode(ERROR_PARAM);
            logger.error(MessageFormat.format("按包裹号{0}查询拦截,packageCode为空或格式非法", packageCode));
            return cancelResponse;
        }
        //根据包裹号查询拦截记录
        CancelWaybill cancelWaybill = cancelWaybillMapper.findPackageBlockedByCodeAndFeatureType(packageCode, featureType);
        if (cancelWaybill == null) {
            cancelResponse.setMessage("没有拦截记录无需拦截");
            cancelResponse.setCode(NO_NEED_BLOCK);
            logger.info(MessageFormat.format("根据包裹号：{0}未查到拦截记录", packageCode));
            return cancelResponse;
        }
        //锁定状态
        if (BUSINESS_TYPE_LOCK.equals(cancelWaybill.getBusinessType())) {
            cancelResponse.setMessage("该包裹拦截待处理");
            cancelResponse.setCode(BLOCK);
            logger.info(MessageFormat.format("根据包裹号：{0}该包裹为拦截状态", packageCode));
            return cancelResponse;
        }
        //解锁状态
        cancelResponse.setMessage("该包裹拦截已解除");
        cancelResponse.setCode(UNBLOCK);
        logger.info(MessageFormat.format("根据包裹号：{0}查询拦截状态，该包裹拦截已解除", packageCode));
        return cancelResponse;
    }

    private List<String> getPackageCodes(List<CancelWaybill> cancelWaybills) {
        if (cancelWaybills == null || cancelWaybills.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        List<String> packageCodes = new ArrayList<String>(cancelWaybills.size());
        for (CancelWaybill cancelWaybill : cancelWaybills) {
            packageCodes.add(cancelWaybill.getPackageCode());
        }
        return packageCodes;
    }

    @Override
    public List<CancelWaybill> getAllWaybillCancel(String waybillCode) {
        return cancelWaybillMapper.getAllWaybillCancel(waybillCode);
    }

    @Override
    public List<CancelWaybill> getAllWaybillCancel(String waybillCode, List<Integer> featureTypes) {
        return cancelWaybillMapper.getAllWaybillCancelWithFeatureTypes(waybillCode, featureTypes);
    }

    @Override
    public List<CancelWaybill> getAllWaybillCancelByCondition(CancelWaybill cancelWaybill) {
        return cancelWaybillMapper.getAllWaybillCancelByCondition(cancelWaybill);
    }


    @Override
    public Long findPackageCodeCountByFeatureTypeAndWaybillCode(String waybillCode, Integer featureType, String businessType) {
        return cancelWaybillMapper.findPackageCodeCountByFeatureTypeAndWaybillCode(waybillCode, featureType, businessType);
    }

    @Override
    public Integer updatWaybillbusinessType(CancelWaybill cancelWaybill) {
        return cancelWaybillMapper.updatWaybillbusinessType(cancelWaybill);
    }

    /**
     * 查询运单的拦截记录
     *
     * @param waybillCode
     * @param businessType
     * @return
     */
    @Override
    public List<CancelWaybill> findWaybillBlockByWaybillCode(String waybillCode, String businessType) {
        return cancelWaybillMapper.findWaybillBlockByWaybillCode(waybillCode, businessType);
    }

    @Override
    public JdResponse checkIfNeedBlock(String waybillCode, List<Integer> featureTypes) {
        JdResponse jdResponse = new JdResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        if (StringHelper.isEmpty(waybillCode) || null == featureTypes) {//校验参数
            logger.warn("取消拦截的参数为空，直接返回");
            return jdResponse;
        }

        List<CancelWaybill> cancelWaybills = getAllWaybillCancel(waybillCode, featureTypes);
        if (cancelWaybills == null || cancelWaybills.size() == 0) {//无拦截信息
            return jdResponse;
        }

        //进行拦截遍历查找
        for (CancelWaybill cancelWaybill : cancelWaybills) {
            Integer featureType = cancelWaybill.getFeatureType();
            String businessType = cancelWaybill.getBusinessType();
            if (FEATURE_TYPE_ORDER_MODIFY.equals(featureType)) {
                //featureType是6表示包裹面单补打拦截，查询该单号对应信息的businessType状态，若为1锁定状态则需要对对应所有包裹拦截
                if (BUSINESS_TYPE_LOCK.equals(cancelWaybill.getBusinessType())) {
                    jdResponse.setCode(SortingResponse.CODE_39135);
                    jdResponse.setMessage(SortingResponse.MESSAGE_39135);
                }
            } else if (CancelWaybill.FEATURE_TYPE_CANCELED.equals(featureType) //
                    || CancelWaybill.FEATURE_TYPE_DELETED.equals(featureType) //
                    || CancelWaybill.FEATURE_TYPE_LOCKED.equals(featureType)) {//
                // 获取运单号在订单中间件的订单信息 (OOM表示订单中间件)
                if (CancelWaybill.FEATURE_TYPE_CANCELED.equals(featureType)) {
                    jdResponse.setCode(SortingResponse.CODE_29302);
                    jdResponse.setMessage(SortingResponse.MESSAGE_29302);
                } else if (CancelWaybill.FEATURE_TYPE_DELETED.equals(featureType)) {
                    jdResponse.setCode(SortingResponse.CODE_29302);
                    jdResponse.setMessage(SortingResponse.MESSAGE_29302);
                } else if (CancelWaybill.FEATURE_TYPE_LOCKED.equals(featureType)) {
                    jdResponse.setCode(SortingResponse.CODE_29301);
                    jdResponse.setMessage(SortingResponse.MESSAGE_29301);
                }
            } else if (CancelWaybill.FEATURE_TYPE_REFUND100.equals(featureType)) {
                jdResponse.setCode(SortingResponse.CODE_29303);
                jdResponse.setMessage(SortingResponse.MESSAGE_29303);
            } else if (CancelWaybill.FEATURE_TYPE_INTERCEPT.equals(featureType)) {
                jdResponse.setCode(SortingResponse.CODE_29305);
                jdResponse.setMessage(SortingResponse.MESSAGE_29305);
            } else if (CancelWaybill.FEATURE_TYPE_INTERCEPT_BUSINESS.equals(featureType)) {
                jdResponse.setCode(SortingResponse.CODE_29306);
                jdResponse.setMessage(SortingResponse.MESSAGE_29306);
            } else if (CancelWaybill.FEATURE_TYPE_SICK.equals(featureType)) {
                jdResponse.setCode(SortingResponse.CODE_29307);
                jdResponse.setMessage(SortingResponse.MESSAGE_29307);
            } else if (CancelWaybill.FEATURE_TYPE_INTERCEPT_LP.equals(featureType)) {
                jdResponse.setCode(SortingResponse.CODE_29308);
                jdResponse.setMessage(SortingResponse.MESSAGE_29308);
            } else if (CancelWaybill.FEATURE_TYPE_B_TRANSPORT_C.equals(featureType)) {
                if (cancelWaybill.BUSINESS_TYPE_LOCK.equals(businessType)) {
                    jdResponse.setCode(SortingResponse.CODE_B_TO_C_29311);
                    jdResponse.setMessage(SortingResponse.MESSAGE_B_TO_C_29311);
                }
            } else if (CancelWaybill.FEATURE_TYPE_C_TRANSPORT_B.equals(featureType)) {
                if (cancelWaybill.BUSINESS_TYPE_LOCK.equals(businessType)) {
                    jdResponse.setCode(SortingResponse.CODE_C_TO_B_29312);
                    jdResponse.setMessage(SortingResponse.MESSAGE_C_TO_B_29312);
                }
            } else {
                jdResponse.setCode(SortingResponse.CODE_29300);
                jdResponse.setMessage(SortingResponse.MESSAGE_29300);
            }
        }
        return jdResponse;
    }

    /**
     * 根据运单号查询指定拦截类型的拦截信息
     * @param waybillCode
     * @param businessType 拦截状态
     * @param featureTypes 拦截业务类型
     * @param interceptTypes 拦截的细分类型
     * @param interceptMode 拦截模式 强拦还是提示
     * @return
     */
    @Override
    public List<CancelWaybill> findWaybillBlockByFeatureTypeAndInterceptType(String waybillCode,
                                                                             String businessType,
                                                                             List<Integer> featureTypes,
                                                                             List<Integer> interceptTypes,
                                                                             Integer interceptMode){
        return cancelWaybillMapper.findWaybillBlockByFeatureTypeAndInterceptType(waybillCode,
                businessType, featureTypes, interceptTypes, interceptMode);
    }


    /**
     * 根据 feature_type 查询 无包裹拦截的运单拦截记录
     * @param waybillCode
     * @param businessType
     * @param featureTypes
     * @param packageCode
     * @return
     */
    @Override
    public List<CancelWaybill> selectWaybillBlockByFeatureTypesAndNoPackageCode(String waybillCode, String businessType,
                                                                                List<Integer> featureTypes,
                                                                                String packageCode){
        return cancelWaybillMapper.selectWaybillBlockByFeatureTypesAndNoPackageCode(waybillCode, businessType,
                featureTypes, packageCode);
    }

    /**
     * 根据运单号 和FeatureTypes 把包裹号的拦截设置为无效
     * @return
     */
    @Override
    public Integer updateInvalidPackageCodeBlockByWaybillCodeAndFeatureTypes(String waybillCode,
                                                                             String businessType,
                                                                             List<Integer> featureTypes){
        return cancelWaybillMapper.updateInvalidPackageCodeBlockByWaybillCodeAndFeatureTypes(waybillCode, businessType,
                featureTypes);
    }


    /**
     * 如果按包裹维度拦截的业务类型，改包裹号拦截记录不存在，则重置包裹拦截记录
     * @param waybillCode
     * @param packageCode
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPackageBlockIfPakcageCodeNotExists(String waybillCode, String packageCode) {
        //按运单打印的不用重置
        if(StringUtils.isBlank(packageCode)){
            return ;
        }
        //查询 需要按包裹拦截的featureType 无此包裹拦截记录的运单拦截记录
        List<CancelWaybill> packageCodeWronglist = selectWaybillBlockByFeatureTypesAndNoPackageCode(
                waybillCode, BUSINESS_TYPE_LOCK, FEATURE_TYPES_NEED_PACKAGE_DEAL, packageCode);
        //未查到，无需继续处理
        if(CollectionUtils.isEmpty(packageCodeWronglist)){
            return;
        }
        List<Integer> featureTypes = getPackageWrongFeatureType(packageCodeWronglist);
        //把包裹拦截记录设置为无效
        Integer count = updateInvalidPackageCodeBlockByWaybillCodeAndFeatureTypes(waybillCode, BUSINESS_TYPE_LOCK,
                featureTypes);
        logger.info(MessageFormat.format("根据运单号:{0},重置包裹拦截记录共{1}条", waybillCode, count));
        //重新拉取包裹记录
        for(CancelWaybill cancelWaybill : packageCodeWronglist){
            Integer featureType = cancelWaybill.getFeatureType();
            String redisKey = CANCEL_WAYBILL_PACKAGE_CODE_RESET_REDIS_KEY_PREFIX + waybillCode + "_"
                    + cancelWaybill.getFeatureType();
            //防止大包裹并发补打 情况
            if(redisStringUtil.existsKey(redisKey)){
                logger.warn(MessageFormat.format("运单：{0},featureType:{1}的包裹拦截记录已经重置", waybillCode, featureType));
                continue;
            }

            redisStringUtil.cacheDataEx(redisKey, packageCode, CANCEL_WAYBILL_PACKAGE_CODE_RESET_REDIS_EXPIRE_TIME);
            CancelWaybillDto cancelWaybillDto = initCancelWaybillDto(cancelWaybill);
            insertBatchPackageCodeLock(cancelWaybillDto);
            logger.info(MessageFormat.format("根据运单号:{0},业务类型{1}更新包裹拦截记录成功", waybillCode, cancelWaybill.getFeatureType()));
        }
    }

    /**
     * 包裹号有问题的FEATURE_TYPES
     * @param packageCodeWronglist
     * @return
     */
    private List<Integer> getPackageWrongFeatureType(List<CancelWaybill> packageCodeWronglist){
        List<Integer> featureTypes = new ArrayList<Integer>(packageCodeWronglist.size());
        for(CancelWaybill cancelWaybill : packageCodeWronglist){
            featureTypes.add(cancelWaybill.getFeatureType());
        }
        return featureTypes;
    }

    private CancelWaybillDto initCancelWaybillDto(CancelWaybill cancelWaybill){
        CancelWaybillDto cancelWaybillDto = new CancelWaybillDto();
        cancelWaybillDto.setWaybillCode(cancelWaybill.getWaybillCode());//单号
        cancelWaybillDto.setCreateTime(cancelWaybill.getCreateTime());//创建时间
        cancelWaybillDto.setUpdateTime(cancelWaybill.getUpdateTime());//更新时间
        cancelWaybillDto.setOperateTime(cancelWaybill.getOperateTime());//操作时间
        cancelWaybillDto.setOperateTimeOrder(cancelWaybill.getOperateTimeOrder());//操作时间时间戳
        cancelWaybillDto.setTs(System.currentTimeMillis());//时间戳
        cancelWaybillDto.setBusinessType(BUSINESS_TYPE_LOCK);//锁定状态
        cancelWaybillDto.setFeatureType(cancelWaybill.getFeatureType());//修改订单地址拦截类型
        cancelWaybillDto.setYn(Integer.valueOf(YNEnum.Y.getCode()));
        return cancelWaybillDto;
    }
}
