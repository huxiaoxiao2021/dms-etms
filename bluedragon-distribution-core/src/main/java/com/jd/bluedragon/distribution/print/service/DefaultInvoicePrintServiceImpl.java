package com.jd.bluedragon.distribution.print.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.print.domain.InvoiceParameters;
import com.jd.invoice.domain.InvoiceTemplate;
import com.jd.invoice.service.jsf.PrintTableJsfService;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;

/**
 * 调用财务生成发票
 * Created by wangtingwei on 2016/4/8.
 */
@Service("invoicePrintService")
public class DefaultInvoicePrintServiceImpl implements InvoicePrintService {

    private static final Logger log = LoggerFactory.getLogger(DefaultInvoicePrintServiceImpl.class);

    private static final String INVOICE_SUCCESS=String.valueOf(1);

    @Qualifier("invoiceJsfService")
    @Autowired
    private PrintTableJsfService invoiceJsfService;

    @Override
    public InvokeResult<String> generateInvoice(InvoiceParameters parameters) {
        CallerInfo info = Profiler.registerInfo("DMSWEB.jsf.invoiceJsfService.generateInvoice", Constants.UMP_APP_NAME_DMSWEB,false, true);
        InvokeResult<String> result=new InvokeResult<String>();
        try {
            InvoiceTemplate templte= invoiceJsfService.getInvoiceTemplateAndPrintNew(parameters.getOrderId(),parameters.getCky2(),parameters.getStoreId(), parameters.getErpAccount(),parameters.getRealName());
            if(log.isInfoEnabled()){
                log.info("发票结果:{}",JsonHelper.toJson(templte));
            }
            if(INVOICE_SUCCESS.equals(templte.getResultCode())){
                result.success();
                result.setData(templte.getHtmlTemplate());
            }else{
                result.customMessage(InvokeResult.RESULT_NULL_CODE,templte.getResultMessage());
            }
        }catch (Throwable e){
            result.error(e);
            log.error("生成发票",e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return result;
    }
}
