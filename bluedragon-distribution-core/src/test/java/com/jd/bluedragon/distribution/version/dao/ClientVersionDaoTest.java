package com.jd.bluedragon.distribution.version.dao;

import com.jd.jim.cli.redis.jedis.Client;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.jd.bluedragon.distribution.dao.common.AbstractDaoIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.version.domain.ClientVersion;

import java.util.List;
import java.util.Random;

public class ClientVersionDaoTest extends AbstractDaoIntegrationTest{
	
	@Autowired
	private ClientVersionDao clientVersionDao;
	
	
	@Test
    public void testExists() {
        ClientVersion parameter = new ClientVersion();
        parameter.setVersionId(new Long(891));
        parameter.setVersionCode("20130323R");
        parameter.setVersionType(30);
        parameter.setDownloadUrl("http://dmspda.etms.360buy.com/bd/");
        parameter.setMemo("【北京】备件库商品分拣 + POP + 批量分拣 ");
        clientVersionDao.add(parameter);
        boolean exist=clientVersionDao.exists(parameter);
        Assert.assertTrue("是否存在20130323R版本",exist);
    }
	
	@Test
    public void testGetByVersionType() {
        ClientVersion parameter = new ClientVersion();
        parameter.setVersionId(new Long(891));
        parameter.setVersionCode("20130323R");
        parameter.setVersionType(30);
        parameter.setDownloadUrl("http://dmspda.etms.360buy.com/bd/");
        parameter.setMemo("【北京】备件库商品分拣 + POP + 批量分拣 ");
        clientVersionDao.add(parameter);
        Integer versionType = 30;
        List<ClientVersion> list= clientVersionDao.getByVersionType(versionType);
        Assert.assertTrue("根据应用类型获取应用列表",list!=null&&list.size()>0);
        if (list != null && list.size() > 0)
            Assert.assertTrue("根据应用类型获取应用列表,检查应用类型", new Integer(30).equals(list.get(0).getVersionType()));
    }
	
	@Test
    public void testUpdate() {

        List<ClientVersion> list=clientVersionDao.getAllAvailable();

        Random rd=new Random();
        String r=Integer.toString(rd.nextInt());
        ClientVersion parameter=new ClientVersion();
        parameter.setVersionCode(r);
        parameter.setVersionId(list.get(0).getVersionId());
        boolean b = clientVersionDao.update(parameter);
        Assert.assertTrue("更新版本新信息", b);
        ClientVersion temp = clientVersionDao.get(ClientVersionDao.class.getName(), list.get(0).getVersionId());
        Assert.assertTrue("检查更新结果", temp.getVersionType() != null && temp.getVersionCode() != null && temp.getDownloadUrl() != null && temp.getMemo() != null && temp.getYn() != null
                && temp.getVersionType().equals(list.get(0).getVersionType()) && temp.getVersionCode().equals(r) && temp.getDownloadUrl().equals(list.get(0).getDownloadUrl()) && temp.getYn() .equals(list.get(0).getYn()) && temp.getVersionId().equals(list.get(0).getVersionId()));
    }
	
	@Test
    public void testAdd() {
        ClientVersion parameter = new ClientVersion();
        parameter.setVersionId(new Long(891));
        parameter.setVersionCode("20130323R");
        parameter.setVersionType(30);
        parameter.setDownloadUrl("http://dmspda.etms.360buy.com/bd/");
        parameter.setMemo("【北京】备件库商品分拣 + POP + 批量分拣 ");
        boolean b= clientVersionDao.add(parameter);
        Assert.assertTrue("版本信息插入失败", b);
    }
	
	@Test
    public void testGet() {

        List<ClientVersion> list=clientVersionDao.getAllAvailable();


        ClientVersion temp= clientVersionDao.get(ClientVersionDao.class.getName(), list.get(0).getVersionId());
        Assert.assertNotNull("查询客户端版本信息！",temp);
    }
	
	@Test
    public void testGetAllAvailable() {
        clientVersionDao.getAllAvailable();
    }
	
	@Test
    public void testGetByVersionCode() {
        Long versionId=new Long(new Random().nextInt());
        ClientVersion parameter = new ClientVersion();
        parameter.setVersionId(versionId);
        parameter.setVersionCode("20130323R");
        parameter.setVersionType(30);
        parameter.setDownloadUrl("http://dmspda.etms.360buy.com/bd/");
        parameter.setMemo("【北京】备件库商品分拣 + POP + 批量分拣 ");
        clientVersionDao.add(parameter);
        clientVersionDao.getByVersionCode("20130323R");
    }
	
	@Test
    public void testDelete() {
        List<ClientVersion> list=clientVersionDao.getAllAvailable();
        Long versionId = list.get(0).getVersionId();
        boolean b=clientVersionDao.delete(versionId);
        Assert.assertTrue("删除版本信息",b);
    }
	
	@Test
    public void testGetAll() {
        clientVersionDao.getAll();
    }
}
