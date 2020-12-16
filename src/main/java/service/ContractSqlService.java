package service;

import bean.Contract;
import config.Constant;
import dao.ContractDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;

public class ContractSqlService {
    private static Logger logger = LoggerFactory.getLogger(ContractSqlService.class);

    private ContractDao dao = new ContractDao();

    public List<Contract> queryAll() throws SQLException, ParseException {
        String sql = Constant.QUERY_ALL_SQL;
        List<Contract> contracts = dao.queryAll(sql);
        return contracts;
    }
}
