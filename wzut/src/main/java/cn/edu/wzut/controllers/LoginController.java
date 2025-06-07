package cn.edu.wzut.controllers;

import cn.edu.wzut.models.User;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Controller
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(RegisterController.class);

    // 处理登录页面的GET请求，返回登录页面视图
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // 处理登录表单提交的POST请求
    @PostMapping("/login")
    public String login(@RequestParam("username") String username,
                        @RequestParam("password") String password,
                        Model model) {
        Connection connect = null;
        ResultSet rs = null;
        String sql;
        Statement stm;
        try {
            // 加载数据库驱动（这里驱动方式可能需要根据实际情况调整，比如使用com.mysql.cj.jdbc.Driver等更合适的新驱动类，具体看MySQL版本）
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e1) {
            e1.printStackTrace();
            System.out.println("11111111");
        }
        try {
            connect = DriverManager.getConnection("jdbc:mysql://localhost:8888/test", "root", "123456");
            stm = connect.createStatement();
            // 查询用户信息，同时获取身份字段identity
            sql = "SELECT identity FROM t_user WHERE name='" + username + "' AND pass='" + password + "'";
            rs = stm.executeQuery(sql);
            if (rs.next()) {
                int identity = rs.getInt("identity");
                if (identity == 0) {
                    logger.info("学生登录成功");
                    return "redirect:/student/index";
                } else if (identity == 1 || identity == 2) {
                    logger.info("管理员或超级管理员登录成功");
                    return "redirect:/admin";
                } else {
                    // 这里可以处理其他未定义的身份情况，比如记录日志、返回错误提示等
                    logger.warn("出现未知身份的登录情况");
                    model.addAttribute("error", "身份信息异常，请联系管理员");
                    return "login";
                }
            } else {
                logger.info("登录失败");
                model.addAttribute("error", "用户名或密码错误，请重新登录");
                return "login";
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            model.addAttribute("error", "系统出现异常，请稍后再试");
            System.out.println("登录失败");
            return "login";
        } finally {
            try {
                if (rs!= null) {
                    rs.close();
                }
                if (connect!= null) {
                    connect.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}