package com.eshop.client.mapping;

import com.eshop.client.entity.CountryEntity;
import com.eshop.client.model.CountryModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CountryMapper extends BaseMapper<CountryModel, CountryEntity> {
}
