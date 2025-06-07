import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class mysql {
    private static Connection dbConn = null;

    public static void main(String[] args) {
        try {
            System.out.println("进来了");
            // 1. 加载MySQL驱动（虽然较新JDBC理论上可自动加载，但写上更稳妥，前提是依赖已正确引入项目）
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("加载驱动成功！");
            // 2. 连接数据库，这里假设本地MySQL，端口3306，数据库名为testdb（替换成你实际的数据库名）
            String dbURL = "jdbc:mysql://127.0.0.1:8888/test?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
            String username = "root";
            String password = "123456";
            dbConn = DriverManager.getConnection(dbURL, username, password);
            System.out.println("连接数据库成功！");
            // 3. 编写查询语句，这里以查询名为students的表为例（替换成实际要查询的表名和字段等信息）
            String sql = "select * from students";
            PreparedStatement statement = null;
            statement = dbConn.prepareStatement(sql);
            ResultSet res = null;
            res = statement.executeQuery();
            while (res.next()) {
                // 假设表中有个字段名为student_name，按需修改获取的字段名
                String title = res.getString("name");
                System.out.println(title);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("连接数据库失败！");
        } finally {
            // 关闭资源，防止资源泄漏，在finally块中确保无论是否出现异常都执行关闭操作
            try {
                if (dbConn!= null) {
                    dbConn.close();
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}