package com.house.rent.mapper;

import com.house.rent.model.MailSendLog;
import org.apache.ibatis.annotations.Param;
import org.springframework.cache.annotation.CachePut;

import java.util.Date;
import java.util.List;

@CachePut
public interface MailSendLogMapper {
    Integer updateMailSendLogStatus(@Param("msgId") String msgId, @Param("status") Integer status);

    Integer insert(MailSendLog mailSendLog);

    List<MailSendLog> getMailSendLogsByStatus();

    Integer updateCount(@Param("msgId") String msgId, @Param("date") Date date);
}
