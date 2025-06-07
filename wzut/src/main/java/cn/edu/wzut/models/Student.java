package cn.edu.wzut.models;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Student {

    private Long id;
    private String username;
    private String password;

    // 生成Getter和Setter方法
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public String studentNo;
    public String name;
    public String className;
    public Integer age;

    // 自定义的getStudentNo方法示例，这里只是简单返回studentNo属性值，你可以根据具体需求修改逻辑
    public String getStudentNo() {
        return this.studentNo;
    }





    public Integer getAge() {
        return this.age;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}