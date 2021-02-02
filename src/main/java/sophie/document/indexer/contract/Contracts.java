package sophie.document.indexer.contract;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sophie.document.indexer.config.Constant;
import sophie.document.indexer.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Contracts {

    private static Logger logger = LoggerFactory.getLogger(Contracts.class);
    private static List<Contract> contractList;

    static {
        try {
            contractList = FileUtil.toLineList(new File(Constant.CONTRACT_RECORD_FILE_PATH))
                    .stream().map(s -> new Contract(s.split("\\|!"))).collect(Collectors.toList());
        } catch (IOException e) {
            logger.error("Wrong with contract record file : {} : {}", Constant.CONTRACT_RECORD_FILE_PATH, e.toString());
        }
    }

    public static List<Contract> toContractList() {
        return contractList;
    }
}
