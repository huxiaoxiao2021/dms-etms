package com.jd.bluedragon.distribution.signAndReturn.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.signAndReturn.dao.SignReturnDao;
import com.jd.bluedragon.distribution.signAndReturn.domain.MergedWaybill;
import com.jd.bluedragon.distribution.signAndReturn.domain.SignReturnPrintM;
import com.jd.bluedragon.distribution.signAndReturn.service.MergedWaybillService;
import com.jd.bluedragon.distribution.signAndReturn.service.SignReturnService;
import com.jd.bluedragon.distribution.signReturn.SignReturnCondition;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.ExportExcelDownFee;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: SignReturnServiceImpl
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2018/11/27 18:21
 */
@Service("signReturnService")
public class SignReturnServiceImpl implements SignReturnService {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private SignReturnDao signReturnDao;

    @Autowired
    private MergedWaybillService mergedWaybillService;

    //excel表格sheet和表头信息
    private static final String sheettitle = "签单返回合单主表";
    private static final String[] headers = new String[] {"签单返回合单运单号","商家编码","商家名称","返单周期","合单操作日期","合单操作机构","合单操作人","合单运单数"};
    private static final String sheettitleDetail = "合单的运单号明细";
    private static final String[] headersDetail = new String[] {"运单号","妥投时间"};

    /**
     * 导出
     * @param result
     * @param response
     */
    @Override
    public void toExport(PagerResult<SignReturnPrintM> result, HttpServletResponse response) {

        try {
            //获取数据
            List<SignReturnPrintM> list = result.getRows();
            List<MergedWaybill> mergedWaybillList = Collections.emptyList();
            if(list != null && list.size() > 0){
                mergedWaybillList = result.getRows().get(0).getMergedWaybillList();
            }

            HSSFWorkbook workbook = new HSSFWorkbook();
            OutputStream out = response.getOutputStream();
            //接下来循环list放到Excel表中
            //文件标题
            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String nowdate = formatter1.format(new Date());
            String title = null;
            title = "签单返回合单打印交接单-" + nowdate + ".xls";
            //第一个sheet页签单返回交接单信息
            List<Object[]> dataList = new ArrayList<Object[]>();
            if(list != null){
                Object[] objs = null;
                for(SignReturnPrintM signReturnPrintM : list){
                    objs = new Object[headers.length];
                    objs[0] = signReturnPrintM.getNewWaybillCode();
                    objs[1] = signReturnPrintM.getBusiId();
                    objs[2] = signReturnPrintM.getBusiName();
                    objs[3] = signReturnPrintM.getReturnCycle();
                    objs[4] = DateHelper.formatDate(signReturnPrintM.getOperateTime());
                    objs[5] = signReturnPrintM.getCreateSiteName();
                    objs[6] = signReturnPrintM.getOperateUser();
                    objs[7] = signReturnPrintM.getMergeCount();
                    dataList.add(objs);
                }
            }
            //第二个sheet页签单返回交接单运单明细
            List<Object[]> dataListDetail = new ArrayList<Object[]>();
            Object[] objs1 = null;
            for(MergedWaybill mergedWaybill : mergedWaybillList){
                objs1 = new Object[headersDetail.length];
                objs1[0] = mergedWaybill.getWaybillCode();
                objs1[1] = DateHelper.formatDate(mergedWaybill.getDeliveredTime(), Constants.DATE_TIME_FORMAT);
                dataListDetail.add(objs1);
            }

            //使用流将数据导出
            //防止中文乱码
            String headStr = "attachment; filename=\"" + new String( title.getBytes("gb2312"), "ISO8859-1" ) + "\"";
            response.setContentType("octets/stream");
            response.setContentType("APPLICATION/OCTET-STREAM");
            response.setHeader("Content-Disposition", headStr);
            ExportExcelDownFee ex ;
            ex = new ExportExcelDownFee(sheettitle, headers, dataList);
            ex.export(workbook,out);

            ex = new ExportExcelDownFee(sheettitleDetail, headersDetail, dataListDetail);
            ex.export(workbook,out);

            workbook.write(out);
            out.close();
        } catch (Exception e) {
            this.logger.error("根据运单号导出excel失败!");
        }

    }

    /**
     * 根据运单号获得签单返回打印交接单信息
     * @param condition
     * @return
     */
    @Override
    public PagerResult<SignReturnPrintM> getListByWaybillCode(SignReturnCondition condition) {

        PagerResult<SignReturnPrintM> result = new PagerResult<SignReturnPrintM>();

        List<SignReturnPrintM> signReturnPrintMList = signReturnDao.getListByWaybillCode(condition);
        for(SignReturnPrintM signReturnPrintM : signReturnPrintMList){
            List<MergedWaybill> mergedWaybills = mergedWaybillService.getListBySignReturnPrintMId(signReturnPrintM.getId());
            signReturnPrintM.setMergedWaybillList(mergedWaybills);
        }
        result.setRows(signReturnPrintMList);
        result.setTotal(signReturnPrintMList.size());

        return result;
    }

    /**
     * 新增
     * @param signReturnPrintM
     * @return
     */
    @Override
    public int add(SignReturnPrintM signReturnPrintM){
        return signReturnDao.add(signReturnPrintM);
    }

}
