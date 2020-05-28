package softuni.productshop.web.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import softuni.productshop.domain.models.view.ProductViewModel;
import softuni.productshop.service.OrderService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class OrderController extends BaseController{

    private final OrderService orderService;
    private final ModelMapper modelMapper;

    @Autowired
    public OrderController(OrderService orderService, ModelMapper modelMapper) {
        this.orderService = orderService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/orders/all")
    @PreAuthorize("hasRole('ROLE_MODERATOR')")
    public ModelAndView showAllOrders(ModelAndView modelAndView, Principal principal){
        List<ProductViewModel>products = this.orderService.findAllOrders().stream()
                .map(o->this.modelMapper.map(o,ProductViewModel.class)).collect(Collectors.toList());
        modelAndView.addObject("products",products);
        modelAndView.addObject("customer",principal.getName());
        return super.view("all-orders",modelAndView);
    }

    @GetMapping("/orders/my")
    @PreAuthorize("isAuthenticated()")
    public ModelAndView showMyOrders(ModelAndView modelAndView, Principal principal){
        List<ProductViewModel>products = this.orderService.findAllOrdersByUser(principal.getName())
                .stream().map(o->this.modelMapper.map(o,ProductViewModel.class)).collect(Collectors.toList());
        modelAndView.addObject("products",products);
        modelAndView.addObject("customer",principal.getName());
        return super.view("my-orders",modelAndView);
    }
}
