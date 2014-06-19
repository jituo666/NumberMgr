import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class CsvConverter {
    private static final String CSV_SEP = "\t";
    private static final int FIELD_COUNT = 3;
    private static final int NUMBER = 0;
    private static final int SORT = 1;
    private static final int NAME = 2;

    private static final String DB_TABLE_NAME = "numbers";

    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            System.out.println("arg#" + i + ": " + args[i]);
        }
        String srcFilepath = args[0];
        String destFilepath = args[1];
        int dbVersion = Integer.parseInt(args[2]);
        createDbShema(srcFilepath, destFilepath, dbVersion);
    }

    private static void createDbShema(String srcFilepath, String destFilepath, int dbVersion) {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(srcFilepath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis, "UTF-8"));

            StringBuilder sb = new StringBuilder();
            sb.append("PRAGMA user_version = ").append(dbVersion).append(";\n");

            sb.append("DROP TABLE IF EXISTS ").append(DB_TABLE_NAME).append(";");
            sb.append("CREATE TABLE ").append(DB_TABLE_NAME);
            sb.append(" (_id INTEGER primary key autoincrement");
            sb.append(",number TEXT");
            sb.append(",sort TEXT");
            sb.append(",name TEXT);");

            String line = null;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(CSV_SEP);
                if (fields.length != FIELD_COUNT) {
                    System.err.print("Bad line: " + line);
                    continue;
                }
                sb.append("INSERT INTO ").append(DB_TABLE_NAME);
                sb.append(" (number,sort,name)");
                sb.append(" VALUES(");
                sb.append("'").append(fields[NUMBER]).append("'");
                sb.append(",'").append(fields[SORT]).append("'");
                sb.append(",'").append(fields[NAME]).append("'");
                sb.append(");\n");
            }

            fos = new FileOutputStream(destFilepath);
            fos.write(sb.toString().getBytes("UTF-8"));
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try { fis.close(); } catch (IOException e) {}
            }
            if (fos != null) {
                try { fos.close(); } catch (IOException e) {}
            }
        }
    }

    private static String processQuotes(String value) {
        return value.replace("'", "''");
    }

    private static String correctFolderName(String folderName) {
        if (folderName.charAt(0) != '/') {
            return "/" + folderName;
        }
        return folderName;
    }
}
