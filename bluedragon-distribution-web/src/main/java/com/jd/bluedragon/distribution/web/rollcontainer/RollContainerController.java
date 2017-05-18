package com.jd.bluedragon.distribution.web.rollcontainer;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.gantry.service.GantryDeviceService;
import com.jd.bluedragon.distribution.rollcontainer.domain.ContainerRelation;
import com.jd.bluedragon.distribution.rollcontainer.domain.RollContainer;
import com.jd.bluedragon.distribution.rollcontainer.service.ContainerRelationService;
import com.jd.bluedragon.distribution.rollcontainer.service.RollContainerService;
import com.jd.bluedragon.distribution.web.ErpUserClient;
import com.jd.bluedragon.distribution.web.gantry.GantryAutoSendController;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

/**
 * Created by lhc on 2017/5/3.
 */
@Controller
@RequestMapping("/rollContainer")
public class RollContainerController {
	
	private static final Log logger = LogFactory.getLog(GantryAutoSendController.class);
	
	@Autowired
    private RollContainerService rollContainerService;
	
	@Autowired
    private ContainerRelationService containerRelationService;
	
	@RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Model model) {
        this.logger.debug("周转箱 --> index");
        try {
        	Map<String, Object> param = new HashMap<String, Object>();
        	List<RollContainer> list = rollContainerService.getRollContainer(param);
        	System.out.println("````````````");
        } catch (Exception e) {
            logger.error("周转箱index失败",e);
        }
        return "rollcontainer/rollContainerIndex";
    }
	
	@RequestMapping(value = "/addRollContainer", method = RequestMethod.GET)
    public String addRollContainer(Model model) {
        this.logger.debug("周转箱 信息添加 -->addRollContainer");
        try {
        	RollContainer rollContainer = new RollContainer();
        	rollContainer.setContainerCode("234");
        	rollContainer.setStatus(0);
        	rollContainer.setCreateTime(new Date());
        	rollContainer.setUpdateTime(new Date());
        	rollContainer.setCreateUser("3445");
        	rollContainer.setUpdateUser("3445");
        	rollContainer.setIsDelete(0);
        	rollContainer.setTs(new Date());
        	
        	int count = rollContainerService.addRollContainer(rollContainer);
        	System.out.println(count);
        } catch (Exception e) {
            logger.error("周转箱添加失败",e);
        }
        return "rollcontainer/rollContainerIndex";
    }
	
	
	@RequestMapping(value = "/updateRollContainerStatus", method = RequestMethod.GET)
    public String updateRollContainerStatus(ContainerRelation relation) {
        InvokeResult<String> response = new InvokeResult<String>();
//        relation.setContainerCode("234");
//        relation.setBoxCode("BC3254656677989");
//        relation.setCreateTime(new Date());
//        relation.setUpdateTime(new Date());
//        relation.setCreateUser("3445");
//        relation.setUpdateUser("3445");
//        relation.setIsDelete(0);
//        relation.setTs(new Date());
//        int count = containerRelationService.addContainerRelation(relation);
//        System.out.println(count);
        
        String containerCode = "234";
        ContainerRelation relationTemp = containerRelationService.getContainerRelation(containerCode);
        System.out.println("----------");
        
        return "";
    }
}
