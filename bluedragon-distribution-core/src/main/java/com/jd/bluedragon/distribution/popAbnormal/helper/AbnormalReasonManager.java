package com.jd.bluedragon.distribution.popAbnormal.helper;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.PropertiesHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service("abnormalReasonManager")
public class AbnormalReasonManager implements InitializingBean {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	private Properties properties;
	private static final String ID = "id";
	private static final String ELEMENT = "element";
	private static final String NAME = "name";

	Map<Integer, AbnormalReason> cachedAbnormalReasons = new HashMap<Integer, AbnormalReason>();
	List<AbnormalReason> cachedMainAbnormalReasons = new ArrayList<AbnormalReason>();

	private void load() {
		InputStream inputStream = null;
		try {
			inputStream = PropertiesHelper.class
					.getResourceAsStream("/configured/"
							+ Constants.POPABNORMAL_CONFIGNAME);
			this.properties = new Properties();
			this.properties.load(inputStream);
		} catch (IOException e) {
			log.error("AbnormalReasonManager load error", e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error("AbnormalReasonManager close inputStream error", e);
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void calc() {
		Enumeration enums = properties.propertyNames();
		Map<Integer, AbnormalReason> allChildMap = new HashMap<Integer, AbnormalReason>();
		while (enums.hasMoreElements()) {
			String key = (String) enums.nextElement();
			if (key.contains(ID)) {
				String codeKey = properties.getProperty(key);
				Integer code = Integer.parseInt(codeKey);
				String nameKey = new StringBuilder().append(ELEMENT)
						.append(".").append(code).append(".").append(NAME)
						.toString();
				String name = properties.getProperty(nameKey);
				AbnormalReason reason = new AbnormalReason();
				reason.setCode(code);
				reason.setName(name);
				Integer mainType = getMainTypeBySubType(code);
				if (mainType == 0) {
					// mainType element
					cachedAbnormalReasons.put(code, reason);
					cachedMainAbnormalReasons.add(reason);
				} else {
					allChildMap.put(code, reason);
				}
			}
		}
		
		for (Map.Entry<Integer, AbnormalReason> entry : allChildMap.entrySet()) {
			Integer childKey = entry.getKey();
			Integer mainType = getMainTypeBySubType(childKey);
			AbnormalReason parentReason = cachedAbnormalReasons.get(mainType);
			if (parentReason == null) {
				continue;
			}
			List<AbnormalReason> childs = parentReason.getClilds();
			if (childs == null) {
				// first child elements
				childs = new ArrayList<AbnormalReason>();
				childs.add(allChildMap.get(childKey));
				parentReason.setClilds(childs);
			} else {
				childs.add(allChildMap.get(childKey));
			}
		}
		
		// 排序
		Collections.sort(cachedMainAbnormalReasons);
		for (AbnormalReason abnormalReason : cachedMainAbnormalReasons) {
			if (abnormalReason.getClilds() != null && abnormalReason.getClilds().size() > 0) {
				Collections.sort(abnormalReason.getClilds());
			}
		}
	}

	/**
	 * SubType examples: 201,301 
	 * MainType examples: 2,3
	 * @param subType
	 * @return
	 */
	private Integer getMainTypeBySubType(Integer subType) {
		return subType / 100;
	}

	public void afterPropertiesSet() throws Exception {
		load();
		calc();
	}

	/**
	 * May return null, means no available reasons
	 * @return
	 */
	public List<AbnormalReason> getMainReasons()  {
		return cachedMainAbnormalReasons;
	}

	/**
	 * May return null, means no available reason for provided mainType
	 * @return
	 */
	public List<AbnormalReason> getSubReasonsByMainType(Integer mainType) {
		try {
			return cachedAbnormalReasons.get(mainType).getClilds();
		} catch (Exception e) {
			log.error("getSubReasonsByMainType error: mainType[{}]",mainType , e);
			return null;
		}
	}
}
