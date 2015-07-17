package com.jd.bluedragon.distribution.rest.audit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.annotations.GZIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;
import com.jd.bluedragon.distribution.kuguan.service.KuGuanService;
import com.jd.bluedragon.distribution.reverse.dao.ReverseReceiveDao;
import com.jd.bluedragon.distribution.reverse.dao.ReverseSpareDao;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSpare;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.utils.ObjectMapHelper;
import com.jd.etms.basic.dto.BaseStaffSiteOrgDto;

@Component
@Path(Constants.REST_URL+"/audit")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class AuditResource {

	@Autowired
	ReverseSpareDao reverseSpareDao;

    @Autowired
    private ReverseReceiveDao reverseReceiveDao;
    
	@Autowired
	private SendDatailDao sendDatailDao;
	
	@Autowired
	KuGuanService tKuGuanService;
	
	private final Log logger = LogFactory.getLog(this.getClass());

	@GET
	@GZIP
	@Path("/reverseSpare/{waybillCode}")
	public List<ReverseSpare> getReverseSpare(@PathParam("waybillCode") String waybillCode) {
		ReverseSpare rs = new ReverseSpare();
		rs.setWaybillCode(waybillCode);
		return this.reverseSpareDao.findByWayBillCode(rs);
	}
	
	@GET
	@GZIP
	@Path("/reverseReceive/{waybillCode}")
	public ReverseReceive getReverseReceive(@PathParam("waybillCode") String waybillCode) {
		return this.reverseReceiveDao.findByPackageCode(waybillCode.toString());
	}
	
	@GET
	@GZIP
	@Path("/sendd/{waybillCode}")
	public List<SendDetail> getSendDetail(@PathParam("waybillCode") String waybillCode) {
		SendDetail querySendDetail = new SendDetail();
		querySendDetail.setWaybillCode(waybillCode);
		return this.sendDatailDao.querySendDatailsBySelective(querySendDetail);
	}
	
	@GET
	@GZIP
	@Path("/stock/{waybillCode}/{ddlType}")
	public KuGuanDomain getStockInfo(@PathParam("waybillCode") String waybillCode, @PathParam("ddlType") String ddlType) {
		KuGuanDomain kuGuanDomain = new KuGuanDomain();
		
		kuGuanDomain.setDdlType(ddlType);
		kuGuanDomain.setWaybillCode(waybillCode);
		
		Map<String, Object> params = ObjectMapHelper
				.makeObject2Map(kuGuanDomain);

		try {
			logger.error("根据订单号获取库管单信息参数错误-queryByParams");
			kuGuanDomain = tKuGuanService.queryByParams(params);
		} catch (Exception e) {
			kuGuanDomain = new KuGuanDomain(); 
			kuGuanDomain.setDdlType(ddlType);
			kuGuanDomain.setWaybillCode(null);
			logger.error("根据订单号获取库管单信息服务异常"+e);
		}
		return kuGuanDomain;
	}
	
	@GET
	@GZIP
	@Path("/stockDetail/{lKdanhao}")
	public List<KuGuanDomain> getStockInfoDetail(@PathParam("lKdanhao") String lKdanhao) {
		
		List<KuGuanDomain> domains = new ArrayList<KuGuanDomain>();
		try {
			logger.error("根据订单号获取库管单信息参数开始-queryByParams");
			domains = tKuGuanService.queryMingxi(lKdanhao);
			
			logger.error("根据订单号获取库管单信息参数错误-1");
			
		} catch (Exception e) {
			logger.error("根据订单号获取库管单信息服务异常"+e);
		}
		return domains;
	}
}
