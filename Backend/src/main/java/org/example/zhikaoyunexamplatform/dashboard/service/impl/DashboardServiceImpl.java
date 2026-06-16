package org.example.zhikaoyunexamplatform.dashboard.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zhikaoyunexamplatform.common.constant.Constant;
import org.example.zhikaoyunexamplatform.dashboard.service.DashboardService;
import org.example.zhikaoyunexamplatform.dashboard.vo.ActivityItemVO;
import org.example.zhikaoyunexamplatform.dashboard.vo.DashboardVO;
import org.example.zhikaoyunexamplatform.exam.mapper.ExamMapper;
import org.example.zhikaoyunexamplatform.question.mapper.QuestionMapper;
import org.example.zhikaoyunexamplatform.record.entity.ExamRecord;
import org.example.zhikaoyunexamplatform.record.mapper.ExamRecordMapper;
import org.example.zhikaoyunexamplatform.user.entity.User;
import org.example.zhikaoyunexamplatform.user.mapper.UserMapper;
import org.example.zhikaoyunexamplatform.wrongbook.mapper.WrongBookMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardServiceImpl implements DashboardService {

    private final UserMapper userMapper;
    private final QuestionMapper questionMapper;
    private final ExamMapper examMapper;
    private final ExamRecordMapper examRecordMapper;
    private final WrongBookMapper wrongBookMapper;

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public DashboardVO getOverview(Long userId, Integer role) {
        DashboardVO vo = new DashboardVO();

        if (Constant.ROLE_ADMIN.equals(role)) {
            // 管理员：看全局数据
            vo.setTotalUsers(userMapper.countAll());
            vo.setTotalStudents(userMapper.countByRole(Constant.ROLE_STUDENT));
            vo.setTotalTeachers(userMapper.countByRole(Constant.ROLE_TEACHER));
            vo.setTotalQuestions(questionMapper.countAll());
            vo.setTotalExams(examMapper.countAll());
            vo.setOngoingExams(examMapper.countByStatus(Constant.EXAM_ONGOING));
            vo.setTotalRecords(examRecordMapper.countAll());
            vo.setTotalWrongBooks(wrongBookMapper.countAll());
        } else {
            // 教师：只看自己相关的数据
            vo.setTotalQuestions(questionMapper.countByCreatorId(userId));
            vo.setTotalExams(examMapper.countByCreatorId(userId));
            vo.setOngoingExams(examMapper.countByCreatorIdAndStatus(userId, Constant.EXAM_ONGOING));
            vo.setTotalStudents(userMapper.countStudentsByTeacherId(userId));
        }

        // 近 7 天考试趋势（所有角色都能看到）
        vo.setExamTrend(buildExamTrend());

        return vo;
    }

    @Override
    public Map<String, Object> getRecentActivity(Long userId, Integer role, int page, int pageSize) {
        List<ActivityItemVO> allActivities = new ArrayList<>();

        // 最近考试提交（最多取 50 条）
        List<Map<String, Object>> submissions = examRecordMapper.selectRecentSubmissions();
        for (Map<String, Object> row : submissions) {
            ActivityItemVO item = new ActivityItemVO();
            item.setType("exam_submit");
            String studentName = (String) row.get("studentName");
            String examTitle = (String) row.get("examTitle");
            Object scoreObj = row.get("score");
            String score = scoreObj != null ? scoreObj.toString() : "-";
            item.setTitle(studentName + " 提交了 " + examTitle);
            item.setSubtitle("得分: " + score);
            Object submitTime = row.get("submitTime");
            if (submitTime instanceof LocalDateTime) {
                item.setTime(((LocalDateTime) submitTime).format(TIME_FMT));
            } else {
                item.setTime("");
            }
            allActivities.add(item);
        }

        // 最近注册用户（最多取 50 条）
        List<User> recentUsers = userMapper.selectRecentRegistrations();
        for (User user : recentUsers) {
            ActivityItemVO item = new ActivityItemVO();
            item.setType("user_register");
            item.setTitle(user.getRealName() + " 注册了账号");
            item.setSubtitle(roleName(user.getRole()));
            if (user.getCreateTime() != null) {
                item.setTime(user.getCreateTime().format(TIME_FMT));
            }
            allActivities.add(item);
        }

        // 按时间倒序排列
        allActivities.sort((a, b) -> {
            String ta = a.getTime() != null ? a.getTime() : "";
            String tb = b.getTime() != null ? b.getTime() : "";
            return tb.compareTo(ta);
        });

        // 分页切片
        int total = allActivities.size();
        int fromIndex = (page - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        List<ActivityItemVO> pageList = fromIndex < total
                ? allActivities.subList(fromIndex, toIndex)
                : Collections.emptyList();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("list", pageList);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);
        return result;
    }

    /** 构建近 7 天考试趋势数据 */
    private List<DashboardVO.TrendItem> buildExamTrend() {
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(6);
        LocalDateTime startDateTime = sevenDaysAgo.atStartOfDay();

        List<ExamRecord> records = examRecordMapper.selectRecentForTrend(startDateTime);

        // 按日期分组统计
        Map<String, Long> countByDate = records.stream()
                .filter(r -> r.getCreateTime() != null)
                .collect(Collectors.groupingBy(
                        r -> r.getCreateTime().toLocalDate().toString(),
                        Collectors.counting()
                ));

        // 生成完整 7 天序列（没有数据的日期补 0）
        List<DashboardVO.TrendItem> trend = new ArrayList<>();
        DateTimeFormatter shortFmt = DateTimeFormatter.ofPattern("MM-dd");
        for (int i = 0; i < 7; i++) {
            LocalDate date = sevenDaysAgo.plusDays(i);
            int count = countByDate.getOrDefault(date.toString(), 0L).intValue();
            trend.add(new DashboardVO.TrendItem(date.format(shortFmt), count));
        }

        return trend;
    }

    private String roleName(Integer role) {
        if (role == null) return "用户";
        switch (role) {
            case 0: return "学生";
            case 1: return "教师";
            case 2: return "管理员";
            default: return "用户";
        }
    }
}
