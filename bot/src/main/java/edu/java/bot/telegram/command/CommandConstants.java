package edu.java.bot.telegram.command;

public class CommandConstants {
    public static final String UNKNOWN_COMMAND =
        "Была введена неизвестная команда, введите /help для получение списка доступных команд.";
    public static final String LISTS_MARKER = "- ";
    public static final String ADDITIONAL_INFO =
        "Для получения информации о доступных коммандах введите комманду /help";
    public static final String START_COMMAND = "/start";
    public static final String START_DESCRIPTION = "зарегистрироваться";
    public static final String START_NEW_USER_MESSAGE =
        "Добро пожаловать в бота для отслеживания обновлений контента по ссылкам!" + System.lineSeparator()
        + ADDITIONAL_INFO;
    public static final String HELP_COMMAND = "/help";
    public static final String HELP_DESCRIPTION = "получить список доступных команд";
    public static final String HELP_RESPONSE = "Доступные команды:" + System.lineSeparator() + System.lineSeparator();

    public static final String LIST_COMMAND = "/list";
    public static final String LIST_DESCRIPTION = "показать список отслеживаемых ресурсов";
    public static final String LIST_RESPONSE = "Список отслеживаемых ресурсов: " + System.lineSeparator();
    public static final String LIST_EMPTY_RESPONSE = "В данный момент вы не отслеживаете какие-либо ресурсы.";

    public static final String TRACK_COMMAND = "/track";
    public static final String TRACK_DESCRIPTION = "начать отслеживать ресурс (формат: /track <ссылка>)";
    public static final String TRACK_WRONG_COMMAND_FORMAT =
        "Необходимо передать ссылку в следующем формате: /track <ссылка>";

    public static final String UNTRACK_COMMAND = "/untrack";
    public static final String UNTRACK_DESCRIPTION = "прекратить отслеживать ресурс (формат: /untrack <ссылка>)";
    public static final String UNTRACK_WRONG_COMMAND_FORMAT =
        "Необходимо передать ссылку в следующем формате: /untrack <ссылка>";
    public static final String UNREGISTER_COMMAND = "/unregister";
    public static final String UNREGISTER_DESCRIPTION = "удалить свой аккаунт из системы";
    public static final String UNREGISTER_RESPONSE =
        "Вы успешно отписались от всех ссылок и удалили аккаунт из бота";

    private CommandConstants() {
    }
}
