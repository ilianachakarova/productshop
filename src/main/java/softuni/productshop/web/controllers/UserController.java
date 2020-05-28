package softuni.productshop.web.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;
import softuni.productshop.domain.models.binding.EditUserBindingModel;
import softuni.productshop.domain.models.binding.UserRegisterBindingModel;
import softuni.productshop.domain.models.service.UserServiceModel;
import softuni.productshop.domain.models.view.UserProfileViewModel;
import softuni.productshop.service.UserService;

import javax.validation.Valid;
import java.security.Principal;

@Controller
public class UserController extends BaseController {

    private final UserService userService;
    private final ModelMapper modelMapper;
    @Autowired
    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/users/register")
    @PreAuthorize("isAnonymous()")
    public ModelAndView register(){
        return super.view("register");
    }

    @PostMapping("/users/register")
    public ModelAndView registerConfirm(ModelAndView modelAndView,@Valid @ModelAttribute UserRegisterBindingModel userRegisterBindingModel,
                                        BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return super.view("register");
        }
        if(!userRegisterBindingModel.getPassword().equals(userRegisterBindingModel.getConfirmPassword())){
            return super.view("register");
        }

        this.userService.registerUser(this.modelMapper.map(userRegisterBindingModel, UserServiceModel.class));
        return super.redirect("/users/login");
    }

    @GetMapping("/users/login")
    @PreAuthorize("isAnonymous()")
    public ModelAndView login(){
       return super.view("login");
    }

    @GetMapping("/users/profile")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView profile(Principal principal, ModelAndView modelAndView){
        UserServiceModel user = this.userService.findUserByUsername(principal.getName());
        modelAndView.addObject("model",this.modelMapper.map(user, UserProfileViewModel.class));

        return super.view("profile",modelAndView);
    }

    @GetMapping("/users/edit")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView editProfile(Principal principal, ModelAndView modelAndView){
        UserServiceModel user = this.userService.findUserByUsername(principal.getName());
        modelAndView.addObject("model",this.modelMapper.map(user, UserProfileViewModel.class));

        return super.view("edit-profile",modelAndView);
    }

    @PostMapping ("users/edit")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView editProfileConfirm(@ModelAttribute EditUserBindingModel editUserBindingModel){
        if(!editUserBindingModel.getPassword().equals(editUserBindingModel.getConfirmPassword())){
            return super.view("edit-profile");
        }

        this.userService.editUserProfile(this.modelMapper.
                map(editUserBindingModel,UserServiceModel.class),editUserBindingModel.getOldPassword());
        return super.redirect("/users/profile");
    }

    @GetMapping("/users/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView allUsers(ModelAndView modelAndView){
        modelAndView.addObject("users", this.userService.findAllUsers());
        return super.view("all-users", modelAndView);
    }

    @PostMapping("/users/set-moderator/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setModerator(@PathVariable("id") String id, ModelAndView modelAndView){
        this.userService.setNewRole(id,"ROLE_MODERATOR");
        return super.redirect("/users/all");
    }

    @PostMapping("/users/set-user/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setUser(@PathVariable("id") String id, ModelAndView modelAndView){
        this.userService.setNewRole(id,"ROLE_USER");
        return super.redirect("/users/all");
    }

    @PostMapping("/users/set-admin/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView setAdmin(@PathVariable("id") String id, ModelAndView modelAndView){
        this.userService.setNewRole(id,"ROLE_ADMIN");
        return super.redirect("/users/all");
    }
}
