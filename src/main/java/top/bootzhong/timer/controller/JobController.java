package top.bootzhong.timer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.bootzhong.timer.model.dto.JobDTO;
import top.bootzhong.timer.model.entity.Job;
import top.bootzhong.timer.service.JobService;

/**
 * 定时任务控制层
 * @author bootzhong
 */
@RequestMapping("/job")
@RestController
public class JobController {
    @Autowired
    private JobService jobService;

    @PostMapping("/add")
    public void add(@RequestBody JobDTO job){
        Assert.notNull(job.getCron(), "cron can't be null");
        Assert.notNull(job.getName(), "name can't be null");
        jobService.add(job);
    }
}
