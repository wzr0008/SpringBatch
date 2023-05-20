package SpringBatchFinalPractice.demo;

import org.springframework.batch.item.validator.ValidatingItemProcessor;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.batch.item.validator.Validator;
import org.springframework.stereotype.Component;

@Component
public class FlatFileValidate implements Validator<Person> {

    @Override
    public void validate(Person person) throws ValidationException {
        System.out.println("Checking");
        if(person.getFirstName().toLowerCase().equals("rui")){
            throw new ValidationException("we found the Rui's");
        }
    }
}
