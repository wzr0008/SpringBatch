package SpringBatchFinalPractice.demo;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class JdbcReading {
  @Autowired
  JobBuilderFactory jobBuilderFactory;
  @Autowired
  StepBuilderFactory stepBuilderFactory;
  @Autowired
  DataSource dataSource;
  @Bean
    public Job job() throws Exception {
      return jobBuilderFactory.get("job").start(step1()).build();
  }
  @Bean
    public Step step1() throws Exception {
      return stepBuilderFactory.get("step1")
              .<Person,Person> chunk(10)
              .reader(jdbcReader())
              .writer(list->list.forEach(System.out::println))
              .allowStartIfComplete(true)
              .build();
  }
  public JdbcPagingItemReader<Person> jdbcReader() throws Exception {
      JdbcPagingItemReader<Person> reader = new JdbcPagingItemReader<>();
      reader.setDataSource(dataSource);
      reader.setFetchSize(5);
      reader.setPageSize(5);
      MySqlPagingQueryProvider provider = new MySqlPagingQueryProvider();
      provider.setSelectClause("id,first_name,last_name");
      provider.setFromClause("people");
      reader.setRowMapper(new RowMapper<Person>() {
          @Override
          public Person mapRow(ResultSet rs, int rowNum) throws SQLException {
              Person p=new Person();
              p.setFirstName(rs.getString(2));
              p.setLastName(rs.getString(3));
              return p;
          }
      });
      Map<String,Order> map=new HashMap<>();
      map.put("id", Order.ASCENDING);
      provider.setSortKeys(map);
      reader.setQueryProvider(provider);
      reader.afterPropertiesSet();
      return reader;
  }
}
