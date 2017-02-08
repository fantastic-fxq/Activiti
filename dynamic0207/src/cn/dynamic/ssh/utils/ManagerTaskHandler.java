package cn.dynamic.ssh.utils;

import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.struts2.ServletActionContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import cn.dynamic.ssh.domain.Employee;
import cn.dynamic.ssh.service.IEmployeeService;
import cn.dynamic.ssh.service.impl.EmployeeServiceImpl;

/**
 * 员工经理任务分配
 *
 */
@SuppressWarnings("serial")
public class ManagerTaskHandler implements TaskListener {

	@Override
	public void notify(DelegateTask delegateTask) {
		// 从Session中获取当前用户;重新查询当前用户，再获取当前用户应对的领导
		Employee employee = SessionContext.get();
		String name = employee.getName();
		// 使用当前用户查询用户的详细信息
		// 加载容器;从Web中获取Spring容器
		WebApplicationContext ac = WebApplicationContextUtils
				.getWebApplicationContext(ServletActionContext
						.getServletContext());
		IEmployeeService employeeService = (IEmployeeService) ac.getBean("employeeService");
		//EmployeeServiceImpl employeeServiceImpl = (EmployeeServiceImpl) ac.getBean("employeeService");
		// 重新查询;
		Employee emp = employeeService.findEmployeeByName(name);
		String assignee = emp.getManager().getName();  //重新查询，Session仍然开启，可以进行关联;
		// 设置任务的办理人;
		delegateTask.setAssignee(assignee);
	}

}
