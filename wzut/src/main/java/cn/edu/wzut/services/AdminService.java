package cn.edu.wzut.services;

import cn.edu.wzut.models.User;

import java.util.List;

public interface AdminService {

    //根据用户输入的条件查询学生清单
     List<User> getUsers(String text);

    //保存学生清单信息
    void saveUsers(List<User> users);

    //新增学生信息
    String addUser(User user);

    //删除学生信息
    void deleteUser(int id);
    void editUser(User user);

    String batchAddUsers(List<User> userList);
}
