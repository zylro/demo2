package zylro.atc.model;

/**
 * Generic error message POJO for errors to be JSON!
 * 
 * @author wot
 */
public class ErrorMessage {
    private String message;
    
    public ErrorMessage(String msg) {
        this.message = msg;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
