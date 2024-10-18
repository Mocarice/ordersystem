import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.project.ExternalSystemOrderApi;
import com.project.Order;
import com.project.OrderRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

class ExternalOrderApiTest {

    private WireMockServer wireMockServer;
    private OrderRepository orderRepository;
    private ExternalSystemOrderApi externalSystemOrderAPI;
    private final String url = "http://localhost:8080/orders";

    @BeforeEach
    void setup() {
        wireMockServer = new WireMockServer(8080);
        wireMockServer.start();
        WireMock.configureFor("localhost", 8080);

        orderRepository = new OrderRepository();
        externalSystemOrderAPI = new ExternalSystemOrderApi(orderRepository);
    }

    @AfterEach
    void tearDown() {
        wireMockServer.stop();
    }

    @Test
    void testFetchOrders() throws Exception {
        // 외부 API 가짜 응답 설정
        stubFor(get(urlEqualTo("/orders"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("[{\"orderId\": 1, \"customerName\": \"최승환\", \"orderDate\": \"2024-10-18\", \"orderStatus\": \"처리 중\"}]")));


        externalSystemOrderAPI.fetchOrders(url); // 가짜 서버에서 데이터 가져오기

        List<Order> orders = orderRepository.getAllOrders();
        assertEquals(1, orders.size());
        assertEquals("최승환", orders.get(0).getCustomerName());
    }

    @Test
    void testSendOrders() throws Exception {
        // 가짜 서버에서 POST 요청을 수신하는 로직 설정
        stubFor(post(urlEqualTo("/orders"))
                .willReturn(aResponse()
                        .withStatus(200)));

        orderRepository.saveOrder(new Order(1L, "최승환", "2024-10-18", "처리 중"));
        externalSystemOrderAPI.sendOrders(orderRepository.getAllOrders(), url);

        verify(postRequestedFor(urlEqualTo("/orders"))
                .withRequestBody(matchingJsonPath("$[0].orderId", equalTo("1")))
                .withRequestBody(matchingJsonPath("$[0].customerName", equalTo("최승환"))));
    }
}