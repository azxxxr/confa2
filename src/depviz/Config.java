package depviz;

import java.util.List;

/**
 * Проверенные параметры, настраиваемые пользователем.
 *
 * @param packageName имя анализируемого пакета
 * @param repository URL репозитория или путь к файлу тестового репозитория
 * @param testMode включён ли режим работы с тестовым репозиторием
 * @param outputFile имя файла с изображением графа
 * @param asciiTree включён ли вывод зависимостей в формате ASCII-дерева
 */
public record Config(
        String packageName,
        String repository,
        boolean testMode,
        String outputFile,
        boolean asciiTree) {

    /**
     * Представляет параметры в формате ключ-значение.
     *
     * @return строки вида {@code ключ=значение}
     */
    public List<String> toKeyValueLines() {
        return List.of(
                "package=" + packageName,
                "repository=" + repository,
                "test_mode=" + testMode,
                "output=" + outputFile,
                "ascii_tree=" + asciiTree);
    }
}
