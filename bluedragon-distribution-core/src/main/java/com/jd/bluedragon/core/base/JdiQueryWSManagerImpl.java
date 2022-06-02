package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.tms.jdi.dto.CommonDto;
import com.jd.tms.jdi.dto.PageDto;
import com.jd.tms.jdi.dto.TransWorkBillDto;
import com.jd.tms.jdi.dto.TransWorkItemDto;
import com.jd.tms.jdi.ws.JdiQueryWS;
import com.jd.tms.jdi.ws.JdiTransWorkWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;


/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/10/8 2:18 下午
 */
@Service("jdiQueryWSManager")
public class JdiQueryWSManagerImpl implements JdiQueryWSManager {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JdiQueryWS jdiQueryWS;

    @Qualifier("jdiTransWorkWS")
    @Autowired
    private JdiTransWorkWS jdiTransWorkWS;

    @Override
    public CommonDto<TransWorkItemDto> queryTransWorkItemBySimpleCode(String simpleCode) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.JdiQueryWSManager.queryTransWorkItemBySimpleCode",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            if(StringUtils.isEmpty(simpleCode)){
                return null;
            }
            return jdiQueryWS.queryTransWorkItemBySimpleCode(simpleCode);
        }catch (Exception e){
            logger.warn("根据派车任务明细简码:{}获取派车任务明细异常!", simpleCode);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }

    /**
     * 根据派车明细编码获取派车任务明细
     *
     * @param var1
     * @return
     */
    @Override
    public TransWorkItemDto getTransWorkItemsDtoByItemCode(String itemCode) {
        CommonDto<List<TransWorkItemDto>> resp = null;
        try {
            if(StringUtils.isEmpty(itemCode)){
                return null;
            }
            resp = jdiQueryWS.getTransWorkItemsDtoByItemCode(itemCode);
            if(resp.isSuccess() && !CollectionUtils.isEmpty(resp.getData())){
                for(TransWorkItemDto dto : resp.getData()){
                    if(itemCode.equals(dto.getTransWorkItemCode())){
                        return dto;
                    }
                }
            }
            return null;
        }catch (Exception e){
            logger.error("getTransWorkItemsDtoByItemCode:{}获取派车任务明细异常!",itemCode,e.getMessage(),e);
        }finally {
            logger.info("JdiQueryWS.getTransWorkItemsDtoByItemCode,req:{},resp:{}",itemCode, JsonHelper.toJson(resp));
        }
        return null;
    }

    @Override
    public List<TransWorkBillDto> queryTransWorkBillByPage(TransWorkBillDto transWorkBillDto, PageDto<TransWorkBillDto> pageDto) {
        if (transWorkBillDto == null || pageDto == null) {
            return null;
        }

        CallerInfo callerInfo = ProfilerHelper.registerInfo("dmsWeb.jsf.JdiQueryWSManager.queryTransWorkBillByPage");
        try {
            CommonDto<PageDto<TransWorkBillDto>> commonDto = jdiTransWorkWS.queryPageByCondition(transWorkBillDto, pageDto);
            if (commonDto.getCode() != CommonDto.CODE_SUCCESS) {
                logger.warn("查询运输派车单失败. {}, {}, resp:{}",
                        JsonHelper.toJson(transWorkBillDto), JsonHelper.toJson(pageDto), JsonHelper.toJson(commonDto));
                return null;
            }
            if (null == commonDto.getData() || CollectionUtils.isEmpty(commonDto.getData().getResult())) {
                logger.warn("调用运输接口获取派车单成功，但是数据为空. {}, {}, resp:{}",
                        JsonHelper.toJson(transWorkBillDto), JsonHelper.toJson(pageDto), JsonHelper.toJson(commonDto));
                return null;
            }

            return commonDto.getData().getResult();
        }
        catch (Exception ex) {
            logger.warn("查询运输派车单异常! {}, {}", JsonHelper.toJson(transWorkBillDto), JsonHelper.toJson(pageDto), ex);
            Profiler.functionError(callerInfo);
        }
        finally {
            Profiler.registerInfoEnd(callerInfo);
        }

        return null;
    }
}
