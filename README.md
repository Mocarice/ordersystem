# ordersystem
test project for Humuson

과제 설명
- 
- Java 21버전을 사용하여 구현하였습니다.
- 별도의 외부 라이브러리 사용에 대한 언급이 없어 테스트 관련 라이브러리를 제외하고 표준 라이브러리만을 사용하여 구현하였습니다.
- 인터페이스 설계시 주문정보와 관련한 객체의 변경 및 확장에 대처하기 위해 타입 제너릭을 사용하였습니다.
- 인터페이스 설계시 중복 로직 구현을 방지하고 복잡한 상속관계를 피하기 위해 별도의 abstract 클래스를 사용하지 않고, 인터페이스 내 default method를 사용하였습니다.
- 또한 default method내에 checked exception에 대한 핸들러를 작성하여 원한다면 구현체에서 unchecked exception 만을 핸들링 할 수 있도록 하였습니다. 
- 차후, 표준 라이브러리가 아닌 jackson과 같은 json 처리 관련 외부 라이브러리를 사용할 경우를 대비해 인터페이스 내 별도의 메소드들을 정의했습니다.
- HttpClient를 사용하여 외부 시스템과의 HTTP 통신을 처리하도록 하였습니다.
- 외부 시스템의 변경에 따른 정보(특히 url)를 유연하게 대처하기 위해 별도의 properties를 작성하여 해당 파일에서 관리하도록 하였습니다.
또한, 데이터가 다건의 리스트 혹은 단일 오브젝트인 JSON 형식의 데이터일 경우를 대비하여 인터페이스를 설계하였습니다.

실행
-
- 외부 시스템 정보를 입력합니다.(ex. URL 정보)
- Java 21 버전이 설치된 환경에서 별도의 빌드도구를 사용하여 빌드하거나, 혹은 Main.java가 포함된 프로젝트 루트에서 javac를 사용하여 컴파일하고 실행합니다.

참고사항
-
- 주문정보 객체의 경우 주문 ID는 Long, 나머지는 String 타입으로 전제하고 작성하였습니다.
- 에러 핸들링의 경우, exception 발생 시 처리 할 수 있다면 처리하고 로깅, 아니라면 로깅처리만 하였습니다.

클래스 다이어그램
-
- UML
![img.png](img.png)
- OrderProcessInterface
  - 외부 시스템과 데이터를 동기화(주고 받기)위한 인터페이스
- ExternalSystemOrderApi
  - OrderProcessInterface 를 구현한 클래스
  - 외부 시스템과 HTTP 통신을 통해 주문 데이터를 가져오고 전송합니다.
  - OrderRepository 객체를 통해 주문 데이터를 저장합니다.
- OrderService
  - 주문 데이터의 처리와 외부 시스템과의 통신을 관리하는 서비스 클래스
  - OrderRepository 와 OrderProcessInterface 를 사용하여 주문 데이터를 외부에서 가져와 내부에 저장, 조회, 외부로 전송하는 역할을 합니다.
- OrderRepository
  - 주문 데이터를 메모리에 저장하고 관리하는 클래스
  - 주문 데이터의 CRUD 를 담당합니다.
- Order
  - 주문 데이터의 정보를 담는 클래스