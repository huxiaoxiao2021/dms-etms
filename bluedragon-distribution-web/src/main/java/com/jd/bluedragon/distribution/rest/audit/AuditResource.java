package com.jd.bluedragon.distribution.rest.audit;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.ChuguanExportManager;
import com.jd.bluedragon.core.base.StockExportManager;
import com.jd.bluedragon.distribution.api.response.ReverseReceiveResponse;
import com.jd.bluedragon.distribution.reverse.dao.ReverseReceiveDao;
import com.jd.bluedragon.distribution.reverse.dao.ReverseSpareDao;
import com.jd.bluedragon.distribution.reverse.domain.ReverseReceive;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSpare;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.annotations.GZIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

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
	private StockExportManager stockExportManager;

    @Autowired
    private ChuguanExportManager chuguanExportManager;

    @Resource
    protected UccPropertyConfiguration uccPropertyConfiguration;
	
	private final Log logger = LogFactory.getLog(this.getClass());
	
	public final static String TYPE_WAYBILCODE = "1";
	
	public final static String TYPE_lKDANHAO = "0";
	
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
	@Path("/reverseReceive/{waybillCode}/{canReceive}/{businessType}")
	public ReverseReceive getReverseReceive(@PathParam("waybillCode") String waybillCode, @PathParam("canReceive") Integer canReceive, @PathParam("businessType") Integer businessType) {
		return this.reverseReceiveDao.findOneReverseReceive(waybillCode, canReceive, businessType, null, null, null);
	}
	
	@GET
	@GZIP
	@Path("/reverseReceive/{waybillCode}")
	public ReverseReceive getReverseReceive(@PathParam("waybillCode") String waybillCode) {
		return this.reverseReceiveDao.findByPackageCode(waybillCode.toString());
	}

    private ReverseReceiveResponse toReverseReceiveResponse(ReverseReceive reverseReceive){
    	if(reverseReceive==null) return null;
        ReverseReceiveResponse response = new ReverseReceiveResponse();
        response.setBusinessDate(reverseReceive.getBusinessDate());
        response.setCanReceive(reverseReceive.getCanReceive());
        response.setCreateTime(reverseReceive.getCreateTime());
        response.setFingerprint(reverseReceive.getFingerprint());
        response.setId(reverseReceive.getId());
        response.setOperatorId(reverseReceive.getOperatorId());
        response.setOperatorName(reverseReceive.getOperatorName());
        response.setOrderId(reverseReceive.getOrderId());
        response.setOrgId(reverseReceive.getOrgId());
        response.setPackageCode(reverseReceive.getPackageCode());
        response.setPickWareCode(reverseReceive.getPickWareCode());
        response.setReceiveTime(reverseReceive.getReceiveTime());
        response.setReceiveType(reverseReceive.getReceiveType());
        response.setRejectCode(reverseReceive.getRejectCode());
        response.setRejectMessage(reverseReceive.getRejectMessage());
        response.setSendCode(reverseReceive.getSendCode());
        response.setStoreId(reverseReceive.getStoreId());
        response.setWaybillCode(reverseReceive.getWaybillCode());
        response.setYn(reverseReceive.getYn());
        return  response;
    }


    @GET
    @GZIP
    @Path("/reverseReceive/waybillCode/{waybillCode}")
    public ReverseReceiveResponse getReverseReceiveByCode(@PathParam("waybillCode") String waybillCode){
        return toReverseReceiveResponse(this.getReverseReceive(waybillCode));
    }

	@GET
	@GZIP
	@Path("/sendd/{waybillCode}")
	public List<SendDetail> getSendDetail(@PathParam("waybillCode") String waybillCode) {
		SendDetail querySendDetail = new SendDetail();
		querySendDetail.setWaybillCode(waybillCode);
		return this.sendDatailDao.querySendDatailsBySelective(querySendDetail);//FIXME:无create_site_code有跨节点风险
	}

}
