
package ltd.Emallix.mall.common;

public class EmallixException extends RuntimeException {

    public EmallixException() {
    }

    public EmallixException(String message) {
        super(message);
    }

    /**
     * 丢出一个异常
     *
     * @param message
     */
    public static void fail(String message) {
        throw new EmallixException(message);
    }

}
