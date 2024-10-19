import static org.mockito.Mockito.*;

import com.project.Order;
import com.project.OrderProcessInterface;
import com.project.OrderRepository;
import com.project.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private OrderProcessInterface<Order> orderProcessInterface;
    private OrderService orderService;

    @BeforeEach
    void setup() {
        orderRepository = new OrderRepository();
        orderProcessInterface = mock(OrderProcessInterface.class); // 외부 시스템 연동을 모킹
        orderService = new OrderService(orderRepository, orderProcessInterface);
    }

    @Test
    void testFetchOrdersFromExternal() throws Exception {
        Order order = new Order(1L, "최승환", "2024-10-18", "처리 중");
        doNothing().when(orderProcessInterface).fetchOrdersAndSave(anyString()); // 외부 시스템에서 데이터 가져오는 작업을 모킹

        orderService.fetchOrdersFromExternalAndSaveOrders();

        verify(orderProcessInterface, times(1)).fetchOrdersAndSave(anyString()); // 외부 시스템과의 연동이 한 번 실행됐는지 확인
    }

    @Test
    void testSendOrdersToExternal() throws Exception {
        List<Order> orders = Arrays.asList(
                new Order(1L, "최승환", "2024-10-18", "처리 중"),
                new Order(2L, "최승환2", "2024-10-18", "배송 중")
        );
        orders.forEach(orderRepository::saveOrder);

        doNothing().when(orderProcessInterface).sendOrders(anyList(), anyString()); // 외부 시스템에 전송 작업을 모킹

        orderService.sendOrdersToExternal();

        verify(orderProcessInterface, times(1)).sendOrders(anyList(), anyString()); // 외부 시스템으로 데이터가 전송되었는지 확인
    }
}
