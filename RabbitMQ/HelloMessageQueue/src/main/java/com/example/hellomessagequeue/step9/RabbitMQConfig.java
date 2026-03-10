package com.example.hellomessagequeue.step9;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    public static final String QUEUE_NAME = "transactionQueue";
    public static final String EXCHANGE_NAME = "transactionExchange";
    public static final String ROUTING_KEY = "transactionRoutingKey";


    @Bean
    public Queue transactionQueue(){
        return QueueBuilder.durable(QUEUE_NAME)
                .withArgument("x-dead-letter-exchange", "") // Dead Letter Exchange
                .withArgument("x-dead-letter-routing-key", "deadLetterQueue")
                .build();
    }

    @Bean
    public Queue deadLetterQueue(){return new Queue("deadLetterQueue");}

    @Bean
    public DirectExchange transactionExchange(){
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding tansactionBinding(Queue transactionQueue, DirectExchange transactionExchange){
        return BindingBuilder.bind(transactionQueue).to(transactionExchange).with(ROUTING_KEY);
    }


    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    //TODO RabbitTemplate 설정, ReturnsCallback 활성화 등록, ConfirmCallback  설정

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter()); // json 변환기
        rabbitTemplate.setMandatory(true); // ReturnCallBack 활성화

        // confirmCallBack 설정

        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if(ack){
                System.out.println("[Message Confirmed] = " + (correlationData != null ? correlationData.getId() : "null"));
            } else{
                System.out.println("[Message not Confirmed] = " + (correlationData != null
                ? correlationData.getId() : "null") + ", reason :" + cause);

                // 실제 메시지가 처리 실패하였을 경우 처리 로직을 작성
            }
        });

        //ReturnCallback 설정
        rabbitTemplate.setReturnsCallback(returned ->{
            System.out.println("Return Message = " + returned.getMessage().getBody());
            System.out.println("Exchange = " + returned.getExchange());
            System.out.println("Exchange = " + returned.getRoutingKey());
            
            // 데드레터 설정
    });
        return rabbitTemplate;

    }

    // RabbitListener 설정 수동 ACK 모드 설정
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter());
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL); // 수동 ACK 모드
        return factory;
    }
}
