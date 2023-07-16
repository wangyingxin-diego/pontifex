package org.wyx.diego.pontifex.flow.sentinel;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.Tracer;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.PontifexResponse;
import org.wyx.diego.pontifex.flow.FlowDeGradePipelineInterface;
import org.wyx.diego.pontifex.flow.FlowModel;
import org.wyx.diego.pontifex.flow.annotation.DeGrade;
import org.wyx.diego.pontifex.flow.annotation.Flow;
import org.wyx.diego.pontifex.flow.annotation.FlowDeGrade;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class SentinelFlowDeGradePipelineInterface implements FlowDeGradePipelineInterface {

    private static final Logger LOGGER = LoggerFactory.getLogger(SentinelFlowDeGradePipelineInterface.class);
    @Override
    public void init(String name, FlowDeGrade flowDeGrade) {

        Flow flow = flowDeGrade.flow();
        initFlow(name, flow);
        DeGrade deGrade = flowDeGrade.deGrade();
        initDeGrade(name, deGrade);

    }

    private void initFlow(String resourceName, Flow flow) {
        List<FlowRule> rules = new ArrayList<>();
        FlowRule rule = new FlowRule(resourceName);
        rule.setCount(flow.count());
        if(flow.model() == FlowModel.QPS) {
            rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
        }
        rule.setLimitApp("default");
        rules.add(rule);
        FlowRuleManager.loadRules(rules);
    }
    private void initDeGrade(String resourceName, DeGrade deGrade) {
        List<DegradeRule> rules = new ArrayList<>();
        DegradeRule rule = new DegradeRule();
        rule.setResource(resourceName);
        rule.setCount(deGrade.rt());
        rule.setGrade(RuleConstant.DEGRADE_GRADE_RT);
        rule.setTimeWindow(deGrade.window());
        rules.add(rule);
        DegradeRuleManager.loadRules(rules);
    }

    @Override
    public PontifexResponse<?> invoke(PontifexRequest pontifexRequest, Function<PontifexRequest<?>, PontifexResponse<?>> function, Function<PontifexRequest<?>, PontifexResponse<?>> fallback) {

        Entry entry = null;
        try {
            entry = SphU.entry(pontifexRequest.getBizKey());
            return function.apply(pontifexRequest);
        } catch (BlockException ex) {
            return handleBlockException(ex, fallback, pontifexRequest);
        } catch (Exception ex) {
            Tracer.traceEntry(ex, entry);
        } finally {
            if (entry != null) {
                entry.exit();
            }
        }

        return null;
    }

    private PontifexResponse<?> handleBlockException(BlockException blockException, Function<PontifexRequest<?>, PontifexResponse<?>> fallback, PontifexRequest pontifexRequest) {

        LOGGER.error("pontifex flow", blockException);

        if(blockException instanceof FlowException) {
            throw new RuntimeException(blockException);
        }
        if(blockException instanceof DegradeException) {
            return fallback.apply(pontifexRequest);
        }
        if(blockException instanceof SystemBlockException) {

        }

        throw new RuntimeException(blockException);

    }

    private void handleException(Exception exception) {

    }

}
