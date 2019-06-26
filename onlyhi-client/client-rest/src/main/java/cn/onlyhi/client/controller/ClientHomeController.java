package cn.onlyhi.client.controller;

import cn.onlyhi.client.dto.CourseDto;
import cn.onlyhi.client.dto.LoginUserCache;
import cn.onlyhi.client.po.AppColumn;
import cn.onlyhi.client.po.Article;
import cn.onlyhi.client.po.Banner;
import cn.onlyhi.client.po.LeadsExt;
import cn.onlyhi.client.po.TeacherRecommend;
import cn.onlyhi.client.request.CourseDateRequest;
import cn.onlyhi.client.request.DeviceTypeRequest;
import cn.onlyhi.client.request.SaveArticleRequest;
import cn.onlyhi.client.request.SaveBannerRequest;
import cn.onlyhi.client.request.SaveColumnRequest;
import cn.onlyhi.client.request.SaveTeacherRecommendRequest;
import cn.onlyhi.client.request.UpdateArticleRequest;
import cn.onlyhi.client.request.UpdateBannerRequest;
import cn.onlyhi.client.request.UpdateColumnRequest;
import cn.onlyhi.client.request.UpdateTeacherRecommendRequest;
import cn.onlyhi.client.service.AppColumnService;
import cn.onlyhi.client.service.ArticleService;
import cn.onlyhi.client.service.BannerService;
import cn.onlyhi.client.service.CpCourseService;
import cn.onlyhi.client.service.CpPayRequestService;
import cn.onlyhi.client.service.TeacherRecommendService;
import cn.onlyhi.client.vo.AppColumnVo;
import cn.onlyhi.client.vo.ArticleVo;
import cn.onlyhi.client.vo.BannerVo;
import cn.onlyhi.client.vo.ClientHomeVo;
import cn.onlyhi.client.vo.ExpendClassTimeVo;
import cn.onlyhi.client.vo.StudentCourseRemindVo;
import cn.onlyhi.client.vo.SurplusClassTimeVo;
import cn.onlyhi.client.vo.TeacherCourseRemindVo;
import cn.onlyhi.client.vo.TeacherRecommendVo;
import cn.onlyhi.common.annotation.LogRecordAnnotation;
import cn.onlyhi.common.enums.AppEnum;
import cn.onlyhi.common.request.BaseRequest;
import cn.onlyhi.common.util.Page;
import cn.onlyhi.common.util.Response;
import cn.onlyhi.common.util.TransferUtil;
import cn.onlyhi.common.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.ValidationException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

/**
 * app首页接口
 *
 * @Author shitongtong
 * <p>
 * Created by shitongtong on 2017/7/19.
 */
@RestController
@RequestMapping("/client/home")
public class ClientHomeController extends BaseController {

    @Autowired
    private BannerService bannerService;
    @Autowired
    private AppColumnService appColumnService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private TeacherRecommendService teacherRecommendService;
    @Autowired
    private CpCourseService cpCourseService;
    @Autowired
    private CpPayRequestService cpPayRequestService;

    private Integer getDeviceType(DeviceTypeRequest request) {
        Integer valueByKey = AppEnum.DeviceType.getEnumValueByKey(request.getDeviceType().toUpperCase());
        if (valueByKey == null) {
            throw new ValidationException("deviceType值不正确！");
        }
        return valueByKey;
    }

    /**
     * 获取app首页banner列表
     *
     * @return
     */
    @GetMapping("/listBanner")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000194, methodName = "listBanner", description = "获取app首页banner列表", checkToken = false)
    public ResponseEntity<Response> listBanner(DeviceTypeRequest request) {
        Integer deviceType = getDeviceType(request);
        List<Banner> bannerList = bannerService.findAllBannerByDeviceType(deviceType);
        List<BannerVo> bannerVoList = TransferUtil.transfer(bannerList, BannerVo.class);
        Page<BannerVo> page = new Page<>(bannerVoList.size(), bannerVoList);
        return ResponseEntity.ok(Response.success(page));
    }

    /**
     * 获取app首页栏目列表
     *
     * @return
     */
    @GetMapping("/listColumn")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000195, methodName = "listColumn", description = "获取app首页栏目列表", checkToken = false)
    public ResponseEntity<Response> listColumn(DeviceTypeRequest request) {
        Integer deviceType = getDeviceType(request);
        List<AppColumn> appColumnList = appColumnService.findAllAppColumnByDeviceType(deviceType);
        List<AppColumnVo> appColumnVoList = TransferUtil.transfer(appColumnList, AppColumnVo.class);
        Page<AppColumnVo> page = new Page<>(appColumnVoList.size(), appColumnVoList);
        return ResponseEntity.ok(Response.success(page));
    }

    /**
     * 获取app首页名师推荐列表
     *
     * @return
     */
    @GetMapping("/listTeacherRecommend")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000196, methodName = "listTeacherRecommend", description = "获取app首页名师推荐列表", checkToken = false)
    public ResponseEntity<Response> listTeacherRecommend(DeviceTypeRequest request) {
        Integer deviceType = getDeviceType(request);
        List<TeacherRecommend> teacherRecommendList = teacherRecommendService.findAllTeacherRecommendByDeviceType(deviceType);
        List<TeacherRecommendVo> teacherRecommendVoList = TransferUtil.transfer(teacherRecommendList, TeacherRecommendVo.class);
        Page<TeacherRecommendVo> page = new Page<>(teacherRecommendVoList.size(), teacherRecommendVoList);
        return ResponseEntity.ok(Response.success(page));
    }

    /**
     * 获取app首页教育头条列表
     *
     * @return
     */
    @GetMapping("/listArticle")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000197, methodName = "listTeacherRecommend", description = "获取app首页教育头条列表", checkToken = false)
    public ResponseEntity<Response> listArticle(DeviceTypeRequest request) {
        Integer deviceType = getDeviceType(request);
        List<Article> articleList = articleService.findAllArticleByDeviceType(deviceType);
        List<ArticleVo> articleVoList = TransferUtil.transfer(articleList, ArticleVo.class);
        Page<ArticleVo> page = new Page<>(articleVoList.size(), articleVoList);
        return ResponseEntity.ok(Response.success(page));
    }

    /**
     * 保存app首页banner
     *
     * @param request
     * @return
     */
    @PostMapping("/saveBanner")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000198, methodName = "saveBanner", description = "保存app首页banner", checkToken = false)
    public ResponseEntity<Response> saveBanner(SaveBannerRequest request) {
        Banner banner = TransferUtil.transfer(request, Banner.class);
        banner.setBannerUuid(UUIDUtil.randomUUID2());
        bannerService.save(banner);
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 更新app首页banner
     *
     * @param request
     * @return
     */
    @PostMapping("/updateBanner")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 10000199, methodName = "updateBanner", description = "更新app首页banner", checkToken = false)
    public ResponseEntity<Response> updateBanner(UpdateBannerRequest request) {
        Banner banner = TransferUtil.transfer(request, Banner.class);
        bannerService.update(banner);
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 保存app首页栏目
     *
     * @param request
     * @return
     */
    @PostMapping("/saveColumn")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001100, methodName = "saveColumn", description = "保存app首页栏目", checkToken = false)
    public ResponseEntity<Response> saveColumn(SaveColumnRequest request) {
        AppColumn appColumn = TransferUtil.transfer(request, AppColumn.class);
        appColumn.setAppColumnUuid(UUIDUtil.randomUUID2());
        appColumnService.save(appColumn);
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 更新app首页栏目
     *
     * @param request
     * @return
     */
    @PostMapping("/updateColumn")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001101, methodName = "updateColumn", description = "更新app首页栏目", checkToken = false)
    public ResponseEntity<Response> updateColumn(UpdateColumnRequest request) {
        AppColumn appColumn = TransferUtil.transfer(request, AppColumn.class);
        appColumnService.update(appColumn);
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 保存app首页名师推荐
     *
     * @param request
     * @return
     */
    @PostMapping("/saveTeacherRecommend")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001102, methodName = "saveTeacherRecommend", description = "保存app首页名师推荐", checkToken = false)
    public ResponseEntity<Response> saveTeacherRecommend(SaveTeacherRecommendRequest request) {
        TeacherRecommend teacherRecommend = TransferUtil.transfer(request, TeacherRecommend.class);
        teacherRecommend.setTeacherRecommendUuid(UUIDUtil.randomUUID2());
        teacherRecommendService.save(teacherRecommend);
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 更新app首页名师推荐
     *
     * @param request
     * @return
     */
    @PostMapping("/updateTeacherRecommend")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001103, methodName = "updateTeacherRecommend", description = "更新app首页名师推荐", checkToken = false)
    public ResponseEntity<Response> updateTeacherRecommend(UpdateTeacherRecommendRequest request) {
        TeacherRecommend teacherRecommend = TransferUtil.transfer(request, TeacherRecommend.class);
        teacherRecommendService.update(teacherRecommend);
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 保存app首页教育头条
     *
     * @param request
     * @return
     */
    @PostMapping("/saveArticle")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001104, methodName = "saveArticle", description = "保存app首页教育头条", checkToken = false)
    public ResponseEntity<Response> saveArticle(SaveArticleRequest request) {
        Article article = TransferUtil.transfer(request, Article.class);
        article.setArticleUuid(UUIDUtil.randomUUID2());
        articleService.save(article);
        return ResponseEntity.ok(Response.success());
    }

    /**
     * 更新app首页教育头条
     *
     * @param request
     * @return
     */
    @PostMapping("/updateArticle")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001105, methodName = "updateArticle", description = "更新app首页教育头条", checkToken = false)
    public ResponseEntity<Response> updateArticle(UpdateArticleRequest request) {
        Article article = TransferUtil.transfer(request, Article.class);
        articleService.update(article);
        return ResponseEntity.ok(Response.success());
    }


    /**
     * pc学生端首页学生基本信息
     *
     * @param request
     * @return
     */
    @GetMapping("/studentClassInfo")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001138, methodName = "studentClassInfo", description = "pc学生端首页学生基本信息")
    public ResponseEntity<Response> studentClassInfo(BaseRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userName = loginUserCache.getUserName();
        String userUuid = loginUserCache.getUserUuid();
        //消耗的课时
        double classTime = cpCourseService.findClassTimeByLeadsUuid(userUuid);
        LeadsExt leadsExt = leadsExtService.findLeadsExtByLeadsUuid(userUuid);
        String iconurl = "";
        if (leadsExt != null) {
            iconurl = leadsExt.getIconurl() == null ? "" : leadsExt.getIconurl();
        }
        //所有有排课的学生总数
        int courseLeadsCount = cpCourseService.countCourseLeads();
        //小于消耗课时的学生数
        int lessClassTimeLeadsCount = cpCourseService.countLessClassTimeLeads(classTime);
        //消耗课时超过其他人比率
        String surpassRatio = "0%";
        if (courseLeadsCount > 0) {
            surpassRatio = new BigDecimal(lessClassTimeLeadsCount * 100 / courseLeadsCount).setScale(0, BigDecimal.ROUND_HALF_UP).toString() + "%";
        }

        //学生没有参加课程的节数
        int noJoinClassCount = cpCourseService.countNoJoinClassCount(userUuid);
        //学生课程总节数
        int courseTotalCount = cpCourseService.countCourseByLeadsUuid(userUuid);
        //出勤率
        String attendanceRate = "0%";
        if (courseTotalCount > 0) {
            attendanceRate = new BigDecimal((courseTotalCount - noJoinClassCount) * 100 / courseTotalCount).setScale(0, BigDecimal.ROUND_HALF_UP).toString() + "%";
        }

        ClientHomeVo clientHomeVo = new ClientHomeVo();
        clientHomeVo.setUserName(userName);
        clientHomeVo.setIconurl(iconurl);
        clientHomeVo.setClassTime(classTime);
        clientHomeVo.setSurpassRatio(surpassRatio);
        clientHomeVo.setNoJoinClassCount(noJoinClassCount);
        clientHomeVo.setAttendanceRate(attendanceRate);

        return ResponseEntity.ok(Response.success(clientHomeVo));
    }


    /**
     * pc学生端首页学生剩余课时
     *
     * @param request
     * @return
     */
    @GetMapping("/studentSurplusClassTime")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001139, methodName = "studentSurplusClassTime", description = "pc学生端首页学生剩余课时")
    public ResponseEntity<Response> studentSurplusClassTime(BaseRequest request) {
        String leadsUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();
        //普通总课时
        double ordinaryTime = cpPayRequestService.findCourseTimeByLeadsUuidAndCourseLevel(leadsUuid, 0);
        //清北总课时
        double qbTime = cpPayRequestService.findCourseTimeByLeadsUuidAndCourseLevel(leadsUuid, 1);
        //精品总课时
        double qualityTime = cpPayRequestService.findCourseTimeByLeadsUuidAndCourseLevel(leadsUuid, 2);

        //普通消耗总课时
        double expendOrdinaryTime = cpCourseService.findClassTimeByLeadsUuidAndCourseLevel(leadsUuid, 0);
        //清北消耗总课时
        double expendQbTime = cpCourseService.findClassTimeByLeadsUuidAndCourseLevel(leadsUuid, 1);
        //精品消耗总课时
        double expendQualityTime = cpCourseService.findClassTimeByLeadsUuidAndCourseLevel(leadsUuid, 2);

        SurplusClassTimeVo vo = new SurplusClassTimeVo();
        vo.setSurplusOrdinaryTime(ordinaryTime - expendOrdinaryTime);
        vo.setSurplusQbTime(qbTime - expendQbTime);
        vo.setSurplusQualityTime(qualityTime - expendQualityTime);

        return ResponseEntity.ok(Response.success(vo));
    }


    /**
     * pc学生端首页课程提醒列表
     *
     * @param request
     * @return
     */
    @GetMapping("/studentCourseRemindList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001140, methodName = "studentCourseRemindList", description = "pc学生端首页课程提醒列表")
    public ResponseEntity<Response> studentCourseRemindList(BaseRequest request) throws ParseException {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userType = loginUserCache.getUserType();
        String leadsUuid = loginUserCache.getUserUuid();
        List<CourseDto> courseDtoTmpList = cpCourseService.findNoEndCourseByLeadsUuid(leadsUuid, 1, Integer.MAX_VALUE);
        Object[] objects = getCourseDtoPageList(courseDtoTmpList, 1, Integer.MAX_VALUE, false, userType);
        List<CourseDto> noEndCourseList = (List<CourseDto>) objects[1];
        List<StudentCourseRemindVo> voList = TransferUtil.transfer(noEndCourseList, StudentCourseRemindVo.class);
        return ResponseEntity.ok(Response.success(voList));
    }

    /**
     * pc学生端首页根据课程日期查询未结束课程列表
     *
     * @param request
     * @return
     */
    @GetMapping("/studentCourseList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001141, methodName = "studentCourseList", description = "pc学生端首页根据某日日期查询未结束课程列表")
    public ResponseEntity<Response> studentCourseList(CourseDateRequest request) throws ParseException {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userType = loginUserCache.getUserType();
        String leadsUuid = loginUserCache.getUserUuid();
        String courseDate = request.getCourseDate();
        List<CourseDto> courseDtoTmpList = cpCourseService.findStudentNoEndCourseListByLeadsUuidAndCourseDate(leadsUuid, courseDate);
        Object[] objects = getCourseDtoPageList(courseDtoTmpList, 1, Integer.MAX_VALUE, false, userType);
        List<CourseDto> noEndCourseList = (List<CourseDto>) objects[1];
        List<StudentCourseRemindVo> voList = TransferUtil.transfer(noEndCourseList, StudentCourseRemindVo.class);
        Page<StudentCourseRemindVo> page = new Page<>(voList.size(), voList);
        return ResponseEntity.ok(Response.success(page));
    }

    /**
     * pc教师端首页教师基本信息
     *
     * @param request
     * @return
     */
    @GetMapping("/teacherClassInfo")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001142, methodName = "teacherClassInfo", description = "pc教师端首页教师基本信息")
    public ResponseEntity<Response> teacherClassInfo(BaseRequest request) {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userName = loginUserCache.getUserName();
        String teacherUuid = loginUserCache.getUserUuid();
        //所上课时
        double classTime = cpCourseService.findClassTimeByTeacherUuid(teacherUuid);
        String iconurl = "";

        //所有有排课的教师总数
        int courseCount = cpCourseService.countCourseTeacher();
        //小于消耗课时的教师数
        int lessClassTimeCount = cpCourseService.countLessClassTimeTeacher(classTime);
        //消耗课时超过其他人比率
        String surpassRatio = "0%";
        if (courseCount > 0) {
            surpassRatio = new BigDecimal(lessClassTimeCount * 100 / courseCount).setScale(0, BigDecimal.ROUND_HALF_UP).toString() + "%";
        }

        //没有参加课程的节数
        int noJoinClassCount = cpCourseService.countTeacherNoJoinClassCount(teacherUuid);
        //参与课程总节数
        int courseTotalCount = cpCourseService.countCourseByTeacherUuid(teacherUuid);
        //出勤率
        String attendanceRate = "0%";
        if (courseTotalCount > 0) {
            attendanceRate = new BigDecimal((courseTotalCount - noJoinClassCount) * 100 / courseTotalCount).setScale(0, BigDecimal.ROUND_HALF_UP).toString() + "%";
        }

        ClientHomeVo clientHomeVo = new ClientHomeVo();
        clientHomeVo.setUserName(userName);
        clientHomeVo.setIconurl(iconurl);
        clientHomeVo.setClassTime(classTime);
        clientHomeVo.setSurpassRatio(surpassRatio);
        clientHomeVo.setNoJoinClassCount(noJoinClassCount);
        clientHomeVo.setAttendanceRate(attendanceRate);

        return ResponseEntity.ok(Response.success(clientHomeVo));
    }


    /**
     * pc教师端首页已上课时
     *
     * @param request
     * @return
     */
    @GetMapping("/teacherExpendClassTime")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001143, methodName = "teacherExpendClassTime", description = "pc教师端首页已上课时")
    public ResponseEntity<Response> teacherExpendClassTime(BaseRequest request) {
        String teacherUuid = redisService.getLoginUserCache(request.getToken()).getUserUuid();

        //普通消耗总课时
        double expendOrdinaryTime = cpCourseService.findClassTimeByTeacherUuidAndCourseLevel(teacherUuid, 0);
        //清北消耗总课时
        double expendQbTime = cpCourseService.findClassTimeByTeacherUuidAndCourseLevel(teacherUuid, 1);
        //精品消耗总课时
        double expendQualityTime = cpCourseService.findClassTimeByTeacherUuidAndCourseLevel(teacherUuid, 2);

        ExpendClassTimeVo vo = new ExpendClassTimeVo();
        vo.setExpendOrdinaryTime(expendOrdinaryTime);
        vo.setExpendQbTime(expendQbTime);
        vo.setExpendQualityTime(expendQualityTime);

        return ResponseEntity.ok(Response.success(vo));
    }


    /**
     * pc教师端首页课程提醒列表
     *
     * @param request
     * @return
     */
    @GetMapping("/teacherCourseRemindList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001144, methodName = "teacherCourseRemindList", description = "pc教师端首页课程提醒列表")
    public ResponseEntity<Response> teacherCourseRemindList(BaseRequest request) throws ParseException {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userType = loginUserCache.getUserType();
        String teacherUuid = loginUserCache.getUserUuid();
        List<CourseDto> courseDtoTmpList = cpCourseService.findTeacherNoEndV1Course(teacherUuid, 1, Integer.MAX_VALUE);
        Object[] objects = getCourseDtoPageList(courseDtoTmpList, 1, Integer.MAX_VALUE, false, userType);
        List<CourseDto> noEndCourseList = (List<CourseDto>) objects[1];
        List<TeacherCourseRemindVo> voList = TransferUtil.transfer(noEndCourseList, TeacherCourseRemindVo.class);
        return ResponseEntity.ok(Response.success(voList));
    }

    /**
     * pc教师端首页根据课程日期查询未结束课程列表
     *
     * @param request
     * @return
     */
    @GetMapping("/teacherCourseList")
    @LogRecordAnnotation(moduleCode = 100001, moduleName = "客户端", methodCode = 100001145, methodName = "teacherCourseList", description = "pc教师端首页根据课程日期查询未结束课程列表")
    public ResponseEntity<Response> teacherCourseList(CourseDateRequest request) throws ParseException {
        LoginUserCache loginUserCache = redisService.getLoginUserCache(request.getToken());
        String userType = loginUserCache.getUserType();
        String teacherUuid = loginUserCache.getUserUuid();
        String courseDate = request.getCourseDate();
        List<CourseDto> courseDtoTmpList = cpCourseService.findStudentNoEndCourseListByTeacherUuidAndCourseDate(teacherUuid, courseDate);
        Object[] objects = getCourseDtoPageList(courseDtoTmpList, 1, Integer.MAX_VALUE, false, userType);
        List<CourseDto> noEndCourseList = (List<CourseDto>) objects[1];
        List<TeacherCourseRemindVo> voList = TransferUtil.transfer(noEndCourseList, TeacherCourseRemindVo.class);
        Page<TeacherCourseRemindVo> page = new Page<>(voList.size(), voList);
        return ResponseEntity.ok(Response.success(page));
    }

}
