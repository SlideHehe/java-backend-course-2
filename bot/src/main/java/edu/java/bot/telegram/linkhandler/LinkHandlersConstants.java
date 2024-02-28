package edu.java.bot.telegram.linkhandler;

public class LinkHandlersConstants {
    public static final String GITHUB_HOST = "github.com";
    public static final String STACKOVERFLOW_HOST = "stackoverflow.com";

    public static final String HTTP_SCHEME = "http";
    public static final String HTTPS_SCHEME = "https";

    public static final String NOT_TRACKING_YET = "Вы не отслеживаете переданный ресурс.";
    public static final String UNTRACKED = "Ресурс больше не отслеживается.";
    public static final String WRONG_URL_FORMAT = "Вы передали ссылку в неверном формате. Отмена команды.";
    public static final String NOT_HTTP_RESOURCE =
        "Вы передали ссылку не на веб-сайт, а на другой источник. Отмена команды.";
    public static final String CURRENTLY_INCAPABLE =
        "Пока что бот не умеет отслеживать подобные ресурсы. Отмена команды.";
    public static final String ALREADY_TRACKING = "Вы уже отслеживаете этот ресурс.";
    public static final String NOW_TRACKING = "Ресурс успешно добавлен!";

    private LinkHandlersConstants() {
    }
}
