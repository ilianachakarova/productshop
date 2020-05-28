package softuni.productshop.web.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import softuni.productshop.domain.models.binding.AddProductBindingModel;
import softuni.productshop.domain.models.service.CategoryServiceModel;
import softuni.productshop.domain.models.service.ProductServiceModel;
import softuni.productshop.domain.models.service.UserServiceModel;
import softuni.productshop.domain.models.view.EditProductViewModel;
import softuni.productshop.domain.models.view.ProductViewModel;
import softuni.productshop.service.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProductController extends BaseController {
    private final ProductService productService;
    private final ModelMapper modelMapper;
    private final CategoryService categoryService;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;
    private final OrderService orderService;

    public ProductController(ProductService productService, ModelMapper modelMapper, CategoryService categoryService, CloudinaryService cloudinaryService, UserService userService, OrderService orderService) {
        this.productService = productService;
        this.modelMapper = modelMapper;
        this.categoryService = categoryService;
        this.cloudinaryService = cloudinaryService;
        this.userService = userService;
        this.orderService = orderService;
    }
    @GetMapping("/products/add")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView addProduct(ModelAndView modelAndView){
        List<String>categories = this.categoryService.findAllCategories()
                .stream().map(CategoryServiceModel::getName).collect(Collectors.toList());
        modelAndView.addObject("categories", categories);
        return super.view("add-product", modelAndView);
    }

    @PostMapping("/products/add")
    public ModelAndView addProductConfirm(ModelAndView modelAndView,
                                          @ModelAttribute(name="model")AddProductBindingModel addProductBindingModel) throws IOException {
        ProductServiceModel productServiceModel = this.modelMapper.map(addProductBindingModel,ProductServiceModel.class);
        productServiceModel.setCategory(this.categoryService.findCategoryByName(addProductBindingModel.getCategory()));
        productServiceModel.setImageUrl(this.cloudinaryService.uploadImage(addProductBindingModel.getImage()));

        this.productService.createProduct(productServiceModel);

        return super.redirect("/products/all");
    }

    @GetMapping("/products/all")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView showAllProducts(ModelAndView modelAndView){
        List<ProductViewModel>products = this.productService.findAllProducts()
                .stream()
                .map(p->{
                    ProductViewModel productViewModel = this.modelMapper.map(p,ProductViewModel.class);
                    productViewModel.setCategory(p.getCategory().getName());

                    return productViewModel;
                }).collect(Collectors.toList());
        modelAndView.addObject("products",products);
        return super.view("all-products",modelAndView);
    }

    @GetMapping("/products/details/{id}")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView showDetails(ModelAndView modelAndView, @PathVariable("id")String id){
        ProductViewModel productViewModel = this.modelMapper.map(this.productService.findProductById(id),ProductViewModel.class);
        modelAndView.addObject("model", productViewModel);
        return super.view("product-details",modelAndView);
    }

    @GetMapping("/products/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView edit(ModelAndView modelAndView, @PathVariable("id")String id){
        ProductServiceModel productServiceModel = this.productService.findProductById(id);
        EditProductViewModel editProductViewModel = this.modelMapper.map(productServiceModel,EditProductViewModel.class);
        editProductViewModel.setCategory(productServiceModel.getCategory().getName());
        modelAndView.addObject("product",editProductViewModel);
        return super.view("product-edit",modelAndView);
    }

    @PostMapping("/products/edit/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView editConfirm( @PathVariable("id")String id, @ModelAttribute EditProductViewModel editProductViewModel){
        ProductServiceModel product = this.productService.findProductById(id);
        this.productService.editProduct(product, editProductViewModel);

        return super.redirect("/products/all");
    }

    @GetMapping("/products/delete/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView delete(@PathVariable("id")String id, ModelAndView modelAndView){
        ProductServiceModel productToDelete = this.productService.findProductById(id);
        EditProductViewModel product = this.modelMapper.map(productToDelete, EditProductViewModel.class);
        product.setCategory(productToDelete.getCategory().getName());
        modelAndView.addObject("product",product);


        return super.view("product-delete",modelAndView);
    }

    @PostMapping("/products/delete/{id}")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView deleteConfirm(@PathVariable("id")String id){
        this.productService.deleteProduct(id);

        return super.redirect("/products/all");
    }

    @PostMapping("products/order/{id}")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView orderConfirm(@PathVariable("id") String id, Principal principal){
        ProductServiceModel productServiceModel = this.productService.findProductById(id);
        UserServiceModel userServiceModel = userService.findUserByUsername(principal.getName());

        this.orderService.createOrder(productServiceModel,userServiceModel);
        return super.redirect("/home");
    }

    @GetMapping("/fetch/all")
    @ResponseBody
    public List<ProductViewModel> fetchAllProducts(){
        return this.productService.findAllProducts()
                .stream()
                .map(p->{
                    ProductViewModel productViewModel = this.modelMapper.map(p,ProductViewModel.class);
                    productViewModel.setCategory(p.getCategory().getName());

                    return productViewModel;
                }).collect(Collectors.toList());
    }




}