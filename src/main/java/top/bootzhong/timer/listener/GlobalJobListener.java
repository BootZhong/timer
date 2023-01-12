package top.bootzhong.timer.listener;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import top.bootzhong.timer.service.JobService;
import top.bootzhong.timer.util.BeanContext;

@Slf4j
public class GlobalJobListener implements JobListener {
    private JobService jobService;

    @Override
    public String getName() {
        return "GlobalJobListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext jobExecutionContext) {

    }

    @Override
    public void jobWasExecuted(JobExecutionContext jobExecutionContext, JobExecutionException e) {
        //执行完毕
        if (jobExecutionContext.getNextFireTime() == null){
            JobDetail jobDetail = jobExecutionContext.getJobDetail();
            Long id = (Long) jobDetail.getJobDataMap().get("id");
            if (jobService == null){
                jobService = BeanContext.getBean(JobService.class);
            }
            jobService.finish(id);
            log.info("jobWasExecuted:{}", JSONObject.toJSONString(jobDetail));
        }
    }
}
