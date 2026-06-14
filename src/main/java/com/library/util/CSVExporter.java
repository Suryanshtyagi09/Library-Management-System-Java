package com.library.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Exports report data to CSV files.
 */
public final class CSVExporter {
    private static final Logger logger = LogManager.getLogger(CSVExporter.class);

    private CSVExporter() {
    }

    public static void export(String filePath, String[] header, List<String[]> rows) {
        try (FileWriter writer = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            writeLine(writer, header);
            for (String[] row : rows) {
                writeLine(writer, row);
            }
            logger.info("Report exported to {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to export CSV report", e);
            throw new RuntimeException("Could not export CSV report", e);
        }
    }

    private static void writeLine(FileWriter writer, String[] values) throws IOException {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            line.append(escape(values[i]));
            if (i < values.length - 1) {
                line.append(',');
            }
        }
        line.append(System.lineSeparator());
        writer.write(line.toString());
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\n") || escaped.contains("\r")) {
            return '"' + escaped + '"';
        }
        return escaped;
    }
}
