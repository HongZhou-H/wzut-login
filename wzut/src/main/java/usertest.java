import cn.edu.wzut.models.Student;
import cn.edu.wzut.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class usertest {
    public static void main(String[] args) {
        try {
            // 获取类路径下的student.txt文件资源
            ClassPathResource resource = new ClassPathResource("users.txt");
            File file = resource.getFile();
            // 创建ObjectMapper用于JSON的反序列化操作
            ObjectMapper objectMapper = new ObjectMapper();
            // 直接将文件内容反序列化为List<Student>类型
            List<User> users = objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
            // 遍历学生列表并打印每个学生的信息
            for (User user : users) {
                System.out.println("账号: " + user.getUsername()+" 密码: " +user.getPassword()+"\n");
                System.out.println("----------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}