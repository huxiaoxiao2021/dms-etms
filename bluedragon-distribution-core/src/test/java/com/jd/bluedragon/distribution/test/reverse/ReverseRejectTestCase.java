package com.jd.bluedragon.distribution.test.reverse;

import javax.ws.rs.core.MediaType;

import com.jd.jmq.common.message.Message;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.junit.Test;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.core.message.base.MessageBaseConsumerFactory;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.RejectRequest;
import com.jd.bluedragon.distribution.api.request.ReverseReceiveRequest;
import com.jd.bluedragon.utils.XmlHelper;

public class ReverseRejectTestCase {
	@Test
	public void test_add_reject() {
		String message = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><RejectRequest><businessType>1</businessType><cky2>6</cky2><storeId>0</storeId><operateTime>2012-10-22 10:05:00</operateTime><orderId>300687686</orderId><actualPackageQuantity>1</actualPackageQuantity><operator>操作人</operator><operatorCode>bjcaozuoren</operatorCode></RejectRequest>";
		// String message =
		// "<?xml version=\"1.0\" encoding=\"UTF-8\"?><RejectRequest xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><businessType>1</businessType><orgId>4</orgId><cky2>4</cky2><storeId>4</storeId><operateTime>2012-12-18 15:36:53</operateTime><OrderId>2012121804</OrderId><actualPackageQuantity>1</actualPackageQuantity><operator>168</operator><operatorCode>168</operatorCode></RejectRequest>";
		
		RejectRequest rejectRequest = null;
		
		if (XmlHelper.isXml(message, RejectRequest.class, null)) {
			rejectRequest = (RejectRequest) XmlHelper.toObject(message, RejectRequest.class);
		}
		
		try {
			String uri = "http://localhost:1111/services/reverse/reject";
			ClientRequest request = new ClientRequest(uri);
			request.accept(MediaType.APPLICATION_JSON);
			request.body(MediaType.APPLICATION_JSON, rejectRequest);
			
			request.post(Integer.class);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void test_notify_stock() throws Exception {
		Message message = new Message();
		message.setTopic("bd_dms_reverse_receive");
        //message.setApp();
		message.setApp("BlueDragonDistributionStock");
		message.setText("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ReceiveRequest><sendCode>具体备件库入库单号</sendCode><orderId>303417779</orderId><receiveType>3</receiveType><operateTime>2012-10-19 17:24:20</operateTime><userName>收货人</userName><userCode>123</userCode><canReceive>1</canReceive></ReceiveRequest>");
		
		ReverseReceiveRequest request = (ReverseReceiveRequest) XmlHelper.toObject(
				message.getText(), ReverseReceiveRequest.class);
		
		System.out.println(request.getOrderId());
		
		MessageBaseConsumerFactory factory = new MessageBaseConsumerFactory();
		MessageBaseConsumer messageConsumer = factory.createMessageConsumer(message.getTopic(),
				message.getApp());
		//messageConsumer.consume(message);
	}
	
	@Test
	public void test_add_loss_order() {
		String json = "[{\"id\":100010,\"orderId\":209026656,\"userErp\":\"bjclia\",\"userName\":\"陈亮\",\"lossTime\":1343374766000,\"productList\":[{\"orderId\":209026656,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":150882,\"productName\":\"宏碁笔记本包\"},{\"orderId\":209026656,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":610214,\"productName\":\"宏碁（acer）V3-571G-52452G50Makk 15.6英寸笔记本电脑 （i5-2450M 2G 500G GT630M 2G独显 Win7) 黑色\"}]},{\"id\":100015,\"orderId\":227878087,\"userErp\":\"bjclia\",\"userName\":\"陈亮\",\"lossTime\":1343377148000,\"productList\":[{\"orderId\":227878087,\"lossType\":0,\"productQuantity\":12,\"lossQuantity\":6,\"productId\":1000581237,\"productName\":\"茅台集团贵州特醇喜庆用酒52度 500ml\"}]},{\"id\":100016,\"orderId\":139112544,\"userErp\":\"gzwjs\",\"userName\":\"王劲松\",\"lossTime\":1344507488000,\"productList\":[{\"orderId\":139112544,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":508600,\"productName\":\"比利时GBS吉贝丝经典贝壳巧克力礼盒250g\"},{\"orderId\":139112544,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":539386,\"productName\":\"MMs花生逗趣礼品装135g\"}]},{\"id\":100017,\"orderId\":109075576,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344576731000,\"productList\":[{\"orderId\":109075576,\"lossType\":0,\"productQuantity\":1,\"lossQuantity\":0,\"productId\":253232,\"productName\":\"索尼（SONY）DAV-DZ810 DVD家庭影音系统\"},{\"orderId\":109075576,\"lossType\":0,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":256775,\"productName\":\"万家乐（macro）欧式吸油烟机CXW-218-ED389(05)\"},{\"orderId\":109075576,\"lossType\":0,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":352604,\"productName\":\"美的（midea）EG823LC7-NSH1  智能平板  微波炉\"},{\"orderId\":109075576,\"lossType\":0,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":337621,\"productName\":\"索尼（SONY） KLV-32BX320 32英寸 高清液晶电视 黑色\"},{\"orderId\":109075576,\"lossType\":0,\"productQuantity\":2,\"lossQuantity\":2,\"productId\":500873,\"productName\":\"飞利浦HDMI高清线 HDMI1.4+平板电视屏幕清洁布套装\"},{\"orderId\":109075576,\"lossType\":0,\"productQuantity\":2,\"lossQuantity\":2,\"productId\":500875,\"productName\":\"飞利浦4位插座SPS1420A/93\"},{\"orderId\":109075576,\"lossType\":0,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":341996,\"productName\":\"索尼（SONY） KDL-46CX520 46英寸 全高清液晶电视 黑色\"}]},{\"id\":100018,\"orderId\":111592006,\"userErp\":\"bjwhs\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":111592006,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":341975,\"productName\":\"索尼（SONY） KLV-40BX420 40英寸 全高清液晶电视 黑色\"},{\"orderId\":111592006,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":500873,\"productName\":\"飞利浦HDMI高清线 HDMI1.4+平板电视屏幕清洁布套装\"},{\"orderId\":111592006,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":500875,\"productName\":\"飞利浦4位插座SPS1420A/93\"}]},{\"id\":100019,\"orderId\":62278824,\"userErp\":\"bjwhs\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":62278824,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":178527,\"productName\":\"LG 42LH22RC 42英寸 全高清液晶电视机\"},{\"orderId\":62278824,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":178563,\"productName\":\"LG 42英寸电视底座AD-42LH30S\"}]},{\"id\":100020,\"orderId\":65697781,\"userErp\":\"bjwhs\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":65697781,\"lossType\":1,\"productQuantity\":2,\"lossQuantity\":2,\"productId\":248713,\"productName\":\"海尔 (haier)  电热水器 ES80H-D1(E)\"},{\"orderId\":65697781,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":248707,\"productName\":\"海尔 (haier)  电热水器 ES40H-C1(E)\"}]},{\"id\":100021,\"orderId\":66988308,\"userErp\":\"bjwhs\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":66988308,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":203185,\"productName\":\"大金 (DAIKIN）豪华型3匹柜式直流变频冷暖空调FVXS72GV2CN 香槟金（R410A新冷媒）\"},{\"orderId\":66988308,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":168569,\"productName\":\"大金空调配件箱CKH255K\"},{\"orderId\":66988308,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":203195,\"productName\":\"大金RXS72GMV2C室外机（对应的室内机为：FVXS72GV2CW）\"}]},{\"id\":100022,\"orderId\":67324614,\"userErp\":\"bjwhs\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":67324614,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":421737,\"productName\":\"格兰仕（Galanz）KFR-35GW/dLC45-130（2）1.5匹 壁挂式家用冷暖空调（白色）\"},{\"orderId\":67324614,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":421740,\"productName\":\"格兰仕（Galanz）KFR-35W/dLC45-130（2）（对应的室内机为：KFR-35GW/dLC45-130（2））\"}]},{\"id\":100023,\"orderId\":67359357,\"userErp\":\"bjwhs\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":67359357,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":379883,\"productName\":\"格兰仕（Galanz） KFR-32GW/DLP45-130(2)  1.5匹 壁挂式家用冷暖空调(白色)\"},{\"orderId\":67359357,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":379895,\"productName\":\"格兰仕（Galanz）室外机KFR-32W/DLP45-130（2）（对应的室内机为：KFR-32G/DLP45-130（2））\"}]},{\"id\":100024,\"orderId\":67966392,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":67966392,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":354415,\"productName\":\"TCL  KFRd-32GW/DK22  小1.5匹 双核钛金壁挂式冷暖空调（白色+花纹）\"},{\"orderId\":67966392,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":354478,\"productName\":\"TCL 室外机 KFR-32W0330（对应的室内机为：KFRd-32G/DK22）\"}]},{\"id\":100025,\"orderId\":68610394,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":68610394,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":368525,\"productName\":\"格力（GREE）凉之静变频系列 正1.5P壁挂式家用冷暖空调KFR-35GW/（35556）FNDc-3\"},{\"orderId\":68610394,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":397486,\"productName\":\"格力（GREE）室外机 KFR-35W/FNC07-3（对应的室内机为：KFR-35GW/（35556）FNDc-3）\"}]},{\"id\":100026,\"orderId\":68763256,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":68763256,\"lossType\":1,\"productQuantity\":2,\"lossQuantity\":2,\"productId\":282695,\"productName\":\"海尔（haier）KFR-35GW/03GCC12 1.5匹 壁挂式家用冷暖空调（白色）\"},{\"orderId\":68763256,\"lossType\":1,\"productQuantity\":2,\"lossQuantity\":2,\"productId\":282697,\"productName\":\"海尔（haier）KFR-35GW/0312室外机（对应的室内机为：KFR-35GW/0312）\"}]},{\"id\":100027,\"orderId\":70340519,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":70340519,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":352664,\"productName\":\"奥克斯（AUX）KFR-32GW/SQB+3 小1.5P  挂壁式家用冷暖空调（白色）\"},{\"orderId\":70340519,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":352663,\"productName\":\"奥克斯（AUX）室外机KFR- 32W/S+3（对应的室内机为：KFR- 32GW/SQB+3）\"}]},{\"id\":100028,\"orderId\":70607827,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":70607827,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":358395,\"productName\":\"惠而浦（Whirlpool ）WI4821MS  4.8公斤 全净系列波轮洗衣机（高级灰）\"},{\"orderId\":70607827,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":255094,\"productName\":\"格兰仕(Galanz)春风宝系列1.5P壁挂式家用冷暖光波空调KFR-32GW/dlc16 -130（1）（红色）\"},{\"orderId\":70607827,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":255099,\"productName\":\"格兰仕（Galanz）室外机KFR-32W/dlc16 -130（1）（对应的室内机为：KFR-32GW/dlc16 -130（1））\"}]},{\"id\":100029,\"orderId\":71833903,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":71833903,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":406379,\"productName\":\"格兰仕（Galanz）KFR-32GW/dLP42-130(2)  1.5匹 冷暖挂机家电下乡系列空调（古典红）\"},{\"orderId\":71833903,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":406381,\"productName\":\"格兰仕（Galanz）  室外机KFR-32W/dLP42-130(2)（对应的室内机为：KFR-32GW/dLP42-130(2)）\"}]},{\"id\":100030,\"orderId\":71938106,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":71938106,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":334791,\"productName\":\"格兰仕 (Galanz)  KFR-32GW/dlc16 -130（1）1.5P春风宝系列壁挂式家用冷暖光波空调 珍珠白\"},{\"orderId\":71938106,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":334817,\"productName\":\"格兰仕（Galanz）室外机KFR-32W/dlc16 -130（1）（对应的室内机为：KFR-32GW/dlc16 -130（1））\"}]},{\"id\":100031,\"orderId\":72231589,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":72231589,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":352668,\"productName\":\"奥克斯（AUX）KFR-35GW/SQB+3  正1.5P  挂壁式家用冷暖空调（白色）\"},{\"orderId\":72231589,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":352670,\"productName\":\"奥克斯（AUX）室外机KFR- 35W/S+3 对应的室内机为：KFR- 35GW/SQB+3）\"},{\"orderId\":72231589,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":352664,\"productName\":\"奥克斯（AUX）KFR-32GW/SQB+3  1.5P  挂壁式家用冷暖空调（白色）\"},{\"orderId\":72231589,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":352663,\"productName\":\"奥克斯（AUX）室外机KFR- 32W/S+3（对应的室内机为：KFR- 32GW/SQB+3）\"}]},{\"id\":100032,\"orderId\":73461144,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":73461144,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":353079,\"productName\":\"松下（panasonic）TH-P42GT20C 全高清等离子3D电视机\"}]},{\"id\":100033,\"orderId\":73718138,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":73718138,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":371308,\"productName\":\"科龙（Kelon）KFR-23GW/UG-1(K18)  小1匹挂式家用冷暖空调（银灰色）\"},{\"orderId\":73718138,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":372843,\"productName\":\"科龙（Kelon）室外机KFR-23W-K18（对应的室内机为：KFR-23GW/UG-1(K18) 银灰色）\"}]},{\"id\":100034,\"orderId\":73722026,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":73722026,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":405483,\"productName\":\"格兰仕（Galanz）KFR-26G/dLP42-130(2) 大1匹 冷暖挂机家电下乡系列空调（古典红）\"},{\"orderId\":73722026,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":405486,\"productName\":\"格兰仕（Galanz）KFR-26W/DLP42-130(2)（对应的室内机为：KFR-26G/DLP42-130(2)\"}]},{\"id\":100035,\"orderId\":73841797,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":73841797,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":178515,\"productName\":\"视贝 大功率插座7721（空调专用，2.8米）\"},{\"orderId\":73841797,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":368525,\"productName\":\"格力（GREE）凉之静变频系列 正1.5P壁挂式家用冷暖空调KFR-35GW/（35556）FNDc-3\"},{\"orderId\":73841797,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":397486,\"productName\":\"格力（GREE）室外机 KFR-35W/FNC07-3（对应的室内机为：KFR-35GW/（35556）FNDc-3）\"}]},{\"id\":100036,\"orderId\":74182971,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":74182971,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":352664,\"productName\":\"奥克斯（AUX）KFR-32GW/SQB+3  1.5P  挂壁式家用冷暖空调（白色）\"},{\"orderId\":74182971,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":352663,\"productName\":\"奥克斯（AUX）室外机KFR- 32W/S+3（对应的室内机为：KFR- 32GW/SQB+3）\"}]},{\"id\":100037,\"orderId\":74183402,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":74183402,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":379883,\"productName\":\"格兰仕（Galanz） KFR-32GW/DLP45-130(2)  1.5匹 壁挂式家用冷暖空调(白色)\"},{\"orderId\":74183402,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":379895,\"productName\":\"格兰仕（Galanz）室外机KFR-32W/DLP45-130（2）（对应的室内机为：KFR-32G/DLP45-130（2））\"}]},{\"id\":100038,\"orderId\":74477280,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":74477280,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":213942,\"productName\":\"海尔（Haier）5匹商用立式冷暖空调KFR-120LW/6302K\"}]},{\"id\":100039,\"orderId\":75016648,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":75016648,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":426396,\"productName\":\"格兰仕（Galanz）KF-26GW/LP39-130(1)  1匹 壁挂式家用单冷空调\"},{\"orderId\":75016648,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":426394,\"productName\":\"格兰仕（Galanz）室外机KF-26W/LP39-130(1)(对应的室内机为：KF-26GW/LP39-130(1））\"}]},{\"id\":100040,\"orderId\":75117259,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":75117259,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":341975,\"productName\":\"索尼（SONY） KLV-40BX420 40英寸 全高清液晶电视 黑色\"}]},{\"id\":100041,\"orderId\":75761889,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":75761889,\"lossType\":1,\"productQuantity\":2,\"lossQuantity\":2,\"productId\":336153,\"productName\":\"TCL  KFRd-32GW/DR22 小1.5匹 壁挂式冷暖空调\"},{\"orderId\":75761889,\"lossType\":1,\"productQuantity\":2,\"lossQuantity\":2,\"productId\":336166,\"productName\":\"TCL 室外机 KFR-32W0329（对应的室内机为：KFRd-32G/DR22）\"}]},{\"id\":100042,\"orderId\":76395497,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":76395497,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":406170,\"productName\":\"海尔（haier）XQB60-M918   6公斤 全自动洗衣机\"}]},{\"id\":100043,\"orderId\":76592513,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":76592513,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":361460,\"productName\":\"格力（GREE）KFR-23GW/K(23556)D1-N1 1匹 凉之夏系列壁挂式家用冷暖空调\"},{\"orderId\":76592513,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":383889,\"productName\":\"格力（GREE)室外机KFR-23W/KGQE（对应的室内机为：KFR-23GW/K(23556)D1-N1）\"},{\"orderId\":76592513,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":361466,\"productName\":\"格力（GREE）KFR-50GW/K（50556）B1-N1  2匹 壁挂式绿满园系列家用冷暖空调\"},{\"orderId\":76592513,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":383892,\"productName\":\"格力（GREE)室外机KFR-50W/KGQE(对应的室内机为:KFR-50G(50556)B1-N1)\"}]},{\"id\":100044,\"orderId\":77104661,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":77104661,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":404353,\"productName\":\"澳柯玛（AUCMA） BCD-128FA  128升 冰箱（白色）\"}]},{\"id\":100045,\"orderId\":77226745,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":77226745,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":178515,\"productName\":\"视贝 大功率插座7721（空调专用，2.8米）\"}]},{\"id\":100046,\"orderId\":77325298,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":77325298,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":133584,\"productName\":\"富士施乐（Fuji Xerox）DocuPrint 202 A3黑白激光打印机\"}]},{\"id\":100047,\"orderId\":77817009,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":77817009,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":361500,\"productName\":\"格力（GREE）KFR-32GW/(32556)FNPa-4  小1.5匹 凯迪斯变频系列 冷暖空调\"},{\"orderId\":77817009,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":383914,\"productName\":\"格力（GREE）室外机 KFR-32W/FNC03-4（对应的室内机为：KFR-32GW/(32556)FNPa-4）\"}]},{\"id\":100048,\"orderId\":78305711,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":78305711,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":325242,\"productName\":\"清华同方（THTF） LC-32B82E  32英寸 高清 LCD液晶电视 黑色\"},{\"orderId\":78305711,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":339311,\"productName\":\"清华同方电视底座PE-32001S或PE-32001\"}]},{\"id\":100049,\"orderId\":78558682,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":78558682,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":337621,\"productName\":\"索尼（SONY） KLV-32BX320 32英寸 高清液晶电视 黑色\"}]},{\"id\":100050,\"orderId\":81630452,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":81630452,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":349061,\"productName\":\"松下 NR-B23SP1-S   230L  双开冰箱（典雅银）\"}]},{\"id\":100051,\"orderId\":84951729,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":84951729,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":149617,\"productName\":\"美菱冰箱BCD-221CHC高光拉丝银\"}]},{\"id\":100052,\"orderId\":87875323,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344592411000,\"productList\":[{\"orderId\":87875323,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":158271,\"productName\":\"创维（Skyworth）32L05HR 32英寸 蓝光高清液晶电视（内置底座）\"}]},{\"id\":100053,\"orderId\":65697781,\"userErp\":\"bjwhs\",\"userName\":\"韦怀树\",\"lossTime\":1344595649000,\"productList\":[{\"orderId\":65697781,\"lossType\":1,\"productQuantity\":2,\"lossQuantity\":2,\"productId\":248713,\"productName\":\"海尔 (haier)  电热水器 ES80H-D1(E)\"},{\"orderId\":65697781,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":248707,\"productName\":\"海尔 (haier)  电热水器 ES40H-C1(E)\"}]},{\"id\":100054,\"orderId\":68763256,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344595649000,\"productList\":[{\"orderId\":68763256,\"lossType\":1,\"productQuantity\":2,\"lossQuantity\":2,\"productId\":282695,\"productName\":\"海尔（haier）KFR-35GW/03GCC12 1.5匹 壁挂式家用冷暖空调（白色）\"},{\"orderId\":68763256,\"lossType\":1,\"productQuantity\":2,\"lossQuantity\":2,\"productId\":282697,\"productName\":\"海尔（haier）KFR-35GW/0312室外机（对应的室内机为：KFR-35GW/0312）\"}]},{\"id\":100055,\"orderId\":75761889,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344595649000,\"productList\":[{\"orderId\":75761889,\"lossType\":1,\"productQuantity\":2,\"lossQuantity\":2,\"productId\":336153,\"productName\":\"TCL  KFRd-32GW/DR22 小1.5匹 壁挂式冷暖空调\"},{\"orderId\":75761889,\"lossType\":1,\"productQuantity\":2,\"lossQuantity\":2,\"productId\":336166,\"productName\":\"TCL 室外机 KFR-32W0329（对应的室内机为：KFRd-32G/DR22）\"}]},{\"id\":100056,\"orderId\":37759145,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344823754000,\"productList\":[{\"orderId\":37759145,\"lossType\":0,\"productQuantity\":1,\"lossQuantity\":0,\"productId\":155717,\"productName\":\"海尔冰箱BCD-195TJ\"},{\"orderId\":37759145,\"lossType\":0,\"productQuantity\":2,\"lossQuantity\":2,\"productId\":197835,\"productName\":\"夏普（SHARP）LCD-46G100A  46英寸 全高清液晶电视\"}]},{\"id\":100057,\"orderId\":37717897,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344824075000,\"productList\":[{\"orderId\":37717897,\"lossType\":0,\"productQuantity\":1,\"lossQuantity\":0,\"productId\":171255,\"productName\":\"海尔（Haier）电热水器FCD-X6.6上出水\"},{\"orderId\":37717897,\"lossType\":0,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":175863,\"productName\":\"西门子（SIEMENS）洗衣机 WS08M360TI\"},{\"orderId\":37717897,\"lossType\":0,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":195246,\"productName\":\"夏普（SHARP）LCD-40G100A  40英寸 全高清液晶电视\"},{\"orderId\":37717897,\"lossType\":0,\"productQuantity\":11,\"lossQuantity\":0,\"productId\":196538,\"productName\":\"海尔（haier）XQB50-918A 全自动洗衣机 瓷白色\"},{\"orderId\":37717897,\"lossType\":0,\"productQuantity\":1,\"lossQuantity\":0,\"productId\":248712,\"productName\":\"海尔 (haier)  电热水器 ES60H-D1(E)\"},{\"orderId\":37717897,\"lossType\":0,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":253238,\"productName\":\"西门子（SIEMENS）欧式油烟机 LC32925TI\"},{\"orderId\":37717897,\"lossType\":0,\"productQuantity\":1,\"lossQuantity\":0,\"productId\":263053,\"productName\":\"三星 （SAMSUNG）UA55C6900VFXXZ 55英寸 全高清LED液晶电视\"}]},{\"id\":100058,\"orderId\":62878333,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344826951000,\"productList\":[{\"orderId\":62878333,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":408249,\"productName\":\"三洋（sanyo）XQB50-S833   5公斤 全自动洗机(亮灰色)\"}]},{\"id\":100059,\"orderId\":66189037,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344826951000,\"productList\":[{\"orderId\":66189037,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":163377,\"productName\":\"夏普（SHARP）LCD-40Z660A  40英寸 全高清液晶电视\"}]},{\"id\":100060,\"orderId\":90119579,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344826951000,\"productList\":[{\"orderId\":90119579,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":503624,\"productName\":\"格兰仕（Galanz）微波炉 P70D20P-TD（WO)白色\"}]},{\"id\":100061,\"orderId\":97076945,\"userErp\":\"BJWHS\",\"userName\":\"韦怀树\",\"lossTime\":1344826951000,\"productList\":[{\"orderId\":97076945,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":142612,\"productName\":\"32寸液晶电视底座LC3266DZ（赠品）\"},{\"orderId\":97076945,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":366756,\"productName\":\"康佳 （KONKA）  LC32HS62B  绿色节能  高清液晶电视  黑色\"},{\"orderId\":97076945,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":500873,\"productName\":\"飞利浦HDMI高清线 HDMI1.4+平板电视屏幕清洁布套装\"},{\"orderId\":97076945,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":516553,\"productName\":\"TCL 4012CDS 40英寸 液晶电视 全高清 双HDMI 珠光黑\"},{\"orderId\":97076945,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":517277,\"productName\":\"TCL  液晶电视机底座 STD42C12B  黑色\"}]},{\"id\":100066,\"orderId\":178870566,\"userErp\":\"bjldil\",\"userName\":\"刘丁玲\",\"lossTime\":1345172857000,\"productList\":[{\"orderId\":178870566,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":581186,\"productName\":\"SilverStone KL04B  顶级效能直立式机箱（USB3.0/超强多硬盘散热架构）\"}]},{\"id\":100067,\"orderId\":199561071,\"userErp\":\"bjldil\",\"userName\":\"刘丁玲\",\"lossTime\":1345173527000,\"productList\":[{\"orderId\":199561071,\"lossType\":1,\"productQuantity\":1,\"lossQuantity\":1,\"productId\":410324,\"productName\":\"诺基亚（NOKIA）C5-03 标准版 3G手机（黑青色）WCDMA/GSM 非定制机\"}]}]";
		/*
		MessageDto message = new MessageDto();
		message.setContent(json);
		
		try {
			String uri = "http://localhost:222/services/order/loss";
			ClientRequest request = new ClientRequest(uri);
			request.accept(MediaType.APPLICATION_JSON);
			request.body(MediaType.APPLICATION_JSON, message);
			
			ClientResponse<JdResponse> response = request.post(JdResponse.class);
			
			System.out.println("code:" + response.getEntity().getCode());
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}
	
}
