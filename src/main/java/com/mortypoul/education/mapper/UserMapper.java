package com.mortypoul.education.mapper;

import com.mortypoul.education.dto.UserDto;
import com.mortypoul.education.dto.UserDtoPersister;
import com.mortypoul.education.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toDto(User user);

    User toEntity(UserDto userDto);
    User toEntity(UserDtoPersister userDto);

    List<UserDto> toDto(List<User> user);

    List<User> toEntity(List<UserDto> userDto);
}
