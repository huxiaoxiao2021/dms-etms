package com.jd.bluedragon.distribution.receive.service.impl;

import com.jd.bluedragon.distribution.receive.dao.TurnoverBoxDao;
import com.jd.bluedragon.distribution.receive.domain.TurnoverBox;
import com.jd.bluedragon.distribution.receive.service.TurnoverBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurnoverBoxServiceImpl implements TurnoverBoxService {
	@Autowired
	private TurnoverBoxDao turnoverBoxDao;

	@Override
	public List<TurnoverBox> getTurnoverBoxList(TurnoverBox turnoverBox) {
		return this.turnoverBoxDao.getTurnoverBoxList(turnoverBox);
	}

	@Override
	public int getCount(TurnoverBox turnoverBox) {
		return this.turnoverBoxDao.getCount(turnoverBox);
	}

}
