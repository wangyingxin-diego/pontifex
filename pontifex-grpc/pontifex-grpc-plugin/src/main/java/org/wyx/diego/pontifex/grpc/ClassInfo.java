package org.wyx.diego.pontifex.grpc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ClassInfo implements Serializable {

    private String grpcServiceClassPath;
    private String packageName;
    private List<String> imports = new ArrayList<>();
    private String className;
    private String classFullName;
    private String superClassName;

    private String realPipelineServiceName;

    private String pontifexRequestName;

    private String pontfexRequestGenericParamFullName;

    private String pontifexResponseGenericParamFullName;
    private String methodReturnType;
    private String methodReturnTypeGenericParam;
    private String methodParamType;
    private String methodParamTypeGenericParam;

    public String getPackageName() {
        return packageName;
    }

    public ClassInfo setPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public List<String> getImports() {
        return imports;
    }

    public ClassInfo setImports(List<String> imports) {
        this.imports = imports;
        return this;
    }

    public String getClassName() {
        return className;
    }

    public ClassInfo setClassName(String className) {
        this.className = className;
        return this;
    }

    public String getSuperClassName() {
        return superClassName;
    }

    public ClassInfo setSuperClassName(String superClassName) {
        this.superClassName = superClassName;
        return this;
    }

    public String getMethodReturnType() {
        return methodReturnType;
    }

    public ClassInfo setMethodReturnType(String methodReturnType) {
        this.methodReturnType = methodReturnType;
        return this;
    }

    public String getMethodReturnTypeGenericParam() {
        return methodReturnTypeGenericParam;
    }

    public ClassInfo setMethodReturnTypeGenericParam(String methodReturnTypeGenericParam) {
        this.methodReturnTypeGenericParam = methodReturnTypeGenericParam;
        return this;
    }

    public String getMethodParamType() {
        return methodParamType;
    }

    public ClassInfo setMethodParamType(String methodParamType) {
        this.methodParamType = methodParamType;
        return this;
    }

    public String getMethodParamTypeGenericParam() {
        return methodParamTypeGenericParam;
    }

    public ClassInfo setMethodParamTypeGenericParam(String methodParamTypeGenericParam) {
        this.methodParamTypeGenericParam = methodParamTypeGenericParam;
        return this;
    }

    public String getClassFullName() {
        return classFullName;
    }

    public ClassInfo setClassFullName(String classFullName) {
        this.classFullName = classFullName;
        return this;
    }

    public String getRealPipelineServiceName() {
        return realPipelineServiceName;
    }

    public ClassInfo setRealPipelineServiceName(String realPipelineServiceName) {
        this.realPipelineServiceName = realPipelineServiceName;
        return this;
    }

    public String getPontifexRequestName() {
        return pontifexRequestName;
    }

    public ClassInfo setPontifexRequestName(String pontifexRequestName) {
        this.pontifexRequestName = pontifexRequestName;
        return this;
    }

    public String getPontfexRequestGenericParamFullName() {
        return pontfexRequestGenericParamFullName;
    }

    public ClassInfo setPontfexRequestGenericParamFullName(String pontfexRequestGenericParamFullName) {
        this.pontfexRequestGenericParamFullName = pontfexRequestGenericParamFullName;
        return this;
    }

    public String getPontifexResponseGenericParamFullName() {
        return pontifexResponseGenericParamFullName;
    }

    public ClassInfo setPontifexResponseGenericParamFullName(String pontifexResponseGenericParamFullName) {
        this.pontifexResponseGenericParamFullName = pontifexResponseGenericParamFullName;
        return this;
    }

    public String getGrpcServiceClassPath() {
        return grpcServiceClassPath;
    }

    public ClassInfo setGrpcServiceClassPath(String grpcServiceClassPath) {
        this.grpcServiceClassPath = grpcServiceClassPath;
        return this;
    }
}
