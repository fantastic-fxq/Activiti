package cn.dynamic.ssh.web.action;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.struts2.ServletActionContext;

import cn.dynamic.ssh.domain.LeaveBill;
import cn.dynamic.ssh.service.ILeaveBillService;
import cn.dynamic.ssh.service.IWorkflowService;
import cn.dynamic.ssh.utils.SessionContext;
import cn.dynamic.ssh.utils.ValueContext;
import cn.dynamic.ssh.web.form.WorkflowBean;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;


@SuppressWarnings("serial")
public class WorkflowAction extends ActionSupport implements
		ModelDriven<WorkflowBean> {

	private WorkflowBean workflowBean = new WorkflowBean();

	@Override
	public WorkflowBean getModel() {
		return workflowBean;
	}

	private IWorkflowService workflowService;

	private ILeaveBillService leaveBillService;

	public void setLeaveBillService(ILeaveBillService leaveBillService) {
		this.leaveBillService = leaveBillService;
	}

	public void setWorkflowService(IWorkflowService workflowService) {
		this.workflowService = workflowService;
	}

	/**
	 * 部署管理首页显示
	 * 
	 * @return
	 */
	public String deployHome() {

		// 1.查询部署对象信息,对应表(act_re_deployment)
		List<Deployment> depList = workflowService.findDeploymentList();
		// 2.查询流程定义的信息,对应表(act_re_procdef)
		List<ProcessDefinition> pdList = workflowService
				.findProcessDefinitionList();

		// 放置到上下文对象中
		ValueContext.putValueContext("depList", depList);
		ValueContext.putValueContext("pdList", pdList);
		return "deployHome";
	}

	/**
	 * 发布流程
	 * 
	 * @return
	 */
	public String newdeploy() {
		// 获取页面传递的值
		// 1.获取页面上传递的zip格式的文件,格式时File类型
		File file = workflowBean.getFile();
		String filename = workflowBean.getFilename();
		// 完成部署
		workflowService.saveNewDeploye(file, filename);
		return "list";
	}

	/**
	 * 删除部署信息
	 */
	public String delDeployment() {
		String deploymentId = workflowBean.getDeploymentId();

		workflowService.deleteProcessDefinitionByDeploymentId(deploymentId);
		return "list";
	}

	/**
	 * 查看流程图
	 * 
	 * @throws IOException
	 */
	public String viewImage() throws IOException {

		// 从资源文件中获得数据流
		// 将图写到页面上,
		String deploymentId = workflowBean.getDeploymentId();
		String imageName = workflowBean.getImageName();

		InputStream in = workflowService.findImageInputStream(deploymentId,
				imageName);
		// 从Response对象获取输出流
		OutputStream outputStream = ServletActionContext.getResponse()
				.getOutputStream();

		// 4.将输入流中的数据读取出来，写入到输出流
		for (int i = -1; (i = in.read()) != -1;) {
			outputStream.write(i);
		}
		outputStream.close();
		return null;
	}

	// 启动流程
	public String startProcess() {
		// 更新请假状态，启动流程实例，让启动的流程实例关联业务;
		workflowService.saveStartProcess(workflowBean);

		return "listTask";
	}

	/**
	 * 任务管理首页显示
	 * 
	 * @return
	 */
	public String listTask() {

		// 1.从Session中获取当前用户名
		String name = SessionContext.get().getName();
		// 2.使用当前用户名查询正在执行的任务列表，获取当前任务的集合List<Task>
		List<Task> lisTasks = workflowService.findTaskListByName(name);
		ValueContext.putValueContext("lisTasks", lisTasks);
		return "task";
	}

	/**
	 * 打开任务表单
	 */
	public String viewTaskForm() {
		// 任务ID
		String taskId = workflowBean.getTaskId();
		// 获取任务表单中任务结点的url连接;
		String url = workflowService.findTaskFormKeyByTaskId(taskId);
		url += "?taskId=" + taskId;
		// 获取任务表单中任务结点的url连接
		ValueContext.putValueContext("url", url);
		return "viewTaskForm";
	}

	// 准备表单数据
	public String audit() {
		// 获取任务ID
		String taskId = workflowBean.getTaskId();
		/**
		 * 一:使用任务ID，查找请假单ID，从而获得请简单信息
		 */
		LeaveBill leaveBill = workflowService.findLeaveBillByTaskId(taskId);

		ValueContext.putValueStack(leaveBill);
		
		/**
		 * 二:已知任务ID,查询ProcessDefinitionEntity对象,从而获取当前任务完成之后的连线名称,并放置到List<String>集合中
		 */
		List<String> outcomeList = workflowService.findOutComeListByTaskId(taskId);
		ValueContext.putValueContext("outcomeList", outcomeList);
		/**
		 * 三.查询所有历史审核人的审核信息,帮助当前人完成审核,返回List<Comment>
		 */
		List<Comment> commentList = null;
		ValueContext.putValueContext("commentList", commentList);
	
		return "taskForm";
	}

	/**
	 * 提交任务
	 */
	public String submitTask() {
		//使用任务ID,完成当前人的个人任务;

		workflowService.saveSubmitTask(workflowBean);
		return "listTask";
	}

	/**
	 * 查看当前流程图（查看当前活动节点，并使用红色的框标注）
	 */
	public String viewCurrentImage() {
		return "image";
	}

	// 查看历史的批注信息
	public String viewHisComment() {
		return "viewHisComment";
	}
}
