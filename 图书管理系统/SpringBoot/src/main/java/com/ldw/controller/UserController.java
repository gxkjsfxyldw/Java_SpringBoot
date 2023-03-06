package com.ldw.controller;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldw.common.Result;
import com.ldw.mapper.UserMapper;
import com.ldw.pojo.User;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@RestController
@RequestMapping( "/user")
public class UserController {

//    @GetMapping("he")
//    public String as() {
//        return "users";
//    }
    @Resource
    UserMapper userMapper;

//    插入
    @PostMapping
    public Result<?> save(@RequestBody User user){//RequestBody将json转换成java对象
        if(user.getPassword()==null){
            user.setPassword("123456");
        }

        userMapper.insert(user);
        return Result.success();
    }

    /**
     * 用户分页查询，包含书籍表的一对多查询
     * @param pageNum
     * @param pageSize
     * @param search
     * @return
     */
//    分页查询
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,//设置默认值，起始页 1
                              @RequestParam (defaultValue = "10")Integer pageSize,//起始最大页10
                              @RequestParam (defaultValue = "")String search){//查询为空
//        new Page<>(pageNum, pageSize);

        //下面是为了避免 某个数据库中某个字段为null时查询不出来
        LambdaQueryWrapper<User> wrapper = Wrappers.<User>lambdaQuery();
        if(StrUtil.isNotBlank(search)){//当search不为空时
            wrapper.like(User::getNickName, search);
        }
        //分页查询  selectPage  第一个参数就是Page查询的起始位置和最大位置，第二个参数是模糊查询 搜索
        //Page<User> userPage = userMapper.selectPage(new Page<>(pageNum, pageSize),wrapper);
        //一对多查询
        Page<User> userPage = userMapper.findPage(new Page<>(pageNum, pageSize));
        return Result.success(userPage);
    }
    //    更新
    @PutMapping
    public Result<?> update(@RequestBody User user){//RequestBody将json转换成java对象
        userMapper.updateById(user);
        return Result.success();
    }
    //    删除
    @DeleteMapping("/{id}")//需要一个url的参数
    public Result<?> update(@PathVariable Long id){//RequestBody将json转换成java对象
        userMapper.deleteById(id);
        return Result.success();
    }

    // 登录
    @PostMapping("/login")//需要一个url的参数
    public Result<?> login(@RequestBody User user){
        //查询数据库，eq并且判断是否与前端传送过来的一致
       User res= userMapper.selectOne(Wrappers.<User>lambdaQuery()
                    .eq(User::getUsername,user.getUsername())  //判断用户名
                    .eq(User::getPassword,user.getPassword()));//判断密码
        if(res==null) {//如果用户不存在
            return Result.error("-1","用户名或密码错误");
        }
        //否则返回登录成功
        return Result.success(res);
    }

    // 注册
    @PostMapping("/register")//需要一个url的参数
    public Result<?> register(@RequestBody User user){
        //在注册用户之前，需要查询数据库是否存在此用户
        User users= userMapper.selectOne(Wrappers.<User>lambdaQuery().eq(User::getUsername,user.getUsername()));
        if(users!=null) {//如果数据库用户不为空
            return Result.error("-1","用户名重复");
        }
        if(user.getPassword()==null){//当前端未输入密码时设置默认密码
            user.setPassword("123456");//默认密码
        }
        userMapper.insert(user);
        //否则返回登录成功
        return Result.success();
    }
    //登录权限分离 查询当前用户最新在数据库的信息，返回将此用户信息到前端
    @GetMapping("/{id}")
    public Result<?> getByid(@PathVariable Long id){
        return Result.success(userMapper.selectById(id));
    }


}
