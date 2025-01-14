package com.eshop.client.mapping;

import com.eshop.client.entity.WalletEntity;
import com.eshop.client.model.WalletModel;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface WalletMapper extends BaseMapper<WalletModel, WalletEntity> {

    @Override
    @Mappings({
            @Mapping(target = "user.parent", ignore = true),
            @Mapping(target = "user.roles", ignore = true)
    })
    WalletModel toModel(final WalletEntity entity);

}
