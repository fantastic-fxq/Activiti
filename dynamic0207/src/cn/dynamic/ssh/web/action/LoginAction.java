package cn.dynamic.ssh.web.action;

import cn.dynamic.ssh.domain.Employee;
import cn.dynamic.ssh.service.IEmployeeService;
import cn.dynamic.ssh.utils.SessionContext;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

@SuppressWarnings("serial")
public class LoginAction extends ActionSupport implements ModelDriven<Employee> {

	private Employee employee = new Employee();

	@Override
	public Employee getModel() {
		return employee;
	}

	private IEmployeeService employeeService;

	public void setEmployeeService(IEmployeeService employeeService) {
		this.employeeService = employeeService;
	}

	/**
	 * 登录
	 * 
	 * @return
	 */
	public String login() {
		// 获取用户名
		String name = employee.getName();
		// 使用用户名作为查询条件，查询员工表,获取当前用户名对应的信息
		Employee employee = employeeService.findEmployeeByName(name);

		// 将查询的对象(唯一)放置到Session中
		SessionContext.setUser(employee);
		return "success";
	}

	/**
	 * 标题
	 * 
	 * @return
	 */
	public String top() {
		return "top";
	}

	/**
	 * 左侧菜单
	 * 
	 * @return
	 */
	public String left() {
		return "left";
	}

	/**
	 * 主页显示
	 * 
	 * @return
	 */
	public String welcome() {
		return "welcome";
	}

	public String logout() {
		//清空Session
		SessionContext.setUser(null);
		return "login";
	}
}
