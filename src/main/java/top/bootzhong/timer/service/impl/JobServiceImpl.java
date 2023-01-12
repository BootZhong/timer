package top.bootzhong.timer.service.impl;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.EverythingMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import top.bootzhong.timer.listener.GlobalJobListener;
import top.bootzhong.timer.mapper.JobMapper;
import top.bootzhong.timer.model.dto.HttpReqDTO;
import top.bootzhong.timer.model.dto.JobDTO;
import top.bootzhong.timer.model.entity.Job;
import top.bootzhong.timer.service.JobService;
import top.bootzhong.timer.util.IdUtil;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class JobServiceImpl implements JobService {
    @Autowired
    private JobMapper jobMapper;

    private static Scheduler scheduler;

    static {
        // 1、创建调度器Scheduler
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        try {
            Scheduler sc = schedulerFactory.getScheduler();
            sc.getListenerManager().addJobListener(new GlobalJobListener(), EverythingMatcher.allJobs());
            sc.start();
            scheduler = sc;
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void add(JobDTO job) {
        long id = IdUtil.newId();
        String req = JSONObject.toJSONString(job.getHttpReqDTO());

        enableJob(id, job.getCron(), req);
        job.setId(id);
        job.setCreatedate(new Date());
        job.setStatus(0);
        job.setReq(req);
        jobMapper.insert(job);
    }

    private void enableJob(Long id, String cron, String req){
        log.info("enableJob id:{}, cron:{}, req:{}", id, cron, req);

        // 2、创建JobDetail实例，并与PrintWordsJob类绑定(Job执行内容)
        JobDetail jobDetail = JobBuilder.newJob(HttpJob.class)
                .usingJobData("id", id)
                .usingJobData("req", req)
                .withIdentity("job:" + id, "group1").build();
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity("trigger:" + id, "triggerGroup1")
                .startNow()//立即生效
                .withSchedule(CronScheduleBuilder.cronSchedule(cron))
                .build();

        //4、执行
        try {
            scheduler.scheduleJob(jobDetail, cronTrigger);
        } catch (SchedulerException e) {
            log.error("enableJob fail:{}", id, e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 主键更新
     * @param status
     * @param id
     */
    @Override
    public void updateStatusById(Integer status, Long id) {
        jobMapper.updateStatusById(status, id);
    }

    /**
     * 启动未完成任务
     */
    @Override
    public void enableUnFinishJob() {
        try {
            scheduler.clear();
        } catch (SchedulerException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }

        List<Job> job = jobMapper.selectExcludeStatus(1);
        if (CollectionUtils.isEmpty(job)){
            return;
        }

        job.forEach(e -> enableJob(e.getId(), e.getCron(), e.getReq()));
    }

    /**
     * 任务完成
     * @param id
     */
    @Override
    public void finish(Long id) {
        updateStatusById(1, id);
    }

    /**
     * http请求任务
     * @author bootzhong
     */
    public static class HttpJob implements org.quartz.Job {
        @Override
        public void execute(JobExecutionContext jobExecutionContext)  {
            JobDetail jobDetail = jobExecutionContext.getJobDetail();
            String req = (String) jobDetail.getJobDataMap().get("req");
            log.info("HttpJob execute:{}", JSONObject.toJSONString(jobDetail));
            HttpReqDTO reqDTO = JSONObject.parseObject(req, HttpReqDTO.class);
            request(reqDTO);
        }
    }


    static RestTemplate restTemplate  = new RestTemplate();

    /**
     * 发起请求
     * @param req
     */
    private static boolean request(HttpReqDTO req){
        return HttpMethod.POST.name().equals(req.getMethod()) ? doPost(req.getBody(), req.getUrl()) : doGet(req.getUrl());
    }

    /**
     * POST请求
     * @param param
     * @return
     */
    private static boolean doPost(Object param, String url) {
        log.info("发起请求,url:{},para:{}", url, JSONObject.toJSONString(param));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Object> response
                = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(param, headers), Object.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("请求成功,url:{},para:{},response:{}", url, JSONObject.toJSONString(param), JSONObject.toJSONString(response.getBody()));
            return true;
        } else {
            log.info("请求失败,url:{},para:{},response:{}", url, JSONObject.toJSONString(param), JSONObject.toJSONString(response.getBody()));
            return false;
        }
    }

    /*
     * 发送 get请求 并且返回resultdto
     * @param param
     * @param url
     * @return
     */
    private static boolean doGet(String url){
        ResponseEntity<Object> response;

        log.info("请求开始,url:{}", JSONObject.toJSONString(url));
        response = restTemplate.getForEntity(url, Object.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            log.info("请求成功,url:{},response:{}", url, JSONObject.toJSONString(response.getBody()));
            return true;
        } else {
            log.info("请求失败,url:{},response:{}", url,  JSONObject.toJSONString(response.getBody()));
            return false;
        }
    }
}
