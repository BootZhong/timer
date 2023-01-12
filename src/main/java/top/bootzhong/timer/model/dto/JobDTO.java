package top.bootzhong.timer.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.bootzhong.timer.model.entity.Job;

/**
 * job dto
 * @author bootzhong
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class JobDTO extends Job {
    private HttpReqDTO httpReqDTO;
}
