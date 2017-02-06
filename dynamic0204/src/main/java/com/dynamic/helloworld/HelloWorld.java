package com.dynamic.helloworld;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;


public class HelloWorld {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	Deployment deployment = null;

	/** 1.部署流程定义 **/
	@Test
	public void deploymentProcessDefinition() {
		deployment = processEngine.getRepositoryService()// 与流程定义和部署对象相关的Service
				.createDeployment()// 创建一个部署对象,对流程文件进行部署
				.name("helloworld入门程序")// 添加部署的名称
				.addClasspathResource("diagrams/helloworld.bpmn")// 从classpath的资源中加载，一次只能加载一个文件;
				.addClasspathResource("diagrams/helloworld.png").deploy();// 执行部署操作,将添加的资源全部写入到数据库中

		System.out.println("部署ID--->" +deployment.getId());
		System.out.println("部署名称--->"+deployment.getName());
	}

	/** 2.启动流程实例 */
	@Test
	public void startProcessInstance() {

		RuntimeService runtimeService = processEngine.getRuntimeService();//与正在执行的流程实例和执行对象相关的Service

		/*
		 * RepositoryService repositoryService=processEngine.getRepositoryService();
		 *  查找流程定义 
		 *  ProcessDefinition pd=repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult(); 
		 * 启动流程
		 * runtimeService.startProcessInstanceById(pd.getId());
		 */
		// 使用ByKey方法，不需要到数据库中重新查找流程定义数据。直接更加描述文件中定义的process结点的id属性来启动流程；
	    //使用流程定义的key启动流程实例，key对应helloworld.bpmn文件中id的属性值，
		//使用key值启动，默认是按照最新版本的流程定义启动
		String processDefinitionKey= "helloworld";
		ProcessInstance pInstance=	runtimeService.startProcessInstanceByKey(processDefinitionKey);
		
		System.out.println("流程实例ID--->"+pInstance.getId());//流程实例ID--->101
		System.out.println("流程定义ID--->"  + pInstance.getProcessDefinitionId());//流程定义ID--->helloworld:1:4
		
	}
	
	/**查询当前人的个人任务*/
	@Test
	public void findMyPersonalTask()
	{
		String assignee = "王五";
		List<Task> list= processEngine.getTaskService()//与正在执行的任务管理相关的Service;
		.createTaskQuery()//创建任务查询对象
		.taskAssignee(assignee)//指定个人任务查询,指定办理人
		.list();
		
		if(list!=null && list.size()>0)
		{
			for (Task task : list) {
				System.out.println("任务ID--->"+task.getId());
				System.out.println("任务名称--->"+task.getName());
				System.out.println("任务的创建时间--->"+task.getCreateTime());
				System.out.println("任务的办理人--->" +task.getAssignee());
				System.out.println("流程实例ID--->"+task.getProcessInstanceId());
				System.out.println("执行对象ID--->"+task.getExecutionId());
				System.out.println("流程定义ID--->"+task.getProcessDefinitionId());
				System.out.println("##################################");
			}
		}
	}
	
	/**完成我的任务*/
	@Test
	public void completeMyPersonalTask()
	{
		String taskId= "302";
		processEngine.getTaskService()//与正在执行的任务管理相关的Service;
		.complete(taskId);
		System.out.println("完成任务：任务ID--->"+ taskId );
	}
	
	
	
}
