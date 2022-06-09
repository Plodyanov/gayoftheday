package gayoftheday.api;

import gayoftheday.objects.ApiResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ApiGayOfTheDay {

    private String apiKey = System.getenv("api_key".toUpperCase());
    private String web = "https://g4y0f7h3d4y.herokuapp.com/api/";

    public ArrayList<ApiResponse> getStatitstics(String guildId) {

        try {
            URL url = new URL(web + "statistics?server_id="+guildId);
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            http.setRequestMethod("GET");
            http.setRequestProperty("X-API-KEY", apiKey);
            http.setRequestProperty("accept", "application/json");

            StringBuilder sb = new StringBuilder();
            int HttpResult = http.getResponseCode();
            if (HttpResult == HttpURLConnection.HTTP_OK) {
                List<ApiResponse> userStat = new ArrayList<>();

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(http.getInputStream(), "utf-8"));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                    userStat.add(new ApiResponse(line));
                }
                br.close();
                System.out.println("GET result: " + http.getResponseMessage() + "\n body: " + sb);

                //Возвращаем список объектов ApiResponse по каждому юзеру
                return parseJson(sb.toString());

            } else {
                System.out.println("Something went wrong during GET request");
                System.out.println(http.getResponseMessage());
                return null;
            }
        } catch (IOException e) {
            System.out.println("Something went wrong during GET request");
            System.out.println(new RuntimeException(e).getMessage());
            return null;
        }
    }

    public boolean sendGayToRemote(String guildId, String userId) {
        try {
            URL url = new URL(web + "gayoftheday");
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            http.setRequestProperty("Accept", "application/json");
            http.setRequestProperty("X-API-KEY", apiKey);
            http.setRequestProperty("Content-Type", "application/json");

            String data = "{\"user_id\": \""+ userId + "\", \"server_id\": \"" + guildId + "\"}";

            System.out.println("Sending data: " + data);
            byte[] out = data.getBytes(StandardCharsets.UTF_8);

            OutputStream stream = http.getOutputStream();
            stream.write(out);

            System.out.println("POST result: " + http.getResponseCode() + " " + http.getResponseMessage());
            http.disconnect();

            return true;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
    public ApiGayOfTheDay() throws IOException {
    }
    private ArrayList<ApiResponse> parseJson(String json){
        ArrayList<ApiResponse> apiResponsesList = new ArrayList<>();
        String apiResponse = json;
        apiResponse = apiResponse.replace("]", "");
        String[]apiResponses = apiResponse.split("},");

        for (String response : apiResponses){
            apiResponsesList.add(new ApiResponse(response));
        }
        Collections.sort(apiResponsesList);
        return apiResponsesList;
    }
}
