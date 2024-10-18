import static org.junit.jupiter.api.Assertions.*;

import com.project.Order;
import com.project.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderRepositoryTest {

    private OrderRepository orderRepository;

    @BeforeEach
    public void setup() {
        orderRepository = new OrderRepository();
    }

    @Test
    void testSaveOrderAndGetOrderById() {
        Order order = new Order(1L, "최승환", "2024-10-18", "처리 중");
        orderRepository.saveOrder(order);

        Order retrievedOrder = orderRepository.getOrderById(1L);
        assertNotNull(retrievedOrder);
        assertEquals("최승환", retrievedOrder.getCustomerName());
    }

    @Test
    void testGetAllOrders() {
        orderRepository.saveOrder(new Order(1L, "최승환", "2024-10-18", "처리 중"));
        orderRepository.saveOrder(new Order(2L, "최승환2", "2024-10-18", "배송 중"));

        assertEquals(2, orderRepository.getAllOrders().size());
    }
}
