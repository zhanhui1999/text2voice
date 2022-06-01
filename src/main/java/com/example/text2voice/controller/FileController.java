package com.example.text2voice.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.text2voice.utils.MyFileUtils;
import com.sun.prism.impl.TextureResourcePool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/file")
public class FileController {
    @Value("${file.upload.url}")
    private String uploadFilePath;
    @Value("${text2voice.algotithm.url}")
    private String algoFilePath;
    @Value("${python.interpreter.url}")
    private String pyInterpreterPath;

    private final String textDirName= "text";
    private final String audioDirName= "audio";
    @PostMapping("/upload/textfile")
    @ResponseBody
    public String uploadTextFile(@RequestParam("files") MultipartFile files){
        return MyFileUtils.upLoad(files,uploadFilePath +'/'+ textDirName);
    }
    @PostMapping("/upload/audiofile")
    @ResponseBody
    public String uploadAudioFile(@RequestParam("files") MultipartFile files){
        return MyFileUtils.upLoad(files,uploadFilePath +'/'+ audioDirName);
    }
    @PostMapping("/list/textfile")
    @ResponseBody
    public String listTextFile(){
        String path = uploadFilePath + '/' + textDirName;
        File file = new File(path);
        JSONObject ob = new JSONObject();
        ob.put("filesNameList",file.list());
        return ob.toString();
    }

    @PostMapping("/list/audiofile")
    @ResponseBody
    public String listAudioFile(){
        String path = uploadFilePath + '/' + audioDirName;
        File file = new File(path);
        JSONObject ob = new JSONObject();
        ob.put("filesNameList",file.list());
        return ob.toString();
    }

    @PostMapping("/text2voice")
    @ResponseBody
    public String getVoice(@RequestParam("textName") String textName, @RequestParam("audioName") String audioName){
        JSONObject object=new JSONObject();
        String textPath = uploadFilePath  + "/" + textDirName + "/" + textName;
        String audioPath = uploadFilePath + "/" + audioDirName +"/" + audioName;
        String resultPath = uploadFilePath + "/" + "result" + "/" + textName + "_1_" + audioName + ".wav";
        List<String> command = new ArrayList<>();
        command.add(pyInterpreterPath + "/python");   //指定python解释器
        command.add("gen_voice.py");   //指定生成声音的脚本文件
        command.add(textPath);                                  //指定文本文件的路径
        command.add(audioPath);
        String cmd = String.join(" ",command);   //生成一条完整的命令行
        Runtime runtime = Runtime.getRuntime();
        System.out.println(cmd);
        try {
            Process process = runtime.exec(cmd,null,new File(algoFilePath));
            int status = process.waitFor();
            if(status != 0) throw new RuntimeException();

        } catch (Exception e) {
            e.printStackTrace();
            object.put("tips","command line err ,please check you command!");
            object.put("status","fail!");
            return object.toString();
        }
        object.put("resultFileName",resultPath);
        object.put("status","success!");
        return object.toString();
    }

    @RequestMapping("/download")
    @ResponseBody
    public String fileDownLoad(HttpServletResponse response, @RequestParam("fileName") String fileName){
        JSONObject object=new JSONObject();
        File file = new File(uploadFilePath +'/' + "result" + '/' + fileName);
        if(!file.exists()){
            object.put("tips","The file is not exists");
            object.put("status","failed!");
            return object.toString();
        }
        response.reset();
        response.setContentType("application/octet-stream");
        response.setCharacterEncoding("utf-8");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName );

        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));) {
            byte[] buff = new byte[1024];
            OutputStream os  = response.getOutputStream();
            int i = 0;
            while ((i = bis.read(buff)) != -1) {
                os.write(buff, 0, i);
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            object.put("tips","The file is not exists");
            object.put("status","failed!");
            return object.toString();
        }
        object.put("status","success!");
        return object.toString();
    }
}
