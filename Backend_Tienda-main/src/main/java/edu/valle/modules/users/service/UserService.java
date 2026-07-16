package edu.valle.modules.users.service;

import edu.valle.modules.users.dto.request.UserRequest;
import edu.valle.modules.users.dto.response.UserResponse;
import java.util.List;

public interface UserService {

    UserResponse create(UserRequest request);

    List<UserResponse> findAll();

    UserResponse findById(Long id);

    UserResponse update(Long id, UserRequest request);

    void deactivate(Long id);
}