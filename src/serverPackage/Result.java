package serverPackage;

import java.io.Serializable;

public class Result implements Serializable {
    private static final long serialVersionUID = 1L;

    private double value;
    private String message;

    public Result(double value, String message) {
        this.value = value;
        this.message = message;
    }

    public double getValue() { return value; }
    public String getMessage() { return message; }
}
