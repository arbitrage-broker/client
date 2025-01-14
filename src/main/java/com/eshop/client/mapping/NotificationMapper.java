package com.eshop.client.mapping;


import com.eshop.client.entity.NotificationEntity;
import com.eshop.client.model.NotificationModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {ParameterGroupMapper.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface NotificationMapper extends BaseMapper<NotificationModel, NotificationEntity> {

}
