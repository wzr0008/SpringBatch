package SpringBatchFinalPractice.demo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.listener.CompositeJobExecutionListener;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;
@Configuration
public class SkipPractice {
    @Autowired
    JobBuilderFactory jobBuilderFactory;
    @Autowired
    StepBuilderFactory stepBuilderFactory;
    @Autowired
    MySkipListener mySkipListener;
    @Autowired
    MyJobListener myJobListener;
    @Autowired
    MyJobListener2 myJobListener2;
    private int count=0;
    @Bean
    public Job job(){
        return jobBuilderFactory.get("job").start(step1()).listener(composite()).build();
    }
    @Bean
    public Step step1(){
        return stepBuilderFactory
                .get("step1")
                .<String,String> chunk(10)
                .reader(listItemReader())
                .processor(myProcessor())
                .writer(list->list.forEach(System.out::println))
                .faultTolerant()
                .skipLimit(3)
                .skip(MyJobExecutionException.class)
                .listener(mySkipListener)
                .allowStartIfComplete(true)
                .build();
    }
    private ListItemReader<String> listItemReader() {
        ArrayList<String> datas = new ArrayList<>();
        IntStream.range(0, 5).forEach(i -> datas.add(String.valueOf(i)));
        return new ListItemReader<>(datas);
    }

    private ItemProcessor<String, String> myProcessor() {
        return item -> {
            System.out.println("当前处理的数据：" + item);
            if ("2".equals(item)&&this.count<=1) {
                this.count++;
                throw new MyJobExecutionException("任务处理出错");
            } else {
                return item;
            }
        };
    }
    private CompositeJobExecutionListener composite(){
        CompositeJobExecutionListener listener = new CompositeJobExecutionListener();
        listener.setListeners(Arrays.asList(myJobListener,myJobListener2));
        return listener;
    }
}
