package com.dynamic.processDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ProcessDefinitionTest {

	ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

	/** 部署流程定义 */
	@Test
	public void deploymentProcessDefinition_classpath() {
		Deployment deployment = processEngine.getRepositoryService()// 与流程定义和部署对象相关的Service
				.createDeployment()// 创建一个部署对象,对流程文件进行部署
				.addClasspathResource("diagrams/helloworld.bpmn")// 从classpath的资源中加载，一次只能加载一个文件;
				.addClasspathResource("diagrams/helloworld.png").name("流程定义")// 添加部署的名称
				.deploy();// 执行部署操作,将添加的资源全部写入到数据库中

		System.out.println("部署ID--->" + deployment.getId());
		System.out.println("部署名称--->" + deployment.getName());
	}

	/**
	 * 使用zip加载
	 * 
	 * @throws Exception
	 */
	@Test
	public void deploymentProcessDefinition_zip() throws Exception {
		// 获取zip文件的输入流
		InputStream inputStream = this.getClass().getClassLoader()
				.getResourceAsStream("diagrams/helloworld.zip");
		// 读取zip文件，创建zipInputStream对象
		// 路径不对
		// FileInputStream fileInputStream = new FileInputStream(new
		// File("/resources/diagrams/helloworld.zip"));
		ZipInputStream zipInputStream = new ZipInputStream(inputStream);
		processEngine.getRepositoryService().createDeployment().name("流程定义")
				.addZipInputStream(zipInputStream).deploy();

	}

	/** 查询流程定义 */
	@Test
	public void findProcessDefinition() {
		List<ProcessDefinition> list = processEngine.getRepositoryService()// 与流程定义和部署相关的Service
				.createProcessDefinitionQuery()// 创建一个流程定义的查询
				/** 指定查询条件 Where */
				// .processDefinitionKey("helloword")// 使用流程定义的Key查询;
				// .processDefinitionId("")//使用流程定义的ID查询
				// .deploymentId(deploymentId)//使用部署Id查询

				/** 排序 */
				.orderByProcessDefinitionVersion().asc()// 按照版本的升序排序
				// .orderByProcessDefinitionName().desc()//按照流程定义名称降序排序

				/** 返回结果集 **/
				.list();// 返回一个集合列表，封装流程定义
		// .singleResult();//返回唯一结果集
		// .count();//返回结果集数量
		// .listPage(firstResult, maxResults);//分页查询

		if (list != null && list.size() > 0) {
			for (ProcessDefinition processDefinition : list) {
				System.out.println("流程定义ID--->" + processDefinition.getId());
				System.out.println("流程定义的名称--->" + processDefinition.getName());
				System.out.println("流程定义的key--->" + processDefinition.getKey());
				System.out.println("流程定义的版本--->"
						+ processDefinition.getVersion());
				System.out.println("资源名称bpmn文件--->"
						+ processDefinition.getResourceName());
				System.out.println("资源名称png文件--->"
						+ processDefinition.getDiagramResourceName());
				System.out.println("部署对象ID--->"
						+ processDefinition.getDeploymentId());
				System.out.println("#####################################");
			}
		}
	}

	/** 删除流程定义 */
	@Test
	public void deleteProcessDefinition() {
		// 使用部署ID,完成删除
		String deploymentId = "1";
		// 不带级联删除，只能删除没有启动的流程，如果流程启动就会抛出异常;
		// processEngine.getRepositoryService().deleteDeployment(deploymentId);

		// 带级联删除,不管流程是否启动，都可以删除，使用级联；
		processEngine.getRepositoryService().deleteDeployment(deploymentId,
				true);

		System.out.println("删除成功");
	}

	/**
	 * 查看流程图
	 * 
	 * @throws IOException
	 */
	@Test
	public void viewPic() throws IOException {
		/** 将生成图片放到文件夹下 */
		String deploymentId = "601";
		String resourceName = "";

		List<String> list = processEngine.getRepositoryService()
				.getDeploymentResourceNames(deploymentId);

		if (list != null && list.size() > 0) {
			for (String name : list) {
				if (name.indexOf(".png") >= 0) {
					resourceName = name;
				}
			}
		}

		// 获取图片的输入流程
		InputStream inputStream = processEngine.getRepositoryService()//
				.getResourceAsStream(deploymentId, resourceName);

		// 将图片生成到D盘的目录下
		File file = new File("D:/" + resourceName);
		// 将输入流的图片写到D盘下
		FileUtils.copyInputStreamToFile(inputStream, file);

	}

	/** 查询最新版本的流程定义 */
	@Test
	public void findLastVersionProcessDefinition() {

		List<ProcessDefinition> list = processEngine.getRepositoryService()
				.createProcessDefinitionQuery()// 查询流程定义
				.orderByProcessDefinitionVersion().asc()// 使用流程定义的版本升序的排列
				.list();

		// 排序Map<String, ProcessDefinition>
		// Map集合的key:流程定义的key,Map集合的value：流程定义的对象
		// Map集合的特点：当map集合key值相同的情况下，后一次的值将替换前一次的值;
		Map<String, ProcessDefinition> map = new LinkedHashMap<String, ProcessDefinition>();
		if (list != null && list.size() > 0) {
			for (ProcessDefinition processDefinition : list) {
				map.put(processDefinition.getKey(), processDefinition);
			}
		}

		List<ProcessDefinition> pdList = new ArrayList<ProcessDefinition>(
				map.values());// map转集合;

		if (pdList != null && pdList.size() > 0) {
			for (ProcessDefinition processDefinition : pdList) {
				System.out.println("流程定义ID--->" + processDefinition.getId());
				System.out.println("流程定义的名称--->" + processDefinition.getName());
				System.out.println("流程定义的key--->" + processDefinition.getKey());
				System.out.println("流程定义的版本--->"
						+ processDefinition.getVersion());
				System.out.println("资源名称bpmn文件--->"
						+ processDefinition.getResourceName());
				System.out.println("资源名称png文件--->"
						+ processDefinition.getDiagramResourceName());
				System.out.println("部署对象ID--->"
						+ processDefinition.getDeploymentId());
				System.out.println("#####################################");
			}

		}
	}
	
	
	/** 删除流程定义（删除key相同的所有不同版本的流程定义）  */
	@Test
	public void deleteProcessDefinitionByKey() {
			//先使用流程定义的Key查询流程定义，查询出所有的版本;
		String processDefinitionKey ="helloworld";
		
		List<ProcessDefinition> list=processEngine.getRepositoryService()//流程存储服务对象
		.createProcessDefinitionQuery()//流程定义
		.processDefinitionKey(processDefinitionKey)//使用流程定义的key查询
		.list();
		
		//遍历,获取每个流程定义的部署ID
		if (list != null && list.size() > 0) {
			for (ProcessDefinition processDefinition : list) {
				//获取部署ID
				String deploymentId= processDefinition.getDeploymentId();
				processEngine.getRepositoryService().deleteDeployment(deploymentId, true);
			}
		
		}
	}
}
