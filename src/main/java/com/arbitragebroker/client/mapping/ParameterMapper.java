package com.arbitragebroker.client.mapping;


import com.arbitragebroker.client.entity.ParameterEntity;
import com.arbitragebroker.client.model.ParameterModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {ParameterGroupMapper.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ParameterMapper extends BaseMapper<ParameterModel, ParameterEntity> {

}
