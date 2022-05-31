package com.example.text2voice.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import com.example.text2voice.utils.MyFileUtils;

@Controller
@RequestMapping("/file")
public class FileController {
    @Value("${file.upload.url}")
    private String uploadFilePath;
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
    public String[] listTextFile(){
        String path = uploadFilePath + '/' + textDirName;
        File file = new File(path);
        return file.list();
    }

    @PostMapping("/list/audiofile")
    @ResponseBody
    public String[] listAudioFile(){
        String path = uploadFilePath + '/' + audioDirName;
        File file = new File(path);
        return file.list();
    }

}
