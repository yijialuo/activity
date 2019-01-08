package com.activity.demo.action;

import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;

import java.util.List;

/**
 * Created by yijialuo on 2019/1/8.
 */
public class GroupAction {
    //用户服务对象
    private IdentityService identityService;

    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

    public List<Group> list(){
        return identityService.createGroupQuery().list();
    }
}
