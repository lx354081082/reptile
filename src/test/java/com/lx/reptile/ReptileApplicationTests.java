package com.lx.reptile;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ReptileApplicationTests {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    @Test
    public void demo1() {
        //根据bpmn文件部署流程
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("processes/MyAct.bpmn").deploy();
        //获取流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        //启动流程定义，返回流程实例
        ProcessInstance pi = runtimeService.startProcessInstanceById(processDefinition.getId());
        String processId = pi.getId();
        System.out.println("流程创建成功，当前流程实例ID：" + processId);

//		Task task=taskService.createTaskQuery().processInstanceId(processId).singleResult();
//		System.out.println("第一次执行前，任务名称："+task.getName());
//		taskService.complete(task.getId());
//
//		task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
//		System.out.println("第二次执行前，任务名称："+task.getName());
//		taskService.complete(task.getId());
//
//		task = taskService.createTaskQuery().processInstanceId(processId).singleResult();
//		System.out.println("task为null，任务执行完毕："+task);
    }

    @Test
    public void t1() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        String processDefinitionKey = "MyDemo";//流程定义的key,也就是bpmn中存在的ID

        ProcessInstance pi = runtimeService//正在执行的操作
                .startProcessInstanceByKey(processDefinitionKey);////按照流程定义的key启动流程实例

        System.out.println("流程实例ID：" + pi.getId());//流程实例ID：101
        System.out.println("流程实例ID：" + pi.getProcessInstanceId());//流程实例ID：101
        System.out.println("流程实例ID:" + pi.getProcessDefinitionId());//myMyHelloWorld:1:
    }

    /**
     * 执行
     */
    @Test
    public void t2() {
        Task task = taskService.createTaskQuery().processInstanceId("40001").singleResult();
        taskService.complete(task.getId());
        System.out.println("执行，任务名称：" + task.getName());
    }

    @Test
    public void t3() {
        List<ProcessDefinition> myDemo = repositoryService.createProcessDefinitionQuery()
                // 查询条件
                .processDefinitionKey("MyDemo")// 按照流程定义的key
                // .processDefinitionId("helloworld")//按照流程定义的ID
                .orderByProcessDefinitionVersion().desc()// 排序
                // 返回结果
                // .singleResult()//返回惟一结果集
                // .count()//返回结果集数量
                // .listPage(firstResult, maxResults)
                .list();
        for (ProcessDefinition processDefinition : myDemo) {
            System.out.println("流程定义的ID："+processDefinition.getId());
            System.out.println("流程定义的名称："+processDefinition.getName());
            System.out.println("流程定义的Key："+processDefinition.getKey());
            System.out.println("流程定义的部署ID："+processDefinition.getDeploymentId());
            System.out.println("流程定义的资源名称："+processDefinition.getResourceName());
            System.out.println("流程定义的版本："+processDefinition.getVersion());
            System.out.println("########################################################");
        }
    }

    @Test
    public void t4() {
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(repositoryService.createProcessDefinitionQuery().processDefinitionKey("MyDemo").orderByProcessDefinitionVersion().desc().list().get(0).getId());
        System.out.println("ID："+processInstance.getId());
    }
}
