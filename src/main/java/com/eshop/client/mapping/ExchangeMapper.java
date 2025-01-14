package com.eshop.client.mapping;

import com.eshop.client.entity.ExchangeEntity;
import com.eshop.client.model.ExchangeModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ExchangeMapper extends BaseMapper<ExchangeModel, ExchangeEntity> {
}
