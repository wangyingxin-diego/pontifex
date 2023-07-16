package org.wyx.diego.pontifex.alarm.biz;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyx.diego.pontifex.alarm.AlarmMessage;
import org.wyx.diego.pontifex.alarm.biz.feishu.FeiShuSendMessage;
import org.wyx.diego.pontifex.annotation.Alarm;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AlarmManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlarmManager.class);

    private static final BlockingQueue BLOCKING_QUEUE = new ArrayBlockingQueue(10000);
    private static final ThreadFactory THREAD_FACTORY = new AlarmThreadFactory("pontifex-alarm");
    private static final ExecutorService executorService = new ThreadPoolExecutor(10, 10, 1, TimeUnit.HOURS, BLOCKING_QUEUE, THREAD_FACTORY, new ThreadPoolExecutor.AbortPolicy());

    private FeiShuSendMessage feiShuSendMessage;

    public final boolean submit(AlarmMessage message) {

        executorService.submit(() -> {

            List<Alarm> alarms = message.getAlarms();
            if(alarms == null || alarms.size() == 0) {
                return;
            }
            boolean log = false;
            for(Alarm alarm : alarms) {
                switch (alarm) {

                    case LOG: {
                        handleLog(message);
                        log = true;
                        break;
                    }
                    case FEI_SHU_NOW: {
                        handleFeiShuNow(message);
                        break;
                    }
                    default: {
                        if(!log) {
                            handleLog(message);
                        }
                    }

                }

            }


        });


        return true;
    }

    private void handleLog(AlarmMessage message) {
        LOGGER.error("pontifex-alarm name={}, expectedTime={}, actualTime={}", message.getName(), message.getExpectedTime(), message.getActualTime());
    }

    private void handleFeiShuNow(AlarmMessage message) {

        if(feiShuSendMessage == null) {
            return;
        }

        feiShuSendMessage.sendMessage(message);

    }



    private static final class AlarmThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        AlarmThreadFactory(String name) {
            SecurityManager s = System.getSecurityManager();
            this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
            this.namePrefix = "pool-" + name + "-" + poolNumber.getAndIncrement() + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }

            if (t.getPriority() != 5) {
                t.setPriority(5);
            }

            return t;
        }
    }

    public FeiShuSendMessage getFeiShuSendMessage() {
        return feiShuSendMessage;
    }

    public AlarmManager setFeiShuSendMessage(FeiShuSendMessage feiShuSendMessage) {
        this.feiShuSendMessage = feiShuSendMessage;
        return this;
    }
}
