<template>
  <div style="padding: 10px">
    <!--    功能区域-->
    <div style="margin: 10px 0">
      <el-button type="primary" @click="add">新增</el-button>
      <el-popconfirm
          title="确定删除吗？"
          @confirm="deleteBatch"
      >
        <template slot="reference">
          <el-button type="danger" v-if="user.role===1">批量删除</el-button>
        </template>
      </el-popconfirm>

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
        style="width: 100%"
        @selection-change="handleSelectionChange"
    >
      <el-table-column
        type="selection"
        width="55"
      >
      </el-table-column>
      <el-table-column
          prop="id"
          label="ID"
          sortable
      >
      </el-table-column>
      <el-table-column
          prop="name"
          label="书名">
      </el-table-column>

      <el-table-column
          prop="price"
          label="单价">
      </el-table-column>
      <el-table-column
          prop="author"
          label="作者">
      </el-table-column>
      <el-table-column
          prop="createTime"
          label="出版时间">
      </el-table-column>
      <el-table-column
          label="封面">
          <template slot-scope="scope">
            <el-image
                preview-teleported="true"
                style="width: 100px; height: 100px"
                :src="scope.row.cover"
                :preview-src-list="[scope.row.cover]">
            </el-image>
          </template>
      </el-table-column>

      <el-table-column label="操作" width="140">
        <template slot-scope="scope">
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

      <el-dialog title="提示" :visible.sync="dialogVisible" width="30%">
        <el-form :model="form" label-width="120px">
          <el-form-item label="书名">
            <el-input v-model="form.name" style="width: 60%"></el-input>
          </el-form-item>
          <el-form-item label="价格">
            <el-input v-model="form.price" style="width: 60%"></el-input>
          </el-form-item>
          <el-form-item label="作者">
            <el-input v-model="form.author" style="width: 60%"></el-input>
          </el-form-item>
          <el-form-item label="书籍拥有者ID">
            <el-input v-model="form.userID" style="width: 60%"></el-input>
          </el-form-item>
          <el-form-item label="出版时间">
            <el-date-picker v-model="form.createTime" type="date" style="width: 80%" clearable></el-date-picker>
          </el-form-item>
          <el-form-item label="封面">
            <!-- 后台文件上传的接口-->
            <el-upload
                ref="upload"
                action="http://localhost:9090/files/upload" :on-success="filesUploadSuccess">
              <el-button  type="primary">点击上传</el-button>
            </el-upload>
          </el-form-item>
        </el-form>

        <span slot="footer" class="dialog-footer">
            <el-button @click="dialogVisible = false">取 消</el-button>
            <el-button type="primary" @click="save">确 定</el-button>
          </span>
      </el-dialog>

    </div>
  </div>
</template>

<script>

import request from "@/utils/rquest";

export default {
  name: 'Book',
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
      tableData: [],//将后台查询到的数据存储
      ids:[],//批量删除
      user:{}
    }
  },
  //在页面加载时调用
  created(){
    let userStr=sessionStorage.getItem("user")||"{}"
    this.user=JSON.parse(userStr)
    //请求服务器，确认当前登录用户的合法信息
    request.get("/user/"+this.user.id).then(res=>{
      if(res.code==='0'){
        this.user=res.data
      }
    })
    this.load()
  },
  methods:{
    //批量删除
    handleSelectionChange(val){
      //val表示所选择到的行对象
      this.ids=val.map(v=>v.id)  //(v=>v.id) 表示[{id,name},{id,name}] =>[id,id]
    },
    deleteBatch(){
      if(!this.ids.length){
        this.$message.warning("请选择数据！")
        return
      }
      request.post("/book/deleteBatch",this.ids).then(res=>{
        if(res.code==='0'){
          this.$message.success("批量删除成功")
          this.load()
        }else{
          this.$message.error(res.msg)
        }
      })
    },
    //文件上传
    filesUploadSuccess(res){
      console.log(res)
      this.form.cover=res.data
    },
    //查询
    load(){
      request.get("/book", {//传入的参数，也就是controller层findPage定义的
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
    //用来打开新增界面
    add(){
      //打开对话框
      this.dialogVisible=true
      this.form={}//这样就是将表单的数据全部清空
      if(this.$refs['upload']) {//先判断此内容demo是否存在，即 弹窗是否出现
        this.$refs['upload'].clearFiles()//清空历史文件列表，书籍封面
      }
    },
    //将数据传入后台
    save(){
      //判断表单提交的更新是否有id
      if(this.form.id) {//有id则更新
        request.put("/book",this.form).then(res=>{
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
        //获取后端request对象  ，这里可能会出现跨域的问题，因为前端是8080.后端9090不能直接访问，需要解决跨域问题
        request.post("/book",this.form).then(res=>{//获取端口+接口,传递对象参数,拿到后台处理结果res
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
      //$nextTick解决数据修改时，弹窗未出现而提前获取元素，解决未来的demo不存在的问题
      this.$nextTick(()=>{
        this.$refs['upload'].clearFiles()//清空历史文件列表，书籍封面
      })
    },
    //删除
    handDelete(id){
      console.log(id)      //拼接传值
      request.delete("/book/"+id).then(res=>{
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
