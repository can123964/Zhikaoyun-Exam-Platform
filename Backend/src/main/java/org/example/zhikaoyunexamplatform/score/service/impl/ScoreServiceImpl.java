package org.example.zhikaoyunexamplatform.score.service.impl;

import com.alibaba.excel.EasyExcel;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zhikaoyunexamplatform.common.enums.RecordStatusEnum;
import org.example.zhikaoyunexamplatform.common.exception.BusinessException;
import org.example.zhikaoyunexamplatform.common.result.PageResult;
import org.example.zhikaoyunexamplatform.exam.entity.Exam;
import org.example.zhikaoyunexamplatform.exam.mapper.ExamMapper;
import org.example.zhikaoyunexamplatform.score.mapper.ScoreMapper;
import org.example.zhikaoyunexamplatform.score.service.ScoreService;
import org.example.zhikaoyunexamplatform.score.vo.RankVO;
import org.example.zhikaoyunexamplatform.score.vo.ScoreExcelVO;
import org.example.zhikaoyunexamplatform.score.vo.ScoreItemVO;
import org.example.zhikaoyunexamplatform.score.vo.ScoreStatVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScoreServiceImpl implements ScoreService {

    private final ScoreMapper scoreMapper;
    private final ExamMapper examMapper;

    @Override
    public PageResult<ScoreItemVO> getExamScores(Long examId, Integer page, Integer size, String keyword, Long classId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }

        int total = scoreMapper.countScore(examId, keyword, classId);
        int offset = (page - 1) * size;
        List<ScoreItemVO> records = scoreMapper.selectScorePage(examId, keyword, classId, offset, size);

        for (ScoreItemVO vo : records) {
            vo.setStatusName(RecordStatusEnum.fromValue(vo.getStatus()).getName());
            vo.setIsPassed(vo.getScore() != null && vo.getPassScore() != null
                    && vo.getScore() >= vo.getPassScore());
        }

        return PageResult.of((long) total, page, size, records);
    }

    @Override
    public ScoreStatVO getExamStat(Long examId, Long classId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }

        ScoreStatVO stat = scoreMapper.selectExamStat(examId, classId);
        if (stat == null) {
            stat = new ScoreStatVO();
        }
        stat.setExamId(examId);
        stat.setExamTitle(exam.getTitle());

        // 计算及格率
        Integer submitCount = stat.getSubmitCount();
        if (submitCount != null && submitCount > 0) {
            int passCount = scoreMapper.countByScoreRange(examId, exam.getPassScore(), Integer.MAX_VALUE, classId);
            stat.setPassRate((double) passCount / submitCount * 100);
        } else {
            stat.setAvgScore(0.0);
            stat.setMaxScore(0);
            stat.setMinScore(0);
            stat.setPassRate(0.0);
        }

        // 分数段分布
        Map<String, Integer> distribution = new LinkedHashMap<>();
        distribution.put("0-59", scoreMapper.countByScoreRange(examId, 0, 60, classId));
        distribution.put("60-69", scoreMapper.countByScoreRange(examId, 60, 70, classId));
        distribution.put("70-79", scoreMapper.countByScoreRange(examId, 70, 80, classId));
        distribution.put("80-89", scoreMapper.countByScoreRange(examId, 80, 90, classId));
        distribution.put("90-100", scoreMapper.countByScoreRange(examId, 90, Integer.MAX_VALUE, classId));
        stat.setScoreDistribution(distribution);

        return stat;
    }

    @Override
    public List<RankVO> getExamRank(Long examId, Long classId) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }
        return scoreMapper.selectRankList(examId, classId);
    }

    @Override
    public void exportExamScores(Long examId, String keyword, Long classId, HttpServletResponse response) {
        Exam exam = examMapper.selectById(examId);
        if (exam == null) {
            throw new BusinessException("考试不存在");
        }

        List<ScoreItemVO> list = scoreMapper.selectAllScores(examId, keyword, classId);
        List<ScoreExcelVO> excelList = new ArrayList<>();
        for (ScoreItemVO item : list) {
            ScoreExcelVO ex = new ScoreExcelVO();
            ex.setRealName(item.getRealName());
            ex.setStudentNo(item.getStudentNo());
            ex.setClassName(item.getClassName());
            ex.setScore(item.getScore());
            ex.setTotalScore(item.getTotalScore());
            ex.setIsPassed(Boolean.TRUE.equals(item.getIsPassed()) ? "是" : "否");
            ex.setSubmitTime(item.getSubmitTime() != null
                    ? item.getSubmitTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                    : "");
            ex.setDurationStr(item.getDuration() != null ? (item.getDuration() / 60) + "分钟" : "");
            excelList.add(ex);
        }

        try {
            String fileName = "成绩表_" + exam.getTitle();
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20"));

            EasyExcel.write(response.getOutputStream(), ScoreExcelVO.class)
                    .sheet("成绩表")
                    .doWrite(excelList);

            log.info("成绩导出成功：examId={}, 行数={}", examId, excelList.size());
        } catch (IOException e) {
            throw new BusinessException("导出失败：" + e.getMessage());
        }
    }
}
