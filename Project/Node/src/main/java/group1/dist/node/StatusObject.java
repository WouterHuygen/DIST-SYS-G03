package group1.dist.node;

public class StatusObject<T> {
    private boolean succes;
    private String message;
    private T body;

    public StatusObject(){}

    public StatusObject(boolean succes, String message, T body) {
        this.succes = succes;
        this.message = message;
        this.body = body;
    }

    public boolean isSucces() {
        return succes;
    }

    public String getMessage() {
        return message;
    }

    public T getBody() {
        return body;
    }

    public void setSucces(boolean succes) {
        this.succes = succes;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setBody(T body) {
        this.body = body;
    }
}
