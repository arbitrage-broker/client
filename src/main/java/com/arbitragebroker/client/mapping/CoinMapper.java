package com.arbitragebroker.client.mapping;

import com.arbitragebroker.client.entity.CoinEntity;
import com.arbitragebroker.client.model.CoinModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface CoinMapper extends BaseMapper<CoinModel, CoinEntity> {
}
