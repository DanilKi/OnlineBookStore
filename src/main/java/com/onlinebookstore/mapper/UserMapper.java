package com.onlinebookstore.mapper;

import com.onlinebookstore.config.MapperConfig;
import com.onlinebookstore.dto.user.UserRegistrationRequestDto;
import com.onlinebookstore.dto.user.UserResponseDto;
import com.onlinebookstore.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toUserResponseDto(User user);

    User toUserEntity(UserRegistrationRequestDto requestDto);
}
