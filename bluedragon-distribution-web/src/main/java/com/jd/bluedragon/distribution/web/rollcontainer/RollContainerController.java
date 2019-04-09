package com.jd.bluedragon.distribution.web.rollcontainer;

import com.jd.bluedragon.distribution.rollcontainer.service.ContainerRelationService;
import com.jd.bluedragon.distribution.rollcontainer.service.RollContainerService;
import com.jd.bluedragon.distribution.web.gantry.GantryAutoSendController;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

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
	
	/*@RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Model model) {
        this.logger.debug("周转箱 --> index");
        try {
        	Map<String, Object> param = new HashMap<String, Object>();
        	List<RollContainer> list = rollContainerService.getRollContainer(param);
        } catch (Exception e) {
            logger.error("周转箱index失败",e);
        }
        return "rollcontainer/rollContainerIndex";
    }*/
	

}
