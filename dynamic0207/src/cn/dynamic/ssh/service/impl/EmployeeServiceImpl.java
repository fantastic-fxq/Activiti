package cn.dynamic.ssh.service.impl;

import cn.dynamic.ssh.dao.IEmployeeDao;
import cn.dynamic.ssh.service.IEmployeeService;

public class EmployeeServiceImpl implements IEmployeeService {

	private IEmployeeDao employeeDao;

	public void setEmployeeDao(IEmployeeDao employeeDao) {
		this.employeeDao = employeeDao;
	}
	
	
}
