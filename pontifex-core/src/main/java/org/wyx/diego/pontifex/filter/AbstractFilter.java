package org.wyx.diego.pontifex.filter;


import org.wyx.diego.pontifex.Filter;
import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.PontifexResponse;

/**
 * @author diego
 * @time 2015-07-11
 * @description
 */
public abstract class AbstractFilter implements Filter {

    abstract void before(final PontifexRequest pontifexRequest, final PontifexResponse pontifexResponse);

    abstract void after(final PontifexRequest pontifexRequest, final PontifexResponse pontifexResponse);

}
