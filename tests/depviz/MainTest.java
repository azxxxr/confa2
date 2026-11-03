package depviz;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Тесты обработки параметров командной строки. Внешние библиотеки не требуются.
 */
public final class MainTest {
    private static final String REPO_FILE = "tests/data/test_repo.txt";
    private static final String URL = "http://archive.ubuntu.com/ubuntu";

    private static int failures;

    private MainTest() {
    }

    /**
     * Запускает все тесты; при наличии упавших завершает процесс с кодом 1.
     *
     * @param args не используются
     */
    public static void main(String[] args) {
        testValidConfigurations();
        testBooleanFlags();
        testArgumentErrors();
        testPackageErrors();
        testRepositoryErrors();
        testOutputErrors();
        testAllErrorsReported();
        if (failures > 0) {
            System.out.println("Упавших тестов: " + failures);
            System.exit(1);
        }
        System.out.println("Все тесты пройдены");
    }

    private static void testValidConfigurations() {
        expectOutput("package=curl", "-p", "curl", "-r", URL);
        expectOutput("repository=" + URL, "-p", "curl", "-r", URL);
        expectOutput("test_mode=false", "-p", "curl", "-r", URL);
        expectOutput("output=graph.svg", "-p", "curl", "-r", URL);
        expectOutput("ascii_tree=false", "-p", "curl", "-r", URL);
        expectOutput("package=libc6", "--package=libc6", "--repository=" + URL);
        expectOutput("package=A", "-p", "A", "-r", REPO_FILE, "-t");
        expectOutput("output=my.svg", "-p", "curl", "-r", URL, "-o", "my.svg");
        expectOutput("Использование", "--help");
    }

    private static void testBooleanFlags() {
        expectOutput("test_mode=true", "-p", "A", "-r", REPO_FILE, "--test-mode");
        expectOutput("ascii_tree=true", "-p", "curl", "-r", URL, "-a");
        expectOutput("ascii_tree=false", "-p", "curl", "-r", URL, "--ascii-tree=false");
        expectError("принимает только true или false", "-p", "curl", "-r", URL, "--ascii-tree=maybe");
        expectError("принимает только true или false", "-p", "curl", "-r", URL, "--test-mode=1");
    }

    private static void testArgumentErrors() {
        expectError("Неизвестная опция", "-p", "curl", "-r", URL, "--unknown");
        expectError("Лишний аргумент", "-p", "curl", "-r", URL, "extra");
        expectError("не указано значение", "-p");
        expectError("не указано значение", "-p", "-r", URL);
        expectError("указана несколько раз", "-p", "curl", "-p", "wget", "-r", URL);
    }

    private static void testPackageErrors() {
        expectError("Не задано имя пакета", "-r", URL);
        expectError("Не задано имя пакета", "--package=", "-r", URL);
        expectError("Недопустимое имя пакета", "-p", "Curl!", "-r", URL);
        expectError("больших латинских букв", "-p", "curl", "-r", REPO_FILE, "-t");
    }

    private static void testRepositoryErrors() {
        expectError("Не задан репозиторий", "-p", "curl");
        expectError("URL-адресом", "-p", "curl", "-r", "not a url");
        expectError("URL-адресом", "-p", "curl", "-r", "ftp://example.com");
        expectError("URL-адресом", "-p", "curl", "-r", REPO_FILE);
        expectError("не найден", "-p", "A", "-r", "tests/data/missing.txt", "-t");
        expectError("не найден", "-p", "A", "-r", "tests/data", "-t");
    }

    private static void testOutputErrors() {
        expectError("<имя>.svg", "-p", "curl", "-r", URL, "-o", "graph.png");
        expectError("<имя>.svg", "-p", "curl", "-r", URL, "-o", ".svg");
        expectError("<имя>.svg", "-p", "curl", "-r", URL, "--output=");
        expectError("Каталог для файла графа", "-p", "curl", "-r", URL, "-o", "no/such/dir/g.svg");
    }

    private static void testAllErrorsReported() {
        String[] args = {"-p", "Bad!", "-r", "bad", "-o", "bad.png", "-a=bad"};
        expectError("Недопустимое имя пакета", args);
        expectError("URL-адресом", args);
        expectError("<имя>.svg", args);
        expectError("принимает только true или false", args);
    }

    private static void expectOutput(String expected, String... args) {
        check(Main.EXIT_OK, expected, args);
    }

    private static void expectError(String expected, String... args) {
        check(Main.EXIT_USAGE, expected, args);
    }

    private static void check(int expectedCode, String expected, String... args) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream stream = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        PrintStream out = expectedCode == Main.EXIT_OK ? stream : new PrintStream(new ByteArrayOutputStream());
        PrintStream err = expectedCode == Main.EXIT_OK ? new PrintStream(new ByteArrayOutputStream()) : stream;
        int code = Main.run(args, out, err);
        String text = buffer.toString(StandardCharsets.UTF_8);
        if (code != expectedCode || !text.contains(expected)) {
            failures++;
            System.out.println("ТЕСТ УПАЛ: " + String.join(" ", args));
            System.out.println("  код " + code + ", ожидался " + expectedCode + "; нет текста '" + expected + "'");
            System.out.println("  вывод: " + text);
        }
    }
}
