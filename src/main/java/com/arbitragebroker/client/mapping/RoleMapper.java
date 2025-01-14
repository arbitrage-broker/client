package com.arbitragebroker.client.mapping;

import com.arbitragebroker.client.entity.RoleEntity;
import com.arbitragebroker.client.model.RoleModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RoleMapper extends BaseMapper<RoleModel, RoleEntity> {
}
