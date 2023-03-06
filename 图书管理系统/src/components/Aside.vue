<!--网页导航侧边栏-->
<!--        router  自动根据路由点击显示不同主页  -->
<!-- :default-active高亮不同的下拉框-->
<template>
  <div>
    <el-menu
        style="width: 200px; min-height: calc(100vh - 50px)"
        :default-active="$route.path.substr(1)"
        router
        class="el-menu-vertical-demo">

      <el-submenu index="1" v-if="user.role === 1">
        <template slot="title">
          <i class="el-icon-s-tools"></i>
          <span>系统管理</span>
        </template>
        <el-menu-item-group>
          <el-menu-item  index="user">用户管理</el-menu-item>
        </el-menu-item-group>
      </el-submenu>

      <el-submenu index="2">
        <template slot="title">
          <i class="el-icon-s-data"></i>
          <span>数据管理</span>
        </template>
        <el-menu-item-group>
          <el-menu-item  index="book">书籍管理</el-menu-item>
        </el-menu-item-group>
        <el-menu-item-group>
          <el-menu-item  index="news">新闻管理</el-menu-item>
        </el-menu-item-group>
        <el-menu-item-group>
          <el-menu-item  index="category">分类管理</el-menu-item>
        </el-menu-item-group>
      </el-submenu>
    </el-menu>
  </div>
</template>

<script>
  export default {
    name: "Aside",
    data(){
      return{
        user:{},
        path:this.$route.path //默认高亮的菜单
         // path:this.$route.path
      }
    },
    created() {
       // console.log(this.$route.path)
      let userstr= sessionStorage.getItem("user")||"{}"; //将session的值取出来并存储到临时对象中
      this.user=JSON.parse(userstr)
      //请求服务端，确认当前登录用户的合法信息
      request.get("/user/"+this.user.id).then(res=>{
        if(res.code==='0'){
          this.user=res.data
        }
      })
    }
  }
</script>

<style scoped>

</style>
