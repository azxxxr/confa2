package depviz;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ArgumentParser {
    private static final String TRUE_VALUE = "true";
    private static final String OPTION_PREFIX = "-";
    private static final char VALUE_SEPARATOR = '=';
    private static final int NOT_FOUND = -1;

    private final String[] args;
    private final Map<Option, String> values = new EnumMap<>(Option.class);
    private final List<String> errors = new ArrayList<>();
    private int position;

    private ArgumentParser(String[] args) {
        this.args = args.clone();
    }

    public static Map<Option, String> parse(String[] args) throws ConfigException {
        ArgumentParser parser = new ArgumentParser(args);
        parser.parseAll();
        if (!parser.errors.isEmpty()) {
            throw new ConfigException(parser.errors);
        }
        return parser.values;
    }

    private void parseAll() {
        while (position < args.length) {
            parseArgument(args[position]);
            position++;
        }
    }

    private void parseArgument(String argument) {
        if (!argument.startsWith(OPTION_PREFIX)) {
            errors.add("Лишний аргумент: '" + argument + "'");
            return;
        }
        int separator = argument.indexOf(VALUE_SEPARATOR);
        String name = separator == NOT_FOUND ? argument : argument.substring(0, separator);
        String inlineValue = separator == NOT_FOUND ? null : argument.substring(separator + 1);
        Optional<Option> option = Option.byName(name);
        if (option.isEmpty()) {
            errors.add("Неизвестная опция: '" + name + "'");
            return;
        }
        store(option.get(), inlineValue);
    }

    private void store(Option option, String inlineValue) {
        String value = inlineValue != null ? inlineValue : nextValue(option);
        if (value == null) {
            errors.add("Для опции " + option.longName() + " не указано значение");
        } else if (values.containsKey(option)) {
            errors.add("Опция " + option.longName() + " указана несколько раз");
        } else {
            values.put(option, value);
        }
    }

    private String nextValue(Option option) {
        if (!option.isValueRequired()) {
            return TRUE_VALUE;
        }
        int next = position + 1;
        if (next >= args.length || args[next].startsWith(OPTION_PREFIX)) {
            return null;
        }
        position = next;
        return args[next];
    }
}
