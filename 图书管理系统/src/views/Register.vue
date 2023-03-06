<template>
<div  style="background-color: #80caf8;width: 100%;height: 100vh">
 <div>                                                  <!--  :rules="rules"   表单校验-->
    <el-form ref="form" :model="form" label-width="65px" class="login-box" :rules="rules">
      <h3 class="login-title">欢迎 注册</h3>
      <el-form-item prop="username" >
        <el-input style="margin-left: -5%" prefix-icon="el-icon-user-solid" type="text" placeholder="请输入账号" v-model="form.username"/>
      </el-form-item>
      <el-form-item  prop="password" >
        <el-input style="margin-left: -5%" prefix-icon="el-icon-lock" type="password" show-password placeholder=" 请输入密码" v-model="form.password"/>
      </el-form-item>
      <el-form-item prop="password">
        <el-input style="margin-left: -5%" prefix-icon="el-icon-lock" type="password" show-password placeholder=" 请确认密码" v-model="form.confirm"/>
      </el-form-item>
      <el-form-item>
        <el-button  type="primary" style="width: 80%;margin-left: 10px" @click="register">注册</el-button>
      </el-form-item>
    </el-form>
  </div>
</div>
</template>

<script>
  import request from "@/utils/rquest";

  export default {
    name: "Login",
    data() {
      return {
        form: {},
        //表单校验
        rules:{
          username:[ //对应 prop="username"
            {required:true,message:'请输入用户名',trigger:'blur'},
          ],
          password:[ //对应 prop="password"
            {required:true,message:'请输入密码',trigger:'blur'},
          ],
        }
      }
    },
    methods:{ //请求数据
      register(){    //this.form将表当存储的账号密码传输给后端
        //判断两次输入的密码是否一致
        if(this.form.password!== this.form.confirm){
          this.$message({
            type:"error",
            message:"两次输入密码不一致"
          })
          return
        }
        //验证表单的规则合法，才会继续执行之后的request
        this.$refs['form'].validate((valid)=>{
          if(valid) {
            request.post("/user/register",this.form).then(res=>{
              if(res.code === '0'){
                this.$message({
                  type:"success",
                  message:"注册成功"
                })
                this.$router.push("/login")
              }else{
                this.$message({
                  type:"error",
                  message:res.msg
                })
              }
            })
          }
        })
      }
    }
  }
</script>

<style scoped>
.login-box {
  background: #328be3;
  border: 1px solid #DCDFE6;
  width: 350px;
  margin: 0px auto;
  position: absolute;
  top: 23%;
  left: 35%;
  padding: 35px 35px 15px 0px;
  border-radius: 5px;
  -webkit-border-radius: 5px;
  -moz-border-radius: 5px;
  box-shadow: 0 0 25px #ffffff;
}

.login-title {
  font-size: 25px;
  text-align: center;
  margin: 0 auto 35px auto;
  margin-left: 20px;
  color: #ffffff;
}
</style>
