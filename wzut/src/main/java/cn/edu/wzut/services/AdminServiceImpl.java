package cn.edu.wzut.services;


import cn.edu.wzut.models.User;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

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
    public List<User> getUsers(String text) {
        List<User> users = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM t_user");
        List<Object> params = new ArrayList<>();

        if (text!= null &&!text.isEmpty()) {
            try {
                // 尝试将text转换为整数，若能转换成功，则当作id进行精确查询
                int id = Integer.parseInt(text);
                sql.append(" WHERE id =?");
                params.add(id);
            } catch (NumberFormatException e) {
                // 如果转换失败，当作name进行模糊查询
                sql.append(" WHERE name LIKE?");
                params.add("%" + text + "%");
            }
        }

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql.toString())) {
            // 设置参数
            for (int i = 0; i < params.size(); i++) {
                preparedStatement.setObject(i + 1, params.get(i));
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    User user = new User();
                    user.setid(resultSet.getInt("id"));
                    user.setUsername(resultSet.getString("name"));
                    user.setPassword(resultSet.getString("pass"));
                    user.setIdentity(resultSet.getInt("identity"));
                    users.add(user);
                }
            }
            // 按照id排序
            Collections.sort(users, (s1, s2) -> {
                Integer num1 = s1.getid();
                Integer num2 = s2.getid();
                return num1.compareTo(num2);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }

    @Override
    public void saveUsers(List<User> users) {
        // 批量插入学生数据到数据库，这里先删除原有数据再插入新数据（可根据实际需求调整为更新等更合适的逻辑）
        String deleteSql = "DELETE FROM t_user";
        String insertSql = "INSERT INTO t_user (id, name, pass,identity) VALUES (?,?,?,?)";
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             PreparedStatement preparedStatement = connection.prepareStatement(insertSql)) {
            // 先执行删除操作
            statement.executeUpdate(deleteSql);
            // 循环插入每条学生数据
            for (User user : users) {
                preparedStatement.setInt(1, user.getid());
                preparedStatement.setString(2, user.getUsername());
                preparedStatement.setString(3, user.getPassword());
                preparedStatement.setInt(4, user.getIdentity());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String addUser(@ModelAttribute("user") User user) {
        Integer id=user.getid();Integer identity= user.getIdentity();
        if (id == null  ||
                user.getUsername() == null || user.getUsername().trim().isEmpty() ||
                user.getPassword() == null || user.getPassword().trim().isEmpty() ||
                identity==null
                ) {
            return "redirect:/admin/add";
        }


        String checkSql = "SELECT id FROM t_user WHERE id =? AND name=?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatementForCheck = connection.prepareStatement(checkSql)) {
            preparedStatementForCheck.setInt(1, user.getid());
            preparedStatementForCheck.setString(2, user.getUsername());
            try (ResultSet resultSet = preparedStatementForCheck.executeQuery()) {
                if (resultSet.next()) {
                    return "redirect:/admin/add";
                }
            }
            // 声明一个新的PreparedStatement用于插入操作
            String insertSql = "INSERT INTO t_user (id, name, pass, identity) VALUES (?,?,?,?)";
            try (PreparedStatement preparedStatementForInsert = connection.prepareStatement(insertSql)) {
                preparedStatementForInsert.setInt(1, user.getid());
                preparedStatementForInsert.setString(2, user.getUsername());
                preparedStatementForInsert.setString(3, user.getPassword());
                preparedStatementForInsert.setInt(4, user.getIdentity());
                preparedStatementForInsert.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "error";
        }

        return "redirect:/admin";
    }
    // 新增的批量添加学生方法
    public String batchAddUsers(List<User> userList) {
        if (userList == null || userList.isEmpty()) {
            return "redirect:/admin/add";
        }

        String checkSql = "SELECT id FROM t_user WHERE id =? and name=?";
        String insertSql = "INSERT INTO t_user (id, name, pass, identity) VALUES (?,?,?,?)";

        try (Connection connection = getConnection()) {
            // 开启事务
            connection.setAutoCommit(false);
            for (User user : userList) {
                Integer id=user.getid();Integer identity= user.getIdentity();
                if (id == null  ||
                        user.getUsername() == null || user.getUsername().trim().isEmpty() ||
                        user.getPassword() == null || user.getPassword().trim().isEmpty() ||
                        identity==null
                ) {
                    connection.rollback();
                    return "redirect:/admin/add";
                }

                try (PreparedStatement preparedStatementForCheck = connection.prepareStatement(checkSql)) {
                    preparedStatementForCheck.setInt(1, user.getid());
                    preparedStatementForCheck.setString(2, user.getUsername());
                    try (ResultSet resultSet = preparedStatementForCheck.executeQuery()) {
                        if (resultSet.next()) {
                            // 如果学号已存在，回滚事务并返回添加页面
                            connection.rollback();
                            return "redirect:/admin/add";
                        }
                    }
                }

                try (PreparedStatement preparedStatementForInsert = connection.prepareStatement(insertSql)) {
                    preparedStatementForInsert.setInt(1, user.getid());
                    preparedStatementForInsert.setString(2, user.getUsername());
                    preparedStatementForInsert.setString(3, user.getPassword());
                    preparedStatementForInsert.setInt(4, user.getIdentity());
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

        return "redirect:/admin";
    }
    @Override
    public void deleteUser(int id) {
        ResultSet rs = null;
        Statement stm;
        Connection connect= null;
        String sql2="SELECT identity FROM t_user WHERE id =?";

        String sql = "DELETE FROM t_user WHERE id =?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void editUser(User user) {
        String updateSql = "UPDATE t_user SET name =?, pass =?, identity =? WHERE id =?";
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateSql)) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setInt(3, user.getIdentity());
            preparedStatement.setInt(4, user.getid());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}