package com.eshop.client.mapping;

import com.eshop.client.entity.RoleEntity;
import com.eshop.client.model.RoleModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface RoleMapper extends BaseMapper<RoleModel, RoleEntity> {
}
