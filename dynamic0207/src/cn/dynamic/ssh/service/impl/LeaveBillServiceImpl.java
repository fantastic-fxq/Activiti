package cn.dynamic.ssh.service.impl;

import java.util.List;

import cn.dynamic.ssh.dao.ILeaveBillDao;
import cn.dynamic.ssh.domain.LeaveBill;
import cn.dynamic.ssh.service.ILeaveBillService;
import cn.dynamic.ssh.utils.SessionContext;

public class LeaveBillServiceImpl implements ILeaveBillService {

	private ILeaveBillDao leaveBillDao;

	public void setLeaveBillDao(ILeaveBillDao leaveBillDao) {
		this.leaveBillDao = leaveBillDao;
	}

	/**
	 * 查询自己的请假单的信息
	 */
	@Override
	public List<LeaveBill> findLeaveBillList() {
		return leaveBillDao.findLeaveBillList();

	}

	// 保存请假单
	@Override
	public void saveLeaveBill(LeaveBill leaveBill) {

		// 获取请假单ID
		Long id = leaveBill.getId();
		if (id == null) {
			// 1.从Session中获取当前用户对象,讲LeaveBill对象中的user和Session中用户进行管理
			leaveBill.setUser(SessionContext.get());// 建立管理关系
			// 2.保存请假单，添加一条数据
			leaveBillDao.saveLeaveBill(leaveBill);
		} else {
			
//	 		<input type="hidden" name="user.id"/>
			//页面设置了隐藏字段，不需要session中获得User进行关联；
			// 执行update操作，完成更新
			leaveBillDao.updateLeaveBill(leaveBill);

		}
	}

	@Override
	public LeaveBill findLeaveBillById(Long id) {
		return leaveBillDao.findLeaveBillById(id);
	}

	
	@Override
	public void deleteLeaveBillById(Long id) {
		leaveBillDao.deleteLeaveBillById(id);
	}
}
