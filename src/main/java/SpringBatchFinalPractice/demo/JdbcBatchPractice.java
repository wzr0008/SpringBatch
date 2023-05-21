package SpringBatchFinalPractice.demo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import javax.sql.DataSource;

import java.util.Arrays;

public class JdbcBatchPractice {
    @Autowired
    JobBuilderFactory jobBuilderFactory;
    @Autowired
    StepBuilderFactory stepBuilderFactory;
    @Value("${FlatFileResource}")
    String input;
    @Autowired
    DataSource datasource;
    @Autowired
    FlatFileProcess flatFileProcess;
    @Bean
    public Job job(){
        return jobBuilderFactory.get("Job1").start(step1()).build();
    }
    @Bean
    public Step step1(){
        return stepBuilderFactory.get("step1")
                .<Person,Person> chunk(10)
                .reader(flatreader())
                .processor(compositeItemprocessor())
                .writer(jdbcwriter())
                .allowStartIfComplete(true)
                .build();
    }
   public FlatFileItemReader<Person> flatreader(){
       FlatFileItemReader<Person> reader = new FlatFileItemReader<>();
       reader.setResource(new ClassPathResource(input));
       DelimitedLineTokenizer tokenizer=new DelimitedLineTokenizer();
       tokenizer.setNames("firstName","lastName");
       DefaultLineMapper<Person> mapper=new DefaultLineMapper<>();
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
   public JdbcBatchItemWriter<Person> jdbcwriter(){
       JdbcBatchItemWriter<Person> writer = new JdbcBatchItemWriter<>();
       writer.setDataSource(datasource);
       String sql="insert into people(first_name,last_name) values(:FirstName,:LastName)";
       writer.setSql(sql);
       BeanPropertyItemSqlParameterSourceProvider<Person> rovider = new BeanPropertyItemSqlParameterSourceProvider<>();
       writer.setItemSqlParameterSourceProvider(rovider);
       writer.afterPropertiesSet();
       return writer;
   }
   public ValidatingItemProcessor<Person> validator(){
       ValidatingItemProcessor<Person> validate = new ValidatingItemProcessor<>();
       validate.setValidator(
               new Validator<Person>() {
                   @Override
                   public void validate(Person person) throws ValidationException {
                       if(person.getFirstName().toLowerCase().equals("rui")){
                           throw new ValidationException("we found a rui here");
                       }
                   }
               }
       );
       validate.setFilter(true);
       return validate;
   }
   public CompositeItemProcessor<Person,Person> compositeItemprocessor(){
       CompositeItemProcessor<Person, Person> processor = new CompositeItemProcessor<Person, Person>();
       processor.setDelegates(Arrays.asList(validator(),flatFileProcess));
       return processor;
   }
}
