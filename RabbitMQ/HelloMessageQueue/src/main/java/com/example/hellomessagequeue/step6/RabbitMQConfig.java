//package com.example.hellomessagequeue.step6;
//
//import org.springframework.amqp.core.*;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RabbitMQConfig {
//    public static final String ORDER_COMPLETED_QUEUE ="order_completed_queue";
//    public static final String ORDER_EXCHANGE = "order_completed_exchange";
//    public static final String DLQ= "deadletterQueue";
//    public static final String DLX = "deadletterExchange";
//
//
//    @Bean
//    public TopicExchange orderExcExchange(){
//        return new TopicExchange(ORDER_EXCHANGE);
//    }
//
//    @Bean
//    public TopicExchange deadLetterExchange(){
//        return new TopicExchange(DLX);
//    }
//
//
//    // 메시지가 처리되지 못하였을 경우에 자동으로 DeadletterQueue 이동시킨다
//
//    @Bean
//    public Queue orderQueue(){
//        return QueueBuilder.durable(ORDER_COMPLETED_QUEUE)
//                .withArgument("x-dead-letter-exchange", DLX) //Dead letter Exchange 설정
//                .withArgument("x-dead-letter-routing-key", DLQ)
//                .ttl(5000)
//                .build();
//    }
//
//    @Bean
//    public Queue deadLetterQueue(){
//        return new Queue(DLQ);
//    }
//
//    @Bean
//    public Binding orderCompletedBindig(){
//        return BindingBuilder.bind(orderQueue()).to(orderExcExchange()).with("order.completed.#");
//
//    }
//
//    @Bean
//    public Binding deadLetterBinding(TopicExchange deadLetterExchange){
//        return BindingBuilder.bind(deadLetterQueue()).to(deadLetterExchange()).with(DLQ);
//    }
//
//
//
//}
