/*
 * Copyright (c) 2005-2020 Esito AS. All rights reserved.
 */
package no.esito.anonymizer.datasource;

import no.esito.anonymizer.*;
import no.esito.anonymizer.column.*;
import no.esito.anonymizer.core.AbstractUpdateTask;
import no.esito.anonymizer.core.ArrayKey;
import no.esito.anonymizer.core.PropagateUpdate;

import java.io.IOException;
import java.sql.Date;
import java.sql.*;
import java.util.*;

import static no.esito.anonymizer.ConfigUtil.getConfig;


@SuppressWarnings("resource")
public class SqlDataSource implements IDataSource {

    Connection connection;

    public SqlDataSource(Properties config) {
        super();
        SqlConnect sqlConnect = new SqlConnect();
        try {
            connection = sqlConnect.makeConnection(null, config);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    private boolean autocommit;

    {
        try {
            autocommit = getConfig().getProperty("connection.autocommit").equals("true");
        } catch (Exception e) {
            autocommit= false;
        }
    }

    ;

    /**
     * Get a JDBC connection based on the configuration.
     *
     * @return connection based on the configuration
     * @throws Throwable SQL error
     */
    Connection getConnection() {
        return connection;
    }

    /**
     * Is it using auto-commit mode?<br>
     * Please note that many operations involving setting unique keys may not work in autocommit mode.
     *
     * @return true if autocommit
     */
    boolean isAutoCommit() {
        return autocommit;
    }

    /**
     * Sets the AutoCommit mode.<br>
     * AutoCommit (true) - commits for every insert/update and is easier to debug <br>
     * Commit (false) - uses batch inserts/updates which is generally faster
     *
     * @param autocommit true if autocommit should be used
     */
    void setAutoCommit(boolean autocommit) {
        this.autocommit = autocommit;
    }


    // *********************************************
    // ** WORKTASK **
    // *********************************************


    @Override
    public List<String[]> dsReadRows(String table, List<String> columns, String where) throws Exception {
        ArrayList<String[]> rows = new ArrayList<>();
        String cWhere = where != null ? " WHERE " + where : "";
        String cmd = "SELECT " + sqlColumnsWrap(columns) + " FROM " + ConfigUtil.schemaPrefix(table) + cWhere;
        ResultSet rs = sqlExecuteQuery(cmd);
        if (rs != null) {
            while (rs.next()) {
                String[] row = new String[columns.size()];
                rows.add(row);
                for (int i = 0; i < columns.size(); i++) {
                    row[i] = rs.getString(columns.get(i));
                }
            }
            rs.close();
        }
        return rows;
    }


    @Override
    public void dsUpdateRows(String table, List<IColumn> columns, IColumn[] index, Collection<String> updatecolumns, List<String[]> rows, String where) throws Exception {
        for (String[] row : rows) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < columns.size(); i++) {
                IColumn column = columns.get(i);
                String name = column.getName();
                if (updatecolumns.contains(name)) {
                    String val = row[i];
                    sb.append(sb.length() == 0 ? "SET " : ", ");
                    sb.append(wrap(name) + "= " + column.addQuotes(val));
                }
            }
            String cmd = "UPDATE " + ConfigUtil.schemaPrefix(table) + " " + sb.toString() + " WHERE " + where;
            dsExecuteUpdate(cmd);
        }
    }

    @Override
    public void dsUpdateRows(String table, IColumn[] columns, IColumn[] index, List<String> updatecolumns, List<String[]> rows, String[][] where)
            throws Throwable {
        StringBuilder ass1 = new StringBuilder();
        for (String name : updatecolumns) {
            ass1.append(ass1.length() > 0 ? ", " : " SET ");
            ass1.append(wrap(name));
            ass1.append(" = ?");
        }
        String cmd = "UPDATE " + ConfigUtil.schemaPrefix(table) + ass1.toString();
        for (int i = 0; i < index.length; i++) {
            cmd += (i == 0 ? " WHERE " : " AND ") + index[i].getName() + " = ?";
        }
        executeBatch(columns, updatecolumns, rows, cmd, index, where);
    }

    private ResultSet sqlExecuteQuery(String cmd) throws SQLException {
        try {
            Log.debug(cmd);
            return getConnection().createStatement().executeQuery(cmd);
        } catch (SQLException e) {
            e.setNextException(new SQLException("Statement: " + cmd));
            throw e;
        }
    }

    @Override
    public void dsUpdateRowsNoIndex(String table, IColumn[] columns, List<String> updatecolumns,
                                    List<String[]> rows) throws Exception {
        StringBuilder ass1 = new StringBuilder();
        for (String name : updatecolumns) {
            ass1.append(ass1.length() > 0 ? ", " : " SET ");
            ass1.append(wrap(name));
            ass1.append(" = ?");
        }
        String cmd = "SELECT " + sqlColumnsWrap(updatecolumns) + " FROM " + ConfigUtil.schemaPrefix(table);
        Log.debug(cmd);
        ResultSet stmt = getConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE)
                .executeQuery(cmd);
        int irow = 0;
        if (stmt != null) {
            while (stmt.next()) {
                String[] row = rows.get(irow++);
                for (IColumn type : columns) {
                    int icol = updatecolumns.indexOf(type.getName());
                    if (icol >= 0) {
                        String val = row[icol];
                        int n = icol + 1;
                        if (type instanceof TextColumn) {
                            if (val == null)
                                stmt.updateNull(n);
                            else
                                stmt.updateString(n, val);
                        } else if (type instanceof NumberColumn) {
                            if (val == null)
                                stmt.updateNull(n);
                            else
                                stmt.updateDouble(n, Double.parseDouble(val));
                        } else if (type instanceof BooleanColumn) {
                            if (val == null)
                                stmt.updateNull(n);
                            else
                                stmt.updateBoolean(n, "1".equals(val));
                        } else if (type instanceof DateColumn) {
                            if (val == null)
                                stmt.updateNull(n);
                            else
                                stmt.updateDate(n, Date.valueOf(val));
                        } else if (type instanceof DateTimeColumn) {
                            if (val == null)
                                stmt.updateNull(n);
                            else
                                stmt.updateTimestamp(n, Timestamp.valueOf(val));
                        } else if (type instanceof TimeColumn) {
                            if (val == null)
                                stmt.updateNull(n);
                            else
                                stmt.updateTime(n, Time.valueOf(val));
                        }
                    }
                }
                stmt.updateRow();
            }
            stmt.close();
        }
    }

    @Override
    public void dsInsertRows(String table, IColumn[] columns, List<String> updatecolumns, List<String[]> rows) throws Throwable {
        StringBuilder sb1 = new StringBuilder();
        StringBuilder sb2 = new StringBuilder();
        for (String name : updatecolumns) {
            sb1.append(sb1.length() > 0 ? "," : "");
            sb1.append(wrap(name));
            sb2.append(sb2.length() > 0 ? "," : "");
            sb2.append("?");
        }
        String cmd = "INSERT INTO " + ConfigUtil.schemaPrefix(table) + " (" + sb1.toString() + ") VALUES (" + sb2.toString()
                + ")";
        executeBatch(columns, updatecolumns, rows, cmd, new IColumn[0], null);
    }

    /**
     * Executing SQL statement using batch mode.
     *
     * @param columns Columns
     * @param updcols list of names of colums to be updated
     * @param rows    of data
     * @param cmd     SQL command line
     * @param index   array of index columns
     * @param where   string clause
     * @throws Throwable
     */
    protected void executeBatch(IColumn[] columns, List<String> updcols, List<String[]> rows,
                                String cmd, IColumn[] index, String[][] where) throws Throwable {
        Log.debug(cmd);
        List<String> colnames = IColumn.listNames(columns);
        try {
            PreparedStatement stmt = getConnection().prepareStatement(cmd);
            int nupd = updcols.size();
            for (int x = 0; x < rows.size(); x++) {
                StringBuilder sb = new StringBuilder();
                String[] row = rows.get(x);
                for (int i = 0; i < nupd; i++) {
                    int n = colnames.indexOf(updcols.get(i));
                    if (n > -1) {
                        String val = row[n];
                        assignCell(stmt, i, columns[n], val);
                        sb.append(i == 0 ? "[" : ",");
                        sb.append(columns[n].addQuotes(val));
                    }
                }
                for (int i = 0; i < index.length; i++) {
                    String val = where[i][x];
                    assignCell(stmt, nupd + i, index[i], val);
                    sb.append(",");
                    sb.append(index[i].addQuotes(val));
                }
                sb.append("]");
                Log.debug(sb.toString());
                stmt.addBatch();
            }
            stmt.executeBatch();
            stmt.close();
        } catch (SQLException e) {
            e.setNextException(new SQLException("Statement: " + cmd));
            throw e;
        }
    }

    /**
     * Assigning cell for SQL Batch operation.
     *
     * @param stmt prepared statement
     * @param n    cell number
     * @param type column type
     * @param val  value to assign
     * @throws SQLException if there's error in JDBC operation
     */
    protected static void assignCell(PreparedStatement stmt, int n, IColumn type, String val) throws SQLException {
        n++;
        if (type instanceof TextColumn) {
            if (val == null)
                stmt.setNull(n, Types.VARCHAR);
            else
                stmt.setString(n, val);
        } else if (type instanceof NumberColumn) {
            if (val == null)
                stmt.setNull(n, Types.NUMERIC);
            else
                stmt.setDouble(n, Double.parseDouble(val));
        } else if (type instanceof BooleanColumn) {
            if (val == null)
                stmt.setNull(n, Types.BOOLEAN);
            else
                stmt.setBoolean(n, "1".equals(val));
        } else if (type instanceof DateColumn) {
            if (val == null)
                stmt.setNull(n, Types.DATE);
            else
                stmt.setDate(n, Date.valueOf(val));
        } else if (type instanceof DateTimeColumn) {
            if (val == null)
                stmt.setNull(n, Types.TIMESTAMP);
            else
                stmt.setTimestamp(n, Timestamp.valueOf(val));
        } else if (type instanceof TimeColumn) {
            if (val == null)
                stmt.setNull(n, Types.TIME);
            else
                stmt.setTime(n, Time.valueOf(val));
        }
    }

    @Override
    public void dsExecuteUpdate(String cmd) throws Exception {
        Log.debug(cmd);
        getConnection().createStatement().executeUpdate(cmd);
    }

    @Override
    public int dsCountRows(String table) throws Throwable {
        int count = 0;
        try {
            String cmd = "SELECT COUNT(*) FROM " + ConfigUtil.schemaPrefix(table);
            ResultSet rs = sqlExecuteQuery(cmd);
            if (rs != null) {
                while (rs.next()) {
                    count = rs.getInt(1);
                }
                rs.close();
            }
        } catch (SQLException e) {
            Log.error(e);
        }
        return count;
    }


    protected static String sqlColumnsWrap(Collection<String> columns) {
        StringBuilder cols = new StringBuilder();
        for (String name : columns) {
            if (cols.length() > 0)
                cols.append(", ");
            cols.append(wrap(name));
        }
        return cols.toString();
    }


    /**
     * Wraps/qutoes in " " (or another symbol defined in properties).
     *
     * @param name schema/table/column name to be wrapped
     * @return wrapped version
     */
    public static String wrap(String name) {
        try {
            return ConfigUtil.getSQLWrapper() + name + ConfigUtil.getSQLWrapper();
        } catch (IOException e) {
            Log.error(e);
        }
        return "\"" + name + "\"";
    }

    // *********************************************
    // ** UPDATE **
    // *********************************************

    @Override
    public void dsUpdateRowsWithKey(String table, IColumn[] types, IColumn[] index, List<String> colnames, Collection<String> updatecolumns,
                                    List<String[]> rows, IAnonymization keys[], ArrayList<ArrayKey> keysbefore, String[][] keyswhere) throws Throwable {
        String[] indexcolumnnames = new String[index.length];
        int[] indexcolumnnums = new int[index.length];

        for (int j = 0; j < index.length; j++) {
            indexcolumnnames[j] = index[j].getName();
            indexcolumnnums[j] = colnames.indexOf(indexcolumnnames[j]);
        }
        ArrayList<ArrayKey> indexbefore = new ArrayList<>(keysbefore);
        ArrayList<ArrayKey> indexafter = new ArrayList<>(rows.size());
        for (String[] row : rows) {
            String[] key = new String[index.length];
            for (int j = 0; j < index.length; j++) {
                key[j] = row[indexcolumnnums[j]];
            }
            indexafter.add(new ArrayKey(key));
        }

        HashSet<ArrayKey> todobefore = new HashSet<>(indexbefore);
        HashSet<ArrayKey> todoafter = new HashSet<>(indexafter);

        // Upodate all where the index is unchanged
        for (int i = 0; i < indexafter.size(); i++) {
            ArrayKey newkey = indexafter.get(i);
            ArrayKey oldkey = indexbefore.get(i);
            if (newkey.equals(oldkey)) {
                List<String[]> row = new ArrayList<>(1);
                row.add(rows.get(i));
                String[][] where = new String[index.length][];
                for (int j = 0; j < index.length; j++) {
                    IColumn col = index[j];
                    int colnum = colnames.indexOf(col.getName());
                    String[] wc = new String[1];
                    where[j] = wc;
                    wc[0] = row.get(0)[colnum];
                }
                dsUpdateRows(table, types, index, new ArrayList<>(updatecolumns), row, where);
                todobefore.remove(oldkey);
                todoafter.remove(newkey);
            }
        }
        // Update the rest by doing insert and delete
        while (!todoafter.isEmpty()) {
            List<ArrayKey> todo = new ArrayList<>();
            ArrayKey newkey = todoafter.iterator().next();
            todo.add(0, newkey);
            boolean needTemp = false;
            while (todobefore.contains(newkey)) {
                int i = indexbefore.indexOf(newkey);
                newkey = indexafter.get(i);
                if (todo.contains(newkey)) {
                    needTemp = true;
                    break;
                }
                todo.add(0, newkey);
            }
            ArrayKey startkey = todo.get(0);
            if (needTemp) {
                int i = indexafter.indexOf(startkey);
                String[] row = rows.get(i);
                String[] keybefore = indexbefore.get(i).key;
                String[] keyafter = new String[index.length];
                boolean exists = true;
                for (int j = 0; j < index.length; j++) {
                    IAnonymization key = AbstractUpdateTask.getAno(keys, index[j]);
                    if (key != null) {
                        keyafter[j] = key.getTempId();
                        if (keyafter[j] != keybefore[j]) {
                            exists = false;
                        }
                        row[indexcolumnnums[j]] = keyafter[j];
                    } else {
                        keyafter[j] = row[indexcolumnnums[j]];
                    }
                }
                if (exists) {
                    throw new RuntimeException("DataSet already contains temp value (" + keyafter + ")");
                }
                moveRow(colnames, keys, index, table, types, indexcolumnnames, row, keybefore, keyafter, keyswhere);
                todobefore.remove(indexbefore.get(i));
                todoafter.remove(startkey);
            }
            for (int j = needTemp ? 1 : 0; j < todo.size(); j++) {
                // Todo gives list of those after values not currently occupied
                ArrayKey key = todo.get(j);
                int i = indexafter.indexOf(key);
                String[] row = rows.get(i);
                String[] keybefore = indexbefore.get(i).key;
                String[] keyafter = indexafter.get(i).key;
                moveRow(colnames, keys, index, table, types, indexcolumnnames, row, keybefore, keyafter, keyswhere);
                todobefore.remove(indexbefore.get(i));
                todoafter.remove(key);
            }
            if (needTemp) {
                int i = indexafter.indexOf(startkey);
                String[] keybefore = new String[index.length];
                String[] keyafter = startkey.key;
                String[] row = rows.get(i);
                for (int j = 0; j < index.length; j++) {
                    row[indexcolumnnums[j]] = keyafter[j];
                    IAnonymization key = AbstractUpdateTask.getAno(keys, index[j]);
                    if (key != null) {
                        keybefore[j] = key.getTempId();
                    } else {
                        keybefore[j] = row[indexcolumnnums[j]];
                    }
                }
                moveRow(colnames, keys, index, table, types, indexcolumnnames, row, keybefore, keyafter, keyswhere);
            }
        }
    }

    private void moveRow(List<String> readcolumns, IAnonymization keys[], IColumn[] index, String table,
                         IColumn[] types, String[] keycolumnnames, String[] row, String[] before, String[] after, String[][] keyswhere) throws Exception {
        String[] befores2 = new String[keycolumnnames.length];
        String[] afters2 = new String[keycolumnnames.length];
        for (int j = 0; j < keycolumnnames.length; j++) {
            IColumn lookup = IColumn.lookup(keycolumnnames[j], types);
            befores2[j] = lookup.addQuotes(before[j]);
            afters2[j] = lookup.addQuotes(after[j]);
        }
        dsExecuteUpdate(
                "INSERT INTO " + ConfigUtil.schemaPrefix(table) + sqlAssignInsert(types, readcolumns, readcolumns, row));
        for (PropagateUpdate uc : keys[0].getPropagatedUpdates()) {
            updateChild(uc, befores2[0], afters2[0]);
        }
        String deletecmd = "DELETE FROM " + ConfigUtil.schemaPrefix(table);

        for (int i = 0; i < index.length; i++) {
            deletecmd += (i == 0 ? " WHERE " : " AND ") + index[i].getName() + (keyswhere[i] == null ? " IS NULL" : (" = " + index[i].addQuotes(before[i])));
        }
        dsExecuteUpdate(deletecmd);
    }

    @Override
    public void dsUpdateRowsWithKeyAuto(String table, IColumn[] types, List<String> colnames, Collection<String> updatecolumns,
                                        List<String[]> rows, IAnonymization anonymization, ArrayList<ArrayKey> keysbefore) throws Exception {
        String keycolumnname = anonymization.getColumn().getName();
        int keycolnum = colnames.indexOf(keycolumnname);
        ArrayList<String> keysafter = new ArrayList<>();
        for (String[] row : rows) {
            keysafter.add(row[keycolnum]);
        }
        for (int i = 0; i < rows.size(); i++) {
            String[] row = rows.get(i);
            String before = keysbefore.get(i).key[0];
            ArrayList<String> minuskey = new ArrayList<>(colnames);
            minuskey.remove(keycolumnname);
            String cmd = "INSERT INTO " + ConfigUtil.schemaPrefix(table) + sqlAssignInsert(types, colnames, minuskey, row);
            Log.debug(cmd);
            String autores = "";
            PreparedStatement stmt = getConnection().prepareStatement(cmd, new String[]{keycolumnname});
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            while (rs.next()) {
                autores = rs.getString(1);
            }
            rs.close();
            stmt.close();
            String before2 = IColumn.lookup(keycolumnname, types).addQuotes(before);
            String after2 = IColumn.lookup(keycolumnname, types).addQuotes(autores);
            for (PropagateUpdate uc : anonymization.getPropagatedUpdates()) {
                updateChild(uc, before2, after2);
            }
            dsExecuteUpdate(
                    "DELETE FROM " + ConfigUtil.schemaPrefix(table) + " WHERE " + wrap(keycolumnname) + (before2 == null ? " IS NULL" : "=" + before2));
        }
    }

    private static String sqlAssignInsert(IColumn[] types, List<String> readcolumns, Collection<String> updatecolumns, String[] row) {
        StringBuilder assign = new StringBuilder();
        assign.append(" (" + sqlColumnsWrap(updatecolumns) + ") VALUES (");
        boolean start = true;
        for (int i = 0; i < readcolumns.size(); i++) {
            String name = readcolumns.get(i);
            if (updatecolumns.contains(name)) {
                String val = row[i];
                assign.append(start ? "" : ", ");
                start = false;
                assign.append(types[i].addQuotes(val));
            }
        }
        assign.append(")");
        return assign.toString();
    }

    private void updateChild(PropagateUpdate uc, String before, String after) throws Exception {
        String cmd = "UPDATE " + ConfigUtil.schemaPrefix(uc.table) + " " + " SET " + wrap(uc.fkColumn) + "=" + after + " WHERE "
                + wrap(uc.fkColumn) + (before == null ? " IS NULL" : "=" + before);
        dsExecuteUpdate(cmd);
    }

    @Override
    public void dsUndo() throws Exception {
        getConnection().rollback();
    }

    @Override
    public void dsCommit(String tname) throws Exception {
        if (!isAutoCommit()) {
            try {
                getConnection().commit();
            } catch (SQLException e) {
                e.setNextException(new SQLException("Commit task: " + tname));
                throw e;
            }
        }
    }

    @Override
    public String dsPrintStatus() {
        return isAutoCommit() ? "AutoCommit" : "Task commit";
    }

    @Override
    public void dsInit() throws Exception {
        getConnection().setAutoCommit(isAutoCommit());
    }

    @Override
    public String dsPrintConnectionInfo() {
        try {
            DatabaseMetaData dbmd = getConnection().getMetaData();
            return String.format("DBMS: %s - %s\nDriver: %s - %s %d.%d\nURL: %s", dbmd.getDatabaseProductName(),
                    dbmd.getDatabaseProductVersion(), dbmd.getDriverName(), dbmd.getDriverVersion(),
                    dbmd.getDriverMajorVersion(), dbmd.getDriverMinorVersion(), dbmd.getURL());
        } catch (Exception e) {
            Log.error(e);
            return "Error:" + e.getMessage();
        }
    }

}
