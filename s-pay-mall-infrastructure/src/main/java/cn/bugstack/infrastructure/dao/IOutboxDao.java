package cn.bugstack.infrastructure.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IOutboxDao {
    int insert(@Param("aggregateId") String aggregateId, @Param("eventType") String eventType, @Param("payload") String payload);
    List<Long> listPendingIds();
    int markPublished(@Param("id") Long id);
}
