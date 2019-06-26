package cn.onlyhi.common.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/11/16.
 */
public class CmdExecuteUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(CmdExecuteUtil.class);

    /**
     * 执行cmd命令
     *
     * @param cmd    命令行
     * @param isWait 是否等待命令执行结束
     * @return
     * @throws Exception
     */
    public static CmdExecResult exec(String cmd, boolean isWait) throws Exception {
        CmdExecResult result = new CmdExecResult();
        try {
            Process pro = Runtime.getRuntime().exec(cmd);
            BufferedReader stdout = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            BufferedReader stderr = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
            String line;
            StringBuilder info = new StringBuilder();
            StringBuilder errinfo = new StringBuilder();
            while ((line = stdout.readLine()) != null) {
                info.append(line);
                info.append(System.lineSeparator());
            }
            while ((line = stderr.readLine()) != null) {
                errinfo.append(line);
                errinfo.append(System.lineSeparator());
            }
            stdout.close();
            stderr.close();
            if (StringUtils.isNotBlank(errinfo)) {
                LOGGER.info("命令【{}】执行结束，执行情况：异常：" + System.lineSeparator() + "{}", cmd, errinfo);
                result.setExecStatus(false);
                result.setErrorInfo(errinfo.toString());
            } else {
                LOGGER.info("命令【{}】执行结束，执行情况：正常：" + System.lineSeparator() + "{}", cmd, info);
                result.setExecStatus(true);
                result.setSuccessInfo(info.toString());
            }
            if (isWait) {
                pro.waitFor();
            }
        } catch (Exception e) {
            LOGGER.info("命令【{}】执行失败！", cmd);
            throw e;
        }
        return result;
    }

    /**
     * 执行cmd命令
     *
     * @param cmd    命令行
     * @param isWait 是否等待命令执行结束
     * @return
     * @throws Exception
     */
    public static CmdExecResult exec(String[] cmd, boolean isWait) throws Exception {
        CmdExecResult result = new CmdExecResult();
        try {
            Process pro = Runtime.getRuntime().exec(cmd);
            BufferedReader stdout = new BufferedReader(new InputStreamReader(pro.getInputStream()));
            BufferedReader stderr = new BufferedReader(new InputStreamReader(pro.getErrorStream()));
            String line;
            StringBuilder info = new StringBuilder();
            StringBuilder errinfo = new StringBuilder();
            while ((line = stdout.readLine()) != null) {
                info.append(line);
                info.append(System.lineSeparator());
            }
            while ((line = stderr.readLine()) != null) {
                errinfo.append(line);
                errinfo.append(System.lineSeparator());
            }
            stdout.close();
            stderr.close();
            if (StringUtils.isNotBlank(errinfo)) {
                LOGGER.info("命令【{}】执行结束，执行情况：异常：" + System.lineSeparator() + "{}", Arrays.asList(cmd), errinfo);
                result.setExecStatus(false);
                result.setErrorInfo(errinfo.toString());
            } else {
                LOGGER.info("命令【{}】执行结束，执行情况：正常：" + System.lineSeparator() + "{}", Arrays.asList(cmd), info);
                result.setExecStatus(true);
                result.setSuccessInfo(info.toString());
            }
            if (isWait) {
                pro.waitFor();
            }
        } catch (Exception e) {
            LOGGER.info("命令【{}】执行失败！", Arrays.asList(cmd));
            throw e;
        }
        return result;
    }
}
