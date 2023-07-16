package org.wyx.diego.pontifex.context;

public enum PontifexContextInstance {

    INSTANCE(new PontifexContext());

    private PontifexContext pontifexContext;

    PontifexContextInstance(PontifexContext pontifexContext) {
        this.pontifexContext = pontifexContext;
    }

    public PontifexContext getPontifexContext() {
        return pontifexContext;
    }

}
