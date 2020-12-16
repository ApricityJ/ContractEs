package dao;

import bean.Contract;
import config.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class ContractDao {

    private static Logger logger = LoggerFactory.getLogger(ContractDao.class);

    private static final String URL = Constant.DATABASE_URL;
    private static final String NAME = Constant.DATABASE_USER;
    private static final String PASSWORD = Constant.DATABASE_PASSWORD;

    private Connection conn;
    private ResultSet rs;


    private Connection toConnection() throws SQLException {
        if (isNullOrClosed()) {
            conn = DriverManager.getConnection(URL, NAME, PASSWORD);
        }
        return conn;
    }

    private boolean isNullOrClosed() throws SQLException {
        return null == conn || conn.isClosed();
    }

    public List<Contract> queryAll(String sql) throws SQLException, ParseException {
        logger.info("Query sql : {}", sql);
        try {
            rs = toConnection().createStatement().executeQuery(sql);
            List<Contract> results = new ArrayList<>();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Contract contract;
            while (rs.next()) {
                contract = new Contract();
                contract.setPactSerial(rs.getString("pact_serial"));
                contract.setPactCall(rs.getString("pact_call"));
                contract.setPactName(rs.getString("pact_name"));
                contract.setPactTime(df.format(rs.getTimestamp("pact_time")));
//                contract.setPactTime(df.parse(rs.getTimestamp("pact_time").toString()));
                contract.setPactDraftName(rs.getString("pact_draft_name"));
                contract.setPactDeptName(rs.getString("pact_dept_name"));
                contract.setPactParty(rs.getString("pact_party"));
                contract.setPactHlcCall(rs.getString("pact_hlc_call"));
                results.add(contract);
            }
            return results;
        } finally {
            close();
        }
    }

    private void close () throws SQLException {
        if (rs != null) {
            rs.close();
        }
        if (conn != null) {
            conn.close();
        }
    }
}
