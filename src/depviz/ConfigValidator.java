package depviz;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Проверяет значения опций и собирает из них {@link Config}.
 *
 * <p>Сообщает сразу обо всех найденных ошибках, а не только о первой.
 */
public final class ConfigValidator {
    /** Имя файла с изображением графа по умолчанию. */
    public static final String DEFAULT_OUTPUT = "graph.svg";

    private static final Pattern APT_PACKAGE = Pattern.compile("[a-z0-9][a-z0-9+.-]+");
    private static final Pattern TEST_PACKAGE = Pattern.compile("[A-Z]+");
    private static final Set<String> URL_SCHEMES = Set.of("http", "https");
    private static final String OUTPUT_EXTENSION = ".svg";
    private static final String TRUE_VALUE = "true";
    private static final String FALSE_VALUE = "false";

    private final Map<Option, String> values;
    private final List<String> errors = new ArrayList<>();

    private ConfigValidator(Map<Option, String> values) {
        this.values = values;
    }

    /**
     * Проверяет значения опций.
     *
     * @param values значения, полученные от {@link ArgumentParser}
     * @return проверенные параметры
     * @throws ConfigException если хотя бы одно значение неверно
     */
    public static Config validate(Map<Option, String> values) throws ConfigException {
        ConfigValidator validator = new ConfigValidator(values);
        Config config = validator.buildConfig();
        if (!validator.errors.isEmpty()) {
            throw new ConfigException(validator.errors);
        }
        return config;
    }

    private Config buildConfig() {
        boolean testMode = readFlag(Option.TEST_MODE);
        boolean asciiTree = readFlag(Option.ASCII_TREE);
        String packageName = values.get(Option.PACKAGE);
        String repository = values.get(Option.REPOSITORY);
        String output = values.getOrDefault(Option.OUTPUT, DEFAULT_OUTPUT);
        checkPackage(packageName, testMode);
        checkRepository(repository, testMode);
        checkOutput(output);
        return new Config(packageName, repository, testMode, output, asciiTree);
    }

    private boolean readFlag(Option option) {
        String value = values.getOrDefault(option, FALSE_VALUE);
        if (!TRUE_VALUE.equals(value) && !FALSE_VALUE.equals(value)) {
            errors.add("Опция " + option.longName() + " принимает только true или false, получено: '" + value + "'");
        }
        return TRUE_VALUE.equals(value);
    }

    private void checkPackage(String name, boolean testMode) {
        if (name == null || name.isBlank()) {
            errors.add("Не задано имя пакета (" + Option.PACKAGE.longName() + ")");
        } else if (testMode && !TEST_PACKAGE.matcher(name).matches()) {
            errors.add("В тестовом режиме имя пакета состоит из больших латинских букв, получено: '" + name + "'");
        } else if (!testMode && !APT_PACKAGE.matcher(name).matches()) {
            errors.add("Недопустимое имя пакета Ubuntu: '" + name + "'");
        }
    }

    private void checkRepository(String repository, boolean testMode) {
        if (repository == null || repository.isBlank()) {
            errors.add("Не задан репозиторий (" + Option.REPOSITORY.longName() + ")");
        } else if (testMode) {
            checkRepositoryFile(repository);
        } else if (!isHttpUrl(repository)) {
            errors.add("Репозиторий должен быть URL-адресом http(s), получено: '" + repository
                    + "'. Для файла тестового репозитория укажите " + Option.TEST_MODE.longName());
        }
    }

    private void checkRepositoryFile(String repository) {
        try {
            Path path = Path.of(repository);
            if (!Files.isRegularFile(path)) {
                errors.add("Файл тестового репозитория не найден: '" + repository + "'");
            } else if (!Files.isReadable(path)) {
                errors.add("Нет доступа на чтение файла тестового репозитория: '" + repository + "'");
            }
        } catch (InvalidPathException e) {
            errors.add("Некорректный путь к тестовому репозиторию: '" + repository + "'");
        }
    }

    private static boolean isHttpUrl(String value) {
        try {
            URI uri = new URI(value);
            return uri.getScheme() != null
                    && URL_SCHEMES.contains(uri.getScheme().toLowerCase())
                    && uri.getHost() != null;
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private void checkOutput(String output) {
        try {
            Path path = Path.of(output);
            Path fileName = path.getFileName();
            if (fileName == null || !isSvgFileName(fileName.toString())) {
                errors.add("Имя файла графа должно иметь вид <имя>.svg, получено: '" + output + "'");
            } else if (Files.isDirectory(path)) {
                errors.add("Файл графа совпадает с существующим каталогом: '" + output + "'");
            } else if (path.getParent() != null && !Files.isDirectory(path.getParent())) {
                errors.add("Каталог для файла графа не существует: '" + path.getParent() + "'");
            }
        } catch (InvalidPathException e) {
            errors.add("Некорректное имя файла графа: '" + output + "'");
        }
    }

    private static boolean isSvgFileName(String fileName) {
        return fileName.endsWith(OUTPUT_EXTENSION) && !fileName.equals(OUTPUT_EXTENSION);
    }
}
