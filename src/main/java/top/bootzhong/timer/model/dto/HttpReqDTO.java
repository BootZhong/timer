package top.bootzhong.timer.model.dto;

import lombok.Data;
import org.springframework.http.HttpMethod;

/**
 * http请求dto
 * @author bootzhong
 */
@Data
public class HttpReqDTO {
    private String url;
    private Object body;
    private String method;
}
