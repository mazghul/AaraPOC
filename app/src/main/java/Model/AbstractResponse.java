package Model;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;

@Getter
public class AbstractResponse {

    @SerializedName("error")
    private boolean error;

    @SerializedName("message")
    private String message;

    @SerializedName("responseCode")
    private int responseCode;

    public String getMessage() {
        return message;
    }

}
