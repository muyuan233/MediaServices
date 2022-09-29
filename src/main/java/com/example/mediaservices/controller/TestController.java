package com.example.mediaservices.controller;

import com.example.mediaservices.utils.FFmpegUtils;
import com.example.mediaservices.utils.M3u8ToMP4;
import com.example.mediaservices.utils.RtspToMP4;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Time;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TODO:
 *
 * @Author: ZHANG
 * @create: 2021/8/27 16:16
 */
@RestController
@RequestMapping("/test")
public class TestController {
    @Autowired
    private RtspToMP4 rtspToMP4;

    @Autowired
    private M3u8ToMP4 m3u8ToMP4;

    //根目录
    @Value("C:\\Users\\zcy\\Desktop\\rtx\\")
    String url;

    private Map<Integer,Process> map=new HashMap<>();

    /**
     * 将rtsp流保存为mp4
     * @param id 进程id
     * @param fileName 保存文件名
     * @param streamUrl 流
     * @return
     */
    @GetMapping(value = "/start/rtsp")
    public String startRtsp(Integer id,String fileName, String streamUrl) {
        String ffmpegPath="ffmpeg";
        String FilePath=url+fileName+"_%Y-%m-%d_%H-%M-%S.mp4";
        Process process = rtspToMP4.StartRecord(ffmpegPath, streamUrl, FilePath);
        if(null!=process){
            map.put(id,process);
            return "Result.success()";
        }
        return "Result.failed()";
    }

    @GetMapping(value = "/start/m3u8")
    public String startM3u8(Integer id,String fileName, String streamUrl) {
        String ffmpegPath="ffmpeg";
        String FilePath=url+fileName+"\\%Y-%m-%d_%H-%M-%S.mp4";
        Process process = m3u8ToMP4.StartRecord(ffmpegPath, streamUrl, FilePath);
        if(null!=process){
            map.put(id,process);
            return "Result.success()";
        }
        return "Result.failed()";
    }


    /**
     * 停止进程
     * @param id 进程id
     * @return
     */
    @GetMapping(value = "/stop")
    public String stop(Integer id) {
        if(map.containsKey(id)){
            Process process = map.get(id);
            if(null!=process){
                //rtspToMP4.stopRecord(process);
                m3u8ToMP4.stopRecord(process);
                return "Result.success()";
            }
        }
        return "Result.failed()";
    }


    /**
     * 剪切视频
     * @param source 源文件
     * @param target 生成目标文件
     * @param start 开始时间
     * @param timeLength 截取长度 单位毫秒
     * @return
     */
    @GetMapping("/cut/video")
    public String cutVideo (String source, String target, Long start, int timeLength){
        source = url + source;
        target = url + target;
        //List<String> fileList = FFmpegUtils.getFileList(url + "摄像头01");
        FFmpegUtils.cutVideo(new File(source), new File(target), new Time(start-28800000), timeLength);
        return "success";
    }


    /**
     * 抽取视频图片
     * @param source 源文件
     * @param target 生成目标文件
     * @param start 时间 单位毫秒
     * @return
     */
    @GetMapping("/cut/videoFlame")
    public String cutVideoFlame (String source, String target, Long start){
        source = url + source;
        target = url + target;
        FFmpegUtils.cutVideoFrame(new File(source), new File(target), new Time(start-28800000));
        return "success";
    }


    /**
     * 前端通过url路径即可访问图片
     * chuhan 2022-04-19
     * @param imagePath  图片的名称
     * @param tempid   随机数
     * @param response
     * @throws IOException
     */
    @GetMapping("/image")
    public void getImage(String imagePath, String tempid, HttpServletResponse response) throws IOException {
        InputStream fis = null;
        OutputStream os = null;
        try {
            fis = new FileInputStream(url+File.separator+"images"
                    +File.separator+imagePath);//服务器本地图片地址
            os = response.getOutputStream();
            int count = 0;
            byte[] buffer = new byte[1024 * 8];
            while ((count = fis.read(buffer)) != -1) {
                os.write(buffer, 0, count);
                os.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
