package com.arbitragebroker.client.mapping;


import com.arbitragebroker.client.entity.NotificationEntity;
import com.arbitragebroker.client.model.NotificationModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {ParameterGroupMapper.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface NotificationMapper extends BaseMapper<NotificationModel, NotificationEntity> {

}
