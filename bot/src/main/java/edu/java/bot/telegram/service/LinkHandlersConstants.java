package edu.java.bot.telegram.service;

public class LinkHandlersConstants {
    public static final String HTTP_SCHEME = "http";
    public static final String HTTPS_SCHEME = "https";
    public static final String UNTRACKED = "Ресурс %s больше не отслеживается.";
    public static final String WRONG_URL_FORMAT = "Вы передали ссылку в неверном формате. Отмена команды.";
    public static final String NOT_HTTP_RESOURCE =
        "Вы передали ссылку не на веб-сайт, а на другой источник. Отмена команды.";
    public static final String CURRENTLY_INCAPABLE =
        "Пока что бот не умеет отслеживать подобные ресурсы. Отмена команды.";
    public static final String NOW_TRACKING = "Ресурс %s успешно добавлен!";

    public static final String REQUEST_ERROR = "Произошла ошибка при соединение с сервером! Повторите позже";
    public static final String UNKNOWN_RESPONSE_ERROR = "Неизвестная ошибка";

    private LinkHandlersConstants() {
    }
}
