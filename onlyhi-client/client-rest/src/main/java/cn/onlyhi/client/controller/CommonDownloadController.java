package cn.onlyhi.client.controller;

import cn.onlyhi.client.request.DownloadRequest;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.constants.Constants;
import cn.onlyhi.common.util.DownloadUtil;
import cn.onlyhi.common.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2018/4/9.
 */
@Controller
@RequestMapping("/common")
public class CommonDownloadController {
    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * 下载文件
     *
     * @param request
     */
    @RequestMapping("/download")
    @LogRecordAnnotation(moduleCode = 100007, moduleName = "公共接口", methodCode = 1000077, methodName = "download", description = "下载文件")
    public ResponseEntity<Response> download(DownloadRequest request) {
        String fileUrl = request.getFileUrl();
        if (!fileUrl.startsWith("http")) {
            return ResponseEntity.ok(Response.errorCustom("fileUrl不是网络路径！"));
        }
        String fileName = fileUrl.substring(fileUrl.lastIndexOf(Constants.FILESEPARATOR) + 1);
        DownloadUtil.download(httpServletResponse, fileUrl, fileName);
        return ResponseEntity.ok(Response.success());
    }

}
