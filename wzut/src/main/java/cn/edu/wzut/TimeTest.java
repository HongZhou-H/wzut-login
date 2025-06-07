package cn.edu.wzut;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;

public class TimeTest{
    public static void main(String[] args) {
        String publishTime = "2023-04-15T10:00:00"; // 示例时间
        int duration = 65; // 示例时长，单位分钟

        // 解析时间字符串
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(publishTime, formatter);

        // 增加时长
        dateTime = dateTime.plusMinutes(duration);

        // 格式化新时间
        String newPublishTime = dateTime.format(formatter);

        // 输出结果
        System.out.println("原始时间: " + publishTime);
        System.out.println("增加时长后的时间: " + newPublishTime);
    }
}
