package SpringBatchFinalPractice.demo;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


public class SimpleJobTask {
    @Autowired
    JobBuilderFactory jobBuilderFactory;
    @Autowired
    StepBuilderFactory stepBuilderFactory;
    @Autowired
    SimpleDecision simpleDecision;
    @Bean
    public Job job(){
        return jobBuilderFactory.get("SimpleJobTask").start(step1())
                .next(simpleDecision)
                .from(simpleDecision).on("success").to(step2())
                .from(simpleDecision).on("*").to(step3())
                .end()
                .build();
    }
    @Bean
    public Step step1(){

        return stepBuilderFactory.get("step1").tasklet(
                new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("Hello, this is the 1st step");
                        return RepeatStatus.FINISHED;
                    }
                }
        ).build();
    }
    public Step step2(){
        return stepBuilderFactory.get("step2").tasklet(
                new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                System.out.println("Hello, this is the 2nd step");
                return RepeatStatus.FINISHED;
            }
        }
        ).build();
    }
    public Step step3(){
        return stepBuilderFactory.get("step3").tasklet(
                new Tasklet() {
                    @Override
                    public RepeatStatus execute(StepContribution stepContribution, ChunkContext chunkContext) throws Exception {
                        System.out.println("Hello, this is the 3rd step");
                        return RepeatStatus.FINISHED;
                    }
                }
        ).build();
    }
    public Flow flow(){
        return new FlowBuilder<Flow>("Flow").start(step2()).next(step3()).build();
    }
}
