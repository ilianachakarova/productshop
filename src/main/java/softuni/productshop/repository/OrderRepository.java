package softuni.productshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.productshop.domain.entities.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order,String> {
}
