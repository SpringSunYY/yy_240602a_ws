package com.yy.manage.domain.vo;

/**
 * @Project: WS
 * @Package: com.yy.manage.domain.vo
 * @Author: YY
 * @CreateTime: 2024-06-19  16:01
 * @Description: ExcelFileDto
 * @Version: 1.0
 */
public class ExcelFileDto {
    private String discardedFilePath;

    private String taskId;

    private String getFileUrl;

    public String getDiscardedFilePath() {
        return discardedFilePath;
    }

    public void setDiscardedFilePath(String discardedFilePath) {
        this.discardedFilePath = discardedFilePath;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getGetFileUrl() {
        return getFileUrl;
    }

    public void setGetFileUrl(String getFileUrl) {
        this.getFileUrl = getFileUrl;
    }
}
