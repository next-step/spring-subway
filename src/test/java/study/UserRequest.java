package study;

public class UserRequest {

    private String name;
    private String nickname;
    private int age;

    public UserRequest() {
    }

    public UserRequest(final String name, final String nickname, final int age) {
        this.name = name;
        this.nickname = nickname;
        this.age = age;
    }

    public String getName() {
        return this.name;
    }

    public String getNickname() {
        return this.nickname;
    }

    public int getAge() {
        return this.age;
    }
}
