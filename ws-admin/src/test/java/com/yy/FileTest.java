package com.yy;

import com.yy.common.exception.ServiceException;
import com.yy.manage.service.IOrderInfoService;
import com.yy.manage.utils.ExcelFileUtil;
import com.yy.manage.utils.FileUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @Project: WS
 * @Package: com.yy
 * @Author: YY
 * @CreateTime: 2024-06-07  21:00
 * @Description: FileTest
 * @Version: 1.0
 */
@SpringBootTest()
public class FileTest {
    @Autowired
    private IOrderInfoService orderInfoService;

    @Test
    void optimize() {
        orderInfoService.optimize();
    }

    @Test
    public void getFileLine() throws Exception {
        // 构建文件路径
//        String filePath = fileParentPath + File.separator + "ws-admin" + File.separator + "src" + File.separator + "main" + File.separator + "resources" + File.separator + "file" + File.separator + "a.txt";
        String filePath = "D:\\ruoyi\\uploadPath\\upload\\profile\\upload\\2024\\06\\07\\测试用的巴西国家账号_20240607230939A002.txt";

        // 创建文件对象
        File file = new File(filePath);

        System.out.println("file.getName() = " + file.getName());
        System.out.println("file = " + file.getAbsolutePath());

        // 检查文件存在性和可读性
        System.out.println("file.exists() = " + file.exists());
        System.out.println("file.isFile() = " + file.isFile());
        System.out.println("file.canRead() = " + file.canRead());
        // 使用BufferedReader读取文件行数
        Path path = Paths.get(filePath);
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            long lineCount = reader.lines().count();
            System.out.println("Number of lines = " + lineCount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void handleOptimize() {
        String filePath = "D:\\ruoyi\\uploadPath\\upload\\2024\\06\\17\\17_filter_res_succ_90260_20240617150932A001.txt";
        String filterPath = "D:\\ruoyi\\uploadPath\\upload\\2024\\06\\17\\filter\\17_filter_res_succ_90260_20240617150932A001.txt";
        String discardedPath = "D:\\ruoyi\\uploadPath\\upload\\2024\\06\\17\\discardedPath\\17_filter_res_succ_90260_20240617150932A001.txt";
        String prefix = "966"; // 要保留的前缀

        long start = System.currentTimeMillis();
        filterPhoneNumbers(filePath, filterPath, prefix, discardedPath);
        long end = System.currentTimeMillis();
        System.out.println("end-start = " + (end - start));
        FileUtil.filterPhoneNumbers(filePath, filterPath, prefix);
        long end1 = System.currentTimeMillis();
        System.out.println("(end1-end) = " + (end1 - end));
    }


//    public static void filterPhoneNumbers(String inputFilePath, String outputFilePath, String prefix) {
//        try {
//            // 创建必要的目录
//            createDirectoryIfNotExists(outputFilePath);
//
//            // 读取输入文件的所有行
//            List<String> phoneNumbers = Files.readAllLines(Paths.get(inputFilePath));
//
//            int nThreads = phoneNumbers.size() / 10000;
//            System.out.println("nThreads = " + nThreads);
//            ExecutorService executorService = Executors.newFixedThreadPool(nThreads);
//
//            // 使用并行流来过滤出以指定前缀开头的号码，并去重
//            Future<Set<String>> futureFilteredNumbers = executorService.submit(() ->
//                    phoneNumbers.parallelStream()
//                            .filter(number -> number.startsWith(prefix))
//                            .collect(Collectors.toSet())
//            );
//
//            // 获取过滤后的号码列表
//            Set<String> filteredNumbers = futureFilteredNumbers.get();
//            System.out.println("filteredNumbers = " + filteredNumbers);
//            List<String> filteredList = new ArrayList<>(filteredNumbers);
//
//            // 打乱号码列表
//            Collections.shuffle(filteredList);
//
//            // 计算需要保留和丢弃的数量
//            int totalSize = filteredList.size();
//            int retainSize = (int) (totalSize * 0.9);
//            System.out.println("retainSize = " + retainSize);
//
//            // 划分保留和丢弃的号码
//            List<String> retainedNumbers = filteredList.subList(0, retainSize);
//
//            // 使用多线程将保留的号码写入输出文件
//            int chunkSize = (int) Math.ceil((double) retainedNumbers.size() / 4);
//            List<Future<Void>> futures = new ArrayList<>();
//
//            for (int i = 0; i < nThreads; i++) {
//                int start = i * chunkSize;
//                int end = Math.min(start + chunkSize, retainedNumbers.size());
//                List<String> chunk = retainedNumbers.subList(start, end);
//
//                Future<Void> future = executorService.submit(() -> {
//                    try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilePath), StandardOpenOption.APPEND)) {
//                        for (String number : chunk) {
//                            writer.write(number);
//                            writer.newLine();
//                        }
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    return null;
//                });
//                futures.add(future);
//            }
//
//            // 等待所有写操作完成
//            for (Future<Void> future : futures) {
//                future.get();
//            }
//            executorService.shutdown();
//        } catch (IOException | InterruptedException | ExecutionException e) {
//            e.printStackTrace();
//            throw new ServiceException("文件过滤失败！！！");
//        } finally {
//
//        }
//    }
//
//    private static void createDirectoryIfNotExists(String filePath) throws IOException {
//        Path path = Paths.get(filePath).getParent();
//        if (path != null && !Files.exists(path)) {
//            Files.createDirectories(path);
//        }
//    }

    public static void filterPhoneNumbers(String inputFilePath, String outputFilePath, String discardedFilePath, String prefix) {
        try {
            // 创建必要的目录
            createDirectoryIfNotExists(outputFilePath);

            // 读取输入文件的所有行
            List<String> phoneNumbers = Files.readAllLines(Paths.get(inputFilePath));

            // 过滤出以指定前缀开头的号码，并去重
            Set<String> filteredNumbers = phoneNumbers.stream()
                    .filter(number -> number.startsWith(prefix))
                    .collect(Collectors.toSet());

            // 将过滤后的号码列表转换为数组
            List<String> filteredList = new ArrayList<>(filteredNumbers);

            // 打乱号码列表
            Collections.shuffle(filteredList);

            // 计算需要保留和丢弃的数量
            int totalSize = filteredList.size();
            int retainSize = (int) (totalSize * 0.9);

            // 划分保留和丢弃的号码
            List<String> retainedNumbers = filteredList.subList(0, retainSize);
            List<String> discardedNumbers = filteredList.subList(retainSize, totalSize);

            // 将保留的号码写入输出文件
            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilePath))) {
                for (String number : retainedNumbers) {
                    writer.write(number);
                    writer.newLine();
                }
            }
            // 将丢弃的号码写入另一个文件
            try (BufferedWriter discardedWriter = Files.newBufferedWriter(Paths.get(discardedFilePath))) {
                for (String number : discardedNumbers) {
                    discardedWriter.write(number);
                    discardedWriter.newLine();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new ServiceException("文件过滤失败！！！");
        }
    }

    // 创建必要的目录
    public static void createDirectoryIfNotExists(String filePath) {
        Path path = Paths.get(filePath).getParent();
        if (path != null && !Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @description: 测试excel文件
     * @author: YY
     * @method: testExcelFile
     * @date: 2024/6/19 14:30
     * @param:
     * @return: void
     **/
    @Test
    public void testExcelFile() {
        String cussPath = "G:\\24\\Java\\yy240602A\\res_succ_92479.txt";
        String discardedPath = "G:\\24\\Java\\yy240602A\\res_succ_90260.txt";
        String failPath = "G:\\24\\Java\\yy240602A\\测试用的巴西国家账号 - 副本.txt";
        //ExcelFileUtil.writeExcelFile(cussPath,discardedPath,failPath);
    }

    public static void main(String[] args) throws Exception {
        // 假设从参数获取完成数量
        int completionNumber = 10000; // 传入的数量，比如 10000

        List<String> successNumbersWithTimestamps = readTxtFile("G:\\24\\Java\\yy240602A\\res_succ_92479.txt");
        List<String> successNumbersWithoutTimestamps = readTxtFile("G:\\24\\Java\\yy240602A\\res_succ_90260.txt");
//        List<String> failureNumbersFromFile  = readTxtFile("G:\\24\\Java\\yy240602A\\res_nosend_90406.txt");
        List<String> failureNumbersFromFile = readTxtFile("G:\\24\\Java\\yy240602A\\测试用的巴西国家账号 - 副本.txt");
        System.out.println("successNumbersWithoutTimestamps.size() = " + successNumbersWithoutTimestamps.size());
        System.out.println("successNumbersWithTimestamps.size() = " + successNumbersWithTimestamps.size());
        List<String[]> successNumbers = parseNumbersWithTimestamps(successNumbersWithTimestamps);
        List<String[]> noTimestampNumbers = parseNumbers(successNumbersWithoutTimestamps);

        // 打乱无时间戳的号码
        Collections.shuffle(noTimestampNumbers);

        // 计算成功和失败的比例
        int totalSuccessNumbers = successNumbers.size();
        double successRatio = (double) totalSuccessNumbers / completionNumber;
        int successCount = (int) Math.round(noTimestampNumbers.size() * successRatio);
        System.out.println("successCount = " + successCount);
        if (successCount > noTimestampNumbers.size()) {
            successCount = noTimestampNumbers.size();
        }
        int failureCount = noTimestampNumbers.size() - successCount;
        System.out.println("failureCount = " + successCount);

        // 分割成功和失败的号码
        List<String[]> successList = new ArrayList<>(noTimestampNumbers.subList(0, successCount));
        System.out.println("successList.size() = " + successList.size());
        List<String[]> failureList = new ArrayList<>(noTimestampNumbers.subList(successCount, noTimestampNumbers.size()));
        System.out.println("failureList = " + failureList.size());
        // 为成功的号码分配时间戳
        assignTimestamps(successList, successNumbers);

        // 合并所有成功的号码
        List<String[]> allSuccessNumbers = new ArrayList<>(successNumbers);
        allSuccessNumbers.addAll(successList);
        System.out.println("allSuccessNumbers = " + allSuccessNumbers.size());

        // 按发送时间排序
        sortSuccessNumbersByTimestamp(allSuccessNumbers);

        // 合并所有失败号码
        List<String[]> allFailureNumbers = new ArrayList<>();
        for (String number : failureNumbersFromFile) {
            allFailureNumbers.add(new String[]{number, "offline"});
        }
        for (String[] failureNumber : failureList) {
            allFailureNumbers.add(new String[]{failureNumber[0], "offline"});
        }

        // 写入 Excel 文件
        writeExcel(allSuccessNumbers, allFailureNumbers, "G:\\24\\Java\\yy240602A\\5output.xlsx");
    }

    private static List<String> readTxtFile(String filePath) throws IOException {
        return Files.readAllLines(Paths.get(filePath));
    }

    private static List<String[]> parseNumbersWithTimestamps(List<String> lines) {
        List<String[]> numbers = new ArrayList<>();
        for (String line : lines) {
            String[] parts = line.split("----");
            numbers.add(new String[]{parts[0].trim(), parts[1].trim()});
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
        successNumbers.sort(Comparator.comparing(a -> a[1]));
    }

    private static void writeExcel(List<String[]> successNumbers, List<String[]> failureNumbers, String outputFilePath) throws IOException {
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
        for (String[] number : failureNumbers) {
            writeRow(failureSheet, rowNum++, number);
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
