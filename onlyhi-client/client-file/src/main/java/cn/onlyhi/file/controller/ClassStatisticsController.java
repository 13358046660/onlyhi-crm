package cn.onlyhi.file.controller;

import cn.onlyhi.client.dto.StatisticsClassVo;
import cn.onlyhi.client.po.ClassStatistics;
import cn.onlyhi.client.service.*;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.enums.CodeEnum;
import cn.onlyhi.common.util.*;
import cn.onlyhi.file.config.YmlMyConfig;
import cn.onlyhi.file.request.ExportDataRequest;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 新课耗统计
 *
 * @Author wqz
 * <p>
 * Created by wqz on 2018/10/11.
 */
@RestController
@RequestMapping("/client/class")
public class ClassStatisticsController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClassStatisticsController.class);

    @Autowired
    private StatisticsClassService statisticsClassService;
    @Autowired
    private HttpServletResponse httpServletResponse;
    @Autowired
    private YmlMyConfig ymlMyConfig;
    @Autowired
    private CpCourseService cpCourseService;

    /**
     * 每天汇总后写入新课耗表
     *
     * @return
     */
    @GetMapping("/statistics")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001190, methodName = "statisticsClass", description = "存课耗", checkToken = false)
    public ResponseEntity<Response> saveClass(String courseDate) throws Exception {
        LOGGER.info("saveClass.");
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = "";
        if (StringUtils.isNotEmpty(courseDate)) {
            today = courseDate;
        } else {
            today = dateFormat.format(new Date());
        }
        boolean flag = statisticsClassService.statisticsClass(today);
        if (flag) {
            return ResponseEntity.ok(Response.success(CodeEnum.SUCCESS));
        } else {
            return ResponseEntity.ok(Response.error(CodeEnum.FAILURE));
        }
    }

    /**
     * 新课耗列表检索及导出
     *
     * @return
     */
    @PostMapping("/list")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001191, methodName = "classList", description = "新课耗列表", checkToken = false)
    public ResponseEntity<Response> classList(StatisticsClassVo vo) throws Exception {
        List<ClassStatistics> list = statisticsClassService.findClass(vo);
        if (vo.isDownloadFlag()) {
            String fileUrl = ymlMyConfig.getUploadAddress();
            DownloadUtil.download(httpServletResponse, fileUrl, "课耗统计");
        }
        return ResponseEntity.ok(Response.success(list));
    }

    /**
     * 老师核查可能与实际不符课耗
     * 提交核查时长、核查人及备注
     *
     * @return
     */
    @PostMapping("/update")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001192, methodName = "classUpdate", description = "核查课耗", checkToken = false)
    public ResponseEntity<Response> classUpdate(Integer id) throws Exception {
        statisticsClassService.updateClassById(id);
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 新课耗定时导出
     *
     * @param request
     * @return
     */
    @GetMapping("/exportClass")
    @LogRecordAnnotation(moduleCode = 200001, moduleName = "客户端", methodCode = 100001191, methodName = "exportClass", description = "新课耗定时导出", checkToken = false)
    public ResponseEntity<Response> exportClass(ExportDataRequest request) {
        try {
            String exportDate = request.getExportDate();
            Map<Map<String, String>, List<Map<String, String>>> map = statisticsClassService.exportClass(exportDate);
            File saveFile = new File("/www/clientfile/uploadPath/exportClass", exportDate.concat("课耗").concat(".xlsx"));
            if (!saveFile.getParentFile().exists()) {
                saveFile.getParentFile().mkdirs();
            }
            List<String> sheetNames = Arrays.asList("当天课耗");

            if (map.size() > 0) {
                ExportUtil.wirteExcelFile2(map, sheetNames, saveFile);
                if (request.isDownloadFlag()) {
                    //下载saveFile
                    String fileUrl = saveFile.getPath().replace("/www/clientfile/", ymlMyConfig.getUploadAddress());
                    DownloadUtil.download(httpServletResponse, fileUrl, saveFile.getName());
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        return ResponseEntity.ok(Response.success());
    }
}
