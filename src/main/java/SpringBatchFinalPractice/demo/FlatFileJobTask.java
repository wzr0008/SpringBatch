package SpringBatchFinalPractice.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.JsonFileItemWriter;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Configuration
public class FlatFileJobTask {
    @Value("${FlatFileResource}")
    String fileInput;
    @Value("${FlatFileOutput}")
    String output;
    @Autowired
    JobBuilderFactory  jobBuilderFactory;
    @Autowired
    StepBuilderFactory stepBuilderFactory;
    @Autowired
    FlatFileProcess flatFileProcess;
    @Autowired
    FlatFileValidate flatFileValidate;
    @Bean
    public Job FlatJob() throws Exception {
        return jobBuilderFactory.get("FlatJob").start(Flatstep()).build();
    }
    @Bean
    public Step Flatstep() throws Exception {
        return stepBuilderFactory.get("Flat-step").<Person,Person>chunk(10).reader(FlatFilereader())
                .processor(validate())
                .writer(compositeitemWriter())
                .allowStartIfComplete(true)
                .build();
    }

    private FlatFileItemReader<Person> FlatFilereader(){
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
    private JsonFileItemWriter<Person> jsonwriter(){
        FileSystemResource resource = new FileSystemResource("target/persons.json");
        JacksonJsonObjectMarshaller<Person>  mashaller = new JacksonJsonObjectMarshaller<>();
        JsonFileItemWriter<Person> personJsonFileItemWriter = new JsonFileItemWriter<>(resource, mashaller);
        personJsonFileItemWriter.setName("Jsonwriter");
        return personJsonFileItemWriter;
    }
    private FlatFileItemWriter<Person> FlatFilewriter() throws Exception {
        FlatFileItemWriter<Person> writer = new FlatFileItemWriter<>();
        FileSystemResource resource = new FileSystemResource(output);
        writer.setResource(resource);
        LineAggregator<Person> aggregator= item->{
            try{
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(item);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        };
        writer.setLineAggregator(aggregator);
        writer.afterPropertiesSet();
        return writer;
    }
    private CompositeItemProcessor<Person,Person> validate(){
        CompositeItemProcessor<Person,Person> processor=new CompositeItemProcessor<>();
        ValidatingItemProcessor<Person> check = new ValidatingItemProcessor<>();
        check.setValidator(flatFileValidate);
        check.setFilter(true);
        processor.setDelegates(Arrays.asList(flatFileProcess,check));
        return processor;
    }
    private CompositeItemWriter<Person> compositeitemWriter() throws Exception {
        CompositeItemWriter<Person> writer = new CompositeItemWriter<>();
        writer.setDelegates(Arrays.asList(jsonwriter(),FlatFilewriter()));
       return writer;
    }
}
