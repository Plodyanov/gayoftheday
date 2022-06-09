package gayoftheday;

import gayoftheday.events.JoinEvents;
import gayoftheday.events.ResponseEvents;
import gayoftheday.events.SlashCommandEvents;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import javax.security.auth.login.LoginException;

public class MainGay {

    public static void main(String[] args) throws LoginException {

        JDA bot = JDABuilder.createDefault(Config.get("token"))
                .setChunkingFilter(ChunkingFilter.ALL)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .enableIntents(GatewayIntent.GUILD_MEMBERS)
                .setActivity(Activity.watching("за пидорами"))
                .addEventListeners(new ResponseEvents(), new SlashCommandEvents(), new JoinEvents())
                .build();

        bot.upsertCommand("gayoftheday", "Вычисляет пидора дня").queue();
        bot.upsertCommand("currentgay", "Показывает кто сейчас сидит в петушином углу").queue();
        bot.upsertCommand("stat", "Показывает статистику").queue();
    }
}
