package org.wyx.diego.pontifex.filter;

import com.google.common.collect.Lists;
import org.wyx.diego.pontifex.FilterChain;
import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.PontifexResponse;


import java.util.List;

public class StandardPipelineFilerChain implements FilterChain {

    private List<AbstractFilter> filters = Lists.newArrayList(new CacheFilter(), new EncryptionDecryptionFilter());

    @Override
    public void doBefore(PontifexRequest pontifexRequest, PontifexResponse pontifexResponse) {
        for(AbstractFilter abstractFilter : filters) {
            abstractFilter.before(pontifexRequest, pontifexResponse);
        }

    }

    @Override
    public void doAfter(PontifexRequest pontifexRequest, PontifexResponse pontifexResponse) {
        for(AbstractFilter abstractFilter : filters) {
            abstractFilter.after(pontifexRequest, pontifexResponse);
        }
    }
}
