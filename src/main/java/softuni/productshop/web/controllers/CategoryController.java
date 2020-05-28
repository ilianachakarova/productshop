package softuni.productshop.web.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import softuni.productshop.domain.models.binding.AddCategoryBindingModel;
import softuni.productshop.domain.models.service.CategoryServiceModel;
import softuni.productshop.service.CategoryService;

import java.util.List;

@Controller
public class CategoryController extends BaseController {

    private final CategoryService categoryService;
    private final ModelMapper modelMapper;
    @Autowired
    public CategoryController(CategoryService categoryService, ModelMapper modelMapper) {
        this.categoryService = categoryService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/categories/add")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView addCategory(){
        return super.view("add-category");
    }

    @PostMapping("/categories/add")
    public ModelAndView addCategoryConfirm(ModelAndView modelAndView,
                                           @ModelAttribute(name = "model")AddCategoryBindingModel
                                                   addCategoryBindingModel){


        this.categoryService.createCategory(this.modelMapper.map(addCategoryBindingModel, CategoryServiceModel.class));
        return super.redirect("/categories/all");
    }

    @GetMapping("/categories/all")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView showCategories(ModelAndView modelAndView){
        List<CategoryServiceModel> categories = this.categoryService.findAllCategories();

        modelAndView.addObject("categories", categories);
        return super.view("all-categories",modelAndView);
    }

    @PostMapping("/categories/delete/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView deleteCategory(@PathVariable("id") String id, ModelAndView modelAndView){
        this.categoryService.deleteCategory(id);
        return super.redirect("/categories/all");
    }

    @GetMapping("/categories/fetch")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    @ResponseBody
    public List<CategoryServiceModel> fetchAllCategories(){
        return this.categoryService.findAllCategories();
    }
}


