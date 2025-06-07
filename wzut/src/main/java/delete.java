import cn.edu.wzut.models.Student;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.core.io.ClassPathResource;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class delete {
    public static void main(String[] args) {

            try {
                // 获取类路径下的student.txt文件资源
                ClassPathResource resource = new ClassPathResource("students.txt");
                File file = resource.getFile();
                // 创建ObjectMapper用于JSON的反序列化操作
                ObjectMapper objectMapper = new ObjectMapper();
                // 直接将文件内容反序列化为List<Student>类型
                List<Student> students = objectMapper.readValue(file, objectMapper.getTypeFactory().constructCollectionType(List.class, Student.class));

                // 获取用户输入，这里假设以学号作为删除的依据，你也可以根据实际需求修改为其他条件
                Scanner scanner = new Scanner(System.in);
                System.out.println("请输入要删除学生的学号：");
                String studentNoToDelete = scanner.nextLine();

                // 标记是否找到要删除的学生
                boolean found = false;
                for (int i = 0; i < students.size(); i++) {
                    if (students.get(i).getStudentNo().equals(studentNoToDelete)) {
                        // 找到要删除的学生，从列表中移除
                        students.remove(i);
                        found = true;
                        break;
                    }
                }

                if (found) {
                    // 使用ObjectMapper将更新后的学生列表写回到文件中
                    objectMapper.writeValue(file, students);
                    System.out.println("删除成功！");
                } else {
                    System.out.println("未找到对应学号的学生，删除操作失败。");
                }

                // 关闭Scanner，释放资源
                scanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
}