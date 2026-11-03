package depviz;

import java.util.List;

public class ConfigException extends Exception {
    private static final long serialVersionUID = 1L;

    private final transient List<String> errors;

    public ConfigException(List<String> errors) {
        super(String.join(System.lineSeparator(), errors));
        this.errors = List.copyOf(errors);
    }

    public List<String> errors() {
        return errors;
    }
}
