package org.wyx.diego.pontifex;

/**
 * @author diego
 * @time 2015-07-11
 * @description
 */
public interface FilterChain {

    void doFilter(PontifexRequest pontifexRequest, PontifexResponse pontifexResponse);

}
