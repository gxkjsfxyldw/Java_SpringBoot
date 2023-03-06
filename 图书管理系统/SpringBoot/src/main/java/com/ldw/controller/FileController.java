package com.ldw.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.ldw.common.Result;
import netscape.javascript.JSObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.List;

@RestController
@RequestMapping("/files")
public class FileController {
    //将url固定的
    @Value("${server.port}")//读取文件application下的port : 9090
    private String port;

    private static final String ip="http://localhost";

    /**
     * 上传接口
     * @param file
     * @return  0成功，1失败
     * @throws IOException
     */

    //文件上传
    @PostMapping("/upload")
    public Result<?> upload (MultipartFile file) throws IOException {
        //获取源文件名称
        String originalFilename = file.getOriginalFilename();
        //存储的时候直接使用此名称  有可能是相同的文件名  UUID.randomUUID();
        String flag= IdUtil.fastSimpleUUID();
        //获取文件上传的路径，获取当前项目的文件路径，和存储文件的路径  绝对路径：通过下面找到files文件夹
        String RootFilePath = System.getProperty("user.dir")+"/SpringBoot/src/main/resources/files/"+flag+"_"+originalFilename;
        //把文件以字节流的形式写入上传路径地址，使用工具类，封装成输入输出流，并保存到文件夹中
        FileUtil.writeBytes(file.getBytes(),RootFilePath);
        return Result.success(ip+":"+port+"/files/"+flag+"_"+originalFilename);//返回url  通过此url可直接下载文件
    }

    /**
     * 文件下载接口
     * @param flag
     * @param response
     */
    //文件下载
    @GetMapping("{flag}")
    public void getFile(@PathVariable String flag,HttpServletResponse response){
        //新建一个输出流对象
        OutputStream os;
        //获取文件上传时的存放的路径
        String basePath=System.getProperty("user.dir")+"/SpringBoot/src/main/resources/files/";
        //获取到文件根路径下的所有的文件名称
        List<String> fileName= FileUtil.listFileNames(basePath);
        //找到跟flag一致的文件名
        String filename=fileName.stream().filter(name->name.contains(flag)).findAny().orElse("");

        try {
            //判断文件是否存在，存在则执行下载功能
            if(StrUtil.isNotBlank(filename)){

               response.addHeader("Content-Disposition","attachment;filename"+ URLEncoder.encode(filename,"utf-8"));
               response.setContentType("application/octet-stream");
               //通过文件的路径读取文件字节流
               byte[] bytes=FileUtil.readBytes(basePath + filename);
               //通过字节流返回文件
               os = response.getOutputStream();
               os.write(bytes);
               os.flush();
               os.close();
            }
        } catch (IOException e) {
            System.out.println("文件下载失败");
        }
    }

    /**
     * 文本编辑器图片上传接口
     * @param file
     * @return
     * @throws IOException
     */
    //文本编辑器图片上传接口
    @PostMapping("/editor/upload")
        public JSON editorUpload (MultipartFile file) throws IOException {
        //获取源文件名称
        String originalFilename = file.getOriginalFilename();
        //存储的时候直接使用此名称  有可能是相同的文件名  UUID.randomUUID();
        String flag= IdUtil.fastSimpleUUID();
        //获取文件上传的路径，获取当前项目的文件路径，和存储文件的路径  绝对路径：通过下面找到files文件夹
        String RootFilePath = System.getProperty("user.dir")+"/SpringBoot/src/main/resources/files/"+flag+"_"+originalFilename;
        //把文件以字节流的形式写入上传路径地址，使用工具类，封装成输入输出流，并保存到文件夹中
        FileUtil.writeBytes(file.getBytes(),RootFilePath);
        //只是改变一个url的结构
        String url=ip+":"+port+"/files/"+flag+"_"+originalFilename;
        //要自定义一个格式
        JSONObject json=new JSONObject();
        json.set("errno",0);
        JSONArray arr=new JSONArray();
        JSONObject data = new JSONObject();
        arr.add(data);
        data.set("url",url);
        json.set("data",arr);
        return json;//返回url  通过此url可直接下载文件
    }



}
