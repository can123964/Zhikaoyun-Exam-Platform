package org.example.zhikaoyunexamplatform.wrongbook.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.zhikaoyunexamplatform.common.enums.QuestionTypeEnum;
import org.example.zhikaoyunexamplatform.common.exception.BusinessException;
import org.example.zhikaoyunexamplatform.common.result.PageResult;
import org.example.zhikaoyunexamplatform.question.entity.Question;
import org.example.zhikaoyunexamplatform.question.mapper.QuestionMapper;
import org.example.zhikaoyunexamplatform.wrongbook.entity.WrongBook;
import org.example.zhikaoyunexamplatform.wrongbook.mapper.WrongBookMapper;
import org.example.zhikaoyunexamplatform.wrongbook.service.WrongBookService;
import org.example.zhikaoyunexamplatform.wrongbook.vo.WrongBookVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class WrongBookServiceImpl implements WrongBookService {

    private final WrongBookMapper wrongBookMapper;
    private final QuestionMapper questionMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResult<WrongBookVO> getMyWrongBooks(Long userId, Integer page, Integer size, Integer type, Integer mastered) {
        int total = wrongBookMapper.countByUserId(userId, type, mastered);
        int offset = (page - 1) * size;
        List<WrongBook> records = wrongBookMapper.selectPageByUserId(userId, type, mastered, offset, size);

        // 批量预加载题目信息，消除 N+1 查询
        List<Long> questionIds = records.stream().map(WrongBook::getQuestionId).distinct().collect(Collectors.toList());
        Map<Long, Question> questionMap = questionIds.isEmpty() ? Collections.emptyMap() :
                questionMapper.selectByIds(questionIds).stream()
                        .collect(Collectors.toMap(Question::getId, q -> q));

        List<WrongBookVO> voList = records.stream()
                .map(wb -> toVO(wb, questionMap.get(wb.getQuestionId())))
                .collect(Collectors.toList());
        return PageResult.of((long) total, page, size, voList);
    }

    @Override
    public void markMastered(Long userId, Long id) {
        WrongBook wb = wrongBookMapper.selectById(id);
        if (wb == null || !wb.getUserId().equals(userId)) {
            throw new BusinessException("错题记录不存在");
        }
        wrongBookMapper.updateMastered(id, 1);
    }

    @Override
    public void deleteWrongBook(Long userId, Long id) {
        WrongBook wb = wrongBookMapper.selectById(id);
        if (wb == null || !wb.getUserId().equals(userId)) {
            throw new BusinessException("错题记录不存在");
        }
        wrongBookMapper.deleteById(id);
    }

    private WrongBookVO toVO(WrongBook wb, Question q) {
        WrongBookVO vo = new WrongBookVO();
        vo.setId(wb.getId());
        vo.setQuestionId(wb.getQuestionId());
        vo.setMyAnswer(wb.getMyAnswer());
        vo.setWrongCount(wb.getWrongCount());
        vo.setMastered(wb.getMastered());
        vo.setCreateTime(wb.getCreateTime());

        if (q != null) {
            vo.setType(q.getType());
            vo.setTypeName(QuestionTypeEnum.fromValue(q.getType()).getName());
            vo.setContent(q.getContent());
            vo.setOptionA(q.getOptionA());
            vo.setOptionB(q.getOptionB());
            vo.setOptionC(q.getOptionC());
            vo.setOptionD(q.getOptionD());
            vo.setCorrectAnswer(q.getAnswer());
            vo.setExplanation(q.getExplanation());
        }
        return vo;
    }
}
