package cn.edu.wzut.models;
//必须与数据库里的列名同一！！！
public class User {
    public int id;
    // 将name属性的访问修饰符设为public
    public String name;
    public String pass;
    public int identity;

    // 其他属性的get、set方法等省略，此处重点展示属性访问权限相关修改

    public int getid() {
        return id;
    }

    public void setid(int id) {
        this.id = id;
    }

    public String getUsername() {
        return name;
    }

    public void setUsername(String name) {
        this.name = name;
    }

    public String getPassword() {
        return pass;
    }

    public void setPassword(String pass) {
        this.pass = pass;
    }

    public int getIdentity() {
        return identity;
    }

    public void setIdentity(int identity) {
        this.identity = identity;
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + name + '\'' +
                ", password='" + pass + '\'' +
                ", identity=" + identity +
                '}';
    }
}