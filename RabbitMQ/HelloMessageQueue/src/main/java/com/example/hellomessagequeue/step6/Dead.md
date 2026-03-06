DLQ : 메시지 큐에서 제대로 처리되지 못할 경우 DLQ에 이동 되며, 실패한 메시지를 저장하는 용도로 사용한다.
- NACK 처리나 거부 : basic.reject 혹은 basic.nack으로 메시지 처리되지 못한 경우
- TTL 만료 : 메시지 TTL이 초과된 경우 DLQ로 이동
- 큐 설정 초과 (OV) : 큐에 설정된 최대 매시지 갯수를 초과하면 오래된 메시지가 삭제되고 DLQ로 이동

DLX : 큐 실패시에 데드레터 익스체인지(DLX)를 설정하여서 메시지가 처리되지 못한 경우 지정된 큐로 이동될 수 있다.
예를 들어, 잘못된 형식의 큐로 인해 처리가 어려운 Dead Letter Queue 영역으로 이동하여 에러의 원인을 분석하고 재 처리를 시도할 수 있다.
이런 경우는 처리 중에 예외가 발생한 메시지, TTL(Time to Live)이 만료된 메시지, 큐의 길이 제한(x-mas-length)을 초과하여 삭제된
메시지 등이 DLX로 전달되고, Dead Letter Queue 에 있는 메시지를 통해 에러의 원인을 분석할 수 있다.


# 듀토리얼. Dead Letter Queue를 활용한 실패 메시지의 처리

개발 프로세스
1. DLQ와 DLX 의 Bean 선언
2. SimpleRabbitListenerContainerFactory를 이용하여 AcknowledgeMode를 Manual로 설정
3. Producer 메시지의 발행
4. Consumer 에서 메시지 실패의 건에 대하여 channel.basicReject 3번의 재시도 이후 DLQ로 이동
5. 성공일 경우 ACK 전송 (channel.basicAck)
6. REST API 로 성공과 실패건에 대한 테스트의 진행

수동 ACK 모드 설정을 통하여 메시지의 처리 결과를 RabbitMQ에 명시적으로 전달하고

basicAck, basicNack, basicReject 를 사용하여 메시지 재시도 및 DLQ 처리를 유연하게 구현가능
channel.basicAck: 메시지를 성공적으로 처리되었다는 것을 리턴
메시지가 Ack 되면 메시지를 큐에서 삭제하고 다른 소비자에게 전송하지 않는다.

channel.basicAck(tag, false); //Ack 전송
- deliveryTag(long) 필드는 메시지의 고유 식별 태그
- multiple(boolean) 필드는 true일 경우 이전의 모든 메시지를 한거번에 Ack 처리, false일 경우 현재 태그의 메시지 하나만 ACK 처리

Channel.basicNack(tag, false, false); // DLQ로 메시지 이동
- deliveryTage, multiple, requeue : basicAck와 동일하며 requeue(boolean)의 경우 true일 경우 메시지를 큐에 다시 넣어 재처리하도록 설정한다.
- false의 경우 메시지를 DLQ로 이동 혹은 삭제한다.\

channel.basic(deliveryTag, requeu);
- 파라미터가 동일하다

처리가 복잡하고 메서드 호출도 혼동되기 쉬우므로 AMQP에서 제공하는 RetryTemplate을 통하여 좀 더 명확하고 간단하게 기능 구현이 가능하다.
