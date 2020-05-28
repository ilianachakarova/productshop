package softuni.productshop.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import softuni.productshop.domain.entities.Role;
import softuni.productshop.domain.entities.User;
import softuni.productshop.domain.models.service.UserServiceModel;
import softuni.productshop.domain.models.view.UserViewModel;
import softuni.productshop.repository.UserRepository;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder encoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleService roleService, ModelMapper modelMapper, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.modelMapper = modelMapper;
        this.encoder = encoder;
    }

    @Override
    @Transactional
    public UserServiceModel registerUser(UserServiceModel userServiceModel) {
       this.roleService.seedRolesInDB();

       if(this.userRepository.count()==0){
           userServiceModel.setAuthorities(this.roleService.findAllRoles());
       }else {
           userServiceModel.setAuthorities(new LinkedHashSet<>());
           userServiceModel.getAuthorities().add(roleService.findByAuthority("ROLE_USER"));
       }
        User user = this.modelMapper.map(userServiceModel, User.class);
       user.setPassword(encoder.encode(userServiceModel.getPassword()));
        return this.modelMapper.map(this.userRepository.saveAndFlush(user),UserServiceModel.class);
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return this.userRepository.findByUsername(s).orElseThrow(()->new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserServiceModel findUserByUsername(String username) {
        return this.userRepository.findByUsername(username).
                map(u->this.modelMapper.map(u,UserServiceModel.class)).orElseThrow(()->new UsernameNotFoundException("User not found"));
    }

    @Override
    public UserServiceModel editUserProfile(UserServiceModel userServiceModel, String oldPassword) {
        User user = this.userRepository.
                findByUsername(userServiceModel.getUsername()).
                orElseThrow(()->new UsernameNotFoundException("User not found"));
        if(!encoder.matches(oldPassword,user.getPassword())){
            throw new IllegalArgumentException("Incorrect password!");
        }

        user.setPassword(userServiceModel.getPassword() !=null ?
                this.encoder.encode(userServiceModel.getPassword()): user.getPassword());

        user.setEmail(userServiceModel.getEmail());

        return this.modelMapper.map(this.userRepository.saveAndFlush(user),UserServiceModel.class);
    }

    @Override
    public List<UserViewModel> findAllUsers() {
        List<UserServiceModel> users = userRepository.findAll().stream().map(u->this.modelMapper.map(u, UserServiceModel.class))
                .collect(Collectors.toList());
        return users.stream().map(userServiceModel -> {
            UserViewModel userViewModel = this.modelMapper.map(userServiceModel,UserViewModel.class);
            userViewModel.setAuthorities(userServiceModel.getAuthorities().stream().map(a->a.getAuthority()).collect(Collectors.toSet()));
            return userViewModel;
        }).collect(Collectors.toList());
    }

    @Override
    public UserServiceModel setNewRole(String id, String role) {
        User user = this.userRepository.findById(id).orElseThrow(()->new UsernameNotFoundException("No such user"));
        switch (role){
            case "ROLE_ADMIN":
                user.getAuthorities().add(this.modelMapper.map(this.roleService.findByAuthority("ROLE_ADMIN"), Role.class));
                user.getAuthorities().add(this.modelMapper.map(this.roleService.findByAuthority("ROLE_MODERATOR"), Role.class));
                break;
            case"ROLE_MODERATOR":
                if(user.getAuthorities().size()==1) {
                    user.getAuthorities().add(this.modelMapper.map(this.roleService.findByAuthority("ROLE_MODERATOR"), Role.class));
                }else {
                    user.getAuthorities().remove(this.modelMapper.map(this.roleService.findByAuthority("ROLE_ADMIN"), Role.class));
                }
                break;
            case"ROLE_USER":
                user.getAuthorities().remove(this.modelMapper.map(this.roleService.findByAuthority("ROLE_MODERATOR"), Role.class));
                user.getAuthorities().remove(this.modelMapper.map(this.roleService.findByAuthority("ROLE_ADMIN"), Role.class));
                break;
        }

        return this.modelMapper.map(this.userRepository.save(user),UserServiceModel.class);
    }
}
