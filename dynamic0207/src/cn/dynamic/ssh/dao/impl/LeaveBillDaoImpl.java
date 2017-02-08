package cn.dynamic.ssh.dao.impl;

import java.util.List;

import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import cn.dynamic.ssh.dao.ILeaveBillDao;
import cn.dynamic.ssh.domain.Employee;
import cn.dynamic.ssh.domain.LeaveBill;
import cn.dynamic.ssh.utils.SessionContext;

public class LeaveBillDaoImpl extends HibernateDaoSupport implements
		ILeaveBillDao {

	@Override
	public List<LeaveBill> findLeaveBillList() {
		// 从Session中获得当前用户
		Employee employee = SessionContext.get();

		String hql = "FROM LeaveBill o WHERE o.user=?";

		return this.getHibernateTemplate().find(hql, employee);
	}

	/**
	 * 增加一条请假业务
	 */
	@Override
	public void saveLeaveBill(LeaveBill leaveBill) {
		this.getHibernateTemplate().save(leaveBill);
	}

	@Override
	public LeaveBill findLeaveBillById(Long id) {
		// String hql = "FROM LeaveBill o WHERE o.id=?";
		// return (LeaveBill) this.getHibernateTemplate().find(hql, id).get(0);

		return this.getHibernateTemplate().get(LeaveBill.class, id);
	}

	// 更新请假单
	@Override
	public void updateLeaveBill(LeaveBill leaveBill) {

		this.getHibernateTemplate().update(leaveBill);
	}

	@Override
	public void deleteLeaveBillById(Long id) {
		LeaveBill leaveBill = this.findLeaveBillById(id);
		this.getHibernateTemplate().delete(leaveBill);
	}

}
