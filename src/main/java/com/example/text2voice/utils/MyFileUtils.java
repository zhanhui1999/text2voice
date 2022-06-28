package com.example.text2voice.utils;

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.multipart.MultipartFile;
import ws.schild.jave.Encoder;
import ws.schild.jave.EncoderException;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;



import java.io.File;

public class MyFileUtils {

    public static String upLoad(MultipartFile files, String path,Integer tag)  {
        JSONObject object=new JSONObject();
        String fileName = files.getOriginalFilename();  // 文件名
        System.out.println(path);
        File dest = new File(path +  '/' + fileName);
        if (!dest.getParentFile().exists()) {
            dest.getParentFile().mkdirs();
        }
        try {
            files.transferTo(dest);
        } catch (Exception e) {
            System.out.println("出错了");
            object.put("success",2);
            object.put("result","程序错误，请重新上传");
            return object.toString();
        }

        if(tag==2){
            String[] datas = fileName.split("\\.");
            if(!datas[datas.length -1].equals("wav")){
                object.put("tips","检测到非.wav文件,自动转换为.wav文件");
                StringBuilder targetPathBuffer = new StringBuilder();
                for(int j=0 ;j < datas.length - 1;j++){
                    targetPathBuffer.append(datas[j]);
                }
                targetPathBuffer.append(".wav");
                String targetPath = path + '/' + targetPathBuffer;
                File source = new File(path + '/' + fileName);
                trans(source,new File(targetPath));
                source.delete();
            }
        }
        object.put("success",1);
        object.put("result","文件上传成功");
        return object.toString();
    }

    public static void trans(File source, File target) throws IllegalArgumentException  {

        AudioAttributes audio = new AudioAttributes();
        audio.setCodec("pcm_s16le");
        audio.setBitRate(256000);
        audio.setChannels(1);
        audio.setSamplingRate(16000);

        EncodingAttributes attrs = new EncodingAttributes();
        attrs.setOutputFormat("wav");
        attrs.setAudioAttributes(audio);
        Encoder encoder = new Encoder();
        try {
            encoder.encode(new MultimediaObject(source), target, attrs);
        } catch (EncoderException e) {
            e.printStackTrace();
        }

    }
}
