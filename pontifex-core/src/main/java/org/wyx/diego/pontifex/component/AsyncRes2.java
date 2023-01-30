package org.wyx.diego.pontifex.component;

import org.wyx.diego.pontifex.exception.ExceptionCode;
import org.wyx.diego.pontifex.exception.PontifexRuntimeException;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author wangyingxin
 * @title: AsyncRes2
 * @projectName pontifex
 * @description: TODO
 * @date 2016/1/1
 */
public class AsyncRes2<R1, R2> extends Res2<R1, R2> {
    private List<CompletableFuture> completableFutures;

    public AsyncRes2(List<CompletableFuture> completableFutures) {
        super(null, null);
        if (completableFutures == null) {
            throw new NullPointerException();
        } else if (completableFutures.isEmpty()) {
            throw new IllegalArgumentException();
        } else {
            this.completableFutures = completableFutures;
        }
    }

    public R1 getR1() {
        CompletableFuture completableFuture = (CompletableFuture)this.completableFutures.get(0);
        Res1 res = this.get(completableFuture, -1L, (TimeUnit)null);
        R1 r1 = (R1) res.getR1();
        return r1;
    }

    public R2 getR2() {
        CompletableFuture completableFuture = (CompletableFuture)this.completableFutures.get(1);
        Res2 res = (Res2)this.get(completableFuture, -1L, (TimeUnit)null);
        R2 r2 = (R2) res.getR2();
        return r2;
    }

    public R1 getR1(long timeout, TimeUnit unit) {
        CompletableFuture completableFuture = (CompletableFuture)this.completableFutures.get(0);
        Res1 res = this.get(completableFuture, timeout, unit);
        R1 r1 = (R1) res.getR1();
        return r1;
    }

    public R2 getR2(long timeout, TimeUnit unit) {
        CompletableFuture completableFuture = (CompletableFuture)this.completableFutures.get(1);
        Res1 res = this.get(completableFuture, timeout, unit);
        R2 r2 = (R2) ((Res2)res).getR2();
        return r2;
    }

    private Res1 get(CompletableFuture completableFuture, long timeout, TimeUnit unit) {
        Res1 res = null;
        Object exception = null;

        try {
            if (unit != null && timeout > 0L) {
                res = (Res1)completableFuture.get(timeout, unit);
            } else {
                res = (Res1)completableFuture.get();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
            exception = e;
        } catch (ExecutionException executionException) {
            executionException.printStackTrace();
            exception = executionException;
        } catch (TimeoutException timeoutException) {
            timeoutException.printStackTrace();
            exception = timeoutException;
        }

        if (exception != null) {
            throw new PontifexRuntimeException(ExceptionCode.EXCEPTION_CODE_COMPONENT_GET);
        } else {
            return res;
        }
    }
}
