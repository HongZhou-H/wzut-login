package cn.edu.wzut.services;

import cn.edu.wzut.models.Student;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    // 数据库连接相关配置
    private static final String DB_URL = "jdbc:mysql://localhost:8888/test";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "123456";

    static {
        try {
            // 加载MySQL驱动
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 获取数据库连接的方法
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    @Override
    public List<Student> getStudents(String text) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students WHERE studentNo LIKE? OR name LIKE? OR className LIKE? OR studentNo =?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            // 设置模糊查询和精确查询的参数
            preparedStatement.setString(1, "%" + text + "%");
            preparedStatement.setString(2, "%" + text + "%");
            preparedStatement.setString(3, "%" + text + "%");
            preparedStatement.setString(4, text);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Student student = new Student();
                    student.setStudentNo(resultSet.getString("studentNo"));
                    student.setName(resultSet.getString("name"));
                    student.setClassName(resultSet.getString("className"));
                    student.setAge(resultSet.getInt("age"));
                    students.add(student);
                }
            }
            // 按照学号排序，这里假设学号可以转换为整数进行排序，如果学号格式不符合要求按字符串排序
            Collections.sort(students, (s1, s2) -> {
                try {
                    Integer num1 = Integer.parseInt(s1.getStudentNo());
                    Integer num2 = Integer.parseInt(s2.getStudentNo());
                    return num1.compareTo(num2);
                } catch (NumberFormatException e) {
                    return s1.getStudentNo().compareTo(s2.getStudentNo());
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    @Override
    public void saveStudents(List<Student> students) {
        // 批量插入学生数据到数据库，这里先删除原有数据再插入新数据（可根据实际需求调整为更新等更合适的逻辑）
        String deleteSql = "DELETE FROM students";
        String insertSql = "INSERT INTO students (studentNo, name, className, age) VALUES (?,?,?,?)";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            // 先执行删除操作
            statement.executeUpdate(deleteSql);
            // 循环插入每条学生数据
            for (Student student : students) {
                preparedStatement.setString(1, student.getStudentNo());
                preparedStatement.setString(2, student.getName());
                preparedStatement.setString(3, student.getClassName());
                preparedStatement.setInt(4, student.getAge());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String addStudent(@ModelAttribute("student") Student student) {
        if (student.getStudentNo() == null || student.getStudentNo().trim().isEmpty() ||
                student.getName() == null || student.getName().trim().isEmpty() ||
                student.getClassName() == null || student.getClassName().trim().isEmpty() ||
                student.getAge() == null) {
            return "redirect:/student/add";
        }


        String checkSql = "SELECT studentNo FROM students WHERE studentNo =?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatementForCheck = connection.prepareStatement(checkSql)) {
            preparedStatementForCheck.setString(1, student.getStudentNo());
            try (ResultSet resultSet = preparedStatementForCheck.executeQuery()) {
                if (resultSet.next()) {
                    return "redirect:/student/add";
                }
            }
            // 声明一个新的PreparedStatement用于插入操作
            String insertSql = "INSERT INTO students (studentNo, name, className, age) VALUES (?,?,?,?)";
            try (PreparedStatement preparedStatementForInsert = connection.prepareStatement(insertSql)) {
                preparedStatementForInsert.setString(1, student.getStudentNo());
                preparedStatementForInsert.setString(2, student.getName());
                preparedStatementForInsert.setString(3, student.getClassName());
                preparedStatementForInsert.setInt(4, student.getAge());
                preparedStatementForInsert.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "error";
        }

        return "redirect:/student/index";
    }
    // 新增的批量添加学生方法
    public String batchAddStudents(List<Student> studentList) {
        if (studentList == null || studentList.isEmpty()) {
            return "redirect:/student/add";
        }

        String checkSql = "SELECT studentNo FROM students WHERE studentNo =?";
        String insertSql = "INSERT INTO students (studentNo, name, className, age) VALUES (?,?,?,?)";

        try (Connection connection = getConnection()) {
            // 开启事务
            connection.setAutoCommit(false);
            for (Student student : studentList) {
                if (student.getStudentNo() == null || student.getStudentNo().trim().isEmpty() ||
                        student.getName() == null || student.getName().trim().isEmpty() ||
                        student.getClassName() == null || student.getClassName().trim().isEmpty() ||
                        student.getAge() == null) {
                    // 如果有学生信息不完整，回滚事务并返回添加页面
                    connection.rollback();
                    return "redirect:/student/add";
                }

                try (PreparedStatement preparedStatementForCheck = connection.prepareStatement(checkSql)) {
                    preparedStatementForCheck.setString(1, student.getStudentNo());
                    try (ResultSet resultSet = preparedStatementForCheck.executeQuery()) {
                        if (resultSet.next()) {
                            // 如果学号已存在，回滚事务并返回添加页面
                            connection.rollback();
                            return "redirect:/student/add";
                        }
                    }
                }

                try (PreparedStatement preparedStatementForInsert = connection.prepareStatement(insertSql)) {
                    preparedStatementForInsert.setString(1, student.getStudentNo());
                    preparedStatementForInsert.setString(2, student.getName());
                    preparedStatementForInsert.setString(3, student.getClassName());
                    preparedStatementForInsert.setInt(4, student.getAge());
                    preparedStatementForInsert.executeUpdate();
                }
            }
            // 所有学生信息添加成功，提交事务
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                // 出现异常，回滚事务
                getConnection().rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return "error";
        }

        return "redirect:/student/index";
    }
    @Override
    public void deleteStudent(String studentNo) {
        String sql = "DELETE FROM students WHERE studentNo =?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, studentNo);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void editStudent(Student student) {
        String updateSql = "UPDATE students SET name =?, className =?, age =? WHERE studentNo =?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
            preparedStatement.setString(1, student.getName());
            preparedStatement.setString(2, student.getClassName());
            preparedStatement.setInt(3, student.getAge());
            preparedStatement.setString(4, student.getStudentNo());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}