package org.wyx.diego.pontifex.loader.handler.invoke;

public class Context {

    private boolean filer = false;

    private Object object;

    public boolean isFiler() {
        return filer;
    }

    public void setFiler(boolean filer) {
        this.filer = filer;
    }

    public Object getObject() {
        return object;
    }

    public Context setObject(Object object) {
        this.object = object;
        return this;
    }
}
