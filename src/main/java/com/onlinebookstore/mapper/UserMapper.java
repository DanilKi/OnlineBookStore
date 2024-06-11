package com.onlinebookstore.mapper;

import com.onlinebookstore.config.MapperConfig;
import com.onlinebookstore.dto.user.UserRegistrationRequestDto;
import com.onlinebookstore.dto.user.UserResponseDto;
import com.onlinebookstore.model.Role;
import com.onlinebookstore.model.RoleName;
import com.onlinebookstore.model.User;
import java.util.Set;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponseDto(User user);

    User toUserEntity(UserRegistrationRequestDto requestDto);

    @AfterMapping
    default void setRoles(@MappingTarget User user) {
        user.setRoles(Set.of(new Role(RoleName.USER.ordinal() + 1L)));
    }
}
