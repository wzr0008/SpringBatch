package SpringBatchFinalPractice.demo;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

import javax.batch.api.listener.JobListener;
@Component
public class MyJobListener2 implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {
        System.out.println("The job start from listener 1");
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        System.out.println("The job end from listener 1");

    }
}