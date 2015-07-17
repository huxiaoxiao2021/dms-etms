package com.jd.bluedragon.distribution.receive.service.impl;

import java.util.List;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jd.bluedragon.distribution.receive.dao.TurnoverBoxDao;
import com.jd.bluedragon.distribution.receive.domain.TurnoverBox;
import com.jd.bluedragon.distribution.receive.service.TurnoverBoxService;

@Service
public class TurnoverBoxServiceImpl implements TurnoverBoxService {
	@Autowired
	private TurnoverBoxDao turnoverBoxDao;

	@Override
	@Profiled(tag = "TurnoverBoxService.getTurnoverBoxList")
	public List<TurnoverBox> getTurnoverBoxList(TurnoverBox turnoverBox) {
		return this.turnoverBoxDao.getTurnoverBoxList(turnoverBox);
	}

	@Override
	@Profiled(tag = "TurnoverBoxService.getCount")
	public int getCount(TurnoverBox turnoverBox) {
		return this.turnoverBoxDao.getCount(turnoverBox);
	}

}
