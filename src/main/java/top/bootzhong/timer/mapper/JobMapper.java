package top.bootzhong.timer.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import top.bootzhong.timer.model.entity.Job;

import java.util.List;

@Repository
public interface JobMapper {
    int deleteByPrimaryKey(Long id);

    int insert(Job record);

    int insertSelective(Job record);

    Job selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(Job record);

    int updateByPrimaryKey(Job record);

    /**
     * 主键更新状态
     * @param status
     * @param id
     */
    void updateStatusById(@Param("status") Integer status,
                          @Param("id") Long id);

    /**
     * 排除掉指定状态的
     * @param status
     * @return
     */
    List<Job> selectExcludeStatus(@Param("status") Integer status);
}