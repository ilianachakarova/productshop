package softuni.productshop.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import softuni.productshop.domain.entities.Order;
import softuni.productshop.domain.entities.Product;
import softuni.productshop.domain.entities.User;
import softuni.productshop.domain.models.service.ProductServiceModel;
import softuni.productshop.domain.models.service.UserServiceModel;
import softuni.productshop.repository.OrderRepository;
import softuni.productshop.repository.ProductRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService{
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ModelMapper modelMapper;
    @Autowired
    public OrderServiceImpl(ProductRepository productRepository, OrderRepository orderRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public void createOrder(ProductServiceModel productServiceModel, UserServiceModel userServiceModel) {
        Product product = this.productRepository.findById(productServiceModel.getId())
                .orElseThrow();
        User user = new User();
        user.setId(userServiceModel.getId());

        Order order = new Order();
        order.setProduct(product);
        order.setUser(user);

        this.orderRepository.save(order);
    }

    @Override
    public List<ProductServiceModel> findAllOrders() {
        List<Order> orders = this.orderRepository.findAll();
        return orders.stream().map(order -> this.modelMapper.map(order.getProduct(),ProductServiceModel.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductServiceModel> findAllOrdersByUser(String username) {
        List<Order> orders = this.orderRepository.findAll();
        return orders.stream().filter(order -> order.getUser().getUsername().equals(username)).
                map(order -> this.modelMapper.map(order.getProduct(),ProductServiceModel.class))
                .collect(Collectors.toList());
    }
}
