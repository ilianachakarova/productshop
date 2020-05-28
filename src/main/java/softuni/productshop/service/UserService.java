package softuni.productshop.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import softuni.productshop.domain.models.service.UserServiceModel;
import softuni.productshop.domain.models.view.UserViewModel;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserServiceModel registerUser(UserServiceModel userServiceModel);
    UserServiceModel findUserByUsername(String username);
    UserServiceModel editUserProfile(UserServiceModel userServiceModel, String oldPassword);
    List<UserViewModel>findAllUsers();
    UserServiceModel setNewRole(String id, String role);
}
