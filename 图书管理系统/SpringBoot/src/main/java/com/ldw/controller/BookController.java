package com.ldw.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldw.common.Result;
import com.ldw.mapper.BookMapper;
import com.ldw.mapper.UserMapper;
import com.ldw.pojo.Book;
import com.ldw.pojo.User;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping( "/book")
public class BookController {
//
//    @GetMapping("/he")
//    public String he(){
//        return "he";
//    }

    @Resource
   BookMapper bookMapper;

//    分页查询
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,//设置默认值，起始页 1
                              @RequestParam (defaultValue = "10")Integer pageSize,//起始最大页10
                              @RequestParam (defaultValue = "")String search){//查询为空
        //下面是为了避免 某个数据库中某个字段为null时查询不出来
        LambdaQueryWrapper<Book> wrapper = Wrappers.<Book>lambdaQuery();
        if(StrUtil.isNotBlank(search)){//当search不为空时
            wrapper.like(Book::getName, search);
        }
        //分页查询  selectPage  第一个参数就是Page查询的起始位置和最大位置，第二个参数是模糊查询 搜索
        Page<Book> bookPage =bookMapper.selectPage(new Page<>(pageNum, pageSize),wrapper);
        return Result.success(bookPage);
    }

    //    插入
    @PostMapping
    public Result<?> save(@RequestBody Book book){//RequestBody将json转换成java对象
        bookMapper.insert(book);
        return Result.success();
    }

    //    更新
    @PutMapping
    public Result<?> update(@RequestBody Book book){//RequestBody将json转换成java对象
        bookMapper.updateById(book);
        return Result.success();
    }
    //    删除
    @DeleteMapping("/{id}")//需要一个url的参数
    public Result<?> update(@PathVariable Long id){//@PathVariable这是rul的参数获取方式
        bookMapper.deleteById(id);
        return Result.success();
    }
    //批量删除
    @PostMapping("/deleteBatch")//需要一个url的参数
    public Result<?> deleteBatch(@RequestBody List<Integer> ids){//RequestBody将json转换成java对象
        bookMapper.deleteBatchIds(ids);//deleteBatchIds是mybatis的方法
        return Result.success();
    }
}
