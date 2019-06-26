package cn.onlyhi.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static cn.onlyhi.common.constants.Constants.cmdQueue;

/**
 * windows下使用OfficeToPDF.exe，linux下使用LibreOffice5.3
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/12/27.
 */
public class OfficeToPdfUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(OfficeToPdfUtil.class);

    /**
     * @param inputFile 待转换的office文件
     * @param outDir    转换完要输出的目录,pdf文件名为office文件名
     * @param cmdPath   转换软件的路径
     */
    public static CmdExecResult conver(File inputFile, File outDir, String cmdPath) throws Exception {
        long startTime = System.currentTimeMillis();
        if (inputFile == null || !inputFile.exists()) {
            throw new OfficeConverPdfException("Input file doesn't exist");
        }
        if (!outDir.exists()) {
            outDir.mkdirs();
        }
        List<String> cmdList = new ArrayList<>();
        if (cmdPath.contains("docker")) {
            String[] cmdPart = cmdPath.split(" ");
            cmdList.addAll(Arrays.asList(cmdPart));
        } else {
            cmdList.add(cmdPath);
        }
        cmdList.add("--headless");
        cmdList.add("--invisible");
        cmdList.add("--convert-to");
        cmdList.add("pdf");
        cmdList.add(inputFile.getPath());
        cmdList.add("--outdir");
        cmdList.add(outDir.getPath());
        String[] cmds = new String[cmdList.size()];
        cmdList.toArray(cmds);
        //LOGGER.info("cmds={}", cmdList);
        CmdExecResult execResult = CmdExecuteUtil.exec(cmds, true);
        if (cmdPath.contains("docker")) {
            cmdQueue.add(cmdPath);
        }
        //LOGGER.info("文件{} office转pdf时间:{}ms", inputFile.getName(), System.currentTimeMillis() - startTime);
        return execResult;
    }

    private static class OfficeConverPdfException extends Exception {

        private static final long serialVersionUID = -2423580048793902496L;

        public OfficeConverPdfException() {
            super();
        }

        public OfficeConverPdfException(String message) {
            super(message);
        }

        public OfficeConverPdfException(Throwable cause) {
            super(cause);
        }

        public OfficeConverPdfException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
