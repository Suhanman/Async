package com.example.hellomessagequeue.step0;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class Sender {

    private final RabbitTemplate rabbitTemplate;

    public Sender(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(String message){
        rabbitTemplate.convertAndSend(RabbitMQConfig.Queue_NAME, message);
        System.out.println("[#] Sent : " + message);
    }
}
