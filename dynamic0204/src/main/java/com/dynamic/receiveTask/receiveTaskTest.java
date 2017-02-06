package com.dynamic.receiveTask;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class receiveTaskTest {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	/** 部署流程定义 */
	@Test
	public void deploymentProcessDifinition_inputStream() {
		InputStream inputStreambpmn = this.getClass().getResourceAsStream(
				"receiveTask.bpmn");
		InputStream inputStreampng = this.getClass().getResourceAsStream(
				"receiveTask.png");

		Deployment deployment = processEngine.getRepositoryService()
				.createDeployment()
				.addInputStream("receiveTask.bpmn", inputStreambpmn)
				.addInputStream("receiveTask.png", inputStreampng).name("接收任务")
				.deploy();

		System.out.println("部署ID--->" + deployment.getId());
		System.out.println("部署名称--->" + deployment.getName());
	}

	/** 启动流程实例, + 设置流程变量 + 获取流程变量 + 向后执行一步 */
	@Test
	public void startProcessInstance() {
		String processDefinitionKey = "receiveTask";
		ProcessInstance processInstance = processEngine.getRuntimeService()
				.startProcessInstanceByKey(processDefinitionKey);
		System.out.println("流程实例ID--->" + processInstance.getId());
		System.out.println("流程定义ID--->"
				+ processInstance.getProcessDefinitionId());

		// 查询执行对象ID
		Execution execution = processEngine.getRuntimeService()
				.createExecutionQuery()
				.processInstanceId(processInstance.getId())
				.activityId("receivetask1").singleResult();// 当前活动的id，对应receivetask.bpmn文件中活动节点的id的属性值;

		// 使用流程变量设置一个当日销售额，用来传递业务参数
		processEngine.getRuntimeService().setVariable(execution.getId(),
				"汇总当日销售额", 20000);

		// 向后执行一步,如果流程处于等待状态，使得流程继续执行
		processEngine.getRuntimeService().signal(execution.getId());// 向后执行一步;

		// ***************************************************************************************************
		// 查询执行对象ID
		Execution execution2 = processEngine.getRuntimeService()
				.createExecutionQuery()
				.processInstanceId(processInstance.getId())
				.activityId("receivetask2").singleResult();// 当前活动的id，对应receivetask.bpmn文件中活动节点的id的属性值;

		Integer value = (Integer) processEngine.getRuntimeService()
				.getVariable(execution2.getId(), "汇总当日销售额");
		System.out.println("给老板发送短信，金额是：" + value + "元");

		// 向后执行一步;
		processEngine.getRuntimeService().signal(execution2.getId());
	}

	/** 查询当前人的任务 */
	@Test
	public void findCurrentPersonTask() {
		String assignee = "王小五";
		List<Task> list = processEngine.getTaskService().createTaskQuery()// 任务查询对象
				.taskAssignee(assignee).list();

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
		String taskId = "4202";
		// 完成任务的同时，设置流程变量,使用流程变量用来指定完成任务之后，下一个连线
		// 对应sequenceFlow.bpmnwen文件中${message=='不重要'};
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put("money", 200);
		processEngine.getTaskService().complete(taskId);// 正在执行任务管理相关的Service
		System.out.println("任务完成");

	}
}
