# DLQ 재처리와 Retry

## DLQ와 DLX

#### DLQ : 메시지가 큐에서 제대로 처리 되지 못할 경우에 DLQ 에 이동되며, 실패한 메시지를 저장하는 용도로 사용된다.
- NACK 처리나 거부 : basic.reject 혹은 basic.nack으로 메시지가 처리되지 못한 경우
- TTL 만료 : 메시지의 TTL이 초과된 경우 DLQ로 이동
- 큐 설정 초과(Overflow) : 큐에 설정된 최대 메시지 갯수를 초과하면 가장 오래된 메시지가 삭제되고 DLQ 로 이동한다.

#### DLX : 큐 실패 시에 DLX(Dead Letter Exchange)를 설정하여 메시지가 처리되지 못한 경우 지정된 큐로 이동가능하다.
예를 들어 , 잘못된 형식의 큐로 인하여 처리가 어려운 경우 DLQ 영역으로 이동하여 에러의 원인을 분석하고 재처리를 시도할 수 있다.
이런 경우는 처리 중에 예외가 발생한 메시지, TTL이 만료된 메시지, 큐의 길이 제한을 초과하여 삭제된 메시지 등이 DLX 로 전달되고, DLQ 에 있는 메시지를 통하여 에러의 원인을 분석가능하다.

## DLQ 를 활용한 실패 메시지의 재처리 방법
1. DLQ 와 DLX의 빈 선언
2. SimpleRabbitListenerContainerFactory를 이용하여 AcknowledgeMode 모드를 Manual로 설정 (ack/nack 처리를 개발 시점에 직접 핸들링하도록 설정)
3. Producer 메시지의 발행
4. Consumer에서 메시지 실패건에 대하여 channel.basicReject 3번의 재시도 후에 DLQ로 이동(channel.basicNack)
5. 성공일 경우 ACK 전송
6. REST API로 성공과 실패건에 대한 테스트의 진행

다만 이 처리가 다소 복잡하고 메서드 호출도 혼동되기 쉬우므로 이런 처리보다는 Spring AMQP 에서 제공하는 RetryTemplate을 통하여 좀 더 명확하고 간단하게 기능을 구현할 수 있다.
단 RabbitMQ에서 메시지 처리 실패를 관리하기 위하여 수동으로 Ack와 Nack를 호출하는 방식은 RabbitMQ의 저수준 API를 명확히 이해를 전제로 하기 때문에 굉장히 세밀한 애플리케이션에는 적용가능하지만 프로덕션에서의 유지보수가 어렵다.
이 경우 Spring AMQP에서 AcknowlegeMode가 Auto로 기본 세팅되어 있기 때문에 별도의 SimpleRabbitListenerContainerFactory를 통하지 않고 자동으로 ACK/NACK 처리가 가능하다.
따라서, RetryTemplate을 통하여 좀 더 로직을 쉽게 관리할 수 있도록 추상화된 방식으로 다음 을 확인한다.

# RetryTemplate을 통한 간편한 재처리의 설정
동일한 프로세스로 SimpleRetryPoclicy를 설정한 뒤에 RetryTemplate에 담아서 처리하는 방식으로 개선한다.

# 자동 재시도 처리
1. RetryTemplate:
    - Spring AMQP는 RetryTemplate을 통하여 재시도 로직을 지원
    - 최대 3번 재시도 후에는 실패하면 Spring이 메시지를 통하여 DLQ로 이동한다

2. AcknowlegeMode.AUTO : 기본 ㅣ설정
    - 재시도 중에 메시지가 성공적으로 처리되면 Spring AMQP가 자동으로 ACK를 전송한다.
    - 모든 재시도가 실패하면 Nack를 보내고 RabbitMQ가 메시지를 DLQ로 이동한다.
    - DLQ에서 메시지를 수정한 후에 원래 큐로 재전송하면 정상 처리된다.

3. build.gradle 에 retry 디펜던시 추가

4. RetryConfig 클래스에서 기본 설정 세팅하여 빈으로 선언

5. OrderConsumer 3번의 retry 후 DLQ에 전송 