package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.distribution.reverse.dao.ReverseStockInDetailDao;
import com.jd.bluedragon.distribution.reverse.domain.ReverseStockInDetail;
import com.jd.bluedragon.distribution.reverse.domain.ReverseStockInDetailStatusEnum;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.coo.sa.sequence.JimdbSequenceGen;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @program: bluedragon-distribution
 * @description: 逆向入库明细业务逻辑
 * @author: liuduo8
 * @create: 2019-12-19 17:00
 **/
@Service("reverseStockInDetailService")
public class ReverseStockInDetailServiceImpl extends BaseService<ReverseStockInDetail> implements ReverseStockInDetailService {

    private final Logger log = LoggerFactory.getLogger(ReverseStockInDetailServiceImpl.class);

    @Autowired
    @Qualifier("reverseStockInDetailDao")
    private ReverseStockInDetailDao reverseStockInDetailDao;

    @Autowired
    @Qualifier("redisSequenceGen")
    private JimdbSequenceGen redisSequenceGen;

    //外部单号默认生产数量
    private int DEFAULT_SIZE = 1;
    //外部单号前缀格式
    private String CODE_FORMAT = "%s-%s";
    //外部单号前缀日期格式
    private String PREFIX_DATE_FORMAT = "yyMMdd";
    //批量插入每批默认大小
    private int DEFAULT_BATCH_INIT_SIZE = 100;

    /**
     * 获取数据操作实体类
     *
     * @return
     */
    @Override
    public Dao<ReverseStockInDetail> getDao() {
        return reverseStockInDetailDao;
    }

    /**
     * 初始化入库单明细数据
     *
     * 初始化状态
     * 初始化外部单号 外部传入已外部为准
     * @param reverseStockInDetail
     * @return
     */
    @Override
    public boolean initReverseStockInDetail(ReverseStockInDetail reverseStockInDetail) {
        //初始化外部单号 外部传入已外部为准
        if(StringUtils.isBlank(reverseStockInDetail.getExternalCode())){
            reverseStockInDetail.setExternalCode(genOneExternalCode(String.valueOf(reverseStockInDetail.getBusiType())));
        }
        reverseStockInDetail.setStatus(ReverseStockInDetailStatusEnum.SEND.getCode());

        return saveOrUpdate(reverseStockInDetail);
    }

    /**
     * 初始化入库单明细数据
     *
     * 初始化状态
     * 初始化外部单号 外部传入已外部为准
     * @param reverseStockInDetails
     * @return
     */
    @Override
    @Transactional(value = "main_undiv", propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public boolean initBatchReverseStockInDetail(List<ReverseStockInDetail> reverseStockInDetails) {
        if(reverseStockInDetails.isEmpty()){
            return true;
        }
        List<ReverseStockInDetail> reverseStockInDetailsBuffer = new ArrayList<>(DEFAULT_BATCH_INIT_SIZE);
        //初始化外部单号 外部传入已外部为准
        //必须保证生产的外部单号足够用
        List<String> externalCodes = genBatchExternalCode(String.valueOf(reverseStockInDetails.get(0).getBusiType()),reverseStockInDetails.size());
        Iterator<String> externalCodesIterator = externalCodes.iterator();
        for(ReverseStockInDetail reverseStockInDetail1 : reverseStockInDetails){
            if(StringUtils.isBlank(reverseStockInDetail1.getExternalCode())){
                reverseStockInDetail1.setExternalCode(externalCodesIterator.next());
            }
            reverseStockInDetailsBuffer.add(reverseStockInDetail1);
            //分批批量插入
            if(reverseStockInDetailsBuffer.size() == DEFAULT_BATCH_INIT_SIZE){
                if(!batchAdd(reverseStockInDetailsBuffer)){
                   throw new RuntimeException("初始化入库单明细数据失败,批量插入数据库失败");
                }
                reverseStockInDetailsBuffer.clear();
            }
        }
        if(!reverseStockInDetailsBuffer.isEmpty()){
            if(!batchAdd(reverseStockInDetailsBuffer)){
                throw new RuntimeException("初始化入库单明细数据失败,批量插入数据库失败");
            }
        }
        return true;
    }

    /**
     * 生产外部单号1条
     *
     * 默认redis生成器生产，如果异常则返回UUID
     *
     * @param key 生产KEY - 此处采用外部单号类型
     * @return
     */
    @Override
    public String genOneExternalCode(String key){
        String prefixStr = String.format(CODE_FORMAT, key, DateHelper.formatDate(new Date(),PREFIX_DATE_FORMAT));
        try{
            long[] codes = redisSequenceGen.batchedGen(prefixStr,DEFAULT_SIZE);
            return String.format(CODE_FORMAT, prefixStr,String.valueOf(codes[0]));
        }catch (Exception e){
            log.error("生产外部单号异常,入参{},原因{}",key,e.getMessage(),e);
            return String.format(CODE_FORMAT, prefixStr,UUID.randomUUID().toString());
        }
    }

    /**
     * 批量生产外部单号
     * 理论上没有最大限制，请使用者自行评估
     *
     * 默认redis生成器生产，如果异常则返回UUID
     *
     * @param key 生产KEY - 此处采用外部单号类型
     * @return
     */
    @Override
    public List<String> genBatchExternalCode(String key, int count){
        //提前分配空间，减少不必要的浪费
        List<String> result = new ArrayList<>(count);
        //前缀
        String prefixStr = String.format(CODE_FORMAT, key, DateHelper.formatDate(new Date(),PREFIX_DATE_FORMAT));
        try{
            long[] codes = redisSequenceGen.batchedGen(prefixStr,DEFAULT_SIZE);
            if(count != codes.length){
                throw new RuntimeException("redis模式生产外部单号异常，生产数量与预期不符");
            }
            for(long code : codes){
                result.add(String.format(CODE_FORMAT, prefixStr,String.valueOf(code)));
            }

        }catch (Exception e){
            log.error("生产外部单号异常,入参{},原因{}",key,e.getMessage(),e);
            //清空数据 重试按UUID生产
            result.clear();
            for(int i = 0 ; i < count ; i++){
                result.add(String.format(CODE_FORMAT, prefixStr,UUID.randomUUID().toString()));
            }
        }
        return result;
    }

    /**
     * 更新入库明细状态
     * 更新条件不支持状态，如果有需求请自行增加
     * 条件中 运单号或者批次必须存在其一
     * @param reverseStockInDetail
     * @param reverseStockInDetailStatusEnum
     * @return
     */
    @Override
    public boolean updateStatus(ReverseStockInDetail reverseStockInDetail, ReverseStockInDetailStatusEnum reverseStockInDetailStatusEnum) {

        if(StringUtils.isBlank(reverseStockInDetail.getWaybillCode()) && StringUtils.isBlank(reverseStockInDetail.getSendCode())){
            log.error("更新入库明细状态失败，入参不正确,{}", JsonHelper.toJson(reverseStockInDetail));
            return false;
        }

        reverseStockInDetail.setStatus(reverseStockInDetailStatusEnum.getCode());

        return reverseStockInDetailDao.updateStatus(reverseStockInDetail);
    }

    /**
     * 根据运单号和类型获取数据
     *
     * @param reverseStockInDetail
     * @return
     */
    @Override
    public List<ReverseStockInDetail> findByWaybillCodeAndType(ReverseStockInDetail reverseStockInDetail) {

        return reverseStockInDetailDao.findByWaybillCodeAndType(reverseStockInDetail);
    }


    public static void main(String[] args) {
        System.out.println(new ReverseStockInDetailServiceImpl().genBatchExternalCode("12",12).size());
    }

}
