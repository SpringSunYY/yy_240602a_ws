package com.yy.manage.utils;

import com.yy.common.utils.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Project: WS
 * @Package: com.yy.manage.utils
 * @Author: YY
 * @CreateTime: 2024-06-19  14:45
 * @Description: ExcelFileUtil
 * @Version: 1.0
 */
public class ExcelFileUtil {

    public static Long writeExcelFile(String[] successFile, String discardedFilePath, Long numbers, String[] failureFile, String outputFilePath) {
        try {
            FileUtil.createDirectoryIfNotExists(discardedFilePath);
            List<String> successNumbersWithoutTimestamps = readTxtFile(discardedFilePath);
            List<String[]> successNumbers = parseNumbersWithTimestamps(successFile);
            List<String[]> noTimestampNumbers = parseNumbers(successNumbersWithoutTimestamps);

            // 打乱无时间戳的号码
            Collections.shuffle(noTimestampNumbers);

            // 计算成功和失败的比例
            int totalSuccessNumbers = successNumbers.size();
            double successRatio = (double) totalSuccessNumbers / numbers;
            int successCount = (int) Math.round(noTimestampNumbers.size() * successRatio);
//            System.out.println("successCount = " + successCount);
            if (successCount > noTimestampNumbers.size()) {
                successCount = noTimestampNumbers.size();
            }
            int failureCount = noTimestampNumbers.size() - successCount;
//            System.out.println("failureCount = " + successCount);

            // 分割成功和失败的号码
            List<String[]> successList = new ArrayList<>(noTimestampNumbers.subList(0, successCount));
            //System.out.println("successList.size() = " + successList.size());
            List<String[]> failureList = new ArrayList<>(noTimestampNumbers.subList(successCount, noTimestampNumbers.size()));
            //System.out.println("failureList = " + failureList.size());
            // 为成功的号码分配时间戳
            assignTimestamps(successList, successNumbers);

            // 合并所有成功的号码
            List<String[]> allSuccessNumbers = new ArrayList<>(successNumbers);
            allSuccessNumbers.addAll(successList);
            //System.out.println("allSuccessNumbers = " + allSuccessNumbers.size());

            // 按发送时间排序
            sortSuccessNumbersByTimestamp(allSuccessNumbers);

            // 合并所有失败号码
            List<String[]> allFailureNumbers = new ArrayList<>();
            for (String number : failureFile) {
                allFailureNumbers.add(new String[]{number, "offline"});
            }
            for (String[] failureNumber : failureList) {
                allFailureNumbers.add(new String[]{failureNumber[0], "offline"});
            }
            // 写入 Excel 文件
            writeExcel(allSuccessNumbers, failureFile, outputFilePath);
            return successNumbers.size() == 0 ? 0 : (long) (allFailureNumbers.size() + allSuccessNumbers.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<String> readTxtFile(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath));
    }

    private static List<String[]> parseNumbersWithTimestamps(String[] lines) {
        if (StringUtils.isEmpty(lines)) {
            return Collections.emptyList();
        }
        List<String[]> numbers = new ArrayList<>();
        for (String line : lines) {
            try {
                String[] parts = line.split("----");
                numbers.add(new String[]{parts[0].trim(), parts[1].trim()});
            } catch (Exception e) {
                return Collections.emptyList();
            }
        }
        return numbers;
    }

    private static List<String[]> parseNumbers(List<String> lines) {
        List<String[]> numbers = new ArrayList<>();
        for (String line : lines) {
            numbers.add(new String[]{line.trim(), null});
        }
        return numbers;
    }

    private static void assignTimestamps(List<String[]> noTimestampNumbers, List<String[]> referenceNumbers) {
        int refSize = referenceNumbers.size();
        for (int i = 0; i < noTimestampNumbers.size(); i++) {
            int refIndex = i % refSize;
            noTimestampNumbers.get(i)[1] = referenceNumbers.get(refIndex)[1];
        }
    }

    private static void sortSuccessNumbersByTimestamp(List<String[]> successNumbers) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        successNumbers.sort((a, b) -> {
            try {
                return sdf.parse(a[1]).compareTo(sdf.parse(b[1]));
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static void writeExcel(List<String[]> successNumbers, String[] failureNumbers, String outputFilePath) throws IOException {
        //判断文件是否存在不存在创建文件
        FileUtil.createDirectoryIfNotExists(outputFilePath);
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet successSheet = workbook.createSheet("成功");
        XSSFSheet failureSheet = workbook.createSheet("失败");

        // 写入成功号码
        writeHeader(successSheet, new String[]{"手机号", "发送时间"});
        int rowNum = 1;
        for (String[] number : successNumbers) {
            writeRow(successSheet, rowNum++, number);
        }

        // 写入失败号码
        writeHeader(failureSheet, new String[]{"手机号", "失败原因"});
        rowNum = 1;
        if (StringUtils.isNotEmpty(failureNumbers)) {
            for (String number : failureNumbers) {
                writeRow(failureSheet, rowNum++, new String[]{number, "offline"});
            }
        }

        // 保存到文件
        try (FileOutputStream fileOut = new FileOutputStream(outputFilePath)) {
            workbook.write(fileOut);
        }
        workbook.close();
    }

    private static void writeHeader(XSSFSheet sheet, String[] headers) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
    }

    private static void writeRow(XSSFSheet sheet, int rowNum, String[] values) {
        Row row = sheet.createRow(rowNum);
        for (int i = 0; i < values.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(values[i]);
        }
    }
}
