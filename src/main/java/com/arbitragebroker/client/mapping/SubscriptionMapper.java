package com.arbitragebroker.client.mapping;


import com.arbitragebroker.client.entity.SubscriptionEntity;
import com.arbitragebroker.client.model.SubscriptionModel;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {SubscriptionPackageMapper.class,UserMapper.class,WalletMapper.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface SubscriptionMapper extends BaseMapper<SubscriptionModel, SubscriptionEntity> {
    @Override
    @Mappings({
            @Mapping(target = "user.parent", ignore = true),
            @Mapping(target = "user.roles", ignore = true)
    })
    SubscriptionModel toModel(final SubscriptionEntity entity);
}
