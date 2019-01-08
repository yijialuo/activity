package com.activity.demo.Service;

import com.activity.demo.bean.VacationForm;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yijialuo on 2019/1/8.
 */
public class PrcoessService {
    public ProcessEngine getProcessEngine() {
        return processEngine;
    }

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    private ProcessEngine processEngine;

    public ProcessInstance startVacation(VacationForm vacation){
        //设置标题
        vacation.setTitle(vacation.getUserName()+"的请假申请");
        vacation.setBusinessType("请假申请");
        //查找流程定义
        ProcessDefinition pd=processEngine.getRepositoryService().createProcessDefinitionQuery().processDefinitionKey("Vacation").singleResult();
        //初始化任务参数
        Map<String,Object> vars=new HashMap<>();
        vars.put("arg",vacation);
        //启动流程
        ProcessInstance pi=processEngine.getRuntimeService().startProcessInstanceByKey(pd.getKey());
        //查询第一个任务
        Task firstTask=this.processEngine.getTaskService().createTaskQuery().processDefinitionId(pi.getId()).singleResult();
        //设置任务受理人
        this.processEngine.getTaskService().setAssignee(firstTask.getId(),vacation.getUserId());
        //完成任务
        processEngine.getTaskService().complete(firstTask.getId(),vars);
        //记录请假数据
        saveVacation();
        return pi;
    }
    private void saveVacation(){

    }
}
