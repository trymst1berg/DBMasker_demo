/*
 * Copyright (c) 2005-2020 Esito AS. All rights reserved.
 */
package no.esito.anonymizer.datasource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

import no.esito.anonymizer.IAnonymization;
import no.esito.anonymizer.IColumn;
import no.esito.anonymizer.IDataSource;
import no.esito.anonymizer.Log;
import no.esito.anonymizer.core.ArrayKey;

public class CsvDataSource implements IDataSource {

    public CsvDataSource(Properties props) {
        super();
        setFrom((String) props.get("csv.in"));
        setTo((String) props.get("csv.out"));
    }

    public CsvDataSource() {
        super();
    }

    @Override
    public List<String[]> dsReadRows(String table, List<String> columns, String where) throws Exception {
        return csvRead(table);
    }

    @Override
    public void dsUpdateRows(String table, List<IColumn> columns, IColumn[] index, Collection<String> updatecolumns, List<String[]> rows,
                             String where) throws Exception {
        csvUpdate(table, rows);
    }

    @Override
    public void dsUpdateRows(String table, IColumn[] columns, IColumn[] index, List<String> updatecolumns, List<String[]> rows,
                             String[][] where) throws Throwable {
        csvUpdate(table, rows);
    }

    @Override
    public void dsUpdateRowsNoIndex(String table, IColumn[] columns, List<String> updatecolumns, List<String[]> rows)
            throws Exception {
        csvUpdate(table, rows);
    }

    @Override
    public void dsUpdateRowsWithKey(String table, IColumn[] types, IColumn[] index, List<String> colnames,
                                    Collection<String> updatecolumns, List<String[]> rows, IAnonymization[] keys, ArrayList<ArrayKey> keysbefore,
                                    String[][] keyswhere) throws Throwable {
        csvUpdate(table, rows);
    }

    @Override
    public void dsUpdateRowsWithKeyAuto(String table, IColumn[] types, List<String> colnames,
                                        Collection<String> updatecolumns, List<String[]> rows, IAnonymization anonymization,
                                        ArrayList<ArrayKey> keysbefore) throws Exception {
        csvUpdate(table, rows);
    }

    @Override
    public int dsCountRows(String table) throws Throwable {
        return 0;
    }

    @Override
    public void dsInsertRows(String table, IColumn[] columns, List<String> updatecolumns, List<String[]> rows)
            throws Throwable {
        // TODO Auto-generated method stub
    }

    @Override
    public void dsUndo() throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void dsCommit(String tname) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void dsExecuteUpdate(String cmd) throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public String dsPrintStatus() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void dsInit() throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public String dsPrintConnectionInfo() {
        return "CSV file reader: from=" + from + ", to=" + to;
    }

    String from;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    String to;

    String header;

    private List<String[]> csvRead(String table) throws IOException, Exception {
        String input = Paths.get(from + table + ".csv").toString();
        String txt = readString(input);
        String[] lines = txt.replace("\r", "").split("\n");
        header = lines[0];
        List<String[]> rows = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            List<String> parse = parse(lines[i]);
            rows.add(parse.toArray(new String[parse.size()]));
        }
        return rows;
    }

    private static String readString(String filePath)
    {
        StringBuilder contentBuilder = new StringBuilder();

        try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
        {
            stream.forEach(s -> contentBuilder.append(s).append("\n"));
        }
        catch (IOException e)
        {
            Log.error(e);
        }

        return contentBuilder.toString();
    }

    private void csvUpdate(String table, List<String[]> rows) throws IOException {
        Path output = Paths.get(to + table + ".csv");
        Files.createDirectories(output.getParent());
        StringBuilder sb = new StringBuilder();
        sb.append(header);
        sb.append("\n");
        for (String[] row : rows) {
            sb.append(assemble(row));
            sb.append("\n");
        }
        Files.write(output, sb.toString().getBytes());
    }

    private static String assemble(String[] csv) {
        for (int i = 0; i < csv.length; i++) {
            String txt = csv[i];
            if (txt.indexOf("\"") >= 0)
                csv[i] = DOUBLE_QUOTES + txt + DOUBLE_QUOTES;
        }
        return String.join(";", csv);
    }

    public static final char DEFAULT_SEPARATOR = ';';

    public static final char DOUBLE_QUOTES = '"';

    public static final String NEW_LINE = "\n";

    private static List<String> parse(String line) throws Exception {
        boolean isMultiLine = false;
        String pendingField = "";
        char quoteChar = DOUBLE_QUOTES;
        char separator = DEFAULT_SEPARATOR;

        List<String> result = new ArrayList<>();

        boolean inQuotes = false;
        boolean isFieldWithEmbeddedDoubleQuotes = false;

        StringBuilder field = new StringBuilder();

        for (char c : line.toCharArray()) {

            if (c == DOUBLE_QUOTES) { // handle embedded double quotes ""
                if (isFieldWithEmbeddedDoubleQuotes) {

                    if (field.length() > 0) { // handle for empty field like "",""
                        field.append(DOUBLE_QUOTES);
                        isFieldWithEmbeddedDoubleQuotes = false;
                    }

                } else {
                    isFieldWithEmbeddedDoubleQuotes = true;
                }
            } else {
                isFieldWithEmbeddedDoubleQuotes = false;
            }

            if (isMultiLine) { // multiline, add pending from the previous field
                field.append(pendingField).append(NEW_LINE);
                pendingField = "";
                inQuotes = true;
                isMultiLine = false;
            }

            if (c == quoteChar) {
                inQuotes = !inQuotes;
            } else {
                if (c == separator && !inQuotes) { // if find separator and not in quotes, add field to the list
                    result.add(field.toString());
                    field.setLength(0); // empty the field and ready for the next
                } else {
                    field.append(c); // else append the char into a field
                }
            }

        }

        // line done, what to do next?
        if (inQuotes) {
            pendingField = field.toString(); // multiline
            isMultiLine = true;
        } else {
            result.add(field.toString()); // this is the last field
        }

        return result;

    }

}
