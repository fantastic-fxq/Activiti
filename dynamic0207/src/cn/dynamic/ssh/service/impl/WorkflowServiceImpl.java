package cn.dynamic.ssh.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.FormService;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;

import cn.dynamic.ssh.dao.ILeaveBillDao;
import cn.dynamic.ssh.domain.LeaveBill;
import cn.dynamic.ssh.service.IWorkflowService;
import cn.dynamic.ssh.utils.SessionContext;
import cn.dynamic.ssh.web.form.WorkflowBean;

public class WorkflowServiceImpl implements IWorkflowService {
	/** 请假申请Dao */
	private ILeaveBillDao leaveBillDao;

	private RepositoryService repositoryService;

	private RuntimeService runtimeService;

	private TaskService taskService;

	private FormService formService;

	private HistoryService historyService;

	public void setLeaveBillDao(ILeaveBillDao leaveBillDao) {
		this.leaveBillDao = leaveBillDao;
	}

	public void setHistoryService(HistoryService historyService) {
		this.historyService = historyService;
	}

	public void setFormService(FormService formService) {
		this.formService = formService;
	}

	public void setRuntimeService(RuntimeService runtimeService) {
		this.runtimeService = runtimeService;
	}

	public void setTaskService(TaskService taskService) {
		this.taskService = taskService;
	}

	public void setRepositoryService(RepositoryService repositoryService) {
		this.repositoryService = repositoryService;
	}

	@Override
	public void saveNewDeploye(File file, String filename) {
		try {
			InputStream inputStream = new FileInputStream(file);
			ZipInputStream zipInputStream = new ZipInputStream(inputStream);
			repositoryService.createDeployment()// 创建部署对象
					.addZipInputStream(zipInputStream).name(filename)// 添加部署名称
					.deploy();// 完成部署
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 1.查询部署对象信息,对应表(act_re_deployment)
	@Override
	public List<Deployment> findDeploymentList() {
		return repositoryService.createDeploymentQuery()// 创建部署对象查询
				.orderByDeploymenTime().asc().list();
	}

	@Override
	public List<ProcessDefinition> findProcessDefinitionList() {
		return repositoryService.createProcessDefinitionQuery()// 创建流程定义查询
				.orderByProcessDefinitionVersion().asc().list();
	}

	@Override
	public InputStream findImageInputStream(String deploymentId,
			String imageName) {

		return repositoryService.getResourceAsStream(deploymentId, imageName);

	}

	/**
	 * 删除流程定义;
	 */
	@Override
	public void deleteProcessDefinitionByDeploymentId(String deploymentId) {

		repositoryService.deleteDeployment(deploymentId, true);
	}

	/**
	 * 更新请假状态，启动流程实例，让启动的流程实例关联业务;
	 */
	@Override
	public void saveStartProcess(WorkflowBean workflowBean) {

		// 1.获取请假单ID ,使用请假单ID，查询去请假单的对象LeaveBill
		Long id = workflowBean.getId();
		LeaveBill leaveBill = leaveBillDao.findLeaveBillById(id);
		// 2.更新请假单的请假状态从0成变1(初始录入-->审核中)
		leaveBill.setState(1);
		// 3.使用当前对象获取到流程定义的key,（对象的名称就是流程定义的key）启动流程实例
		String key = leaveBill.getClass().getSimpleName();
		// 4.从Session中获得当前任务的办理人，使用流程变量设置下一个任务的办理人；
		// inputUser是流程变量的名称
		// 获取的办理人是流程变量的值；
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("inputUser", SessionContext.get().getName());// 表示唯一用户
		/**
		 * 5 1.使用流程变量设置字符串(格式：LeaveBill.id的形式),通过设置,让启动的流程(流程实例)关联业务;
		 * 2.使用正在执行对象表中的一个字段BUSINESS_KEY(Activiti提供的一个字段),让启动的流程(流程实例)关联业务
		 */
		// 格式：LeaveBill.id的形式(使用流程变量)
		String objId = key + "." + id;
		variables.put("objId", objId);

		// 6.使用流程定义的key,启动流程实例,同时设置流程实例
		runtimeService.startProcessInstanceByKey(key, objId, variables);
	}

	@Override
	public List<Task> findTaskListByName(String name) {
		return taskService.createTaskQuery().taskAssignee(name)
				.orderByTaskCreateTime().asc().list();
	}

	// 使用任务ID，获取当前任务结点中对应的Form key中的连接的值;
	@Override
	public String findTaskFormKeyByTaskId(String taskId) {

		TaskFormData formData = formService.getTaskFormData(taskId);
		return formData.getFormKey();
	}

	// 使用任务ID，查找请假单ID，从而获得请简单信息
	@Override
	public LeaveBill findLeaveBillByTaskId(String taskId) {
		// 1.使用任务ID，查询任务对象Task
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();// 单一结果集
		// 2.使用任务对象Task获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		// 3.使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		// 4.使用流程实例对象获取BUSINESS_KEY
		String business_key = processInstance.getBusinessKey();
		// 5.获取BUSINESS_KEY对应的主键ID，使用主键ID，查询请假单对象;
		String id = "";
		if (StringUtils.isNotBlank(business_key)) {
			// 截取字符串，截取小数点后第二个值;需要转义;
			id = business_key.split("\\.")[1];
		}
		// 查询请假单对象;
		return leaveBillDao.findLeaveBillById(Long.parseLong(id));
	}

	// 二:已知任务ID,查询ProcessDefinitionEntity对象,从而获取当前任务完成之后的连线名称,并放置到List<String>集合中
	@Override
	public List<String> findOutComeListByTaskId(String taskId) {
		// 返回存放连线的名称集合
		List<String> list = new ArrayList<String>();

		// 1.使用任务ID，查询任务对象Task
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();// 单一结果集
		// ******************************************
		// 2.使用任务对象Task获取流程实例ID
		String processInstanceId = task.getProcessInstanceId();
		// 3.使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		// ******************************************
		// 2.使用任务对象Task获取流程定义ID
		String processDefinitionId = task.getProcessDefinitionId();
		// 3.查询processDefinitionEntity对象;
		ProcessDefinitionEntity processDefinitionEntity = (ProcessDefinitionEntity) repositoryService
				.getProcessDefinition(processDefinitionId);
		// 4.获取当前的活动
		ActivityImpl activityImpl = processDefinitionEntity
				.findActivity(processInstance.getActivityId());
		// 5.获取当前活动的连线
		List<PvmTransition> pvtList = activityImpl.getOutgoingTransitions();
		if (pvtList != null && pvtList.size() > 0) {
			for (PvmTransition pvmTransition : pvtList) {
				String name = (String) pvmTransition.getProperty("name");
				if (StringUtils.isNotBlank(name)) {
					list.add(name);
				} else {
					list.add("默认提交");

				}
			}
		}
		return list;
	}

	/**
	 * 完成任务,指定连线的名称;
	 */
	@Override
	public void saveSubmitTask(WorkflowBean workflowBean) {
		// 获取任务ID
		String taskId = workflowBean.getTaskId();
		// 获取连线名称
		String outcome = workflowBean.getOutcome();
		// 请假单Id
		Long id = workflowBean.getId();
		//批注信息
		String comment= workflowBean.getComment();

		// 使用任务ID，等任务对象，获得流程实例ID
		Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
		// 获得流程实例ID
		String processInstanceId = task.getProcessInstanceId();

		/**
		 * 添加批注的时候，底层代码是使用comment.setUserId(userId)
		 * 需要从Session中获取当前登录人,作为该任务的办理人（对应审核人）,对应act_hi_comment表中的User_ID字段;
		 * 如果不添加审核人，该字段为Null;
		 * 所以要求添加配置执行使用Authentication.setAuthenticatedUserId();添加当前任务的审核人;
		 * 
		 */
		Authentication.setAuthenticatedUserId(SessionContext.get().getName());

		/**
		 * 在完成之前，添加一个批注信息，用于记录对当前申请人的一些审核信息
		 * 向act_hi_comment表中添加数据,用于记录对当前申请人的一些审核信息
		 */
		taskService.addComment(taskId, processInstanceId,comment);

		/**
		 * 1.如果连线的名称是"默认提交",那么就不需要设置,如果不是，就需要设置流程变量
		 * 完成任务之前,设置流程变量，就是按照连线的名称，去完成任务 流程变量名称：outcome 流程变量的值：连线的名称
		 */
		Map<String, Object> variables = new HashMap<String, Object>();
		if (outcome != null && !outcome.equals("默认提交")) {
			variables.put("outcome", outcome);
		}

		/**
		 * 2.使用任务Id,完成当前人的个人任务
		 */
		taskService.complete(taskId, variables);

		/**
		 * 3.任务完成之后，需要指定下一个任务的办理人（shiyong ）
		 */

		/**
		 * 判断流程是否结束,如果流程结束了，更新请假单表的状态，从1到2，审核完毕;
		 */
		ProcessInstance processInstance = runtimeService
				.createProcessInstanceQuery()//
				.processInstanceId(processInstanceId)//
				.singleResult();
		//流程结束
		if (processInstance == null) {
			//更新请假单表的状态从1变成2(审核中--->审核完成)
			LeaveBill leaveBill = leaveBillDao.findLeaveBillById(id);
			leaveBill.setState(2);
		}
	}
}
