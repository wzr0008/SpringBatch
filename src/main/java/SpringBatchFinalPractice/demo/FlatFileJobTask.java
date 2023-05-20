package SpringBatchFinalPractice.demo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Configuration
public class FlatFileJobTask {
    @Value("${FlatFileResource}")
    String fileInput;
    @Autowired
    JobBuilderFactory  jobBuilderFactory;
    @Autowired
    StepBuilderFactory stepBuilderFactory;
    @Autowired
    FlatFileProcess flatFileProcess;
    @Bean
    public Job FlatJob(){
        return jobBuilderFactory.get("FlatJob").start(Flatstep()).build();
    }
    @Bean
    public Step Flatstep(){
        return stepBuilderFactory.get("Flat-step").<Person,Person>chunk(10).reader(reader()).processor(flatFileProcess).writer(list->{
            list.forEach(System.out::println);
        }).build();
    }
    @Bean
    public FlatFileItemReader<Person> reader(){
        FlatFileItemReader<Person> reader = new FlatFileItemReader<>();
        reader.setLinesToSkip(0);
        reader.setResource(new ClassPathResource(fileInput));
        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setNames("FirstName","LastName");
        DefaultLineMapper<Person> mapper = new DefaultLineMapper<>();
        mapper.setFieldSetMapper(fieldSet -> {
            Person p=new Person();
            p.setFirstName(fieldSet.readString(0));
            p.setLastName(fieldSet.readString(1));
            return p;
        });
        mapper.setLineTokenizer(tokenizer);
        reader.setLineMapper(mapper);
        return reader;
    }

}
