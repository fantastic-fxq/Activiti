package cn.dynamic.ssh.service.impl;

import cn.dynamic.ssh.dao.ILeaveBillDao;
import cn.dynamic.ssh.service.ILeaveBillService;

public class LeaveBillServiceImpl implements ILeaveBillService {

	private ILeaveBillDao leaveBillDao;

	public void setLeaveBillDao(ILeaveBillDao leaveBillDao) {
		this.leaveBillDao = leaveBillDao;
	}

}
