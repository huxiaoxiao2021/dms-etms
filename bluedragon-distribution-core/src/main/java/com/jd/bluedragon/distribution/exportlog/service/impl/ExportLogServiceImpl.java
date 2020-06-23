package com.jd.bluedragon.distribution.exportlog.service.impl;

import IceInternal.Ex;
import com.jd.bluedragon.distribution.abnormalwaybill.domain.AbnormalWayBill;
import com.jd.bluedragon.distribution.exportlog.dao.ExportLogDao;
import com.jd.bluedragon.distribution.exportlog.domain.ExportLog;
import com.jd.bluedragon.distribution.exportlog.domain.ExportLogCondition;
import com.jd.bluedragon.distribution.exportlog.service.ExportLogService;
import com.jd.bluedragon.distribution.financialForKA.domain.WaybillCodeCheckDto;
import com.jd.bluedragon.distribution.financialForKA.service.impl.WaybillCodeCheckServiceImpl;
import com.jd.bluedragon.distribution.jss.JssService;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverWaybill;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageM;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: 刘春和
 * @date: 2020/6/17 16:43
 * @description:
 */
@Service
public class ExportLogServiceImpl implements ExportLogService {
    private Logger log = LoggerFactory.getLogger(WaybillCodeCheckServiceImpl.class);

    @Autowired
    private ExportLogDao exportLogDao;
    @Autowired
    private JssService jssService;
    @Value("${jss.waybillcheck.export.zip.bucket}")
    private String bucket;

    @Override
    public PagerResult<ExportLog> listData(ExportLogCondition condition) {
        PagerResult<ExportLog> pagerResult = new PagerResult<ExportLog>();
        try {
            List<ExportLog> list = exportLogDao.queryByCondition(condition);
            Integer count = exportLogDao.queryCountByCondition(condition);
            pagerResult.setRows(list);
            pagerResult.setTotal(count);
        } catch (Exception e) {
            log.error("查询失败", e);
        }
        return pagerResult;
    }

    @Override
    public Integer update(ExportLog exportLog) {
        return exportLogDao.update(exportLog);
    }

    @Override
    public ExportLog findOne(Long id) {
        return exportLogDao.getById(id);
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @Override
    public Integer delete(Long id) {
        ExportLog exportLog = exportLogDao.getById(id);
        if(exportLog!=null) {
            try {
                jssService.deleteFile(bucket, exportLog.getExportCode());
            }catch (Exception ex){
                log.error("删除导出运单号校验记录失败",ex);
            }
        }
        return exportLogDao.delete(id);
    }

}
