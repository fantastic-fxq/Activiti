package com.dynamic.processInstance;

import java.io.InputStream;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;

/**
 * @author fxq
 *
 */
public class ProcessInstanceTest {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	/** 1.部署流程定义 */
	@Test
	public void deploymentProcessDefinition_zip() {
		InputStream inputStream = this.getClass().getClassLoader()
				.getResourceAsStream("diagrams/helloworld.zip");
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		processEngine.getRepositoryService().createDeployment()
				.addZipInputStream(zipInputStream).name("流程定义").deploy();

	}

	/* 2.启动流程实例 */
	@Test
	public void startProcessInstance() {
		String processDefinitionKey = "helloworld";
		ProcessInstance pInstance = processEngine.getRuntimeService()// 与正在执行的流程实例相关的Service，默认是最新版本启动
				.startProcessInstanceByKey(processDefinitionKey);
		System.out.println("流程实例ID--->" + pInstance.getId());// 流程实例ID--->101
		System.out.println("流程定义ID--->" + pInstance.getProcessDefinitionId());// 流程定义ID--->helloworld:1:4

	}

	/* 3.查询当前人的任务 */
	@Test
	public void findCurrentUserTask() {
		String assignee = "李一鸣";
		List<Task> list = processEngine.getTaskService()
				// 用于任务管理
				.createTaskQuery()
				// 创建任务查询对象
				.taskAssignee(assignee)
				.list();

		if (list != null && list.size() > 0) {
			for (Task task : list) {
				System.out.println("任务ID--->" + task.getId());
				System.out.println("任务名称--->" + task.getName());
				System.out.println("任务的创建时间--->" + task.getCreateTime());
				System.out.println("任务的办理人--->" + task.getAssignee());
				System.out.println("流程实例ID--->" + task.getProcessInstanceId());
				System.out.println("执行对象ID--->" + task.getExecutionId());
				System.out
						.println("流程定义ID--->" + task.getProcessDefinitionId());
				System.out.println("##################################");
			}

		}

	}

	/* 完成当前人的任务 */
	@Test
	public void completeCurrentUserTask() {
		String taskId = "1502";
		processEngine.getTaskService().complete(taskId);
		System.out.println("完成任务");

	}

	/* 查看流程状态(判断流程是正在执行，还是已经结束) */
	@Test
	public void queryProcessState() {
		String processInstanceId = "1002";
		ProcessInstance processInstance = processEngine.getRuntimeService()// 正在执行的流程实例和执行对象
				.createProcessInstanceQuery()// 创建流程实例查询
				.processInstanceId(processInstanceId).singleResult();

		if (processInstance != null) {
			System.out.println("流程正在执行！");
		} else {
			System.out.println("流程已经结束！");
		}

	}

	/* 查询历史任务 */
	@Test
	public void findHistoryTask() {
		String taskAssignee = "范晓权";
		List<HistoricTaskInstance> list = processEngine.getHistoryService()// 历史相关的Service
				.createHistoricTaskInstanceQuery()// 创建历史任务实例查询
				.taskAssignee(taskAssignee).list();

		if (list != null && list.size() > 0) {
			for (HistoricTaskInstance historicTaskInstance : list) {
				System.out.println("历史任务ID--->" + historicTaskInstance.getId());
				System.out.println("历史流程实例ID--->"
						+ historicTaskInstance.getProcessInstanceId());
				System.out.println("历史任务办理人--->"
						+ historicTaskInstance.getAssignee());
				System.out.println("执行对象ID--->"
						+ historicTaskInstance.getExecutionId());
				System.out.println(historicTaskInstance.getStartTime() + " "
						+ historicTaskInstance.getEndTime() + " "
						+ historicTaskInstance.getDurationInMillis());
			}
		}

	}
	
	
	
	/*查询历史流程实例*/
	@Test
	public void findHistoryProcessInstance()
	{
		String processInstanceId = "801";
		List<HistoricProcessInstance> list= 	processEngine.getHistoryService()
		.createHistoricProcessInstanceQuery()
		.processInstanceId(processInstanceId)
		.list();
		
		if (list != null && list.size() > 0) {
		for (HistoricProcessInstance historicProcessInstance : list) {
				System.out.println("历史任务ID--->" + historicProcessInstance.getId());
				System.out.println(historicProcessInstance.getStartTime() + " "
						+ historicProcessInstance.getEndTime() + " "
						+ historicProcessInstance.getDurationInMillis());
			}
		}
		
		
		
	}
	

}
