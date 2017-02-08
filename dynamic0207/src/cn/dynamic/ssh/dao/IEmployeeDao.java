package cn.dynamic.ssh.dao;

import cn.dynamic.ssh.domain.Employee;


public interface IEmployeeDao {

	Employee findEmployeeByName(String name);

	

}
