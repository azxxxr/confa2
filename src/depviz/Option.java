package depviz;

import java.util.Arrays;
import java.util.Optional;

/**
 * Опции командной строки, которые понимает приложение.
 */
public enum Option {
    /** Имя анализируемого пакета. */
    PACKAGE("--package", "-p", true, "имя анализируемого пакета"),
    /** URL репозитория или путь к файлу тестового репозитория. */
    REPOSITORY("--repository", "-r", true, "URL репозитория или путь к файлу тестового репозитория"),
    /** Режим работы с тестовым репозиторием. */
    TEST_MODE("--test-mode", "-t", false, "режим работы с тестовым репозиторием"),
    /** Имя сгенерированного файла с изображением графа. */
    OUTPUT("--output", "-o", true, "имя файла с изображением графа (.svg)"),
    /** Режим вывода зависимостей в формате ASCII-дерева. */
    ASCII_TREE("--ascii-tree", "-a", false, "вывод зависимостей в формате ASCII-дерева"),
    /** Вывод справки. */
    HELP("--help", "-h", false, "показать эту справку");

    private final String longName;
    private final String shortName;
    private final boolean valueRequired;
    private final String description;

    Option(String longName, String shortName, boolean valueRequired, String description) {
        this.longName = longName;
        this.shortName = shortName;
        this.valueRequired = valueRequired;
        this.description = description;
    }

    /**
     * Ищет опцию по длинному или короткому имени.
     *
     * @param name имя опции вместе с дефисами
     * @return найденная опция или пустой результат
     */
    public static Optional<Option> byName(String name) {
        return Arrays.stream(values())
                .filter(option -> option.longName.equals(name) || option.shortName.equals(name))
                .findFirst();
    }

    /**
     * Возвращает длинное имя опции.
     *
     * @return имя вида {@code --package}
     */
    public String longName() {
        return longName;
    }

    /**
     * Сообщает, обязано ли у опции быть значение.
     *
     * @return {@code true} для опций со значением, {@code false} для флагов
     */
    public boolean isValueRequired() {
        return valueRequired;
    }

    /**
     * Формирует строку справки об опции.
     *
     * @return имена и описание опции
     */
    public String helpLine() {
        String value = valueRequired ? " <значение>" : "";
        return String.format("  %s, %s%s%n      %s", shortName, longName, value, description);
    }
}
