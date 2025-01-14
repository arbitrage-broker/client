package com.eshop.client.mapping;

import com.eshop.client.entity.UserEntity;
import com.eshop.client.model.UserModel;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring", uses = {RoleMapper.class},
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface UserMapper extends BaseMapper<UserModel, UserEntity> {

    @Override
    @Mappings({
            @Mapping(target = "parent.roles", ignore = true),
            @Mapping(target = "password", ignore = true)
    })
    UserModel toModel(final UserEntity entity);

    @Override
    @Mappings({
            @Mapping(target = "password", ignore = true),
            @Mapping(target = "emailVerified", ignore = true)
    })
    UserEntity toEntity(final UserModel model);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mappings({
            @Mapping(target = "password", ignore = true)
    })
    UserEntity updateEntity(UserModel model, @MappingTarget UserEntity entity);

}
