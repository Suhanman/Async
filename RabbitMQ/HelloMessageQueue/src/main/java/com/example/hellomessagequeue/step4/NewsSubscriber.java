//package com.example.hellomessagequeue.step4;
//
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class NewsSubscriber {
//    private final SimpMessagingTemplate messagingTemplate;
//
//    public NewsSubscriber(SimpMessagingTemplate messagingTemplate) {
//        this.messagingTemplate = messagingTemplate;
//    }
//
//    @RabbitListener(queues = RabbitMQConfig.JAVA_QUEUE)
//    public void javaNews(String message){
//        messagingTemplate.convertAndSend("/topic/java", message);
//    }
//
//    // 자바 브로드캐스트
//    @RabbitListener(queues = RabbitMQConfig.SPRING_QUEUE)
//    public void springNews(String message){
//        messagingTemplate.convertAndSend("/topic/spring", message);
//    }
//    // 기술 뉴스 브로드캐스트
//
//    @RabbitListener(queues = RabbitMQConfig.VUE_QUEUE)
//    public void vueNews(String message){
//        messagingTemplate.convertAndSend("/topic/vue", message);
//    }
//
//}
