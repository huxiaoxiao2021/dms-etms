package com.jd.bluedragon.distribution.goodsPrint;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.base.controller.DmsBaseController;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.goodsPrint.service.GoodsPrintService;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jim.cli.Cluster;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.report.domain.GoodsPrintDto;
import com.jd.uim.annotation.Authorization;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;

/**
 * @author tangchunqing
 * @Description: 托寄物品名打印
 * @date 2018年11月16日 13时:58分
 */
@Controller
@RequestMapping("goodsPrint")
public class GoodsPrintController extends DmsBaseController {
    private static final Logger log = LoggerFactory.getLogger(GoodsPrintController.class);

    @Autowired
    GoodsPrintService goodsPrintService;

    @Autowired
    @Qualifier("redisClientCache")
    private Cluster redisClientCache;

    private static final String CONCURRENCY_LIMT_KEY  = "GOODS_PRINT_CONCURRENCY_LIMT_KEY";

    @Value("${goodsPrintConcurrencyLimt:2}")
    private Integer concurrencyLimt;

    @Autowired
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

    /**
     * 返回主页面
     *
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_GOODSPRINT_R)
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
        return "/goodsPrint/goodsPrintList";
    }

    /**
     * 根据条件分页查询数据信息
     *
     * @param goodsPrint
     * @return
     */
    @Authorization(Constants.DMS_WEB_SORTING_GOODSPRINT_R)
    @RequestMapping(value = "/listData")
    public @ResponseBody JdResponse<List<GoodsPrintDto>> listData(@RequestBody GoodsPrintDto goodsPrint) {
        try {
            Long limit = redisClientCache.incr(CONCURRENCY_LIMT_KEY.getBytes());
            JdResponse<List<GoodsPrintDto>> jdResponse = new JdResponse();
            if(limit.intValue() > concurrencyLimt){
                jdResponse.setCode(3001);
                jdResponse.setMessage("点击太频繁，请稍后再说！");
                return jdResponse;
            }
            LoginUser loginUser = getLoginUser();
            log.warn("托寄物打印查询erp[{}]goodsPrint[{}]",loginUser.getUserErp(), JsonHelper.toJson(goodsPrint));
            return goodsPrintService.query(goodsPrint);
        }catch (Exception e){
            JdResponse jdResponse=new JdResponse();
            jdResponse.setCode(500);
            jdResponse.setMessage("调用服务失败");
            log.error("goodsPrint.listData调用服务失败",e);
            return jdResponse;
        }finally {
            redisClientCache.decr(CONCURRENCY_LIMT_KEY.getBytes());
        }
    }

    @Authorization(Constants.DMS_WEB_SORTING_GOODSPRINT_R)
    @RequestMapping(value = "/toExport")
    @JProfiler(jKey = "com.jd.bluedragon.distribution.goodsPrint.GoodsPrintController.toExport", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
    @ResponseBody
    public InvokeResult toExport(GoodsPrintDto goodsPrint, HttpServletResponse response) {
        InvokeResult result = new InvokeResult();
        BufferedWriter bfw = null;
        try {
            Long limit = redisClientCache.incr(CONCURRENCY_LIMT_KEY.getBytes());
            if(limit.intValue() > concurrencyLimt){
                result.customMessage(InvokeResult.SERVER_ERROR_CODE,"点击太频繁，请稍后再试！");
                return result;
            }
            LoginUser loginUser = getLoginUser();
            log.warn("托寄物打印导出erp[{}]goodsPrint[{}]",loginUser.getUserErp(), JsonHelper.toJson(goodsPrint));
            exportConcurrencyLimitService.incrKey(ExportConcurrencyLimitEnum.GOODS_PRINT_REPORT.getCode());
            String fileName = "托寄物品名";
            //设置文件后缀
            String fn = fileName.concat(DateHelper.formatDate(new Date(),DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS) + ".csv");
            bfw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "GBK"));
            //设置响应
            CsvExporterUtils.setResponseHeader(response, fn);

            goodsPrintService.export(goodsPrint,bfw);
            exportConcurrencyLimitService.decrKey(ExportConcurrencyLimitEnum.GOODS_PRINT_REPORT.getCode());
        } catch (Exception e) {
            log.error("托寄物品名--toExport error:", e);
            result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE);
            return result;
        }finally {
                try {
                    if (bfw != null) {
                        bfw.flush();
                        bfw.close();
                    }
                } catch (IOException e) {
                    log.error("托寄物品名export-error", e);
                    result.customMessage(InvokeResult.SERVER_ERROR_CODE,InvokeResult.RESULT_EXPORT_MESSAGE+"流关闭异常");
                }
            redisClientCache.decr(CONCURRENCY_LIMT_KEY.getBytes());
        }
        return result;
    }
}
