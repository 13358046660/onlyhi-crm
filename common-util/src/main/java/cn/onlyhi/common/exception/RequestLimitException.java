package cn.onlyhi.common.exception;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/6/2.
 */
public class RequestLimitException extends Exception {
    private static final long serialVersionUID = 1364225358754654702L;

    public RequestLimitException() {
        super("操作频繁，请稍后操作");
    }

    public RequestLimitException(String message) {
        super(message);
    }
}
