package com.eshop.client.mapping;

import com.eshop.client.entity.RoleDetailEntity;
import com.eshop.client.model.RoleDetailModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RoleDetailMapper extends BaseMapper<RoleDetailModel, RoleDetailEntity> {
}
