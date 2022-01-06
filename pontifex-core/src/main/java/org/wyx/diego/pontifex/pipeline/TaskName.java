package org.wyx.diego.pontifex.pipeline;

/**
 * @author wangyingxin
 * @title: TaskName
 * @projectName pontifex
 * @description: TODO
 * @date 2016/1/3
 */
public interface TaskName {

    TaskName defaultTaskName = new TaskName() {
        public String getTaskName() {
            return "";
        }

        public int sort() {
            return 0;
        }
    };

    String getTaskName();

    int sort();

    public static enum DefaultTaskName implements TaskName {
        DEFAULT_TASK_NAME("default", 0);

        private String taskName;
        private int sort;

        private DefaultTaskName(String taskName, int sort) {
            this.taskName = taskName;
            this.sort = sort;
        }

        public String getTaskName() {
            return "";
        }

        public int sort() {
            return 0;
        }

        public int getSort() {
            return this.sort;
        }
    }

}
