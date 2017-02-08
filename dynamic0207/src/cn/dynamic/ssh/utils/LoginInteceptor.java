package cn.dynamic.ssh.utils;

import org.apache.commons.io.output.NullWriter;

import cn.dynamic.ssh.domain.Employee;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * 登录验证拦截器
 *
 */
@SuppressWarnings("serial")
public class LoginInteceptor implements Interceptor {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	// 每次访问action之前先执行拦截器
	@Override
	public String intercept(ActionInvocation invocation) throws Exception {
		// 获取当前访问Action的URL
		String actionName = invocation.getProxy().getActionName();
		if (!actionName.equals("loginAction_login")) {//刚开始登陆的时候没有Session要放行;

			// 从Session中获取当前用户对象;
			Employee employee = SessionContext.get();
			if (employee == null) {
				return "login";
			}
		}
		// 放行，访问Action类中的方法;
		return invocation.invoke();

	}

}
