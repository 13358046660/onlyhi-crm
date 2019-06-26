package cn.onlyhi.client.dto;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/4/20.
 */
public class UserDto extends BaseDto {
    private String userUuid;
    private String userName;

    public String getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(String userUuid) {
        this.userUuid = userUuid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
