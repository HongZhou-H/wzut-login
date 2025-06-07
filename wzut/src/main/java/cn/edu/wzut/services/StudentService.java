package cn.edu.wzut.services;

import cn.edu.wzut.models.Student;

import java.util.List;

public interface StudentService {

    //根据用户输入的条件查询学生清单
    List<Student> getStudents(String text);

    //保存学生清单信息
    void saveStudents(List<Student> students);

    //新增学生信息
    String addStudent(Student student);

    //删除学生信息
    void deleteStudent(String studentNo);
    void editStudent(Student student);

    String batchAddStudents(List<Student> studentList);
}
