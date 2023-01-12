package top.bootzhong.timer.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import top.bootzhong.timer.service.JobService;

/**
 * 项目启动时监听器
 * @author bootzhong
 */
@Component
public class ApplicationStartListener implements CommandLineRunner {
    @Autowired
    private JobService jobService;

    @Override
    public void run(String... args) throws Exception {
        jobService.enableUnFinishJob();
    }
}
