package junit;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;

public class TestActiviti {
	/**
	 * 使用代码创建工作流需要的23张表
	 */
	@Test
	public void createTable() {
		ProcessEngineConfiguration processEngineConfiguration = ProcessEngineConfiguration
				.createStandaloneProcessEngineConfiguration();

		processEngineConfiguration.setJdbcDriver("com.mysql.jdbc.Driver");
		processEngineConfiguration
				.setJdbcUrl("jdbc:mysql://localhost:3306/dynamic0204activiti?useUnicode=true&characterEncoding=utf8");
		processEngineConfiguration.setJdbcUsername("root");
		processEngineConfiguration.setJdbcPassword("fxq123");

		processEngineConfiguration
				.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);

		// 工作流的核心对象，processEnginee对象
		ProcessEngine processEngine = processEngineConfiguration
				.buildProcessEngine();
		System.out.println("processEngine" + processEngine);
	}

	/**
	 * 使用xml配置来生成数据库
	 */
	@Test
	public void createTableUseXml() {
		// 流程引擎的全部配置，读取和解析相应的配置文件，并返回ProcessEngineConfiguration实例
		// ProcessEngineConfiguration processEngineConfiguration =
		// ProcessEngineConfiguration//
		// .createProcessEngineConfigurationFromResource("activiti.cfg.xml");
		// ProcessEngine processEngine = processEngineConfiguration
		// .buildProcessEngine();

		ProcessEngine processEngine = ProcessEngineConfiguration//
				.createProcessEngineConfigurationFromResource("activiti.cfg.xml")//
				.buildProcessEngine();
		System.out.println("processEngine" + processEngine);

	}

}
