package depviz;

import java.util.List;

public record Config(
        String packageName,
        String repository,
        boolean testMode,
        String outputFile,
        boolean asciiTree) {

    public List<String> toKeyValueLines() {
        return List.of(
                "package=" + packageName,
                "repository=" + repository,
                "test_mode=" + testMode,
                "output=" + outputFile,
                "ascii_tree=" + asciiTree);
    }
}
