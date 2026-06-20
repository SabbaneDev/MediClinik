package models;

// Représenter un patient
public class Patient {

    private int id;
    private String firstName;
    private String lastName;
    private int age;
    private String phoneNumber;
    private String gender;

    // Constructeur par défaut
    public Patient() {}

    // Constructeur sans id
    public Patient(String firstName, String lastName, int age, String phoneNumber, String gender) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
    }

    // Constructeur avec id
    public Patient(int id, String firstName, String lastName, int age, String phoneNumber, String gender) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.phoneNumber = phoneNumber;
        this.gender = gender;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // Retourne le nom complet
    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public String toString() {
        return getFullName() + " (ID: " + id + ")";
    }
}
