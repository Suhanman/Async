//package com.example.hellomessagequeue.step3;
//
//
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class NotificationSubscriber {
//
//    public static final String CLIENT_URL = "/topic/notifications";
//    // websocket으로 메시지를 전달하기
//    private final SimpMessagingTemplate simpMessagingTemplate;
//
//    public NotificationSubscriber(SimpMessagingTemplate simpMessagingTemplate) {
//        this.simpMessagingTemplate = simpMessagingTemplate;
//    }
//
//    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
//    public void subscriber(String message){
//        //RabbitMQ Queue에서 메시지를 수신
//        //String message = (String)
//        System.out.println("Received Notification: " + message);
//        //convertAndSend를 통하여 특정경로로의 메시지 전달
//        simpMessagingTemplate.convertAndSend(CLIENT_URL, message);
//    }
//}
