package depviz;

import java.util.Arrays;
import java.util.Optional;

public enum Option {
    PACKAGE("--package", "-p", true, "имя анализируемого пакета"),
    REPOSITORY("--repository", "-r", true, "URL репозитория или путь к файлу тестового репозитория"),
    TEST_MODE("--test-mode", "-t", false, "режим работы с тестовым репозиторием"),
    OUTPUT("--output", "-o", true, "имя файла с изображением графа (.svg)"),
    ASCII_TREE("--ascii-tree", "-a", false, "вывод зависимостей в формате ASCII-дерева"),
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

    public static Optional<Option> byName(String name) {
        return Arrays.stream(values())
                .filter(option -> option.longName.equals(name) || option.shortName.equals(name))
                .findFirst();
    }

    public String longName() {
        return longName;
    }

    public boolean isValueRequired() {
        return valueRequired;
    }

    public String helpLine() {
        String value = valueRequired ? " <значение>" : "";
        return String.format("  %s, %s%s%n      %s", shortName, longName, value, description);
    }
}
