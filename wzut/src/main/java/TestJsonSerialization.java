import cn.edu.wzut.models.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestJsonSerialization {
    public static void main(String[] args) {
        List<User> testUsers = new ArrayList<>();
        User user1 = new User();
        user1.setUsername("testuser1");
        user1.setPassword("testpassword1");
        testUsers.add(user1);

        try {
            // 设置ObjectMapper的配置，使其格式化输出JSON（方便查看格式）
            ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
            String json = objectMapper.writeValueAsString(testUsers);
            System.out.println(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
