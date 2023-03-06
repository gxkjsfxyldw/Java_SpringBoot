package com.ldw.pojo;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.lang.annotation.Target;
import java.util.List;

@TableName("user")//直接定位到数据库中user的表
@Data
public class User {
    @TableId(type = IdType.AUTO)
    private int id;
    private String username;
    private String password;
    private String nickName;
    private int age;
    private String sex;
    private String address;
    private Integer role;

    //一对多查询
    @TableField(exist = false) //表示在数据库重实际不存在此字段 bookList
    private List<Book>bookList;
}
