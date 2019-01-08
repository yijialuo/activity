package com.activity.demo.listener;

import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.ExecutionListener;
import org.activiti.engine.delegate.Expression;

/**
 * Created by yijialuo on 2019/1/7.
 */
public class ExecutionListenerInvocation implements ExecutionListener {
    private Expression message;

    public void setMessage(Expression message){
        this.message=message;
    }

    @Override
    public void notify(DelegateExecution delegateExecution) {
        System.out.println("流程监听器:"+message.getValue(delegateExecution));
    }
}
