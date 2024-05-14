package com.example.demo.tasks;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;
import cn.hutool.core.io.FileUtil;
import com.example.demo.entity.PCTaskEntity;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.select.Elements;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

@Setter
@Getter
public class MoeimgTask extends BreadthCrawler implements Runnable{
    private String BASE_PATH = "E:\\moeimg2/";
    public static ThreadPoolExecutor executor = null;
    /**
     * @param crawlPath crawlPath is the path of the directory which maintains
     *                  information of this crawler
     * @param autoParse if autoParse is true,BreadthCrawler will auto extract
     *                  links which match regex rules from pag
     */
    public MoeimgTask(String crawlPath, boolean autoParse) {
        super(crawlPath, autoParse);
        this.addSeedAndReturn("https://moeimg.net/page/1/").type("page");
        this.addSeed("https://moeimg.net/taglist","tagList");
        setThreads(100);
        getConf().setTopN(1000000);
    }

    public static MoeimgTask getInstance(){
        MoeimgTask moeimgTask = new MoeimgTask("crawl", false);
        return moeimgTask;
    }

    @Override
    public void visit(Page page, CrawlDatums next) {
        String url = page.url();
        if(page.code()==301 || page.code()==302){
            next.addAndReturn(page.location()).meta(page.meta());
            return;
        }
        if(page.matchType("tagList")){
            // 所有标签
            // 大类别
            Elements bigCategory = page.select("div.taglist > ul.navi-sub > li > a");
            for (int i = 0; i < bigCategory.size(); i++) {
                String bigCategoryText = bigCategory.get(i).text();
                // 翻译为中文

//                System.out.println(bigCategory.text());
            }

        }else if (page.matchType("page")) {
            Elements select = page.select("#main-2 > div > div > div.post-field-left.box.list > a");
            System.out.println(select.size());
            for (int i=0;i<select.size();i++){
                String href1 = select.get(i).attr("href");
                next.add(href1,"taoItem");
                System.out.println(href1);
            }

        }else if(page.matchType("taoItem")) {
            // 套图的所有图片

//            System.out.println(page.select("#main-2 > div > div > .box"));
            Elements imgs = page.select("#main-2 > div > div > .box > div > img");
            System.out.println(imgs.toString());
            List<String> imgUrlList = new ArrayList<String>();
            if(imgs.size() == 0){
                imgs = page.select("#main-2 > div > div > .box > a");
                for (int i = 0; i < imgs.size(); i++) {
                    imgUrlList.add(imgs.get(i).attr("href"));
                    System.out.println(imgs.get(i).attr("href"));
                }
            }else{
                for (int i = 0; i < imgs.size(); i++) {
                    imgUrlList.add(imgs.get(i).attr("src"));
                    System.out.println(imgs.get(i).attr("src"));
                }
            }


            // 类别
            Elements category = page.select("div.entry-footer > div.category > a");
            String categoryText = category.text();
            System.out.println(categoryText);
            // 标签
            Elements tag = page.select("div.entry-footer > div.tag > a");
            String tagText = tag.text();
            System.out.println(tagText);
            // 图片数量
            int size = imgs.size();
            System.out.println(size);

            // 创建文件夹
            String title = page.select("h1.title").text();
            System.out.println(title);
            title = title.replace("\\","")
                    .replace("/","")
                    .replace("\"","")
                    .replace("*","")
                    .replace(":","")
                    .replace("?","")
                    .replace(">","")
                    .replace("<","")
                    .replace("|","");
            File file = FileUtil.newFile(BASE_PATH + title);
            if(!file.exists()){
                file.mkdirs();
            }
            PCTaskEntity pcTaskEntity = new PCTaskEntity(title,null,null,file.getAbsolutePath()+"/",size,imgUrlList);
            try {
                executor.execute(pcTaskEntity);
            }catch (Exception e){
                e.printStackTrace();
                System.out.println(e.getStackTrace());
            }

        }

    }

    @Override
    public void run() {
        try {
            this.start(10000000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
