package com.arbitragebroker.client.mapping;

import com.arbitragebroker.client.entity.CountryEntity;
import com.arbitragebroker.client.model.CountryModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CountryMapper extends BaseMapper<CountryModel, CountryEntity> {
}
