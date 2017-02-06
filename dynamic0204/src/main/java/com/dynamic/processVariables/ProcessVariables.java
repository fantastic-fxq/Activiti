package com.dynamic.processVariables;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricVariableInstance;
import org.junit.Test;

import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * @author fxq
 *
 */
public class ProcessVariables {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	/* 部署流程定义 */
	@Test
	public void deploymentProcessDefinition_classpath() {
		processEngine.getRepositoryService().createDeployment()
				.addClasspathResource("diagrams/processVariables.bpmn")
				.addClasspathResource("diagrams/processVariables.png")
				.name("流程参数").deploy();
	}

	/* 启动流程定义 */
	@Test
	public void startProcessDefiniton() {
		String processDefinitionKey = "processVariables";
		processEngine.getRuntimeService().startProcessInstanceByKey(
				processDefinitionKey);
	}

	@Test
	public void setVariables() {
		/* 与任务(正在执行) */
		TaskService taskService = processEngine.getTaskService();
		// 任务ID
		String taskId = "1804";
		// 一.设置流程变量，使用基本数据类型
/*		taskService.setVariable(taskId, "请假天数", 3);
		taskService.setVariable(taskId, "请假日期", new Date());
		taskService.setVariable(taskId, "请假原因", "回家探亲");*/

		//二.设置流程变量，使用JavaBean类型
		Person person = new Person();
		person.setId(20);
		person.setName("翠花");
		taskService.setVariable(taskId, "人员信息(添加固定版本)", person);
		
		System.out.println("设置流程变量成功！");
	}

	@Test
	public void getVariables() {
		//与任务(正在执行)
		TaskService taskService = processEngine.getTaskService();
		
		String taskId= "1804";

	 System.out.println(taskService.getVariable(taskId, "人员信息(添加固定版本)").toString());
	}

	/* 模拟设置和获取流程变量的场景 */
	public void SetAndGetVariables() {
		// 与流程实例
		RuntimeService runtimeService = processEngine.getRuntimeService();

		TaskService taskService = processEngine.getTaskService();

	}
	
	
//	完成个人当前任务
	@Test
	public void completeMyPersonTask()
	{
		String taskId="1804";
		processEngine.getTaskService().complete(taskId);
	}
	
	//查询历史参数
	@Test
	public void findHistoryProcessVariable()
	{
		List<HistoricVariableInstance> list=	processEngine.getHistoryService()
		.createHistoricVariableInstanceQuery()
		.variableName("请假天数")
		.list();
		
		
		if(list!=null && list.size()>0)
		{
			for (HistoricVariableInstance historicVariableInstance : list) {
				System.out.println("参数ID--->"+historicVariableInstance.getId());
				System.out.println("参数名称--->" + historicVariableInstance.getVariableName());
				System.out.println("参数类型--->"+historicVariableInstance.getVariableTypeName());
				System.out.println("#############################");
			}
		}
		
		
		
		
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
