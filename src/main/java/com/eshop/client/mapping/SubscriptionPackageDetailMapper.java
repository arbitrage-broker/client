package com.eshop.client.mapping;


import com.eshop.client.entity.SubscriptionPackageDetailEntity;
import com.eshop.client.entity.UserEntity;
import com.eshop.client.model.SubscriptionPackageDetailModel;
import com.eshop.client.model.UserModel;
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
