package top.bootzhong.timer.model.entity;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * job
 * @author 
 */
@Data
public class Job implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 请求内容
     */
    private String req;

    /**
     * 描述
     */
    private String descrpition;

    /**
     * 执行cron
     */
    private String cron;

    /**
     * 执行状态: 0执行中 1已结束
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createdate;

    /**
     * 更新时间
     */
    private Date updateDate;

    private static final long serialVersionUID = 1L;
}