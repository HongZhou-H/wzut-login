package cn.edu.wzut.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserJson{
    public static String convertToJson(String input) {
        try {
            // 按行分割输入字符串，得到每行数据
            String[] lines = input.split("\n");
            List<Map<String, Object>> students = new ArrayList<>();

            // 遍历每行数据进行解析并封装成Map形式表示学生信息，添加到列表中
            for (String line : lines) {
                line = line.trim();
                if (!line.isEmpty()) {
                    String[] fields = line.split(",");
                    if (fields.length == 4) {
                        Map<String, Object> student = new HashMap<>();
                        student.put("id", fields[0].trim());
                        student.put("name", fields[1].trim());
                        student.put("pass", fields[2].trim());
                        student.put("identity", Integer.parseInt(fields[3].trim()));
                        students.add(student);
                    }
                }
            }

            // 创建ObjectMapper实例用于JSON序列化
            ObjectMapper objectMapper = new ObjectMapper();
            // 将学生信息的Map列表转换为JSON格式字符串（JSON数组形式）
            return objectMapper.writeValueAsString(students);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String input = "10,sa,sd,20\n10,sa,sd,20\n10,sa,sd,20\n10,sa,sd,20";
        String json = convertToJson(input);
        System.out.println(json);
    }
}