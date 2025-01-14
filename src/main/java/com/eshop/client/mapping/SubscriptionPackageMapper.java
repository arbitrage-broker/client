package com.eshop.client.mapping;

import com.eshop.client.entity.SubscriptionPackageEntity;
import com.eshop.client.model.SubscriptionPackageModel;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,uses = {SubscriptionPackageDetailMapper.class})
public interface SubscriptionPackageMapper extends BaseMapper<SubscriptionPackageModel, SubscriptionPackageEntity> {

}
