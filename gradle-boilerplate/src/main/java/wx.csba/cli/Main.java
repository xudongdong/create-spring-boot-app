package wx.csba.cli;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.sql.*;
import java.util.*;

public class Main {


    public static void main(String args[]) {

        List<String> fields = Arrays.asList(
                "CASESERIAL",
                "CREATEDATE",
                "REQUESTTITLE",
                "DESCRIPTION",
                "REQUESTNOTE",
                "SUQIUADDRESS",
                "DEALUNITGUID",
                "DEALUNIT"
        );

        String tableName = "CASEINFO_2015BAK";

        String fieldsStr = fields.toString().replaceAll("(\\[|\\])", "");

        String SQL = "SELECT\n" +
                fieldsStr +
                " FROM " +
                tableName;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");

            Connection con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@153.3.251.190:1521:orcl", "system", "123");

            Statement stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery(SQL);

            Writer writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream("dist.csv"), "utf-8"));

            int flag = 0;

            while (rs.next()) {

                String str = "";

                //遍历获取所有的列
                for (String field : fields) {
                    str += " ## " + (rs.getString(field) != null ? rs.getString(field) : "null");
                }

                //移除首个 ##
                str = str.replaceFirst("##", "");

                //移除所有的换行
                str = str.replaceAll("\\r\\n|\\r|\\n", " ");

                //末尾添加换行
                str += "\n";
                writer.write(str);
                System.out.println(flag);
                flag++;
            }

            writer.flush();
            writer.close();
            con.close();

        } catch (Exception e) {
            System.out.println(e);
        }

    }
}
