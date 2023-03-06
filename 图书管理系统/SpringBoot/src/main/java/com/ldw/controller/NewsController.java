package com.ldw.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ldw.common.Result;
import com.ldw.mapper.NewsMapper;
import com.ldw.pojo.News;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;

@RestController
@RequestMapping( "/news")
public class NewsController {
    @Resource
    NewsMapper newsMapper;

//    分页查询
    @GetMapping
    public Result<?> findPage(@RequestParam(defaultValue = "1") Integer pageNum,//设置默认值，起始页 1
                              @RequestParam (defaultValue = "10")Integer pageSize,//起始最大页10
                              @RequestParam (defaultValue = "")String search){//查询为空
        //下面是为了避免 某个数据库中某个字段为null时查询不出来
        LambdaQueryWrapper<News> wrapper = Wrappers.<News>lambdaQuery();
        if(StrUtil.isNotBlank(search)){//当search不为空时
            wrapper.like(News::getTitle, search);
        }
        //分页查询  selectPage  第一个参数就是Page查询的起始位置和最大位置，第二个参数是模糊查询 搜索
        Page<News> NewsPage =newsMapper.selectPage(new Page<>(pageNum, pageSize),wrapper);
        return Result.success(NewsPage);
    }

    //    插入
    @PostMapping
    public Result<?> save(@RequestBody News News){//RequestBody将json转换成java对象
        News.setTime(new Date());//设置当前时间
        newsMapper.insert(News);
        return Result.success();
    }

    //    更新
    @PutMapping
    public Result<?> update(@RequestBody News News){//RequestBody将json转换成java对象
        newsMapper.updateById(News);
        return Result.success();
    }
    //    删除
    @DeleteMapping("/{id}")//需要一个url的参数
    public Result<?> update(@PathVariable Long id){//RequestBody将json转换成java对象
        newsMapper.deleteById(id);
        return Result.success();
    }
}
