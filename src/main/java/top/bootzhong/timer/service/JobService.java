package top.bootzhong.timer.service;

import top.bootzhong.timer.model.dto.JobDTO;
import top.bootzhong.timer.model.entity.Job;

public interface JobService {
    /**
     * 新增任务
     * @param job
     */
    void add(JobDTO job);

    /**
     * 更新状态
     * @param status
     * @param id
     */
    void updateStatusById(Integer status, Long id);

    /**
     * 启动未完成任务
     */
    void enableUnFinishJob();

    /**
     * 任务完全结束
     * @param id
     */
    void finish(Long id);
}
