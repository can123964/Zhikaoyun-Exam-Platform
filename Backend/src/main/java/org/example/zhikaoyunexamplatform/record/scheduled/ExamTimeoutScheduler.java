package org.example.zhikaoyunexamplatform.record.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.zhikaoyunexamplatform.record.service.ExamRecordService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExamTimeoutScheduler {

    private final ExamRecordService examRecordService;

    @Scheduled(fixedRate = 60000)
    public void autoSubmitTimeoutRecords() {
        log.debug("扫描超时考试记录...");
        examRecordService.autoSubmitTimeoutRecords();
    }
}
