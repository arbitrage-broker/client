package com.arbitragebroker.client.mapping;

import com.arbitragebroker.client.entity.SubscriptionPackageEntity;
import com.arbitragebroker.client.model.SubscriptionPackageModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,uses = {SubscriptionPackageDetailMapper.class})
public interface SubscriptionPackageMapper extends BaseMapper<SubscriptionPackageModel, SubscriptionPackageEntity> {

}
