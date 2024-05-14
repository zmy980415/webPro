package com.example.demo;


import com.example.demo.entity.TreadPoolFactory;
import com.example.demo.tasks.MoeimgTask;
import org.noear.solon.annotation.Controller;
import org.noear.solon.annotation.Mapping;
import org.noear.solon.web.cors.annotation.CrossOrigin;


import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@Controller
@Mapping("/pc")
public class PachongController {



    @Mapping("/start")
    public String start(){
        MoeimgTask crawler = MoeimgTask.getInstance();
        crawler.setBASE_PATH("E:\\moeimg6/");
        MoeimgTask.executor = TreadPoolFactory.executor;

        crawler.getConf().setExecuteInterval(5000);
        MoeimgTask.executor.execute(crawler);
        return "start.....";
    }
    /**
     * 获取爬虫线程池的使用情况
     */
    @Mapping("/getPcThreadPoolInfo")

    public Map<String,Object> getPcThreadPoolInfo(){
        Map<String,Object> infoMap = new HashMap<>();
        if(MoeimgTask.executor == null){
            infoMap.put("提示信息","PC 线程池未创建。。。");
            return infoMap;
        }
        int activeCount = MoeimgTask.executor.getActiveCount();
        long completedTaskCount = MoeimgTask.executor.getCompletedTaskCount();
        int corePoolSize = MoeimgTask.executor.getCorePoolSize();
        int poolSize = MoeimgTask.executor.getPoolSize();
        long taskCount = MoeimgTask.executor.getTaskCount();
        int queueSize = MoeimgTask.executor.getQueue().size();
        infoMap.put("提示信息","PC 线程池已创建");
        infoMap.put("线程池大小",poolSize);
        infoMap.put("核心线程数",corePoolSize);
        infoMap.put("当前活跃线程数",activeCount);
        infoMap.put("任务数量",taskCount);
        infoMap.put("已完成任务数量",completedTaskCount);
        infoMap.put("队列当前容量",queueSize);
        return infoMap;
    }

}
