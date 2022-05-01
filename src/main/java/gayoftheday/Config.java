package gayoftheday;

import io.github.cdimascio.dotenv.Dotenv;

public class Config {
    private static final Dotenv dotenv = Dotenv.load();
    public static String get(String key){
        //получаем токен из .env
        //return dotenv.get(key.toUpperCase());

        //получаем токен из среды heroku
        return System.getenv("token".toUpperCase());
    }
}
