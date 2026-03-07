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

3. 