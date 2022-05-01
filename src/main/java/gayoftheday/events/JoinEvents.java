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

public class JoinEvents extends ListenerAdapter {

    //TODO: говорят тут завелась петушня. Создаю клетку для геюг

    @Override
    public void onGuildJoin(GuildJoinEvent event) {

        Guild guild = event.getGuild();
        MessageChannel channel = guild.getTextChannelsByName("general", true).get(0);
        channel.sendMessage("Привет, работяги").queue();
        channel.sendMessage("Говорят у вас тут завелись глиномесы").queue();
        channel.sendMessage("Создаю петушиную клетку").queue();
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
        channel.sendMessage("Клетка готова, проверить кто в ней сидит можно командой /currentgay").queue();
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Guild guild = event.getGuild();
        MessageChannel channel = guild.getTextChannelsByName("general", true).get(0);
        channel.sendMessage("Ну привет, дружок-пирожок").queue();
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        Guild guild = event.getGuild();
        MessageChannel channel = guild.getTextChannelsByName("general", true).get(0);
        channel.sendMessage("Пиздуй - бороздуй, " + event.getUser().getName()).queue();
    }
}
