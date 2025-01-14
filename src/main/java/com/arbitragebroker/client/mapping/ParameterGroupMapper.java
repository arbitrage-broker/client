package com.arbitragebroker.client.mapping;

import com.arbitragebroker.client.entity.ParameterGroupEntity;
import com.arbitragebroker.client.model.ParameterGroupModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ParameterGroupMapper extends BaseMapper<ParameterGroupModel, ParameterGroupEntity> {
}
