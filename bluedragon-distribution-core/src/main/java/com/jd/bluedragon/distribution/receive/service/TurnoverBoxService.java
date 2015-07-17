package com.jd.bluedragon.distribution.receive.service;

import java.util.List;
import com.jd.bluedragon.distribution.receive.domain.TurnoverBox;

public interface TurnoverBoxService {
	
	public List<TurnoverBox> getTurnoverBoxList(TurnoverBox turnoverBox);
	
	public int getCount(TurnoverBox turnoverBox);

}
