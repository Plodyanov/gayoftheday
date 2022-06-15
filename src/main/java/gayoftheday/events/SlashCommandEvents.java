package gayoftheday.events;

import gayoftheday.api.ApiGayOfTheDay;
import gayoftheday.objects.ApiResponse;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;


public class SlashCommandEvents extends ListenerAdapter {

    int delay = 0;
    boolean replySent = false;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {

        delay = 0;
        replySent = false;

        //проверка на бота
        if (event.getUser().isBot()) {
            return;
        }
        if (!event.isFromGuild()) {
            event.reply("Я работаю только на серверах, а здесь пидор дня всегда ты, " + event.getUser().getName()).queueAfter(++delay, TimeUnit.SECONDS);
            return;
        }
        //получаем инстанс сервера и проверяем наличие роли
        Guild guild = event.getGuild();
        List<Role> gayRoles = guild.getRolesByName("Пидор дня", true);
        if (gayRoles.size() == 0) {
            System.out.println("Created new gayRole");
            guild.createRole().setName("Пидор дня").setColor(Color.pink).setMentionable(true).setHoisted(true).queueAfter(++delay, TimeUnit.SECONDS);
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

        if (event.getName().equalsIgnoreCase("stat")) {
            getStat(event, guild);
        }

//        //Удалить все лишние роли
//        if (event.getName().equalsIgnoreCase("destroyсages")) {
//            List<Role> guildRoles = event.getGuild().getRolesByName("Пидор дня", true);
//            for (Role role : guildRoles) {
//                try {
//                    role.delete().queueAfter(++delay, TimeUnit.SECONDS);
//                    System.out.println("Deleted old gayRole");
//                } catch (Exception e) {
//                    System.out.println("Something went wrong");
//                    e.printStackTrace();
//                }
//            }
//            event.reply("Готово").queueAfter(++delay, TimeUnit.SECONDS);
//        }
//
//        //Создать новую роль
//        if (event.getName().equalsIgnoreCase("buildсage")) {
//            event.getGuild().createRole().setName("Пидор дня").setColor(Color.pink).setMentionable(true).setHoisted(true).queueAfter(++delay, TimeUnit.SECONDS);
//            event.reply("Готово").queueAfter(++delay, TimeUnit.SECONDS);
//        }
    }

    private void getGayOfTheDay(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild, @NotNull Role gayRole) {
        if (event.getMember().getRoles().contains(gayRole)) {
            event.reply("Хорошая попытка, пидарюга").queueAfter(++delay, TimeUnit.SECONDS);
            replySent = true;
            event.getChannel().sendMessage("Пидором дня все еще остается " + event.getUser().getAsMention()).queueAfter(++delay, TimeUnit.SECONDS);
            return;
        }

        List<Member> members = guild.getMembersWithRoles(gayRole);
        System.out.println("gays on this server: " + members.size());
        Member currentGay = null;
        if (members.size() != 0) {
            for (Member member : members) {
                currentGay = member;
                System.out.println("deleting role from " + member.getUser().getAsTag());
                guild.removeRoleFromMember(member, gayRole).queueAfter(++delay, TimeUnit.SECONDS);
                event.reply(member.getAsMention() + " отсидел свой срок в клетке и возвращается к людям").queueAfter(++delay, TimeUnit.SECONDS);
                replySent = true;
            }
        }

        Member newGay = getRandomGay(guild.getMembers());
        guild.addRoleToMember(newGay, gayRole).queueAfter(++delay, TimeUnit.SECONDS);

        String reply = "";
        if (event.getUser().equals(newGay.getUser())) {
            reply = "Ого, у нас каминг-аут. Новым пидором дня становится " + newGay.getUser().getAsMention();
        } else {
            reply = "Новым пидором дня становится " + newGay.getUser().getAsMention();
        }

        if (currentGay != null) {
            if (newGay.getAsMention().equals(currentGay.getAsMention())) {
                reply = newGay.getUser().getName() + " только расслабился, как опять угодил в петушатню. Что ж, от судьбы не убежишь, пидор дня все еще " + newGay.getAsMention();
            }
        }

        if (!replySent) {
            event.reply("Итак, кто же займет вакантное место?").queueAfter(++delay, TimeUnit.SECONDS);
        }

        event.getChannel().sendMessage(reply).queueAfter(++delay, TimeUnit.SECONDS);

        //отправляем запрос к api
        sendNewGayToRemote(guild, newGay.getUser());
    }

    private Member getRandomGay(@NotNull List<Member> members) {

        List<Member> correctMembers = new ArrayList<>();
        for (Member member : members) {
            if (!member.getUser().isBot()) {
                correctMembers.add(member);
                System.out.println(member.getUser().getName() + " added to potential gay list");
            }
        }

        int newRand = correctMembers.size();
        if (newRand > 0) {
            Random rand = new Random();
            newRand = rand.nextInt(newRand);
        } else {
            newRand = 0;
        }
        System.out.println("Choosing random number in range 0.." + correctMembers.size() + ", newRand = " + newRand);

        Member newGay = correctMembers.get(newRand);
        return newGay;
    }

    private void getCurrentGay(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild, @NotNull Role gayRole) {
        if (event.getMember().getRoles().contains(gayRole)) {
            event.reply("Постыдился бы. Сам знаешь, что ты и есть пидор дня").queueAfter(++delay, TimeUnit.SECONDS);
            replySent = true;
            event.getChannel().sendMessage("Пидором дня все еще остается " + event.getUser().getAsMention()).queueAfter(++delay, TimeUnit.SECONDS);
            return;
        }

        List<Member> members = guild.getMembersWithRoles(gayRole);

        System.out.println("gays on this server: " + members.size());
        if (members.size() > 0) {
            event.reply("В клетке сидит " + members.get(0).getAsMention()).queueAfter(++delay, TimeUnit.SECONDS);
            replySent = true;
        } else if (members.size() == 0) {
            event.reply("Клетка пока пуста, непорядок :thinking:").queueAfter(++delay, TimeUnit.SECONDS);
            replySent = true;
            event.getChannel().sendTyping().queueAfter(++delay, TimeUnit.SECONDS);
            getGayOfTheDay(event, guild, gayRole);
        }
    }

    private void getStat(@NotNull SlashCommandInteractionEvent event, @NotNull Guild guild) {

        event.getChannel().sendTyping().queueAfter(++delay, TimeUnit.SECONDS);
        event.reply("Давайте поглядим на вашу статистику").queueAfter(++delay, TimeUnit.SECONDS);
        replySent = true;
        System.out.println("Getting statistics, guildId = " + guild.getId());

        try {
            List<String> userIds = new ArrayList<>();
            for (Member m : guild.getMembers()) {
                userIds.add(m.getUser().getId());
            }

            ApiGayOfTheDay api = new ApiGayOfTheDay();
            ArrayList<ApiResponse> allUsersStats;


            try {
                allUsersStats = api.getStatitstics(guild.getId());
            } catch (Exception e) {
                event.getChannel().sendMessage("У меня пока нет статистики по этому серверу").queueAfter(++delay, TimeUnit.SECONDS);
                return;
            }

            ArrayList<UserInfo> mostPopularGays = getMostPopularGays(guild, allUsersStats);
            ArrayList<UserInfo> oldestGays = getOldestGays(guild, allUsersStats);
            ArrayList<UserInfo> allGays = getAllStat(guild, allUsersStats);

            event.getChannel().sendMessageEmbeds(buildTable(mostPopularGays, oldestGays, allGays)).queueAfter(++delay, TimeUnit.SECONDS);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void sendNewGayToRemote(@NotNull Guild guild, @NotNull User newGay) {
        try {
            ApiGayOfTheDay api = new ApiGayOfTheDay();
            System.out.println("sending " + newGay.getName() + " (id:" + newGay.getId() + ") as new gay");
            api.sendGayToRemote(guild.getId(), newGay.getId());
        } catch (IOException e) {
            System.out.println("error while sending new gay to remote: " + e.getMessage());
        }
    }

    private ArrayList<UserInfo> getAllStat(Guild guild, ArrayList<ApiResponse> allUsersStats) {
        ArrayList<UserInfo> allGaysList = new ArrayList<>();

        for (ApiResponse userStat : allUsersStats) {
            String currentUserStat = "";
            String userName = "Анонимный пидарюга";
            for (Member member : guild.getMembers()) {
                if (member.getUser().getId().equals(userStat.getUserId())) {
                    userName = member.getUser().getName();
                }
            }
            currentUserStat += "был";
            if (userStat.isGay()) {
                currentUserStat += " **(и является)**";
            }
            currentUserStat += " пидором дня " + calculateCounter(userStat.getCounter());
            currentUserStat += ", времени в качестве пидора дня: " + calculateTime(userStat.getDuration()) + "\n";
            allGaysList.add(new UserInfo(userName, currentUserStat));
        }
        return allGaysList;
    }

    private ArrayList<UserInfo> getMostPopularGays(Guild guild, ArrayList<ApiResponse> allUsersStats) {
        ArrayList<UserInfo> mostPopularList = new ArrayList<>();
        //String mostPopularGays = "";
        int counter = 0;

        for (ApiResponse user : allUsersStats) {
            int userCounter = user.getCounter();
            if (userCounter > counter) {
                counter = userCounter;
            }
        }

        for (ApiResponse user : allUsersStats) {
            if (user.getCounter() == counter) {
                String userName = "Анонимный пидор";
                for (Member member : guild.getMembers()) {
                    if (member.getUser().getId().equals(user.getUserId())) {
                        userName = member.getUser().getName();
                    }
                }
                String counts = calculateCounter(user.getCounter());
                mostPopularList.add(new UserInfo(userName, counts));
            }
        }

        return mostPopularList;
    }

    private ArrayList<UserInfo> getOldestGays(Guild guild, ArrayList<ApiResponse> allUsersStats) {
        ArrayList<UserInfo> oldestList = new ArrayList<>();
        long duration = 0;

        for (ApiResponse user : allUsersStats) {
            long userDuration = user.getDuration();
            if (userDuration > duration) {
                duration = userDuration;
            }
        }

        for (ApiResponse user : allUsersStats) {
            if (user.getDuration() == duration) {

                String userName = "Анонимный пидор";
                for (Member member : guild.getMembers()) {
                    if (member.getUser().getId().equals(user.getUserId())) {
                        userName = member.getUser().getName();
                    }
                }
                String hours = calculateTime(user.getDuration());

                oldestList.add(new UserInfo(userName, hours));
            }
        }

        return oldestList;
    }

    private MessageEmbed buildTable(ArrayList<UserInfo> mostPopularGays, ArrayList<UserInfo> oldestGays, ArrayList<UserInfo> allGays) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setAuthor("/stat");
        eb.setTitle("**Таблица пидрил**");
        eb.setDescription("*Список лидеров и общая статистика*");
        eb.setThumbnail("https://images.emojiterra.com/google/android-pie/512px/1f413.png");
        eb.setColor(Color.PINK);
        eb.setTimestamp(new Date().toInstant());

        eb.addBlankField(false);
        eb.addField("       :crown: Чемпионы сервера :crown:", "", false);

        //заполняем таблицу самых популярных пидоров
        if (mostPopularGays.size() == 1) {
            String fieldValue = "";
            for (UserInfo userInfo : mostPopularGays) {
                fieldValue += "\n**" + userInfo.getName() + "**: " + userInfo.getData();
            }
            eb.addField("Самый популярный пидор:", fieldValue, true);
            eb.addBlankField(true);

        } else if (mostPopularGays.size() > 1) {
            String fieldValue = "";
            for (UserInfo userInfo : mostPopularGays) {
                fieldValue += "\n**" + userInfo.getName() + "**: " + userInfo.getData();
            }
            eb.addField("Самые популярные пидоры:", fieldValue, true);
            eb.addBlankField(true);
        }

        //заполняем таблицу самых старых пидоров
        if (oldestGays.size() == 1) {
            String fieldValue = "";
            for (UserInfo userInfo : oldestGays) {
                fieldValue += "\n**" + userInfo.getName() + "**: " + userInfo.getData();
            }
            eb.addField("Самый опытный пидор:", fieldValue, true);

        } else if (oldestGays.size() > 1) {
            String fieldValue = "";
            for (UserInfo userInfo : oldestGays) {
                fieldValue += "\n**" + userInfo.getName() + "**: " + userInfo.getData();
            }
            eb.addField("Самые опытные пидоры:", fieldValue, true);
        }

        eb.addBlankField(false);
        eb.addField("       :rainbow_flag: Общая статистика :rainbow_flag:", "", false);

        for (UserInfo userInfo : allGays) {
            eb.addField(userInfo.getName(), userInfo.getData(), false);
        }

        return eb.build();
    }

    private class UserInfo {
        private String name;
        private String data;

        public String getName() {
            return name;
        }

        public String getData() {
            return data;
        }

        UserInfo(String name, String data) {
            this.name = name;
            this.data = data;
        }
    }

    private String calculateTime(long seconds) {
        String result = "";

        int day = (int) TimeUnit.SECONDS.toDays(seconds);

        //1 день, 2-4 дня, 5-0 дней, 10-20 дней
        if (day % 100 >= 10 && day % 100 <= 20) {
            result += day + " дней";
        } else if (day != 0) {
            switch (day % 10) {
                case 1:
                    result += day + " день";
                    break;
                case 2:
                case 3:
                case 4:
                    result += day + " дня";
                    break;
                default:
                    result += day + " дней";
                    break;
            }
        }

        int hours = (int) TimeUnit.SECONDS.toHours(seconds) - (day * 24);

        //1 час, 2-4 часа, 5-0 часов, 10-20 часов
        if (hours % 100 >= 10 && hours % 100 <= 20) {
            result += " " + hours + " часов";
        } else if (hours != 0) {
            switch (hours % 10) {
                case 1:
                    result += " " + hours + " час";
                    break;
                case 2:
                case 3:
                case 4:
                    result += " " + hours + " часа";
                    break;
                default:
                    result += " " + hours + " часов";
                    break;
            }
        }

        int minute = (int) (TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds) * 60));

        //1 минута, 2-4 минуты, 5-0 минут, 10-20 минут
        if (minute % 100 >= 10 && minute % 100 <= 20) {
            result += " " + minute + " минут";
        } else if (minute != 0) {
            switch (minute % 10) {
                case 1:
                    result += " " + minute + " минута";
                    break;
                case 2:
                case 3:
                case 4:
                    result += " " + minute + " минуты";
                    break;
                default:
                    result += " " + minute + " минут";
                    break;
            }
        }
        if (result.equals("")) {
            result = " меньше минуты";
        }
        return result;
    }

    private String calculateCounter(int counter) {
        String result = "";
        //1 день, 2-4 дня, 5-0 дней, 10-20 дней
        if (counter % 100 >= 10 && counter % 100 <= 20) {
            result += counter + " раз";
        } else {
            switch (counter % 10) {
                case 2:
                case 3:
                case 4:
                    result += counter + " раза";
                    break;
                default:
                    result += counter + " раз";
                    break;
            }
        }
        return result;
    }
}
