package courses.pluralsight.com.tabianconsulting.models;

import java.io.Serializable;

public class PushNotification implements Serializable {

    private int badge;

    private String title;

    private String body;

    public int getBadge() {
        return badge;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
