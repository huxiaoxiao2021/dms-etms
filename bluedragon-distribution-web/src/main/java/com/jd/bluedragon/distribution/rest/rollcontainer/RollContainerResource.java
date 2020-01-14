package com.jd.bluedragon.distribution.rest.rollcontainer;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.ContainerRelationResponse;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.rollcontainer.domain.ContainerRelation;
import com.jd.bluedragon.distribution.rollcontainer.domain.ContainerRelationCondition;
import com.jd.bluedragon.distribution.rollcontainer.domain.RollContainer;
import com.jd.bluedragon.distribution.rollcontainer.service.ContainerRelationService;
import com.jd.bluedragon.distribution.rollcontainer.service.RollContainerService;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Collections;
import java.util.Date;
import java.util.List;
/**
* 无人分拣周转箱(笼车)
* Created by lhc on 2017/4/26.
*/
@Controller
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class RollContainerResource {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
    private RollContainerService rollContainerService;
	
	
	@Autowired
	private BoxService boxService;
	
	@Autowired
	private DeliveryService deliveryService;
	
	@Autowired
	private ContainerRelationService containerRelationService;
	
	/**
	 * 中转箱状态更新
	 * @param request
	 * @return
	 */
	@POST
    @Path("/rollContainer/updateStatus")
    public ContainerRelationResponse updateRollContainerStatus(RollContainer request) {
		ContainerRelationResponse response = new ContainerRelationResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
        if(request.getContainerCode() == null && request.getStatus() == null ){
        	response.setCode(JdResponse.CODE_PARAM_ERROR);
        	response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
        	return response;
        }
        try{
        	int count = rollContainerService.updateRollContainerByCode(request);
        	response.setContainerCode(request.getContainerCode());
        	response.setSendStatus(request.getStatus());
        }catch(Exception e){
        	response.setCode(JdResponse.CODE_INTERNAL_ERROR);
        	response.setMessage("内部错误");
        }
        
        return response;
    }
	
	
	/**
	 * 根据编号获得对应的关系,根据编号获取最近生成的关系(kvindex里获取对应关系)
	 * @param containerCode
	 * @return
	 */
	@GET
    @Path("/rollContainer/getBoxCodeByContainerCode/{containerCode}/{siteCode}")
    public ContainerRelationResponse getBoxCodeByContainerCode(@PathParam("containerCode") String containerCode,
			@PathParam("siteCode") Integer siteCode) {
		ContainerRelationResponse response = new ContainerRelationResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		if(StringUtils.isBlank(containerCode) || siteCode == null){
        	response.setCode(JdResponse.CODE_PARAM_ERROR);
        	response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
        	return response;
        }
		try{
//			//从send_m表的turnoverbox_code得到对应的box_code
//			SendM sendm = deliveryService.getBoxCodeByTurnoverBox(containerCode,siteCode);
//			if(sendm != null){
//				String boxCode = sendm.getBoxCode();
//				response.setBoxCode(boxCode);
//				if(boxCode == null || "".equals(boxCode)){
//					response.setCode(JdResponse.CODE_OK_NULL);
//					response.setMessage("异常提示：上游未将周转箱号与箱号绑定！");
//					log.error("周转箱"+containerCode+"对应的箱号为空，请确认！");
//				}
//			}else{
//				response.setCode(JdResponse.CODE_OK_NULL);
//				response.setMessage("异常提示：上游未将周转箱号与箱号绑定！");
//				log.error("周转箱"+containerCode+"对应的发货信息sendm数据为空，请确认！");
//			}
			//kvindex里根据rfid获取箱号
			String boxCode = containerRelationService.getBoxCodeByContainerCode(containerCode);
			if(boxCode != null){
				response.setBoxCode(boxCode);
				log.info("根据周转箱号:{}获取箱号({})成功!",containerCode,boxCode);
			}else{
				response.setCode(JdResponse.CODE_OK_NULL);
				response.setMessage("异常提示：上游未将周转箱号与箱号绑定！");
				log.warn("周转箱{}对应的发货信息sendm数据为空，请确认！",containerCode);
			}
			
		}catch(Exception e){
        	response.setCode(JdResponse.CODE_INTERNAL_ERROR);
        	response.setMessage("内部错误");
        	log.error("内部错误",e);
        }
        
        return response;
    }
	
	/**
	 * 中转箱与箱号绑定，分拣机满格时需要绑定分拣机传过来的箱号
	 * @param relation
	 * @return
	 */
	@POST
    @Path("/rollContainer/bindRollContainerBoxCode")
    public ContainerRelationResponse bindRollContainerBoxCode(ContainerRelation relation) {
		ContainerRelationResponse response = new ContainerRelationResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		if(relation.getContainerCode() == null && relation.getBoxCode() == null ){
        	response.setCode(JdResponse.CODE_PARAM_ERROR);
        	response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
        	return response;
        }
        List<ContainerRelation> containerRelations = containerRelationService.getContainerRelationByBoxCode(relation.getBoxCode());
		//箱号已存在
		if(containerRelations != null && !containerRelations.isEmpty()){
			response.setCode(JdResponse.CODE_EXIST_BOX_CODE);
			response.setMessage(JdResponse.MESSAGE_EXIST_BOX_CODE);
			return response;
		}
        try{
        	ErpUserClient.ErpUser erpUser = ErpUserClient.getCurrUser();
        	if(erpUser != null){
        		relation.setCreateUser(erpUser.getUserCode());
                relation.setUpdateUser(erpUser.getUserCode());
        	}else{
        		relation.setCreateUser("");
                relation.setUpdateUser("");
        	}
            relation.setCreateTime(new Date());
            relation.setUpdateTime(new Date());
            relation.setIsDelete(0);
            relation.setSendStatus(Constants.CONTAINER_RELATION_SEND_STATUS_NO);
            relation.setTs(new Date());
        	int count = containerRelationService.addContainerRelation(relation);
        }catch(Exception e){
        	response.setCode(JdResponse.CODE_INTERNAL_ERROR);
        	response.setMessage("内部错误");
        	log.error("中转箱与箱号绑定内部错误",e);
        }
        return response;
    }
	
	/**
	 * 根据编号获得对应关系container_relation
	 * @param relationRequest
	 * @return
	 */
	@POST
    @Path("/rollContainer/getContainerRelationByCode")
    public ContainerRelationResponse getContainerRelationByCode(ContainerRelation relationRequest) {
		ContainerRelationResponse response = new ContainerRelationResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		if(relationRequest == null || relationRequest.getContainerCode()==null){
        	response.setCode(JdResponse.CODE_PARAM_ERROR);
        	response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
        	return response;
        }
		String containerCode = relationRequest.getContainerCode();
		try{
			ContainerRelation relation = containerRelationService.getContainerRelation(containerCode, relationRequest.getDmsId());
			if(relation != null){
				response.setContainerCode(containerCode);
				response.setBoxCode(relation.getBoxCode());
				response.setSiteCode(Integer.parseInt(relation.getSiteCode()));
				response.setDmsId(relation.getDmsId());
				response.setSendStatus(relation.getSendStatus());
			}else{
				response.setCode(JdResponse.CODE_NOT_CONTAINER_RELATION);
	        	response.setMessage("没有对应周转箱"+containerCode+"与箱号的对应关系！");
			}
        }catch(Exception e){
        	response.setCode(JdResponse.CODE_INTERNAL_ERROR);
        	response.setMessage("内部错误");
        	log.error("根据编号获得对应关系内部错误",e);
        }
		
        return response;
    }
	
	/**
	 * 中转箱与箱号释放绑定关系,周转箱收货时需要释放rfid与箱号的关系
	 * @param relation
	 * @return
	 */
	@POST
    @Path("/rollContainer/releaseContainerRelation")
    public ContainerRelationResponse releaseContainerRelation(ContainerRelation relation) {
		ContainerRelationResponse response = new ContainerRelationResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		if(relation.getContainerCode() == null && relation.getBoxCode() == null ){
        	response.setCode(JdResponse.CODE_PARAM_ERROR);
        	response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
        	return response;
        }
		try{
			relation.setUpdateTime(new Date());
			relation.setSendStatus(Constants.CONTAINER_RELATION_SEND_STATUS_YES);
	        relation.setTs(new Date());
	        int count = containerRelationService.updateContainerRelationByCode(relation);
        }catch(Exception e){
        	response.setCode(JdResponse.CODE_INTERNAL_ERROR);
        	response.setMessage("内部错误");
        	log.error("周转箱与箱号释放绑定关系内部错误",e);
        }
		
        return response;
    }

	/**
	 * 根据 ContainerRelationCondition查询 分页ContainerRelation
	 * @param condition
	 * @return
	 */
	@POST
	@Path("/rollContainer/getContainerRelationPager")
    public Pager<List<ContainerRelation>> getContainerRelationPager(ContainerRelationCondition condition){
    	if(condition.getDmsId() == null || condition.getDmsId() == 0){
			Pager reponese = new Pager();
			reponese.setTotalSize(0);
			reponese.setData(Collections.EMPTY_LIST);
    		return reponese;
		}
		Pager<List<ContainerRelation>> pager = new Pager<List<ContainerRelation>>(condition.getPage(), condition.getPageSize());
		return containerRelationService.getContainerRelationPager(condition, pager);
	}
	/**
	 * 根据 ContainerRelationCondition查询 ContainerRelation 列表
	 */
	@POST
	@Path("/rollContainer/getContainerRelationByModel")
	public List<ContainerRelation> getContainerRelationByModel(ContainerRelationCondition condition){
		return containerRelationService.getContainerRelationByModel(condition);
	}

	/**
	 * 根据boxCode得到box信息,供周转箱(笼车)发货使用
	 * @param request
	 * @return
	 */
//	@POST
//    @Path("/rollContainer/getBoxByBoxCode")
//    public ContainerRelationResponse getBoxByBoxCode(ContainerRelation relation) {
//		ContainerRelationResponse response = new ContainerRelationResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
//		if(relation.getBoxCode() == null ){
//        	response.setCode(JdResponse.CODE_PARAM_ERROR);
//        	response.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
//        	return response;
//        }
//		try{
//			String boxCode = relation.getBoxCode();
//			Box box = boxService.findBoxByCode(boxCode);
//			if(box != null){
//				box.getReceiveSiteCode();
//				response.setSiteCode(box.getCreateSiteCode());
//				response.setReceiveSiteCode(box.getReceiveSiteCode());
//			}
//			
//        }catch(Exception e){
//        	response.setCode(JdResponse.CODE_INTERNAL_ERROR);
//        	response.setMessage("内部错误");
//        	log.error("内部错误",e);
//        }
//		
//        return response;
//    }
	
}
