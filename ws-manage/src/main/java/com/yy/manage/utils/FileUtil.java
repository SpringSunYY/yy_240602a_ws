package com.yy.manage.utils;

import com.yy.common.exception.ServiceException;
import com.yy.common.utils.file.FileUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.concurrent.*;

/**
 * @Project: WS
 * @Package: com.yy.manage.utils
 * @Author: YY
 * @CreateTime: 2024-06-09  16:50
 * @Description: FileUtil
 * @Version: 1.0
 */
public class FileUtil {
    private static final int CHUNK_SIZE = 100000;  // 每个线程处理的行数

    /**
     * 获取文件行数
     * @param filePath
     * @return
     */
    public static Long getFileLines(String filePath) {
        // 使用BufferedReader读取文件行数
        Path path = Paths.get(filePath);
        Long lineCount = 0L;
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            lineCount = reader.lines().count();
//            System.out.println("Number of lines = " + lineCount);
        } catch (IOException e) {
//            System.out.println("e.getMessage() = " + e.getMessage());
            throw new ServiceException("文件检查失败，或许是文件太大！！！");
        } catch (NullPointerException e) {
            throw new ServiceException("文件检查失败！！！");
        } catch (Exception e) {
//            System.out.println("e = " + e.getMessage());
            throw new ServiceException("运行异常，请联系管理员查看是否全局短信转换率格式是否正确！！！");
        }
        return lineCount;
    }

    public static void filterPhoneNumbers(String inputFilePath, String outputFilePath, String prefix) {
        try {
            // 创建必要的目录
            createDirectoryIfNotExists(outputFilePath);

            // 获取输入文件的总行数
            long totalLines = Files.lines(Paths.get(inputFilePath)).count();

            // 计算需要的线程数
            int nThreads = (int) Math.ceil((double) totalLines / CHUNK_SIZE);
            //System.out.println("nThreads = " + nThreads);

            ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

            // 读取和过滤数据
            List<Future<Set<String>>> futureResults = new ArrayList<>();
            for (int i = 0; i < nThreads; i++) {
                long startLine = (long) i * CHUNK_SIZE;
                futureResults.add(executorService.submit(new PhoneNumberFilterTask(inputFilePath, startLine, CHUNK_SIZE, prefix)));
            }

            // 合并过滤结果
            Set<String> filteredNumbers = new HashSet<>();
            for (Future<Set<String>> future : futureResults) {
                filteredNumbers.addAll(future.get());
            }
            //System.out.println("filteredNumbers = " + filteredNumbers);

            List<String> filteredList = new ArrayList<>(filteredNumbers);

            // 打乱号码列表
            Collections.shuffle(filteredList);

            // 计算需要保留和丢弃的数量
            int totalSize = filteredList.size();
            int retainSize = (int) (totalSize * 0.9);
            //System.out.println("retainSize = " + retainSize);

            // 划分保留和丢弃的号码
            List<String> retainedNumbers = filteredList.subList(0, retainSize);

            // 使用多线程将保留的号码写入输出文件
            int chunkSize = (int) Math.ceil((double) retainedNumbers.size() / nThreads);
            List<Future<Void>> writeFutures = new ArrayList<>();

            for (int i = 0; i < nThreads; i++) {
                int start = i * chunkSize;
                int end = Math.min(start + chunkSize, retainedNumbers.size());
                if (start < end) {
                    List<String> chunk = retainedNumbers.subList(start, end);

                    writeFutures.add(executorService.submit(() -> {
                        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(outputFilePath), StandardOpenOption.APPEND)) {
                            for (String number : chunk) {
                                writer.write(number);
                                writer.newLine();
                            }
                        }
                        return null;
                    }));
                }
            }

            // 等待所有写操作完成
            for (Future<Void> future : writeFutures) {
                future.get();
            }

            executorService.shutdown();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
            throw new ServiceException("文件过滤失败！！！");
        }
    }

    /**
     * @description: 判断文件是否存在，不存在创建文件
     * @author: YY
     * @method: createDirectoryIfNotExists
     * @date: 2024/6/27 14:30
     * @param:
     * @param: filePath
     * @return: void
     **/
    public static void createDirectoryIfNotExists(String filePath) throws IOException {
        Path path = Paths.get(filePath).getParent();
        //System.out.println("path = " + path);
        if (path != null && !Files.exists(path)) {
            //System.out.println("path = " + path);
            Files.createDirectories(path);
        }
        Path fileNamePath = Paths.get(filePath);
        if (fileNamePath != null && !Files.exists(fileNamePath)) {
            new File(filePath).createNewFile();
        }
    }

    public static class ServiceException extends RuntimeException {
        public ServiceException(String message) {
            super(message);
        }
    }

    static class PhoneNumberFilterTask implements Callable<Set<String>> {
        private final String filePath;
        private final long startLine;
        private final long size;
        private final String prefix;

        PhoneNumberFilterTask(String filePath, long startLine, long size, String prefix) {
            this.filePath = filePath;
            this.startLine = startLine;
            this.size = size;
            this.prefix = prefix;
        }

        @Override
        public Set<String> call() throws Exception {
            Set<String> filteredNumbers = new HashSet<>();
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath))) {
                // Skip to the starting line
                for (long i = 0; i < startLine; i++) {
                    reader.readLine();
                }

                String line;
                for (long i = 0; i < size && (line = reader.readLine()) != null; i++) {
                    if (line.startsWith(prefix)) {
                        filteredNumbers.add(line);
                    }
                }
            }
            return filteredNumbers;
        }
    }

}
