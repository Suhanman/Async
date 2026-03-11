# 주요 용어와 Exchange의 이해

## RabbitMQ 주요 용어 정리

### 1. Producer (생산자):
- 메시지를 생성하고 RabbitMQ에 전송하는 애플리케이션
- Producer는 특정 Exchange에 메시지를 전송하고 Exchange는 메시지를 라우팅하여 큐에 배치

### 2. Exchange : 
- Producer로 부터 받은 메시지를 큐에 전달
- Exchange 유형 : 
    - Direct: 특정 라우팅 키와 정확히 일치하는 큐에 메시지를 전송
    - Fanout: 모든 큐에 메시지를 브로드캐스트
    - Topic: 라우팅 키 패턴을 기반으로 메시지를 특정 큐에 전달
    - Headers: 메시지 헤더 속성에 따라 메시지를 라우팅
### 3. Routing Key : 
- 메시지를 전송할 때 Producer가 Exchange에 전달하는 키
- Exchange는 이 Routing Key를 참고하여 어떤 큐에 메시지를 전달할지 결정한다.

### 4. Queue : 
- 메시지를 일시적으로 저장하는 버퍼 역할, RabbitMQ의 큐는 FIFO 방식으로 동작하며, 메시지가 소비자에게 전달될 때 까지 보관.
- 각 큐는 여러 Consumer가 구독(수신)할 수 있으며, 메시지는 큐에 들어온 순서대로 전달
- 비동기적으로 동작하며, 여러 컨슈머가 동시에 메시지를 소비할 수 있다. 단 하나의 메시지가 여러 소비자에게 중복으로 전달될 수는 없다. 동일한 메시지를 수신하려면 Fanout Exchange 방식으로 가야만함.

### 5. Binding : 
- exchange와 큐간의 관계를 정의
- 바인딩의 메시지를 라우팅할 때 어떤 조건으로 큐에 보낼지 정의하고 이를 위해 binding key가 사용됨
- Binding Key와 Routing Key 가 일치하며 해당 큐로 메시지가 전달(패턴 매칭 가능)

### 6. Consumer (소비자) : 
- 큐에서 메시지를 가져와 처리하는 애플리케이션
- RabbitMQ는 여러 소비자에게 메시지를 로드밸런싱 할 수 있다.
- Consumer는 큐에서 메시지를 받아 처리하면 메시지에 대한 확인(Ack)를 브로커에 전송한다.
- 확인을 보내지 않으면, 브로커 메시지를 재전송하거나 설정한 다른 Consumer에게 전달할 수 있다.

### 7. Message Acknowledgment (메시지 확인):
- 메시지가 성공적으로 처리되었음을 RabbitMQ에 알리는 과정
- 만약 소비자가 메시지를 성공적으로 처리하지 못했다면, 메시지를 다시 큐에 넣어 다른 소비자가 처리하도록 할 수 있다.

1. Producer가 메시지와 Routing Key를 Exchange에 전송
2. Exchange가 Routing Key를 사용하여 Binding Key가 일치하는 큐에 메시지를 라우팅
3. Consumer가 큐에서 메시지를 가져와서 처리하고 성공적 처리가 되었음을 acknowlegement로 RabbitMQ에 알림.
