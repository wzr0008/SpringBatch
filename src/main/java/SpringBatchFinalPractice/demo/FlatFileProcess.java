package SpringBatchFinalPractice.demo;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class FlatFileProcess implements ItemProcessor<Person,Person> {

    @Override
    public Person process(Person person) throws Exception {
        Person person1 = new Person();
        person1.setFirstName(person.getFirstName().toUpperCase());
        person1.setLastName(person.getLastName().toUpperCase());
        return person1;
    }
}
