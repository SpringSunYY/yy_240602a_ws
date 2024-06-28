package com.yy.manage.domain.vo;

import java.util.List;

/**
 * @Project: WS
 * @Package: com.yy.manage.domain
 * @Author: YY
 * @CreateTime: 2024-06-14  17:52
 * @Description: ReturnTaskVo
 * @Version: 1.0
 */

public class ReturnTaskVo {
    private String status;
    private TaskInfo task_info;

    private String tips;

    private int new_task_status;

    private String task_id;

    public static class TaskInfo {
        private String check_key;
        private String create_time;
        private String is_api;
        private List<String> kefu_list;
        private Integer num_all;
        private Integer num_fail;
        private Integer num_succ;
        private Integer num_wait;
        private String price;
        private String send_time;
        private String status;
        private String task_name;
        private String task_type;
        private String text;
        private List<String> url_list;
        private String user_id;

        public String getCheck_key() {
            return check_key;
        }

        public void setCheck_key(String check_key) {
            this.check_key = check_key;
        }

        public String getCreate_time() {
            return create_time;
        }

        public void setCreate_time(String create_time) {
            this.create_time = create_time;
        }

        public String getIs_api() {
            return is_api;
        }

        public void setIs_api(String is_api) {
            this.is_api = is_api;
        }

        public List<String> getKefu_list() {
            return kefu_list;
        }

        public void setKefu_list(List<String> kefu_list) {
            this.kefu_list = kefu_list;
        }

        public Integer getNum_all() {
            return num_all;
        }

        public void setNum_all(Integer num_all) {
            this.num_all = num_all;
        }

        public Integer getNum_fail() {
            return num_fail;
        }

        public void setNum_fail(Integer num_fail) {
            this.num_fail = num_fail;
        }

        public Integer getNum_succ() {
            return num_succ;
        }

        public void setNum_succ(Integer num_succ) {
            this.num_succ = num_succ;
        }

        public Integer getNum_wait() {
            return num_wait;
        }

        public void setNum_wait(Integer num_wait) {
            this.num_wait = num_wait;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getSend_time() {
            return send_time;
        }

        public void setSend_time(String send_time) {
            this.send_time = send_time;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTask_name() {
            return task_name;
        }

        public void setTask_name(String task_name) {
            this.task_name = task_name;
        }

        public String getTask_type() {
            return task_type;
        }

        public void setTask_type(String task_type) {
            this.task_type = task_type;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public List<String> getUrl_list() {
            return url_list;
        }

        public void setUrl_list(List<String> url_list) {
            this.url_list = url_list;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        @Override
        public String toString() {
            return "TaskInfo{" +
                    "check_key='" + check_key + '\'' +
                    ", create_time='" + create_time + '\'' +
                    ", is_api='" + is_api + '\'' +
                    ", kefu_list=" + kefu_list +
                    ", num_all=" + num_all +
                    ", num_fail=" + num_fail +
                    ", num_succ=" + num_succ +
                    ", num_wait=" + num_wait +
                    ", price='" + price + '\'' +
                    ", send_time='" + send_time + '\'' +
                    ", status='" + status + '\'' +
                    ", task_name='" + task_name + '\'' +
                    ", task_type='" + task_type + '\'' +
                    ", text='" + text + '\'' +
                    ", url_list=" + url_list +
                    ", user_id='" + user_id + '\'' +
                    '}';
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TaskInfo getTask_info() {
        return task_info;
    }

    public void setTask_info(TaskInfo task_info) {
        this.task_info = task_info;
    }

    public int getNew_task_status() {
        return new_task_status;
    }

    public void setNew_task_status(int new_task_status) {
        this.new_task_status = new_task_status;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    @Override
    public String toString() {
        return "ReturnTaskVo{" +
                "status='" + status + '\'' +
                ", task_info=" + task_info +
                ", tips='" + tips + '\'' +
                ", new_task_status=" + new_task_status +
                '}';
    }
}
