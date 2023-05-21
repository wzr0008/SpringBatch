package SpringBatchFinalPractice.demo;

import org.springframework.stereotype.Component;

@Component
public class MyJobExecutionException extends Exception{
    public MyJobExecutionException(){

    }
    public MyJobExecutionException(String message) {
        super(message);
    }
}
