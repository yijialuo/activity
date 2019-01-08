package com.activity.demo;

import com.activity.demo.bean.MyjavaBean;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.activiti.engine.management.TableMetaData;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.DataObject;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Attachment;
import org.activiti.engine.task.IdentityLink;
import org.activiti.engine.task.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivityApplicationTests {

    @Test
    public void contextLoads() {
    }

    @Test
    public void activitiTable() {
        ProcessEngineConfiguration cfg = ProcessEngineConfiguration
                .createProcessEngineConfigurationFromResource("activiti.cfg.xml");
        ProcessEngine processEngine = cfg.buildProcessEngine();
    }



    //得到default engine
    @Test
    public void getEngine() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        System.out.println(processEngine);
    }

    //engine数量
    @Test
    public void umberEngine() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        Map<String, ProcessEngine> engs = ProcessEngines.getProcessEngines();
        System.out.println("engs:" + engs.size()); //1
        ProcessEngines.unregister(processEngine);
        System.out.println("engs:" + engs.size()); //0
    }

    //服务组件
    @Test
    public void serviceEngine() {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService = processEngine.getRepositoryService();
        RuntimeService runtimeService = processEngine.getRuntimeService();
        TaskService taskService = processEngine.getTaskService();
        IdentityService identityService = processEngine.getIdentityService();
        ManagementService managementService = processEngine.getManagementService();
        HistoryService historyService = processEngine.getHistoryService();
        DynamicBpmnService dynamicBpmnService = processEngine.getDynamicBpmnService();
        //输出类名
        System.out.println(repositoryService.getClass().getName());
        System.out.println(runtimeService.getClass().getName());
        System.out.println(taskService.getClass().getName());
        System.out.println(identityService.getClass().getName());
        System.out.println(managementService.getClass().getName());
        System.out.println(historyService.getClass().getName());
        System.out.println(dynamicBpmnService.getClass().getName());
    }

    @Test
    public void saveGroup(){
        ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
        IdentityService identityService=processEngine.getIdentityService();
        String genId= UUID.randomUUID().toString();
        Group group=identityService.newGroup(genId);
        group.setName("经理组");
        group.setType("manager");
        //保存到数据库
        identityService.saveGroup(group);
    }

    //查询用户组
    @Test
    public void chaxunyhz(){
        ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
        IdentityService identityService=processEngine.getIdentityService();
        Group data=identityService.createGroupQuery().groupId("f7584b97-e013-4f29-a9ab-32a5a501b16d").singleResult();
        System.out.println("Group ID:"+data.getId()+",Group Name:"+data.getName());
    }

    //修改用户组
    @Test
    public void xiugaiyhz(){
        //先查询出来、再修改
        ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
        IdentityService identityService=processEngine.getIdentityService();
        Group group=identityService.createGroupQuery().groupId("f7584b97-e013-4f29-a9ab-32a5a501b16d").singleResult();
        group.setName("经理2组");
        identityService.saveGroup(group);
    }

    //删除用户组
    @Test
    public void scyhz(){
        ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
        IdentityService identityService=processEngine.getIdentityService();
        identityService.deleteGroup("f7584b97-e013-4f29-a9ab-32a5a501b16d");
    }

    //查询用户组
    @Test
    public void cxyhz(){
        ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
        IdentityService identityService=processEngine.getIdentityService();
        createGroup(identityService,"1","GroupA","typeA");
        createGroup(identityService,"2","GroupB","typeB");
        createGroup(identityService,"3","GroupC","typeC");
        createGroup(identityService,"4","GroupD","typeD");
        createGroup(identityService,"5","GroupE","typeE");
        //使用list全部查出来
        List<Group> groups=identityService.createGroupQuery().list();
        for(Group group:groups){
            System.out.println(group.getId()+"---"+group.getName()+"---"+group.getType());
        }
    }

    public void createGroup(IdentityService identityService,String id,String name,String type){
        Group group=identityService.newGroup(id);
        group.setName(name);
        group.setType(type);
        identityService.saveGroup(group);
    }

    //listPage 分页方法
    @Test
    public void listPage(){
        ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
        IdentityService identityService=processEngine.getIdentityService();
        List<Group> groups=identityService.createGroupQuery().listPage(2,3);//查第2个后面的3个
        for (Group group:groups){
            System.out.println(group.getId()+"---"+group.getName()+"---"+group.getType());
        }
        System.out.println("一共："+identityService.createGroupQuery().count());
    }

    //排序
    @Test
    public void paixu(){
        ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
        IdentityService identityService=processEngine.getIdentityService();
        List<Group> groups=identityService.createGroupQuery().orderByGroupId().desc().list();//降序输出
        for (Group group:groups){
            System.out.println(group.getId()+"---"+group.getName()+"---"+group.getType());
        }
        groups=identityService.createGroupQuery().orderByGroupId().asc().list();//升序输出
        for (Group group:groups){
            System.out.println(group.getId()+"---"+group.getName()+"---"+group.getType());
        }
    }

    //查询权限数据
    @Test
    public void cxqx(){
        ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
        //得到流程存储服务实例
        RepositoryService repositoryService=processEngine.getRepositoryService();
        //得到身份服务组件
        IdentityService identityService=processEngine.getIdentityService();
        //部署流程描述文件
        Deployment deployment=repositoryService.createDeployment().addClasspathResource("candidateQuery.bpmn").deploy();
        //添加两个用户
        createUser(identityService,"user1","张三","张三","mail1","123");
        createUser(identityService,"user2","李四","李四","mail2","123");
        //添加两个用户组
        createGroup(identityService,"group1","经理组","manager");
        createGroup(identityService,"group2","员工组","employee");
        //查询流程定义
        ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        //设置权限数据
        repositoryService.addCandidateStarterGroup(processDefinition.getId(),"group1");
        repositoryService.addCandidateStarterGroup(processDefinition.getId(),"group2");
        repositoryService.addCandidateStarterGroup(processDefinition.getId(),"user1");
        repositoryService.addCandidateStarterGroup(processDefinition.getId(),"user2");
        //根据用户查询有权限的流程定义
        List<ProcessDefinition> processDefinitions=repositoryService.createProcessDefinitionQuery().startableByUser("user1").list();
        System.out.println("用户张三有权限的流程定义为:");
        for(ProcessDefinition processDefinition1:processDefinitions){
            System.out.println(" "+processDefinition1.getName());
        }
        //根据流程定义查询用户组数据
        List<Group> groups=identityService.createGroupQuery().potentialStarter(processDefinition.getId()).list();
        System.out.println("以下用户组对流程定义有权限:");
        for (Group group:groups){
            System.out.println(" "+group.getName());
        }
        //根据流程定义查询用户数据
        List<User> users=identityService.createUserQuery().potentialStarter(processDefinition.getId()).list();
        System.out.println("一下用户对流程定义有权限");
        for (User user:users){
            System.out.println(" "+user.getFirstName());
        }
        //根据流程定义查询全部的IdentityLink(ACT_RU_IDENTITYLINK表)数据
        List<IdentityLink> identityLinks=repositoryService.getIdentityLinksForProcessDefinition(processDefinition.getId());
        System.out.println("与流程定义相关的数据量: "+identityLinks.size());
    }

    public void createUser(IdentityService identityService,String userId,String firstName,String lastName,String email,String passwd){
        User user=identityService.newUser(userId);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwd);
        identityService.saveUser(user);

    }

    //部署资源文件,读取资源文件
    @Test
    public void bssywj() throws IOException {
        ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService=processEngine.getRepositoryService();
        //部署一份TXT文件
        Deployment deployment=repositoryService.createDeployment().addClasspathResource("GetResource.txt").deploy();
        //查询资源文件
        InputStream inputStream=repositoryService.getResourceAsStream(deployment.getId(),"GetResource.txt");
        int count=inputStream.available();
        byte[] contents=new byte[count];
        inputStream.read(contents);
        String result=new String(contents);
        //输出结果
        System.out.println(result);
    }

    //部署,读取bpmn
    @Test
    public void bsdq() throws IOException {
        ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService=processEngine.getRepositoryService();
        Deployment deployment=repositoryService.createDeployment().addClasspathResource("getProcessModel.bpmn").deploy();
        //查询流程定义实体
        ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        //查询资源文件
        InputStream is=repositoryService.getProcessModel(processDefinition.getId());
        //读取输入流
        int count=is.available();
        byte[] contents=new byte[count];
        is.read(contents);
        String result=new String(contents);
        System.out.println(result);
    }


    //删除部署数据
    @Test
    public void scbs(){
        ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService=processEngine.getRepositoryService();
        Deployment deployment=repositoryService.createDeployment().addClasspathResource("deleteDeployment.bpmn").deploy();
        ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        //启动流程
        processEngine.getRuntimeService().startProcessInstanceById(processDefinition.getId());
        try {
            //删除部署数据失败,此时将会抛出异常,由于cascade为false
            repositoryService.deleteDeployment(deployment.getId());
        }catch (Exception e){
            System.out.println("删除失败,流程开始,没有设置cascade为true");
        }
        System.out.println("===================");
        repositoryService.deleteDeployment(deployment.getId(),true);
    }

    //任务的删除Task
    @Test
    public void task(){
        ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
        //获取任务服务组件
        TaskService taskService=processEngine.getTaskService();
        //保存若干个Task
        for(int i=1;i<10;i++){
            saveTask(taskService,String.valueOf(i));
        }
        //删除task(不包括历史数据和子任务)
        taskService.deleteTask("1");
        //删除task(包括历史数据和子任务)
        taskService.deleteTask("2",true);
        //删除多个task(不包括历史数据和子任务)
        List<String> ids=new ArrayList<String>();
        ids.add("3");
        ids.add("4");
        taskService.deleteTasks(ids);
        //删除多个task(包括历史数据和子任务)
        ids=new ArrayList<String>();
        ids.add("5");
        ids.add("6");
        taskService.deleteTasks(ids,true);
        //再删除ID为3的task,此时任务3的历史数据也会被删除
        taskService.deleteTask("3",true);
    }

    public void saveTask(TaskService taskService,String id){
        Task task = taskService.newTask(id);
        taskService.saveTask(task);
    }

    //任务参数
    @Test
    public void tastVar(){
        ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
        TaskService taskService=processEngine.getTaskService();
        Task task1=taskService.newTask("task1");
        task1.setName("请差申请");
        taskService.saveTask(task1);
        //设置各种基本类型参数
        taskService.setVariable(task1.getId(),"days",5);
        taskService.setVariable(task1.getId(),"target",new TestVO("北京"));
        //获取天数
        Integer days=(Integer) taskService.getVariable(task1.getId(),"days");
        System.out.println("出差天数:"+days);
        //获取目的地
        TestVO target=(TestVO) taskService.getVariable(task1.getId(),"target");
        System.out.println("出差目的地:"+target.getName());

    }

    //数据对象
    @Test
    public void sjdx(){
        ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
        //获取任务服务组件
        TaskService taskService=processEngine.getTaskService();
        //获取运行服务组件
        RuntimeService runtimeService=processEngine.getRuntimeService();
        //获取流程存储服务组件
        RepositoryService repositoryService=processEngine.getRepositoryService();
        //部署流程描述文件
        Deployment dep=repositoryService.createDeployment()
                .addClasspathResource("dataObject.bpmn").deploy();
        //查找流程定义
        ProcessDefinition pd=repositoryService.createProcessDefinitionQuery()
                .deploymentId(dep.getId()).singleResult();
        //启动流程
        ProcessInstance pi=runtimeService.startProcessInstanceById(pd.getId());
        //查询流程任务
        Task task=taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        //获取全部参数
        Map<String,DataObject> objs=taskService.getDataObjects(task.getId());
        for(String key:objs.keySet()){
            System.out.println(key+"----"+objs.get(key).getValue());
        }
    }

    @Test
    public void cs(){
        ProcessEngine engine=ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService=engine.getRepositoryService();
        RuntimeService runtimeService=engine.getRuntimeService();
        TaskService taskService=engine.getTaskService();
        repositoryService.createDeployment().addClasspathResource("localVariable.bpmn20.xml").deploy();
        //启动流程
        ProcessInstance pi=runtimeService.startProcessInstanceByKey("vacationRequest");
        //查询全部的任务,得到相应的执行流,设置不同的参数
        List<Task> tasks=taskService.createTaskQuery().processInstanceId(pi.getId()).list();
        for(Task task:tasks){
            Execution exe=runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
            if("Manager Audit".equals(task.getName())){
                //经理审核节点,设置local参数
                runtimeService.setVariableLocal(exe.getId(),"managerVar","manager var");
            }else {
                //HR审核节点,设置全局参数
                runtimeService.setVariable(exe.getId(),"hrVar","hr var");
            }
        }
        //执行两个执行流时输出参数
        for(Task task:tasks){
            Execution exe=runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
            if("Manager Audit".equals(task.getName())){
                System.out.println("使用getVartiableLocal方法获取经理参数:"+runtimeService.getVariableLocal(exe.getId(),"managerVar"));
                System.out.println("使用getVartiable方法获取经理参数:"+runtimeService.getVariable(exe.getId(),"managerVar"));
            }else {
                System.out.println("使用getVartiableLocal方法获取HR参数:"+runtimeService.getVariableLocal(exe.getId(),"hrVar"));
                System.out.println("使用getVartiable方法获取HR参数:"+runtimeService.getVariable(exe.getId(),"hrVar"));
            }
        }
        //完成任务
        for(Task task:tasks){
            taskService.complete(task.getId());
        }
        System.out.println("===========完成流程分支后===========");
        //重新查找流程任务
        tasks=taskService.createTaskQuery().processInstanceId(pi.getId()).list();
        for(Task task:tasks){
            System.out.println("当前流程节点:"+task.getName());
            Execution exe=runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
            System.out.println("经理参数:"+runtimeService.getVariable(exe.getId(),"namagerVar"));
            System.out.println("HR参数:"+runtimeService.getVariable(exe.getId(),"hrVar"));
        }
    }

    //complete
    @Test
    public void complete(){
        ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
        //获取任务服务组件
        TaskService taskService=processEngine.getTaskService();
        //获取运行服务组件
        RuntimeService runtimeService=processEngine.getRuntimeService();
        //获取流程存储服务组件
        RepositoryService repositoryService=processEngine.getRepositoryService();
        //部署流程描述文件
        Deployment dep=repositoryService.createDeployment()
                .addClasspathResource("vacation2.bpmn").deploy();
        //查找流程定义
        ProcessDefinition pd=repositoryService.createProcessDefinitionQuery()
                .deploymentId(dep.getId()).singleResult();
        //启动流程
        ProcessInstance pi=runtimeService.startProcessInstanceById(pd.getId());
        //查询流程任务
        Task task=taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        //调用complete方法完成任务,传入参数
        Map<String,Object> vars=new HashMap<String,Object>();
        vars.put("days",2);
        //设置临时的参数
        Map<String,Object> vars2=new HashMap<>();
        vars2.put("temp","temp var");
        taskService.complete(task.getId(),vars,vars2);
        //再次查找任务
        task=taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        //无法查询临时参数
        String tempVar=(String) taskService.getVariable(task.getId(),"temp");
        System.out.println("查询临时参数:"+tempVar);
        //得到参数
        Integer days=(Integer) taskService.getVariable(task.getId(),"days");
        if(days>5){
            System.out.println("大于5天,不能");
        }else {
            System.out.println("小于5天,完成任务,流程结束");
            taskService.complete(task.getId());
        }
    }

    //附件
    @Test
    public void fj() throws FileNotFoundException {
        ProcessEngine processEngine=ProcessEngines.getDefaultProcessEngine();
        //获取任务服务组件
        TaskService taskService=processEngine.getTaskService();
        //获取运行服务组件
        RuntimeService runtimeService=processEngine.getRuntimeService();
        //获取流程存储服务组件
        RepositoryService repositoryService=processEngine.getRepositoryService();
        //部署流程描述文件
        Deployment dep=repositoryService.createDeployment()
                .addClasspathResource("vacation.bpmn").deploy();
        //查找流程定义
        ProcessDefinition pd=repositoryService.createProcessDefinitionQuery()
                .deploymentId(dep.getId()).singleResult();
        //启动流程
        ProcessInstance pi=runtimeService.startProcessInstanceById(pd.getId());
        //查询流程任务
        Task task=taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        //设置任务附件
        Attachment att1=taskService.createAttachment("web url",task.getId(),pi.getId(),"Attachement1","163 web page","http://www.162.com");
        //创建图片输入流
        InputStream is=new FileInputStream(new File("/Users/yijialuo/Desktop/activity/src/main/resources/result.png"));
        //设置输入流为任务附件
        Attachment att2=taskService.createAttachment("web url",task.getId(),pi.getId(),"Attachement2","Image InputStream",is);
        //根据流程实例ID查询附件
        List<Attachment> attas1=taskService.getProcessInstanceAttachments(pi.getId());
        System.out.println("流程附件数量:"+attas1.size());
        //根据任务ID查询附件
        List<Attachment> attas2=taskService.getTaskAttachments(task.getId());
        System.out.println("任务附件数量:"+attas2.size());
        //根据附件ID查询附件
        Attachment attResult=taskService.getAttachment(att1.getId());
        System.out.println("附件1名称:"+attResult.getName());
        //根据附件ID查询附件内容
        InputStream stream1=taskService.getAttachmentContent(att1.getId());
        System.out.println("附件1的输入流:"+stream1);
        InputStream stream2=taskService.getAttachmentContent(att2.getId());
        System.out.println("附件2的输入流"+stream2);
    }

    @Test
    public void managementService(){
        ProcessEngine engine=ProcessEngines.getDefaultProcessEngine();
        ManagementService managementService=engine.getManagementService();
        //查询表信息
        TableMetaData data=managementService.getTableMetaData("ACT_GE_PROPERTY");
        System.out.println("输出ACT_GE_PROPERTY的列");
        List<String> columns=data.getColumnNames();
        for(String column:columns){
            System.out.println(column);
        }
        System.out.println("输出ACT_GE_PROPERTY的类类型");
        List<String> types=data.getColumnTypes();
        for(String type:types){
            System.out.println(type);
        }
        //查询数据量
        Map<String,Long> count=managementService.getTableCount();
        System.out.println("ACT_GE_PROOERTY数据量:"+count.get("ACT_GE_PROPERTY"));
    }

    //ExecutionListener测试
    @Test
    public void testExecutionListener(){
        ProcessEngine engine=ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService=engine.getRepositoryService();
        Deployment deployment=repositoryService.createDeployment().addClasspathResource("bpmn/ExecutionListenerInvocation.xml").deploy();
        RuntimeService runtimeService=engine.getRuntimeService();
        TaskService taskService=engine.getTaskService();
        //启动流程
        ProcessInstance pi=runtimeService.startProcessInstanceByKey("process1");
        //查找并且完成任务
        Task task=taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        taskService.complete(task.getId());
    }

//catching事件
    @Test
    public void catching(){
        ProcessEngine engine=ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService=engine.getRepositoryService();
        RuntimeService runtimeService=engine.getRuntimeService();
        repositoryService.createDeployment().addClasspathResource("signalEventReceived.bpmn").deploy();
        //启动流程
        ProcessInstance pi=runtimeService.startProcessInstanceByKey("testProcess");
        //查询执行流
        Execution exe=runtimeService.createExecutionQuery().activityId("usertask1").singleResult();
        System.out.println("当前节点:"+exe.getActivityId());
        //触发receiveTask
        runtimeService.trigger(exe.getId());
        //查询当前节点
        exe=runtimeService.createExecutionQuery().activityId("signalCatchEvent").singleResult();
        System.out.println("调用trigger方法后当前节点:"+exe.getActivityId());
        //发送信号给事件,流程结束
        runtimeService.signalEventReceived("testSignal");
        List exes=runtimeService.createExecutionQuery().processInstanceId(pi.getId()).list();
        System.out.println("触发Catching事件后,执行流数量:"+exes.size());
    }
//    public void createGroup(IdentityService identityService,String groupID,String name,String type){
//        Group group=identityService.newGroup(groupID);
//        group.setName(name);
//        group.setType(type);
//        identityService.saveGroup(group);
//    }


    @Test
    public void test(){
        ProcessEngine engine=ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService=engine.getRepositoryService();
        RuntimeService runtimeService=engine.getRuntimeService();
        TaskService taskService=engine.getTaskService();
        Deployment deployment=repositoryService.createDeployment().addClasspathResource("bpmn/test.bpmn").deploy();
        ProcessInstance pi=runtimeService.startProcessInstanceByKey("myProcess_1");
//        runtimeService.trigger(pi.getId());
        List<Task> tasks=taskService.createTaskQuery().taskAssignee("user1_test").list();
        System.out.println(tasks.size());
        tasks=taskService.createTaskQuery().taskCandidateGroup("group1_test").list();
        System.out.println(tasks.size());
        tasks=taskService.createTaskQuery().taskCandidateUser("user2_test").list();
        System.out.println(tasks.size());
    }

    //javaBean
    @Test
    public void javaBean(){
        ProcessEngine engine=ProcessEngines.getDefaultProcessEngine();
        RepositoryService repositoryService=engine.getRepositoryService();
        RuntimeService runtimeService=engine.getRuntimeService();
        repositoryService.createDeployment().addClasspathResource("bpmn/JavaBeanServiceTask.bpmn").deploy();
        Map<String,Object> vars=new HashMap<>();
        vars.put("myBean",new MyjavaBean());
        //启动
        ProcessInstance pi=runtimeService.startProcessInstanceByKey("process1",vars);
        System.out.println("aa:"+pi.getId());
        //进行任务参数查询
        System.out.println("运行两个Service Task 的myName参数为:"+runtimeService.getVariable(pi.getId(),"myName"));
    }

    @Test
    public void TestPng() throws IOException {
        ProcessEngine engine=ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService=engine.getRuntimeService();
        RepositoryService repositoryService=engine.getRepositoryService();
        //查询流程实例
        ProcessInstance pi=runtimeService.startProcessInstanceByKey("SalaryAdjust");
        //查询流程定义
        ProcessDefinition pd=repositoryService.createProcessDefinitionQuery().processDefinitionKey("SalaryAdjust").singleResult();
        //获取bpmn模型对象
        BpmnModel model=repositoryService.getBpmnModel(pd.getId());

        TaskService taskService=engine.getTaskService();

        Task task=taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();

        taskService.complete(task.getId());

        //定义使用宋体
        String fontName="宋体";
        //获取流程实例当前的节点,需要高亮显示
        List<String> currentActs=runtimeService.getActiveActivityIds(pi.getId());
        //BPMN模型对象,图片类型,显示的节点
        InputStream is=engine.getProcessEngineConfiguration()
                .getProcessDiagramGenerator()
                .generateDiagram(model,"png",currentActs,new ArrayList<String>(),fontName,fontName,fontName,null,1.0);


        //将输入流转换为byte数组
        ByteArrayOutputStream bytestream=new ByteArrayOutputStream();
        int b;
        while ((b=is.read())!=-1){
            bytestream.write(b);
        }
        byte[] bs=bytestream.toByteArray();


        File file = new File("SalaryAdjust1.png");
        FileOutputStream fop = new FileOutputStream(file);

        // if file doesnt exists, then create it
        if (!file.exists()) {
            file.createNewFile();
        }
        fop.write(bs);
        is.close();
        bytestream.close();
        fop.flush();
        fop.close();

//        int len;
//        FileOutputStream os = new FileOutputStream("static/1.png");
//        // 开始读取
//        while ((len = is.read(bs)) != -1) {
//            os.write(bs, 0, len);
//        }

    }
}

class TestVO implements Serializable{
    private String name;
    public TestVO(String name){
        this.name=name;
    }
    public String getName(){
        return this.name;
    }
}

