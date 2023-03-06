package com.ldw.controller;
import com.ldw.common.Result;
import com.ldw.mapper.CategoryMapper;
import com.ldw.pojo.Category;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping( "/category")
public class CategoryController {

    @Resource
    CategoryMapper CategoryMapper;

    //    插入
    @PostMapping
    public Result<?> save(@RequestBody Category catagory){//RequestBody将json转换成java对象
        CategoryMapper.insert(catagory);
        return Result.success();
    }

    //    更新
    @PutMapping
    public Result<?> update(@RequestBody Category catagory){//RequestBody将json转换成java对象
        CategoryMapper.updateById(catagory);
        return Result.success();
    }
    //    删除
    @DeleteMapping("/{id}")//需要一个url的参数
    public Result<?> update(@PathVariable Integer id){//@PathVariable这是rul的参数获取方式
        CategoryMapper.deleteById(id);
        return Result.success();
    }
    @GetMapping("/{id}")//需要一个url的参数
    public Result<?>getById(@PathVariable Integer id){
        return Result.success(CategoryMapper.selectById(id));
    }

    /**
     * 分类父子查询
     * @return
     */
    @GetMapping
    public Result<?>getAll(){//先从数据库里面查询所有的列，放到一个集合里面
        //查询所有数据
        List<Category> allCategories=CategoryMapper.selectList(null);
        return Result.success(loopQuery(null,allCategories));
    }

    /**
     * 递归调用子集
     * @param pid
     * @param allCategories
     * @return
     */
    private List<Category>loopQuery(Integer pid,List<Category> allCategories){//将上面查询到所有数据进行排序
        List<Category> categoriesList=new ArrayList<>();

        for(Category category:allCategories){//循环遍历查到所有列
            if(pid==null){//判断传入的pid是否为首结点
                if(category.getPid()==null){//判断所有列里面是否有pid是null，有则做为结点
                    categoriesList.add(category);//放入集合
                    category.setChildren(loopQuery(category.getId(),allCategories));//然后设置它的子结点
                }
            }else if(pid.equals(category.getPid())){//查询集合里面与传入的pid是否有相同的
                categoriesList.add(category);//添加到集合
                category.setChildren(loopQuery(category.getId(), allCategories));//然后设置它的子结点
            }
        }
        return categoriesList;
    }
}
