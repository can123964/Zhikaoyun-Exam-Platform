package org.example.zhikaoyunexamplatform.common.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.zhikaoyunexamplatform.common.entity.SysLog;

import java.util.List;

/**
 * 系统操作日志 Mapper
 */
@Mapper
public interface SysLogMapper {

    @Insert("""
            INSERT INTO t_sys_log (user_id, username, operation, method, url, params,
                                   result, error_msg, ip, user_agent, response_time, create_time)
            VALUES (#{userId}, #{username}, #{operation}, #{method}, #{url}, #{params},
                    #{result}, #{errorMsg}, #{ip}, #{userAgent}, #{responseTime}, #{createTime})
            """)
    void insert(SysLog sysLog);

    @Select("SELECT COUNT(*) FROM t_sys_log WHERE " +
            "(#{keyword} IS NULL OR operation LIKE CONCAT('%', #{keyword}, '%') OR username LIKE CONCAT('%', #{keyword}, '%'))")
    int countByKeyword(@Param("keyword") String keyword);

    @Select("SELECT * FROM t_sys_log WHERE " +
            "(#{keyword} IS NULL OR operation LIKE CONCAT('%', #{keyword}, '%') OR username LIKE CONCAT('%', #{keyword}, '%')) " +
            "ORDER BY create_time DESC LIMIT #{size} OFFSET #{offset}")
    List<SysLog> selectPage(@Param("keyword") String keyword, @Param("offset") int offset, @Param("size") int size);
}
