package com.activity.demo.bean;

import org.activiti.engine.runtime.Execution;

import java.io.Serializable;

/**
 * Created by yijialuo on 2019/1/7.
 */
public class MyjavaBean implements Serializable {
    private String name="crazyit";


    public String getName() {
        return this.name;
    }

    public void print(Execution exe){
        System.out.println("使用Java Bean的print方法:"+exe.getId());
    }
}
