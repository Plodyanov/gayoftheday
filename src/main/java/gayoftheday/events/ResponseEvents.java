package gayoftheday.events;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.List;

public class ResponseEvents extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        //проверка на бота
        if (event.getMessage().getAuthor().isBot()) {
            return;
        }
        if (!event.isFromGuild()) {
            event.getMessage().reply("Я работаю только на серверах, а здесь пидор дня всегда ты, " + event.getAuthor().getName()).queue();
            event.getMessage().addReaction("U+1F413").queue();
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

        //сообщения петуха помечаем эмодзи :rooster:
        if (event.getMember().getRoles().contains(gayRole)) {
            event.getMessage().addReaction("U+1F413").queue();
        }

        //нет ты
        if (event.getMessage().getContentRaw().contains("нет ты") || event.getMessage().getContentRaw().contains("нет, ты")) {
            event.getMessage().reply("нет ты").queue();
            return;
        }

        if (event.getMessage().getContentRaw().contains("/check_users")) {
            String users = "Список участников сервера:";
            for (Member member : guild.getMembers()){
                users += "\n\"user_name\": "+member.getUser().getName()+", {\"user_id\":\""+member.getUser().getId()
                        +"\",\"server_id\":\""+guild.getId()
                        +"\",\"duration\":0,\"times\":0,\"is_gay\":false}";
            }
            System.out.println(users);
            event.getChannel().sendMessage(users).queue();
        }

        //да? пизда. нет? пидора ответ
        String[] messageSent = event.getMessage().getContentRaw().split(" ");
        if (messageSent.length <= 3) {
            String lastWord = messageSent[messageSent.length - 1];

            String[] noWord = {"нет", "неет", "нет!", "нет?", "ytn", "ytn!", "ytn&",};
            String[] yesWord = {"да", "да!", "да?", "lf", "lf!", "lf&"};
            for (String word : noWord) {
                if (lastWord.equalsIgnoreCase(word)) {
                    event.getChannel().sendTyping().queue();
                    event.getMessage().reply("пидора ответ").queue();
                    event.getMessage().addReaction("U+1F413").queue();
                    System.out.println("reply sent");
                    return;
                }
            }
            for (String word : yesWord) {
                if (lastWord.equalsIgnoreCase(word)) {
                    event.getChannel().sendTyping().queue();
                    event.getMessage().reply("пизда").queue();
                    System.out.println("reply sent");
                    return;
                }
            }
        }
    }
}
