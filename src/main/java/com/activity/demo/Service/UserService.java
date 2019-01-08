package com.activity.demo.Service;

import com.activity.demo.bean.UserVO;
import org.activiti.engine.IdentityService;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.omg.PortableInterceptor.SUCCESSFUL;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by yijialuo on 2019/1/8.
 */
public class UserService {
    private IdentityService identityService;
    public UserService(IdentityService identityService){
        this.identityService=identityService;
    }
    //查询全部用户
    public List<UserVO> list(){
        List<User> users=identityService.createUserQuery().list();
        List<UserVO> userVOs=new ArrayList<>();
        for (User user:users){
            UserVO userVO=new UserVO();
            userVO.setUserId(user.getId());
            userVO.setUserName(user.getLastName());
            userVO.setPasswd(user.getPassword());
            //查询年龄信息
            String age=identityService.getUserInfo(user.getId(),"age");
            userVO.setAge(Integer.valueOf(age));
            //查询用户组
            Group group=identityService.createGroupQuery().groupMember(user.getId()).singleResult();
            userVO.setGroupName(group.getName());
            userVOs.add(userVO);
        }
        return userVOs;
    }

    //新建用户
    public void saveUser(UserVO userVO){
        User user=this.identityService.newUser(UUID.randomUUID().toString());
        user.setLastName(userVO.getLastName());
        user.setPassword(userVO.getPasswd());
        //加入年龄信息
        this.identityService.setUserInfo(userVO.getUserId(),"age",String.valueOf(userVO.getAge()));
        this.identityService.saveUser(user);
        this.identityService.createMembership(userVO.getUserId(),userVO.getGroupId());
    }

    //用户登录 identityService有一个checkPassWord方法
    public String Userlogin(UserVO userVO){
        User user=this.identityService.createUserQuery().userLastName(userVO.getLastName()).singleResult();
        if (user==null){
            return null;
        }
        if(user.getPassword().equals(userVO.getPasswd())){
            return "ok";
        }
        return null;
    }
}
