package edu.java.scrapper.scheduler.linkupdater.resourceupdater;

public class ResourceUpdaterConstants {
    public static final String GITHUB_HOST = "github.com";
    public static final String GITHUB_UPDATE_RESPONSE =
        "В репозитории %s произошло обновление" + System.lineSeparator().repeat(2);
    public static final String GITHUB_NEW_PULL_REQUEST =
        "- Создали новый Pull Request. Заголовок - %s" + System.lineSeparator();
    public static final String GITHUB_PULL_REQUEST_CLOSED =
        "- Был закрыт один или несколько Pull Request'ов" + System.lineSeparator();
    public static final String GITHUB_NEW_COMMIT =
        "- Появился новый commit. Автор: %s, сообщение: %s" + System.lineSeparator();
    public static final String STACKOVERFLOW_HOST = "stackoverflow.com";
    public static final String STACKOVERFLOW_UPDATE_RESPONSE =
        "В вопросе %s произошло обновление" + System.lineSeparator();
    public static final String STACKOVERFLOW_NEW_ANSWER = "- Появился новый ответ. Автор: %s" + System.lineSeparator();
    public static final String STACKOVERFLOW_ANSWER_DELETED =
        "- Был удален один или несколько ответов" + System.lineSeparator();
    public static final String STACKOVERFLOW_NEW_COMMENT = "- Новый комментарий. Автор: %s" + System.lineSeparator();
    public static final String STACKOVERFLOW_COMMENT_DELETED =
        "- Был удален один или несколько комментариев" + System.lineSeparator();
    public static final String EMPTY_STRING = "";

    private ResourceUpdaterConstants() {
    }
}
