package cn.edu.wzut.controllers;
import cn.edu.wzut.models.User;
import cn.edu.wzut.services.AdminService;
import cn.edu.wzut.services.UserJson;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import java.io.IOException;
import java.util.List;

@Controller
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    @Autowired
    private AdminService adminService;
    // 显示主页，获取所有学生信息并传递给视图
    @RequestMapping(value = "/testadmin", method = RequestMethod.GET)
    public String showtestAdmin(Model model) {
        List<User> users = adminService.getUsers("");
        model.addAttribute("userList", users);
        return "testadmin.html";
    }
    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String showAdmin(Model model) {
        List<User> users = adminService.getUsers("");
        model.addAttribute("userList", users);
        return "admin.html";
    }

    // 执行删除操作，删除指定学号的学生，然后重新获取学生列表展示主页
    @RequestMapping(value = "/admin/delete", method = RequestMethod.GET)
    public String doDelete(@RequestParam("id") int id, Model model) {
        adminService.deleteUser(id);
        List<User> users = adminService.getUsers("");
        model.addAttribute("userList", users);
        logger.info("用户删除成功");
        return "admin.html";
    }

    // 执行检索操作，根据输入的文本查询学生信息并展示结果页面
    @RequestMapping(value = "/admin/search", method = RequestMethod.POST)
    public String doSearch(@RequestParam("text") String text, Model model) {
        List<User> users = adminService.getUsers(text);
        model.addAttribute("userList", users);
        logger.info("用户信息查询成功");
        return "admin.html";
    }

    // 显示新增学生信息页面（单个学生添加和批量添加公用此页面）
    @RequestMapping(value = "/admin/add", method = RequestMethod.GET)
    public String showAdd(Model model) {
        model.addAttribute("user", new User());
        return "AddAdmin.html";
    }

    // 执行新增学生操作（包括单个添加和批量添加），验证学生信息完整性后调用服务层添加学生到数据库，再重定向到主页
    @RequestMapping(value = "/admin/add", method = RequestMethod.POST)
    public String doAdd(@RequestParam(value = "users", required = false) String usersJson,
                        @ModelAttribute User user, Model model) {
        logger.info("批量:"+usersJson);
        logger.info("用户名："+user.getUsername()+"密码"+user.getPassword()+"身份"+user.getIdentity());
        if (usersJson!= null &&!usersJson.isEmpty()) {
            usersJson= UserJson.convertToJson(usersJson);
            // 如果接收到了students参数，说明是批量添加，解析JSON数据并转换为学生对象列表
            List<User> userList = parseStudentsFromJson(usersJson);
            if (userList!= null &&!userList.isEmpty()) {
                String result = adminService.batchAddUsers(userList);
                if ("redirect:/admin/add".equals(result)) {
                    model.addAttribute("error", "部分学生信息有误或学号已存在，请稍后再试");
                    logger.info("批量添加失败，存在信息问题或学号重复");
                    return "redirect:/admin/add";
                }
                logger.info("批量学生信息添加成功");
            }
        } else {
            // 如果没有students参数，说明是单个学生添加
            String result = adminService.addUser(user);
            if ("redirect:/admin/add".equals(result)) {
                model.addAttribute("error", "学号存在，请稍后再试");
                logger.info("添加失败，因为学号存在或信息为空");
                return "redirect:/admin/add";
            }
            logger.info("学生信息添加成功");
        }

        List<User> allUsers = adminService.getUsers("");
        model.addAttribute("userList", allUsers);
        return "redirect:/admin";
    }

    // 辅助方法，用于将JSON格式的学生信息字符串解析为学生对象列表（这里只是示意，实际需要引入JSON解析库，如Jackson或Gson等）
    private List<User> parseStudentsFromJson(String json) {
        // 此处假设使用Jackson来解析JSON，实际需导入相关依赖并正确配置
        // 示例代码，需要完善实际的解析逻辑，以下是简单示意
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, new TypeReference<List<User>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 显示编辑学生信息页面（根据学号查询并展示对应学生信息）

    @RequestMapping(value = "/admin/edit{id}", method = RequestMethod.GET)
    public String showEdit(@PathVariable String id, Model model) throws IOException {
        List<User> users = adminService.getUsers(id);
        if (!users.isEmpty()) {
            model.addAttribute("user", users.get(0));
        }
        //logger.info("用户信息修改失败");
        return "useredit.html";
    }

    // 处理编辑学生信息提交的请求
    @RequestMapping(value = "/admin/edit", method = RequestMethod.POST)
    public String doEdit(@ModelAttribute User user,Model model) throws IOException {
        logger.info("用户名"+user.getUsername()+"密码"+user.getPassword()+"身份"+user.getIdentity());
        adminService.editUser(user);
        List<User> users = adminService.getUsers("");
        model.addAttribute("userList", users);
        logger.info("用户信息修改成功");
        return "admin.html";

    }
}