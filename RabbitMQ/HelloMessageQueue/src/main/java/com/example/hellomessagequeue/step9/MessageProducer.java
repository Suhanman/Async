package com.example.hellomessagequeue.step9;


import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Component
public class MessageProducer {

    private final StockRepository stockRepository;
    private final RabbitTemplate rabbitTemplate;


    public MessageProducer(RabbitTemplate rabbitTemplate, StockRepository stockRepository){

        this.stockRepository = stockRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Transactional
    public void sendMessage(StockEntity stockEntity, Boolean testCase){
        stockEntity.setProcessed(false);
        stockEntity.setCreatedAt(LocalDateTime.now());
        StockEntity entity = stockRepository.save(stockEntity);

        System.out.println("[producer entity] : " + entity );

        if (stockEntity.getUserId() == null || stockEntity.getUserId().isEmpty()){
            throw new RuntimeException("User id is required");
        }

        try{
            CorrelationData correlationData = new CorrelationData(entity.getId().toString());
            rabbitTemplate.convertAndSend(
                    testCase ? "nonExistentExchange" : RabbitMQConfig.EXCHANGE_NAME,
                    testCase ? "invalidRoutingKey" : RabbitMQConfig.ROUTING_KEY,
                    entity,
                    correlationData
            );

            if (correlationData.getFuture().get(5, TimeUnit.SECONDS).isAck()){
                System.out.println("[producer correlationData] 성공 " + entity);
                stockRepository.save(entity);
            } else {
                throw new RuntimeException(" # confirm 실패 - 롤백");
            }

        } catch (Exception e){
            System.out.println("[producer exception fail] : " + e);
            throw new RuntimeException(e);
        }


    }
}
