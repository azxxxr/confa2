package depviz;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Map;

/**
 * Точка входа инструмента визуализации графа зависимостей.
 */
public final class Main {
    /** Код завершения при успешном запуске. */
    public static final int EXIT_OK = 0;
    /** Код завершения при ошибке в параметрах. */
    public static final int EXIT_USAGE = 2;

    private static final String USAGE = "Использование: depviz -p <пакет> -r <репозиторий> [-t] [-o <файл.svg>] [-a]";

    private Main() {
    }

    /**
     * Запускает приложение и завершает процесс с кодом результата.
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        System.exit(run(args, System.out, System.err));
    }

    /**
     * Выполняет приложение с заданными потоками вывода.
     *
     * @param args аргументы командной строки
     * @param out поток для результата
     * @param err поток для сообщений об ошибках
     * @return код завершения
     */
    public static int run(String[] args, PrintStream out, PrintStream err) {
        try {
            Map<Option, String> values = ArgumentParser.parse(args);
            if (values.containsKey(Option.HELP)) {
                printHelp(out);
            } else {
                ConfigValidator.validate(values).toKeyValueLines().forEach(out::println);
            }
            return EXIT_OK;
        } catch (ConfigException e) {
            e.errors().forEach(error -> err.println("Ошибка: " + error));
            err.println(USAGE);
            return EXIT_USAGE;
        }
    }

    private static void printHelp(PrintStream out) {
        out.println(USAGE);
        out.println("Опции:");
        Arrays.stream(Option.values()).map(Option::helpLine).forEach(out::println);
        out.println("Значение можно указать через пробел или через '=': -p curl, --package=curl.");
        out.println("Флагам можно явно задать значение: --test-mode=true, --ascii-tree=false.");
        out.println("Файл графа по умолчанию: " + ConfigValidator.DEFAULT_OUTPUT);
    }
}
