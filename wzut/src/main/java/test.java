import cn.edu.wzut.models.Student;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class test {
    public static void main(String[] args) {
        try {
            // 获取类路径下的student.txt文件资源
            ClassPathResource resource = new ClassPathResource("students.txt");
            File file = resource.getFile();
            // 创建ObjectMapper用于JSON的反序列化操作
            ObjectMapper objectMapper = new ObjectMapper();
            // 直接将文件内容反序列化为List<Student>类型
            List<Student> students = objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Student.class));
            // 遍历学生列表并打印每个学生的信息
            for (Student student : students) {
                System.out.println("学号: " + student.getStudentNo()+" 姓名: " + student.getName()+" 班级: " + student.getClassName()+" 年龄: " + student.getAge()+"\n");
                System.out.println("----------------------");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}