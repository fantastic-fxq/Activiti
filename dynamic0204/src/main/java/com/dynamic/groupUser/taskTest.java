package com.dynamic.groupUser;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class taskTest {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	/** 部署流程定义 */
	@Test
	public void deploymentProcessDifinition_inputStream() {
		InputStream inputStreambpmn = this.getClass().getResourceAsStream(
				"task.bpmn");
		InputStream inputStreampng = this.getClass().getResourceAsStream(
				"task.png");

		Deployment deployment = processEngine.getRepositoryService()
				.createDeployment()
				.addInputStream("task.bpmn", inputStreambpmn)
				.addInputStream("task.png", inputStreampng).name("组任务")
				.deploy();

		System.out.println("部署ID--->" + deployment.getId());
		System.out.println("部署名称--->" + deployment.getName());

		// 添加用户角色组
		IdentityService identityService = processEngine.getIdentityService();//
		// 创建角色
		Group group1 = new GroupEntity("总经理");
		Group group2 = new GroupEntity("部门经理");

		identityService.saveGroup(group1);
		identityService.saveGroup(group2);

		User user1 = new UserEntity("张三一");
		User user2 = new UserEntity("李四一");
		User user3 = new UserEntity("王五一");
		identityService.saveUser(user1);
		identityService.saveUser(user2);
		identityService.saveUser(user3);

		// 建立用户和角色的关联关系
		identityService.createMembership("张三一", "部门经理");
		identityService.createMembership("李四一", "部门经理");
		identityService.createMembership("王五一", "总经理");

	}

	/** 启动流程实例 */
	@Test
	public void startProcessInstance() {
		String processDefinitionKey = "task";
		ProcessInstance processInstance = processEngine.getRuntimeService()
				.startProcessInstanceByKey(processDefinitionKey);
		System.out.println("流程实例ID--->" + processInstance.getId());
		System.out.println("流程定义ID--->"
				+ processInstance.getProcessDefinitionId());
	}

	/** 查询当前人的任务 */
	@Test
	public void findCurrentGroupTask() {
		String candidateUser = "李四一";
		List<Task> list = processEngine.getTaskService().createTaskQuery()// 任务查询对象
				.taskCandidateUser(candidateUser).list();

		if (list != null && list.size() > 0) {
			for (Task task : list) {
				System.out.println("任务ID--->" + task.getId());
				System.out.println("任务名称--->" + task.getName());
				System.out.println("任务的办理人--->" + task.getAssignee());
				System.out.println("任务实例ID--->" + task.getProcessInstanceId());
				System.out.println("执行对象ID--->" + task.getExecutionId());
				System.out
						.println("实例定义ID--->" + task.getProcessDefinitionId());
				System.out.println("###############################");
			}
		}

	}

	// 完成当前人任务
	@Test
	public void completeCurrentPersonTask() {
		String taskId = "4704";
		// 完成任务的同时，设置流程变量,使用流程变量用来指定完成任务之后，下一个连线
		// 对应sequenceFlow.bpmnwen文件中${message=='不重要'};
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("money", 200);
		processEngine.getTaskService().complete(taskId);// 正在执行任务管理相关的Service
		System.out.println("任务完成");

	}

	// 拾取任务,将组任务分给个人任务,指定任务的办理人字段
	@Test
	public void claim() {
		// 将组任务分配给个人任务
		String taskId = "4704";
		String userId = "张三一";
		processEngine.getTaskService().setAssignee(taskId, userId);

	}
}
