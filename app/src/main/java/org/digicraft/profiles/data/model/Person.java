package org.digicraft.profiles.data.model;

/**
 * Created by Andrey Koryazhkin on 03-03-2019.
 */
public class Person {

    public static final String F_ID = "id";
    public static final String F_NAME = "name";
    public static final String F_AGE = "age";
    public static final String F_GENDER = "gender";
    public static final String F_IMAGE = "image";
    public static final String F_HOBBIES = "hobbies";

    public static final String GENDER_MALE = "M";
    public static final String GENDER_FEMALE = "F";

//    a. A ​unique ​integer ​ID
//    b. Gender ​(Male/Female)
//    c. Name
//    d. Age
//    e. Profile ​Image
//    f. Hobbies

    private Integer id;

    private String fbId;

    private String name;

    private Integer age;

    private String gender;

    private String image;

    private String hobbies;

    //todo: make rule for proGuard
    // required to deserialize from db
    public Person() {

    }

    public Person(Integer id, String name, Integer age, String gender, String image, String hobbies) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = checkGender(gender);
        this.image = image;
        this.hobbies = hobbies;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getFbId() {
        return this.fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = checkGender(gender);
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

    private String checkGender(String in) {
        if (GENDER_MALE.equals(in)) {
            return GENDER_MALE;
        } else if (GENDER_FEMALE.equals(in)) {
            return GENDER_FEMALE;
        } else {
            return GENDER_MALE;
        }
    }

}
