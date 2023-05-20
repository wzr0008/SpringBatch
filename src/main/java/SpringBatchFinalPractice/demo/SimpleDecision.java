package SpringBatchFinalPractice.demo;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.stereotype.Component;

@Component
public class SimpleDecision implements JobExecutionDecider {
    @Override
    public FlowExecutionStatus decide(JobExecution jobExecution, StepExecution stepExecution) {
        if(stepExecution==null){
            System.out.println("we can not get anything");
            return new FlowExecutionStatus("fail");
        }
        String exitCode = stepExecution.getExitStatus().getExitCode();
        if (exitCode.equals(ExitStatus.STOPPED.getExitCode())||exitCode.equals(ExitStatus.COMPLETED.getExitCode())){
            return new FlowExecutionStatus("success");
        }
        if(exitCode.equals(ExitStatus.FAILED.getExitCode())){
            System.out.println("xie xie");
        }
        return new FlowExecutionStatus("fail");
    }
}
