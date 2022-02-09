package com.base.batchproject.main.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
@Component
public class TestExcelStepListener implements StepExecutionListener {

    public XSSFWorkbook workbook;
    public XSSFSheet sheet;

    private String fileName = "test.xlsx";
    private String outPath = "output/" + fileName;

    //step 실행 전
    @Override
    public void beforeStep(StepExecution stepExecution) {
        log.info("### before step!! ###");

        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet();
    }

    //step 실행 후
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        log.info("### after step!! ###");

        try{
            FileOutputStream out = new FileOutputStream(outPath);
            workbook.write(out);
            workbook.close();
            out.close();
            return ExitStatus.COMPLETED;
        }catch (IOException e){
            log.error("@@@ IOException "+e.getMessage());
            return ExitStatus.FAILED;
        }
    }
}
