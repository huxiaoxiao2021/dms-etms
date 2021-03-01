package com.jd.bluedragon.distribution.reflowPackage.service.impl;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.reflowPackage.dao.ReflowPackageDao;
import com.jd.bluedragon.distribution.reflowPackage.doman.ReflowPackage;
import com.jd.bluedragon.distribution.reflowPackage.request.ReflowPackageQuery;
import com.jd.bluedragon.distribution.reflowPackage.response.ReflowPackageVo;
import com.jd.bluedragon.distribution.reflowPackage.service.ReflowPackageService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.ql.basic.domain.BaseDmsStore;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("reflowPackageService")
public class ReflowPackageImpl implements ReflowPackageService {
    private static final Logger log = LoggerFactory.getLogger(ReflowPackageImpl.class);

    @Autowired
    @Qualifier("reflowPackageDao")
    private ReflowPackageDao reflowPackageDao;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    private BaseMinorManager baseMinorManager;

    /**
     * 新增
     * @param mode
     * @return
     */
    @Override
    public int add(ReflowPackage mode){
        return reflowPackageDao.add(mode);
    }

    /**
     * 更新
     * @param mode
     * @return
     */
    @Override
    public int update(ReflowPackage mode){
        return reflowPackageDao.update(mode);
    }

    /**
     * 判断数据是否存在
     * @param mode
     * @return
     */
    @Override
    public boolean isExist(ReflowPackage mode){
        return reflowPackageDao.getCount(mode)>0;
    }

    /**
     * 包裹回流扫描提交
     * @param mode
     * @return
     */
    @Override
    public JdCResponse<Boolean> reflowPackageSubmit(ReflowPackage mode){
        JdCResponse<Boolean> result = new JdCResponse<>(JdCResponse.CODE_SUCCESS, JdCResponse.MESSAGE_SUCCESS);
        String waybillCode = WaybillUtil.getWaybillCodeByPackCode(mode.getPackageCode());
        Waybill waybill=this.waybillCommonService.findByWaybillCode(waybillCode);
        if(null==waybill){
            log.warn("获取不到运单信息：{}",waybillCode);
            result.toFail("包裹回流上报失败，没有找到该包裹运单信息");
            return result;
        }

        JdResult<CrossPackageTagNew> tag=getCrossAndCageCarByWaybill(mode.getSiteCode(),waybill);
        if(tag.isSucceed() && null!=tag.getData()) {
            mode.setChuteCode(tag.getData().getDestinationCrossCode());
            mode.setCageCarCode(tag.getData().getDestinationTabletrolleyCode());
        }

        int res=-1;
        if(isExist(mode)){
            res=update(mode);
        } else {
            res=add(mode);
        }

        if(res<0){
            result.toFail("包裹回流上报失败，数据落库异常");
        }

        return result;
    }

    /**
     * 根据运单始发地和运单信息获取运单目的地滑道号、目的地笼车号
     * @param originalDmsId 运单始发地
     * @param waybill 运单信息
     * @return destinationCrossCode：目的滑道；destinationTabletrolleyCode：目的笼车
     */
    public JdResult<CrossPackageTagNew> getCrossAndCageCarByWaybill(Integer originalDmsId,Waybill waybill){
        Integer receiveSiteCode=waybill.getSiteCode();
        //获取始发道口类型
        Integer originalCrossType = BusinessUtil.getOriginalCrossType(waybill.getWaybillSign(), waybill.getSendPay());

        BaseDmsStore baseDmsStore = new BaseDmsStore();
        JdResult<CrossPackageTagNew> jdResult = baseMinorManager.queryCrossPackageTagForPrint(baseDmsStore,receiveSiteCode,originalDmsId,originalCrossType);
        if(!jdResult.isSucceed()) {
            log.warn("获取运单滑道号笼车号信息失败:{}，运单号:{}", jdResult.getMessage(),waybill.getWaybillCode());
        }

        return jdResult;
    }

    /**
     * 根据对象查询符合条件的数据
     * @param mode
     * @return
     */
    @Override
    public List<ReflowPackage> getDataByBean(ReflowPackage mode){
        return reflowPackageDao.getDataByBean(mode);
    }

    /**
     * 按条件统计查询条数
     *
     * @param query 查询参数
     * @return 结果总条数
     * @author fanggang7
     * @time 2021-02-28 19:14:53 周日
     */
    @Override
    public Response<Long> selectCount(ReflowPackageQuery query) {
        log.info("ReflowPackageImpl.selectCount");
        Response<Long> result = new Response<>();
        result.toSucceed();
        try {
            long total = reflowPackageDao.selectCount(query);
            result.setData(total);
        } catch (Exception e) {
            log.error("ReflowPackageImpl.selectCount exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        return result;
    }

    /**
     * 按条件查询列表
     *
     * @param query 查询参数
     * @return 查询结果集合
     * @author fanggang7
     * @time 2021-02-28 19:14:53 周日
     */
    @Override
    public Response<List<ReflowPackage>> selectList(ReflowPackageQuery query) {
        log.info("ReflowPackageImpl.selectList");
        Response<List<ReflowPackage>> result = new Response<>();
        result.toSucceed();
        try {
            List<ReflowPackage> dataList = reflowPackageDao.selectList(query);
            result.setData(dataList);
        } catch (Exception e) {
            log.error("ReflowPackageImpl.selectList exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        return result;
    }

    /**
     * 按条件查询分页数据
     *
     * @param query 查询参数
     * @return 查询结果集合
     * @author fanggang7
     * @time 2021-02-28 19:14:53 周日
     */
    @Override
    public Response<PageDto<ReflowPackageVo>> selectPageList(ReflowPackageQuery query) {
        log.info("ReflowPackageImpl.selectPageList");
        Response<PageDto<ReflowPackageVo>> result = new Response<>();
        result.toSucceed();
        PageDto<ReflowPackageVo> pageDto = new PageDto<>(query.getPageNumber(), query.getPageSize());
        List<ReflowPackageVo> dataList = new ArrayList<>();
        try {
            Response<Void> checkResult = this.checkPram4SelectPageList(query);
            if(!checkResult.isSucceed()){
                result.toError(checkResult.getMessage());
                return result;
            }
            long total = reflowPackageDao.selectCount(query);
            pageDto.setTotalRow(new Long(total).intValue());
            if (total > 0) {
                List<ReflowPackage> rawDataList = reflowPackageDao.selectList(query);
                for (ReflowPackage reflowPackage : rawDataList) {
                    ReflowPackageVo vo = new ReflowPackageVo();
                    BeanUtils.copyProperties(reflowPackage, vo);
                    vo.setCreateTimeFormative(DateUtil.formatDateTime(reflowPackage.getCreateTime()));
                    dataList.add(vo);
                }
            }
            pageDto.setResult(dataList);
        } catch (Exception e) {
            log.error("ReflowPackageImpl.selectPageList exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        result.setData(pageDto);
        return result;
    }

    private Response<Void> checkPram4SelectPageList(ReflowPackageQuery query){
        Response<Void> result = new Response<>();
        result.toSucceed();
        if(query.getPageNumber() <= 0){
            result.toError("参数错误，pageNumber必须大于0");
            return result;
        }
        if(query.getPageSize() == null){
            result.toError("参数错误，pageSize不能为空");
            return result;
        }
        return result;

    }
}
