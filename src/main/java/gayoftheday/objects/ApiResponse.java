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

        for (String value : values) {
            value += ",";
            try {
                if (value.contains("\"user_id\"")){
                    String valueDecoded = "";
                    Pattern pattern = Pattern.compile("\"user_id\":\"(\\d*)\"");
                    Matcher matcher = pattern.matcher(value);
                    if (matcher.find()){
                        valueDecoded = matcher.group(1);
                        this.userId = valueDecoded;
                        continue;
                    }
                }
                if (value.contains("\"server_id\"")){
                    String valueDecoded = "";
                    Pattern pattern = Pattern.compile("\"server_id\":\"(\\d*)\"");
                    Matcher matcher = pattern.matcher(value);
                    if (matcher.find()){
                        valueDecoded = matcher.group(1);
                        this.guildId = valueDecoded;
                        continue;
                    }
                }
                if (value.contains("\"duration\"")){
                    String valueDecoded = "";
                    Pattern pattern = Pattern.compile("\"duration\":(\\d*)");
                    Matcher matcher = pattern.matcher(value);
                    if (matcher.find()){
                        valueDecoded = matcher.group(1);
                        this.duration = Long.parseLong(valueDecoded);
                        continue;
                    }
                }
                if (value.contains("\"times\"")){
                    String valueDecoded = "";
                    Pattern pattern = Pattern.compile("\"times\":(\\d*)");
                    Matcher matcher = pattern.matcher(value);
                    if (matcher.find()){
                        valueDecoded = matcher.group(1);
                        this.counter = Integer.parseInt(valueDecoded);
                        continue;
                    }
                }
                if (value.contains("\"is_gay\"")){
                    String valueDecoded = "";
                    Pattern pattern = Pattern.compile("\"is_gay\":([a-zA-Z]*)");
                    Matcher matcher = pattern.matcher(value);
                    if (matcher.find()){
                        valueDecoded = matcher.group(1);
                        this.isGay = valueDecoded.equals("true");
                        continue;
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    @Override
    public int compareTo(@NotNull ApiResponse apiResponse) {
        if (apiResponse.counter - this.counter == 0){
            return (int) (apiResponse.duration - this.duration);
        } else {
            return apiResponse.counter - this.counter;
        }
    }
}
