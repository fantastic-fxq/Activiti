package cn.dynamic.ssh.web.action;

import java.util.List;

import cn.dynamic.ssh.domain.LeaveBill;
import cn.dynamic.ssh.service.ILeaveBillService;
import cn.dynamic.ssh.utils.ValueContext;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;

@SuppressWarnings("serial")
public class LeaveBillAction extends ActionSupport implements
		ModelDriven<LeaveBill> {

	private LeaveBill leaveBill = new LeaveBill();

	@Override
	public LeaveBill getModel() {
		return leaveBill;
	}

	private ILeaveBillService leaveBillService;

	public void setLeaveBillService(ILeaveBillService leaveBillService) {
		this.leaveBillService = leaveBillService;
	}

	/**
	 * 请假管理首页显示
	 * 
	 * @return
	 */
	public String home() {
		// 1.查询所有的请假信息(对应a_leavebill),返回List<LeaveBill>;
		List<LeaveBill> lbList = leaveBillService.findLeaveBillList();
		// 放置到上下文对象中
		if (lbList != null && lbList.size() > 0) {
			ValueContext.putValueContext("lbList", lbList);
		}

		return "home";
	}

	/**
	 * 添加请假申请
	 * 
	 * @return
	 */
	public String input() {
		//获取传递请假单ID的值
		Long id = leaveBill.getId();
		//修改
		if(id!=null)
		{
			//使用请假单Id,查询请假单信息,
			LeaveBill leaveBill = leaveBillService.findLeaveBillById(id);
			//将请假单信息放置到栈顶，页面使用Struts的标签,支持表单回显
			ValueContext.putValueStack(leaveBill);
		}
		//新增
		return "input";
	}

	/**
	 * 保存/更新，请假申请
	 * 
	 * */
	public String save() {

		leaveBillService.saveLeaveBill(leaveBill);
		return "save";
	}

	/**
	 * 删除，请假申请
	 * 
	 * */
	public String delete() {
		//获取请假单ID
		Long id = leaveBill.getId();
		//执行删除
		leaveBillService.deleteLeaveBillById(id);
		return "save";
	}

}
