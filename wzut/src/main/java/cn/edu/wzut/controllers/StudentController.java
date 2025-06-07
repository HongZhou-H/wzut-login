package cn.edu.wzut.controllers;

import cn.edu.wzut.models.Student;
import cn.edu.wzut.services.JsonConversion;
import cn.edu.wzut.services.StudentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    @Autowired
    private StudentService studentService;

    // 显示主页，获取所有学生信息并传递给视图
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String showIndex(Model model) {
        List<Student> students = studentService.getStudents("");
        model.addAttribute("studentList", students);
        return "index.html";
    }

    // 执行删除操作，删除指定学号的学生，然后重新获取学生列表展示主页
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String doDelete(@RequestParam("studentNo") String studentNo, Model model) {
        studentService.deleteStudent(studentNo);
        List<Student> students = studentService.getStudents("");
        model.addAttribute("userList", students);
        logger.info("学生信息删除成功");
        return "index.html";
    }

    // 执行检索操作，根据输入的文本查询学生信息并展示结果页面
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    public String doSearch(@RequestParam("text") String text, Model model) {
        List<Student> students = studentService.getStudents(text);
        model.addAttribute("userList", students);
        logger.info("学生信息查询成功");
        return "index.html";
    }

    // 显示新增学生信息页面（单个学生添加和批量添加公用此页面）
    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String showAdd(Model model) {
        model.addAttribute("student", new Student());
        return "add.html";
    }

    // 执行新增学生操作（包括单个添加和批量添加），验证学生信息完整性后调用服务层添加学生到数据库，再重定向到主页
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String doAdd(@RequestParam(value = "students", required = false) String studentsJson,
                        @ModelAttribute Student student, Model model) {
        System.out.println(studentsJson);

        if (studentsJson!= null &&!studentsJson.isEmpty()) {
            studentsJson= JsonConversion.convertToJson(studentsJson);
            // 如果接收到了students参数，说明是批量添加，解析JSON数据并转换为学生对象列表
            List<Student> studentList = parseStudentsFromJson(studentsJson);
            if (studentList!= null &&!studentList.isEmpty()) {
                String result = studentService.batchAddStudents(studentList);
                if ("redirect:/student/add".equals(result)) {
                    model.addAttribute("error", "部分学生信息有误或学号已存在，请稍后再试");
                    logger.info("批量添加失败，存在信息问题或学号重复");
                    return "redirect:/student/add";
                }
                logger.info("批量学生信息添加成功");
            }
        } else {
            // 如果没有students参数，说明是单个学生添加
            String result = studentService.addStudent(student);
            if ("redirect:/student/add".equals(result)) {
                model.addAttribute("error", "学号存在，请稍后再试");
                logger.info("添加失败，因为学号存在或信息为空");
                return "redirect:/student/add";
            }
            logger.info("学生信息添加成功");
        }

        List<Student> allStudents = studentService.getStudents("");
        model.addAttribute("userList", allStudents);
        return "redirect:/student/index";
    }

    // 辅助方法，用于将JSON格式的学生信息字符串解析为学生对象列表（这里只是示意，实际需要引入JSON解析库，如Jackson或Gson等）
    private List<Student> parseStudentsFromJson(String json) {
        // 此处假设使用Jackson来解析JSON，实际需导入相关依赖并正确配置
        // 示例代码，需要完善实际的解析逻辑，以下是简单示意
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, new TypeReference<List<Student>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    // 显示编辑学生信息页面（根据学号查询并展示对应学生信息）

    @RequestMapping(value = "/edit{studentNo}", method = RequestMethod.GET)
    public String showEdit(@PathVariable String studentNo, Model model) throws IOException {
        List<Student> students = studentService.getStudents(studentNo);
        if (!students.isEmpty()) {
            model.addAttribute("student", students.get(0));
        }
        //logger.info("学生信息修改失败");
        return "edit.html";
    }

    // 处理编辑学生信息提交的请求
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String doEdit(@ModelAttribute Student student,Model model) throws IOException {
        studentService.editStudent(student);
        List<Student> students = studentService.getStudents("");
        model.addAttribute("studentList", students);
        logger.info("学生信息修改成功");
        return "redirect:/student/index";

    }
}