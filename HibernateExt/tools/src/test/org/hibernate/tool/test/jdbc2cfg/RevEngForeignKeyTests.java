/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.test.jdbc2cfg;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.hibernate.MappingException;
import org.hibernate.cfg.Environment;
import org.hibernate.cfg.JDBCMetaDataConfiguration;
import org.hibernate.cfg.Settings;
import org.hibernate.cfg.SettingsFactory;
import org.hibernate.cfg.reveng.DefaultReverseEngineeringStrategy;
import org.hibernate.cfg.reveng.OverrideRepository;
import org.hibernate.cfg.reveng.ReverseEngineeringStrategy;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.SimpleValue;
import org.hibernate.tool.JDBCMetaDataBinderTestCase;

/**
 * @author max
 *
 */
public class RevEngForeignKeyTests extends JDBCMetaDataBinderTestCase {
	
	private static final String OVERRIDETEST_FOREIGNKEY_XML = "org/hibernate/tool/test/jdbc2cfg/foreignkeytest.reveng.xml";
	private static final String BAD_FOREIGNKEY_XML = "org/hibernate/tool/test/jdbc2cfg/badforeignkeytest.reveng.xml";;
	
	public static Test suite() {
		return new TestSuite(RevEngForeignKeyTests.class);
	}

	private Settings settings;

	public void testDefaultBiDirectional() {
		
		PersistentClass project = cfg.getClassMapping("Project");
		
		assertNotNull(project.getProperty("worksOns"));
		assertNotNull(project.getProperty("employee"));
		assertEquals(3, project.getPropertyClosureSpan());		
		assertEquals("projectId", project.getIdentifierProperty().getName());
		
		PersistentClass employee = cfg.getClassMapping("Employee");
		
		assertNotNull(employee.getProperty("worksOns"));
		assertNotNull(employee.getProperty("employees"));
		assertNotNull(employee.getProperty("employee"));
		assertNotNull(employee.getProperty("projects"));
		assertEquals(5, employee.getPropertyClosureSpan());
		assertEquals("id", employee.getIdentifierProperty().getName());
		
		PersistentClass worksOn = cfg.getClassMapping("WorksOn");
		
		assertNotNull(worksOn.getProperty("project"));
		assertNotNull(worksOn.getProperty("employee"));
		assertEquals(4, worksOn.getPropertyClosureSpan());
		assertEquals("id", worksOn.getIdentifierProperty().getName());
		
	}

	public void testSetAndManyToOne() {
		
		OverrideRepository or = buildOverrideRepository();
		
		or.addResource(OVERRIDETEST_FOREIGNKEY_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());

		JDBCMetaDataConfiguration localCfg = new JDBCMetaDataConfiguration();
		localCfg.setReverseEngineeringStrategy(repository);
		localCfg.readFromJDBC();			
		
		PersistentClass project = localCfg.getClassMapping("Project");
		
		assertNotNull(project.getProperty("worksOns"));
		assertPropertyNotExists(project, "employee", "should be removed by reveng.xml");
		Property property = project.getProperty("teamLead");
		assertNotNull(property);
		assertTrue(property.getValue() instanceof SimpleValue);
		assertEquals(3, project.getPropertyClosureSpan());		
		assertEquals("projectId", project.getIdentifierProperty().getName());
		
		PersistentClass employee = localCfg.getClassMapping("Employee");
		
		assertNotNull(employee.getProperty("worksOns"));
		assertNotNull("property should be renamed by reveng.xml", employee.getProperty("manager"));		
		assertPropertyNotExists( employee, "employees", "set should be excluded by reveng.xml" );
		assertNotNull("should be renamed by reveng.xml", employee.getProperty("managedProjects"));
		
		assertEquals(4, employee.getPropertyClosureSpan());
		assertEquals("id", employee.getIdentifierProperty().getName());
		
		PersistentClass worksOn = localCfg.getClassMapping("WorksOn");
		
		assertNotNull(worksOn.getProperty("project"));
		assertNotNull(worksOn.getProperty("employee"));
		assertEquals(4, worksOn.getPropertyClosureSpan());
		assertEquals("id", worksOn.getIdentifierProperty().getName());
	
	}
	
	public void testDuplicateForeignKeyDefinition() {
		
		OverrideRepository or = buildOverrideRepository();
		
		or.addResource(BAD_FOREIGNKEY_XML);
		ReverseEngineeringStrategy repository = or.getReverseEngineeringStrategy(new DefaultReverseEngineeringStrategy());

		JDBCMetaDataConfiguration localCfg = new JDBCMetaDataConfiguration();
		localCfg.setReverseEngineeringStrategy(repository);
		
		try {
			localCfg.readFromJDBC();
			fail("Should fail because foreign key is already defined in the database"); // maybe we should ignore the definition and only listen to what is overwritten ? For now we error. 
		} catch(MappingException me) {
			assertTrue(me.getMessage().indexOf("already defined")>=0);			
		}
		
	}

	private void assertPropertyNotExists(PersistentClass employee, String name, String msg) {
		try {
			employee.getProperty(name);
			fail(msg);
		} catch(MappingException me) {
			// excpected
		}
	}

	private OverrideRepository buildOverrideRepository() {
		if(settings==null) {
			settings = new SettingsFactory() {
				// trick to get hibernate.properties settings for defaultschema/catalog in here
			}.buildSettings(Environment.getProperties());
		}
		//return new OverrideRepository(settings.getDefaultCatalogName(),settings.getDefaultSchemaName());
		return new OverrideRepository();
	}
	
	protected String[] getCreateSQL() {
		return new String[] {
			"create table PROJECT ( project_id integer not null, name varchar(50), team_lead integer, primary key (project_id) )",
			"create table EMPLOYEE ( id integer not null, name varchar(50), manager_id integer, primary key (id), constraint employee_manager foreign key (manager_id) references EMPLOYEE)",
			"create table WORKS_ON ( project_id integer not null, employee_id integer not null, start_date date, end_date date, primary key (project_id, employee_id), constraint workson_employee foreign key (employee_id) references EMPLOYEE, foreign key (project_id) references PROJECT )",
			"alter  table PROJECT add constraint project_manager foreign key (team_lead) references EMPLOYEE"
		};
	}

	protected String[] getDropSQL() {
		return new String[] {
				"alter table PROJECT drop constraint project_manager",
				"drop table WORKS_ON",
				"drop table EMPLOYEE",
				"drop table PROJECT",											
			};
	}

}
