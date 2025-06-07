import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class sjktest {
    private static Connection dbConn = null;

    public static void main(String[] args) {
        try {
            System.out.println("进来了");
            // 1. 加载驱动（如果使用的JDBC版本较新，可不写此行，前提是依赖正确引入，这里写上更稳妥些）
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("加载驱动成功！");
            // 2. 连接，这里使用SQL Server账户验证，修改数据库名等信息为正确的
            String dbURL = "jdbc:sqlserver://127.0.0.1:1433;DatabaseName=master";
            dbConn = DriverManager.getConnection(dbURL, "sa", "123456");
            System.out.println("连接数据库成功！");
            // 编写正确的查询语句，这里以查询students表为例，替换成实际要查询的表和字段等信息
            String sql = "select * from table1";
            PreparedStatement statement = null;
            statement = dbConn.prepareStatement(sql);
            ResultSet res = null;
            res = statement.executeQuery();
            while (res.next()) {
                // 这里获取相应字段并输出，假设表中有个名为student_name的字段，按需修改
                String title = res.getString("b");
                System.out.println(title);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("连接数据库失败！");
        } finally {
            // 记得关闭资源，释放连接等，避免资源泄漏

        }
    }
}