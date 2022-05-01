package gayoftheday.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SlashCommandEvents extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {


        //проверка на бота
        if (event.getUser().isBot()) {
            return;
        }
        if (!event.isFromGuild()) {
            event.reply("Я работаю только на серверах, а здесь пидор дня всегда ты, " + event.getUser().getName()).queue();
            return;
        }
        //получаем инстанс сервера и проверяем наличие роли
        Guild guild = event.getGuild();
        List<Role> gayRoles = guild.getRolesByName("Пидор дня", true);
        if (gayRoles.size() == 0) {
            System.out.println("Created new gayRole");
            guild.createRole().setName("Пидор дня").setColor(Color.pink).setMentionable(true).setHoisted(true).queue();
        }
        gayRoles = guild.getRolesByName("Пидор дня", true);
        Role gayRole = gayRoles.get(gayRoles.size() - 1);

        //Получаем нового пидора дня
        if (event.getName().equalsIgnoreCase("gayoftheday") && !event.getUser().isBot()) {
            getGayOfTheDay(event, guild, gayRole);
        }

        //Получаем текущего пидора дня
        if (event.getName().equalsIgnoreCase("currentgay") && !event.getUser().isBot()) {
            getCurrentGay(event, guild, gayRole);
        }

//        //Удалить все лишние роли
//        if (event.getName().equalsIgnoreCase("destroyсages")) {
//            List<Role> guildRoles = event.getGuild().getRolesByName("Пидор дня", true);
//            for (Role role : guildRoles) {
//                try {
//                    role.delete().queue();
//                    System.out.println("Deleted old gayRole");
//                } catch (Exception e) {
//                    System.out.println("Something went wrong");
//                    e.printStackTrace();
//                }
//            }
//            event.reply("Готово").queue();
//        }
//
//        //Создать новую роль
//        if (event.getName().equalsIgnoreCase("buildсage")) {
//            event.getGuild().createRole().setName("Пидор дня").setColor(Color.pink).setMentionable(true).setHoisted(true).queue();
//            event.reply("Готово").queue();
//        }
    }

    private void getGayOfTheDay(@NotNull SlashCommandInteractionEvent event, Guild guild, Role gayRole) {
        if (event.getMember().getRoles().contains(gayRole)) {
            event.reply("Хорошая попытка, пидарюга").queue();
            event.getChannel().sendMessage("Пидором дня все еще остается " + event.getUser().getAsMention()).queue();
            return;
        }

        List<Member> members = guild.getMembersWithRoles(gayRole);
        System.out.println("gays on this server: " + members.size());
        Member currentGay = null;
        if (members.size() != 0) {
            for (Member member : members) {
                currentGay = member;
                System.out.println("deleting role from " + member.getUser().getAsTag());
                guild.removeRoleFromMember(member, gayRole).queue();
                event.reply(member.getAsMention() + " отсидел свой срок в клетке и возвращается к людям").queue();
            }
        }

        Member newGay = getRandomGay(guild.getMembers());
        guild.addRoleToMember(newGay, gayRole).queue();
        String reply = "Новым пидором дня становится " + newGay.getUser().getAsMention();

        if (currentGay != null) {
            if (newGay.getAsMention().equals(currentGay.getAsMention())) {
                reply = newGay.getUser().getName() + " только расслабился, как опять угодил в петушатню. Что ж, от судьбы не убежишь, пидор дня все еще " + newGay.getAsMention();
            }
        }
        event.getChannel().sendMessage(reply).queue();
    }

    private Member getRandomGay(@NotNull List<Member> members) {

        System.out.println("members size = " + members.size());

        List<Member> correctMembers = new ArrayList<>();
        for (Member member : members) {
            if (!member.getUser().isBot()) {
                correctMembers.add(member);
                System.out.println(member.getUser().getName() + " added to potential gay list");
            }
        }
        System.out.println("correct members size = " + correctMembers.size());

        int newRand = correctMembers.size();
        if (newRand > 0) {
            Random rand = new Random();
            newRand = rand.nextInt(newRand);
        } else {
            newRand = 0;
        }
        Member newGay = correctMembers.get(newRand);
        return newGay;
    }

    private void getCurrentGay(@NotNull SlashCommandInteractionEvent event, Guild guild, Role gayRole) {
        if (event.getMember().getRoles().contains(gayRole)) {
            event.reply("Постыдился бы. Сам знаешь, что ты и есть пидор дня").queue();
            event.getChannel().sendMessage("Пидором дня все еще остается " + event.getUser().getAsMention()).queue();
            return;
        }

        List<Member> members = guild.getMembersWithRoles(gayRole);

        System.out.println("gays on this server: " + members.size());
        if (members.size() > 0) {
            event.reply("В клетке сидит " + members.get(0).getAsMention()).queue();
        } else if (members.size() == 0) {
            event.reply("Клетка пока пуста, непорядок :thinking:").queue();
            event.getChannel().sendTyping().queue();
            getGayOfTheDay(event, guild, gayRole);
        }
    }
}
