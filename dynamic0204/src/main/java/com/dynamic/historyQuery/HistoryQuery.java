package com.dynamic.historyQuery;

import java.util.List;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.junit.Test;

//历史数据查询
public class HistoryQuery {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
	
	@Test
	public void findHistoryProcessInstance()
	{
		String  processInstanceId ="1801";
		HistoricProcessInstance historicProcessInstance=	processEngine.getHistoryService()
		.createHistoricProcessInstanceQuery()
		.processInstanceId(processInstanceId)
		.singleResult();
		
		System.out.println(historicProcessInstance.getId()+ "  " + historicProcessInstance.getProcessVariables());
	}
	
	
//	查询历史活动
	@Test
	public void findHistoryActiviti()
	{
		String processInstanceId = "1301";
		List<HistoricActivityInstance> list= 	processEngine.getHistoryService()
		.createHistoricActivityInstanceQuery()
		.processInstanceId(processInstanceId)
		.list();
		
		if(list!=null && list.size()>0)
		{
			for (HistoricActivityInstance historicActivityInstance : list) {
				System.out.println(historicActivityInstance.getActivityName());
				System.out.println(historicActivityInstance.getActivityType());
				System.out.println("################################");
			
			}
		}
	}
	
//	查询历史任务	
	@Test
	public void findHistoryTask()
	{
		String processInstanceId = "1301";
		List<HistoricTaskInstance> list =	processEngine.getHistoryService()
		.createHistoricTaskInstanceQuery()
		.processInstanceId(processInstanceId)
		.list();
		
		if(list!=null && list.size()>0)
		{
			for (HistoricTaskInstance historicTaskInstance : list) {
				System.out.println(historicTaskInstance.getId());
				System.out.println(historicTaskInstance.getName());
				System.out.println("################################");
			
			}
		}
	}
}
