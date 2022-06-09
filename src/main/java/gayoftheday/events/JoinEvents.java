package gayoftheday.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class JoinEvents extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent event) {

        int delay = 0;
        Guild guild = event.getGuild();
        MessageChannel channel = getMainChannel(guild);
        channel.sendMessage("Привет, работяги").queue();
        channel.sendMessage("Говорят у вас тут завелись глиномесы").queueAfter(++delay, TimeUnit.SECONDS);
        channel.sendMessage("Создаю петушиную клетку").queueAfter(++delay, TimeUnit.SECONDS);
        List<Role> guildRoles = event.getGuild().getRolesByName("Пидор дня", true);

        for (Role role : guildRoles) {
            try {
                role.delete().queue();
                System.out.println("Deleted old gayRole");
            } catch (Exception e) {
                System.out.println("Something went wrong");
                e.printStackTrace();
            }
        }

        guild.createRole().setName("Пидор дня").setColor(Color.pink).setMentionable(true).setHoisted(true).queue();
        channel.sendMessage("Клетка готова, проверить кто в ней сидит можно командой /currentgay").queueAfter(++delay, TimeUnit.SECONDS);
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Guild guild = event.getGuild();
        MessageChannel channel = getMainChannel(guild);
        channel.sendMessage("Ну привет, дружок-пирожок").queue();
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        Guild guild = event.getGuild();
        MessageChannel channel = getMainChannel(guild);
        channel.sendMessage("Пиздуй - бороздуй, " + event.getUser().getName()).queue();
    }

    public MessageChannel getMainChannel (Guild guild) {
        MessageChannel channel;
        if (!(guild.getTextChannelsByName("general", true) == null)){
            channel = guild.getTextChannelsByName("general", true).get(0);
        } else {
            channel = guild.getTextChannels().get(0);
        }
        return channel;
    }
}
