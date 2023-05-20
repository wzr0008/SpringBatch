package SpringBatchFinalPractice.demo;

public class Person {
    String FirstName;
    String LastName;

    public Person() {
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    @Override
    public String toString() {
        return "The Persion's " +
                "FirstName is " + FirstName + '\'' +
                "and  LastName  is '" + LastName + '\''
                ;
    }
}
