package depviz;

import java.util.List;

/**
 * Сообщает, что параметры командной строки заданы неверно.
 */
public class ConfigException extends Exception {
    private static final long serialVersionUID = 1L;

    private final transient List<String> errors;

    /**
     * Создаёт исключение со списком найденных ошибок.
     *
     * @param errors сообщения об ошибках
     */
    public ConfigException(List<String> errors) {
        super(String.join(System.lineSeparator(), errors));
        this.errors = List.copyOf(errors);
    }

    /**
     * Возвращает все найденные ошибки.
     *
     * @return неизменяемый список сообщений
     */
    public List<String> errors() {
        return errors;
    }
}
