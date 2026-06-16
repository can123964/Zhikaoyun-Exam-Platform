package org.example.zhikaoyunexamplatform.wrongbook.service;

import org.example.zhikaoyunexamplatform.common.result.PageResult;
import org.example.zhikaoyunexamplatform.wrongbook.vo.WrongBookVO;

public interface WrongBookService {
    PageResult<WrongBookVO> getMyWrongBooks(Long userId, Integer page, Integer size, Integer type, Integer mastered);
    void markMastered(Long userId, Long id);
    void deleteWrongBook(Long userId, Long id);
}
