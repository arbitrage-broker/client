package com.arbitragebroker.client.mapping;


import com.arbitragebroker.client.entity.SubscriptionPackageDetailEntity;
import com.arbitragebroker.client.entity.UserEntity;
import com.arbitragebroker.client.model.SubscriptionPackageDetailModel;
import com.arbitragebroker.client.model.UserModel;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring",
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface SubscriptionPackageDetailMapper extends BaseMapper<SubscriptionPackageDetailModel, SubscriptionPackageDetailEntity> {
    @Override
    @Mappings({
            @Mapping(target = "subscriptionPackage", ignore = true),
    })
    SubscriptionPackageDetailModel toModel(final SubscriptionPackageDetailEntity entity);
}
