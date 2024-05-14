package com.example.demo.entity;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;
public class PCTaskEntity implements Runnable{
    Logger LOG = LoggerFactory.getLogger(PCTaskEntity.class);
    public String pictureCollectionName;
    public String category;
    public String tag;
    public String path;
    public int size;
    public List<String> imgList;

    public PCTaskEntity(){}

    public PCTaskEntity(String pictureCollectionName, String category, String tag, String path, int size, List<String> imgList) {
        this.pictureCollectionName = pictureCollectionName;
        this.category = category;
        this.tag = tag;
        this.path = path;
        this.size = size;
        this.imgList = imgList;
    }

    @Override
    public void run() {
        if(!(path.indexOf(path.length()) == '\\' || path.indexOf(path.length()) == '/')){
            path  += "\\";
        }

        String infoPath = path + "info.json";
        String failPath = path + "fail.json";
        String successPath = path + "success.json";
        File successFile = FileUtil.newFile(successPath);
        if(successFile.exists()){
            LOG.info("已经执行过的任务，跳过执行。。。。");
            return;
        }
        String infoJson = "{\n" +
                "\t\"name\":\""+pictureCollectionName+"\",\n"+
                "\t\"size\":\""+size+"\",\n"+
                "\t\"tag\":\""+tag+"\",\n"+
                "\t\"category\":\""+category+"\",\n"+
                "\t\"imgList\":"+ JSONUtil.toJsonStr(imgList) +"\n"+

                "}";
        try {
            File file = FileUtil.newFile(infoPath);
            File failFile = FileUtil.newFile(failPath);
            file.createNewFile();
            failFile.createNewFile();
            FileUtil.writeString(infoJson,file,"UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOG.info("start download "+pictureCollectionName+" ,size is "+size);
        for (int i = 0; i < imgList.size(); i++) {
            String url = imgList.get(i);
            String data = HttpUtil.get(url);
            String suffix = url.substring(url.lastIndexOf("."));
            File file = FileUtil.newFile(path + (i + 1) + suffix);
            if(!file.exists() && !".html".equals(suffix)){
                try{
                    HttpUtil.downloadFileFromUrl(url,file);
                }catch (Exception e)
                {
                    LOG.error(e.getLocalizedMessage());
                }
            }
            LOG.info("start download {}, 进度 {}/{}",pictureCollectionName,i+1,size);
        }

        try {
            successFile.createNewFile();
            FileUtil.writeString("success",successFile,"UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
