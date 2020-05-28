package softuni.productshop.service;

import softuni.productshop.domain.models.service.ProductServiceModel;
import softuni.productshop.domain.models.service.UserServiceModel;

import java.util.List;

public interface OrderService {
     void createOrder(ProductServiceModel productServiceModel, UserServiceModel userServiceModel);
     List<ProductServiceModel>findAllOrders();
     List<ProductServiceModel>findAllOrdersByUser(String username);
}
