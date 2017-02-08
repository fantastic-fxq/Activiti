package cn.dynamic.ssh.dao;

import java.util.List;

import cn.dynamic.ssh.domain.LeaveBill;



public interface ILeaveBillDao {

	List<LeaveBill> findLeaveBillList();

	void saveLeaveBill(LeaveBill leaveBill);

	LeaveBill findLeaveBillById(Long id);

	void deleteLeaveBillById(Long id);

	void updateLeaveBill(LeaveBill leaveBill);
}
