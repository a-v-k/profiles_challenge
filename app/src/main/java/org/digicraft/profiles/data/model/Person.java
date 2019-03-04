package org.digicraft.profiles.data.model;

/**
 * Created by Andrey Koryazhkin on 03-03-2019.
 */
public class Person {

    public static final String GENDER_MALE = "M";
    public static final String GENDER_FEMALE = "F";

//    a. A ​unique ​integer ​ID
//    b. Gender ​(Male/Female)
//    c. Name
//    d. Age
//    e. Profile ​Image
//    f. Hobbies

    private Integer id;

    private String gender;

    private String name;

    private Integer age;

    private String image;

    private String hobbies;

    public Person(Integer id, String gender, String name, Integer age, String image, String hobbies) {
        this.id = id;
        this.gender = gender;
        this.name = name;
        this.age = age;
        this.image = image;
        this.hobbies = hobbies;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getHobbies() {
        return hobbies;
    }

    public void setHobbies(String hobbies) {
        this.hobbies = hobbies;
    }
}
