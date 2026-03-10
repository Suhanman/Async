package com.example.hellomessagequeue.step9;


import com.example.hellomessagequeue.step9.MessageProducer;
import com.example.hellomessagequeue.step9.StockEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/message")
public class TransactionController {
    private MessageProducer messageProducer;

    public TransactionController(MessageProducer messageProducer) {
        this.messageProducer = messageProducer;
    }

    @PostMapping()
    public ResponseEntity<String> sendMessage(@RequestBody StockEntity stockEntity,
                                              @RequestParam boolean testCase) {
        //do something
        System.out.println("Publisher Confirms Send message= " + stockEntity );

        try{
            messageProducer.sendMessage(stockEntity, testCase);
            return ResponseEntity.ok("Publisher Confirms sent successfully");

        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Publisher Confirms 트랜잭션 실패 : " + e.getMessage());
        }

    }

}
