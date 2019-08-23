package com.jd.bluedragon.distribution.box.service;

import com.jd.bluedragon.core.base.ContainerManager;
import com.jd.bluedragon.distribution.box.dao.GroupBoxDao;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.utils.StringHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

/**
 * @ClassName: GroupBoxServiceImpl
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2018/12/11 21:45
 */
@Service("groupBoxService")
public class GroupBoxServiceImpl implements GroupBoxService {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private GroupBoxDao groupBoxDao;

    @Autowired
    private ContainerManager containerManager;

    /**
     * 批量新增
     * @param groupList
     * @return
     */
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    @Override
    public Integer batchAdd(List<Box> groupList) {
        Assert.notNull(groupList, "groupList must not be null");
        Integer result = this.groupBoxDao.batchAdd(groupList);
        containerManager.updateContainerGroup(groupList);
        return result;
    }

    /**
     * 根据分组箱号获取此分组所有箱号
     * @param boxCode
     * @return
     */
    @Override
    public List<Box> getAllBoxByBoxCode(String boxCode) {
        Box box = groupBoxDao.getBoxInfoByBoxCode(boxCode);
        if(box != null && StringHelper.isNotEmpty(box.getGroupSendCode())){
            List<Box> list = groupBoxDao.getAllBoxByGroupSendCode(box.getGroupSendCode());
            return list;
        }else {
            return Collections.EMPTY_LIST;
        }
    }

}
