package cn.edu.wzut.controllers;

import cn.edu.wzut.models.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class RegisterController {
    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    // 处理注册页面的GET请求，返回注册页面视图
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    // 处理注册表单提交的POST请求
    @PostMapping("/register")
    public String register(@RequestParam("username") String username,
                           @RequestParam("password") String password,
                           @RequestParam("confirmPassword") String confirmPassword,
                           Model model) {
        Connection connect = null;
        String sql1="Select * from t_user where name='"+username+"'";
        ResultSet rs = null;
        String sql= "Select count(*) from t_user where name='"+username+"' and pass='"+password+"'";;;
        String sql2;
        Statement stm;
        int flag=0;
        try{ connect=DriverManager.getConnection("jdbc:mysql://localhost:8888/test","root","123456");
            stm=connect.createStatement();
            rs =stm.executeQuery(sql1);
            if(rs.next()){
                model.addAttribute("error", "用户名已被注册，请更换用户名");
                logger.info("用户名已被注册，请更换用户名");
                System.out.println("用户名已被注册，请更换用户名");
                return "register";
            }
        }
        catch(Exception e1)
        {
            e1.printStackTrace();
            System.out.println("222222");
        }
        try{
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("连接成功 ");

        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("数据库连接创建异常1");

        }
        try{
            connect=DriverManager.getConnection("jdbc:mysql://localhost:8888/test","root","123456");
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.out.println("数据库连接创建异常2");
        }
        sql2="INSERT into t_user(name,pass,identity) values('"+username+"','"+password+"','"+0+"')";
        try{
            stm=connect.createStatement();
            stm.executeUpdate(sql2);
            logger.info("注册成功");
            System.out.println("注册成功");
            model.addAttribute("success", "注册成功，请登录！");
            return "register";
        }
        catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("success", "注册失败，请重新注册！");
            logger.info("注册失败");
            System.out.println("数据库连接创建异常3");
            return "register";
        }





        }

}