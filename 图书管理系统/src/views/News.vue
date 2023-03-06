<template>
  <div style="padding: 10px">
    <!--    功能区域-->
    <div style="margin: 10px 0">
      <el-button type="primary" @click="add">新增</el-button>
      <el-button type="primary">导入</el-button>
      <el-button type="primary">导出</el-button>
    </div>
    <!--    搜索区域-->
    <div style="margin: 10px 0">
      <el-input v-model="search" placeholder="请输入内容" style="width: 20%" clearable></el-input>
      <el-button type="primary" style="margin-left: 5px" @click="load">查询</el-button>
    </div>
    <!--    功能区域-->
    <el-table
        :data="tableData"
        border
        stripe
        style="width: 100%">
      <el-table-column
          prop="id"
          label="ID"
          sortable
      >
      </el-table-column>
      <el-table-column
          prop="title"
          label="标题">
      </el-table-column>

      <el-table-column
          prop="author"
          label="作者">
      </el-table-column>
      <el-table-column
          prop="time"
          label="时间">
      </el-table-column>

      <el-table-column label="操作" width="208">
        <template slot-scope="scope">
          <el-button size="mini" @click="detailed(scope.row)" style="margin-right: 2px">详情</el-button>
          <el-button size="mini" @click="handleEdit(scope.row)" style="margin-right: 2px">编辑</el-button>
          <el-popconfirm title="确认删除吗？" @confirm="handDelete(scope.row.id)">
            <el-button size="mini" type="danger" slot="reference" >删除</el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <div style="margin: 10px 0">
      <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage4"
          :page-sizes="[1,5, 10, 20]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="total">
      </el-pagination>
    </div>

    <el-dialog title="提示" :visible.sync="dialogVisible" width="50%">
      <el-form :model="form" label-width="120px">
        <el-form-item label="标题" >
          <el-input v-model="form.title" style="width: 60%;margin-left: -120px"></el-input>
        </el-form-item>


<!--        文本编辑器-->
        <div id="div1" style="width: 100%"></div>
<!--        <el-form-item label="内容">-->
<!--          <el-input v-model="form.content" style="width: 60%"></el-input>-->
<!--        </el-form-item>-->

      </el-form>
      <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="save">确 定</el-button>
          </span>
    </el-dialog>
    <el-dialog title="详情" :visible.sync="vis" width="50%">
          <el-card>
            <div v-html="details.content" style="min-height: 100px"></div>
          </el-card>
    </el-dialog>

  </div>
</template>

<script src="https://unpkg.com/wangeditor/release/wangEditor.min.js"></script>

<script>

import request from "@/utils/rquest";
import E from 'wangeditor'

let editor;//全局变量

export default {
  name: 'News',
  components: {
  },
  data(){
    return {
      form:{},//form的值自动存储成json,表单提交数据后全部存储在这里
      dialogVisible:false,
      search:'',
      currentPage4:1, //当前页 变量
      pageSize:10,  //每页的个数
      total:0,//默认显示10条数据，从后台取
      tableData: [], //将后台查询到的数据存储
      vis:false, //关闭文章详情
      details:{}
    }
  },
  //在页面加载时调用
  created(){
    this.load()
  },
  //在页面所有元素加载完之后才会调用
  mounted() {

  },
  methods:{
    //文章详情
    detailed(row){
      this.details=row
      this.vis=true
    },
    //文件上传
    filesUploadSuccess(res){
      console.log(res)
      this.form.cover=res.data
    },
    //查询
    load(){
      request.get("/news", {//传入的参数，也就是controller层findPage定义的
        //将参数封装到请求头中
        params:{
          //url的三个参数
          pageNum:this.currentPage4,
          pageSize:this.pageSize,
          search:this.search
        }
      }).then(res =>{//完成上面的操作之后，将结果封装到这里再执行
        console.log(res)
        this.tableData=res.data.records//res就是后端传送过来的整个json数据，将他们赋值给tableData变量
        this.total=res.data.total//总条数
      })
    },
    //用来打开新增界面  打开弹窗
    add(){
      //打开对话框
      this.dialogVisible=true
      this.form={}//这样就是将表单的数据全部清空

      this.$nextTick(()=>{
        if(editor!=null){
          editor.destroy()//打开弹窗之前之前先释放之前的数据
        }
        editor = new E( document.getElementById('div1') )//使用全局变量
        //配置文件上传接口
        editor.config.uploadImgServer='http://localhost:9090/files/editor/upload'
        editor.config.uploadFileName="file"//与后台接口参数一致
        editor.create()
      })
    },
    //将数据传入后台
    save(){
      // console.log(editor.txt.html())//获取文本编辑器里面的值
      this.form.content=editor.txt.html();//然后赋予到实体当中

      //判断表单提交的更新是否有id
      if(this.form.id) {//有id则更新
        request.put("/news",this.form).then(res=>{
          console.log(res)
          //  在操作成功之后显示弹窗，在失败时弹出失败提示
          if(res.code==='0'){//三个等于号表示判断带类型的
            this.$message({
              type:"success",
              message:"更新成功"
            })
          }else{
            this.$message({
              type:"error",
              message:res.msg
            })
          }
          this.load() //在完成操作后刷新表格数据
          this.dialogVisible=false//将弹窗关闭
        })
      }else{//没有这个id就新增
        //获取session的用户值，如果获取不到赋空值  文本编辑器
        var userStr= sessionStorage.getItem("user")||"{}"
        //将session值用json中解析出来
        let user=JSON.parse(userStr)
        //将表单的id赋值为当前session用户
        this.form.author=user.nickName

        //获取后端request对象  ，这里可能会出现跨域的问题，因为前端是8080.后端9090不能直接访问，需要解决跨域问题
        request.post("/news",this.form).then(res=>{//获取端口+接口,传递对象参数,拿到后台处理结果res
          console.log(res)
          //  在操作成功之后显示弹窗，在失败时弹出失败提示
          if(res.code==='0'){
            this.$message({
              type:"success",
              message:"新增成功"
            })
          }else{
            this.$message({
              type:"error",
              message:res.msg
            })
          }
          this.load() //在完成操作后刷新表格数据
          this.dialogVisible=false//将弹窗关闭
        })
      }
    },
    //编辑
    handleEdit(row){
      //防止编辑之后又关闭数据的重复性，进行深拷贝
      this.form=JSON.parse(JSON.stringify(row))
      //  将编辑弹窗打开
      this.dialogVisible=true;

      this.$nextTick(()=>{
        if(editor!=null){
          editor.destroy()//打开弹窗之前之前先释放之前的数据
        }
        editor = new E( document.getElementById('div1') )
        //配置文件上传接口
        editor.config.uploadImgServer='http://localhost:9090/files/editor/upload'
        editor.config.uploadFileName="file"//与后台接口参数一致
        editor.create()
        editor.txt.html(row.content)//追加之前的信息到文本框里面
      })
    },
    //删除
    handDelete(id){
      console.log(id)      //拼接传值
      request.delete("/news/"+id).then(res=>{
        console.log(res)

        //  在操作成功之后显示弹窗，在失败时弹出失败提示
        if (res.code === '0') {
          this.$message({
            type: "success",
            message: "删除成功"
          })
        } else {
          this.$message({
            type: "error",
            message: res.msg
          })
        }
        this.load() //在完成操作后刷新表格数据
        this.dialogVisible = false//将弹窗关闭
      })
    },
    //当改变页码时触发
    handleSizeChange(pageSize){
      this.pageSize=pageSize //在改变页数时赋值
      this.load()
    },
    //改变当前页时触发
    handleCurrentChange(pageNum){
      this.currentPage4=pageNum
      this.load()
    },
  }
}
</script>
