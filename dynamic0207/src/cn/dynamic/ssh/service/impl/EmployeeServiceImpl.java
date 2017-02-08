package cn.dynamic.ssh.service.impl;

import cn.dynamic.ssh.dao.IEmployeeDao;
import cn.dynamic.ssh.domain.Employee;
import cn.dynamic.ssh.service.IEmployeeService;

public class EmployeeServiceImpl implements IEmployeeService {

	private IEmployeeDao employeeDao;

	public void setEmployeeDao(IEmployeeDao employeeDao) {
		this.employeeDao = employeeDao;
	}

	/**
	 * 使用用户名作为查询条件，查询用户对象
	 */
	@Override
	public Employee findEmployeeByName(String name) {
		return employeeDao.findEmployeeByName(name);
	}
	

	
}
