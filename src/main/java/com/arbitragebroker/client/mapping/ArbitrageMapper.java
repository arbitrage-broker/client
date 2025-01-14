package com.arbitragebroker.client.mapping;

import com.arbitragebroker.client.entity.ArbitrageEntity;
import com.arbitragebroker.client.model.ArbitrageModel;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ArbitrageMapper extends BaseMapper<ArbitrageModel, ArbitrageEntity> {

    @Override
    @Mappings({
            @Mapping(target = "user.parent", ignore = true),
            @Mapping(target = "user.roles", ignore = true)
    })
    ArbitrageModel toModel(final ArbitrageEntity entity);

}
