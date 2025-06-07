package cn.edu.wzut;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class UserDao {

    // 假设这里有合适的获取数据库连接的方法，原代码中是 getConnection()，此处保持一致形式
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
                    user.setId(resultSet.getInt("id"));
                    user.setUsername(resultSet.getString("name"));
                    user.setPassword(resultSet.getString("pass"));
                    user.setIdentity(resultSet.getInt("identity"));

                    users.add(user);
                }
            }
            // 按照id排序
            Collections.sort(users, (s1, s2) -> {
                Integer num1 = s1.getId();
                Integer num2 = s2.getId();
                return num1.compareTo(num2);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return users;
    }}

    // 假设User类有对应的属性和相应的get、set方法，如下示例（需根据实际完整定义）
     class User {
        private int id;
        private String username;
        private String password;
        private int identity;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getIdentity() {
            return identity;
        }

        public void setIdentity(int identity) {
            this.identity = identity;
        }
        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", password='" + password + '\'' +
                    ", identity=" + identity +
                    '}';
        }
    }
public class testuser {
    public static void main(String[] args) {
        UserDao userDao = new UserDao();

        // 传入空字符串，查询并显示全部用户信息
        List<User> allUsers = userDao.getUsers("");

        // 传入数字，按照id查找
        List<User> usersById = userDao.getUsers("1");

        // 传入字符串，按照name模糊查找
        List<User> usersByName = userDao.getUsers("张");
        // 使用增强for循环遍历并打印
        for (User user : allUsers) {
            System.out.println(user);
        }

    }
}
