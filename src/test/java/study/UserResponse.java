package study;

public class UserResponse {

    private final String name;
    private final int age;

    public UserResponse(final String name, final int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return this.name;
    }

    public int getAge() {
        return this.age;
    }
}
