package gayoftheday.objects;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiResponse implements Comparable<ApiResponse> {

    public String getUserId() {
        return userId;
    }

    public String getGuildId() {
        return guildId;
    }

    public long getDuration() {
        return duration;
    }

    public int getCounter() {
        return counter;
    }

    public boolean isGay() {
        return isGay;
    }

    String userId;
    String guildId;
    long duration;
    int counter;
    boolean isGay;
    public ApiResponse(String response){

        response += "}";
        String[] values = response.split(",");

        try {
            this.userId = getValue(values[0]);
            this.guildId = getValue(values[1]);
            this.duration = Long.parseLong(getValue(values[2]));
            this.counter = Integer.parseInt(getValue(values[3]));
            this.isGay = getValue(values[4]).equals("true");
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    private String getValue(String rawValue){
        rawValue += ",";
        //System.out.println(rawValue);
        Pattern[] patterns = {Pattern.compile("\":\"(.*?)\","), Pattern.compile("\":(.*?)}"), Pattern.compile("\":(.*?),"), };

        String value = "";
        for (Pattern pattern : patterns){
            Matcher matcher = pattern.matcher(rawValue);

            if (matcher.find()){
                value = matcher.group(1);
                return value;
            }
        }
        return value;
    }

    @Override
    public int compareTo(@NotNull ApiResponse apiResponse) {
        return apiResponse.counter - this.counter;
    }
}
