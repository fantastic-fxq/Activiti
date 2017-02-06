package com.dynamic.parallelGateWay;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

public class parallelGateWayTest {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	/** 部署流程定义 */
	@Test
	public void deploymentProcessDifinition_inputStream() {
		InputStream inputStreambpmn = this.getClass().getResourceAsStream(
				"parallelGateWay.bpmn");
		InputStream inputStreampng = this.getClass().getResourceAsStream(
				"parallelGateWay.png");

		Deployment deployment = processEngine.getRepositoryService()
				.createDeployment()
				.addInputStream("parallelGateWay.bpmn", inputStreambpmn)
				.addInputStream("parallelGateWay.png", inputStreampng).name("并行网关")
				.deploy();

		System.out.println("部署ID--->" + deployment.getId());
		System.out.println("部署名称--->" + deployment.getName());
	}

	/** 启动流程实例 */
	@Test
	public void startProcessInstance() {
		String processDefinitionKey = "parallelGateWay";
		ProcessInstance processInstance = processEngine.getRuntimeService()
				.startProcessInstanceByKey(processDefinitionKey);
		System.out.println("流程实例ID--->" + processInstance.getId());
		System.out.println("流程定义ID--->"
				+ processInstance.getProcessDefinitionId());
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

	
//	完成当前人任务
	@Test
	public void completeCurrentPersonTask()
	{
		String taskId = "4202";
		//完成任务的同时，设置流程变量,使用流程变量用来指定完成任务之后，下一个连线
		//对应sequenceFlow.bpmnwen文件中${message=='不重要'};
		Map<String, Object>variables = new HashMap<String, Object>();
		variables.put("money", 200);
		processEngine.getTaskService().complete(taskId);//正在执行任务管理相关的Service
		System.out.println("任务完成");
		
	}
}
