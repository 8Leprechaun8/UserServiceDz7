package org.example.security.mapper;

import org.example.security.dto.UserDtoForSaving;
import org.example.security.dto.UserDtoForUpdating;
import org.example.security.dto.UserDtoResponse;
import org.example.security.entity.User;
import org.example.security.entity.UserDetailsAdditional;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserMapper {

    UserDetails userDtoForSavingToUserDetails(UserDtoForSaving userDto);

    UserDetailsAdditional userDtoForSavingToUserDetailsAdditional(UserDtoForSaving userDto);

    UserDetailsAdditional userDtoForUpdatingToUserDetailsAdditional(UserDtoForUpdating userDto);

    User userDetailsToUser(UserDetails userDetails);

    User userDetailsAdditionalToUser(UserDetailsAdditional userDetailsAdditional);

    UserDtoResponse userToUserDtoResponse(User user);

    List<UserDtoResponse> userListToUserDtoResponseList(List<User> userList);
}
