package org.wyx.diego.pontifex.grpc;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.artifact.handler.manager.ArtifactHandlerManager;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectHelper;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.dependency.graph.DependencyCollectorBuilder;
import org.apache.maven.shared.dependency.graph.DependencyCollectorBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.invoker.*;
import org.apache.maven.toolchain.ToolchainManager;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;
import org.eclipse.aether.RepositorySystem;
import org.wyx.diego.pontifex.PontifexRequest;
import org.wyx.diego.pontifex.Request;
import org.wyx.diego.pontifex.Response;
import org.wyx.diego.pontifex.grpc.base.anotation.GrpcService;
import sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl;

import javax.tools.*;
import java.io.*;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

import static org.wyx.diego.pontifex.grpc.PontifexConstant.CLASS_INFO_KEY;

/**
 * @requiresDependencyResolution compile
 */
@Mojo( name = "genProto", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresDependencyResolution = ResolutionScope.TEST, requiresDependencyCollection=ResolutionScope.TEST)
//@Execute(goal = "org.apache.maven.plugins:maven-dependency-plugin:list", phase = LifecyclePhase.PROCESS_RESOURCES)
public class GenProtoMojo extends AbstractMojo {

    private static final List<String> DEFAULT_IMPORTS = new ArrayList() {
        {
            add("import io.grpc.stub.StreamObserver;");
            add("import net.devh.boot.grpc.server.service.GrpcService;");
            add("import org.wyx.diego.pontifex.PontifexRequest;");
            add("import org.wyx.diego.pontifex.PontifexResponse;");
            add("import javax.annotation.Resource;");
            add("import java.lang.reflect.Method;");
            add("import java.lang.reflect.Type;");

        }
    };

    private static final String FILE_SEPARATOR = File.separator;
    private static final String LINE_SEPARATOR = "\n";
    private static final String SOURCE_PATH = "src" + FILE_SEPARATOR + "main" + FILE_SEPARATOR + "java";
    private static final String PROTO_PATH = "src" + FILE_SEPARATOR + "main" + FILE_SEPARATOR + "proto";
    private static final String TEMP_PATH = "src" + FILE_SEPARATOR + "main" + FILE_SEPARATOR + "temp";
    private static final String TEMP_CLASSES_PATH = TEMP_PATH + FILE_SEPARATOR + "classes";

    /**
     * @parameter expression="${project.build.directory}/src/main/java/org/wyx/diego/pontifex"
     */
    private File javasDirectory;

    private File classesDirectory;

    private File outputDirectory;

    @Parameter
    private String[] usedDependencies;

    /**
     * The computed dependency tree root node of the Maven project.
     */
    private DependencyNode rootNode;

    @Component
    private PlexusContainer plexusContainer;


    /**
     * The dependency collector builder to use.
     */
    @Component(hint = "default")
    private DependencyCollectorBuilder dependencyCollectorBuilder;

    /**
     * The dependency graph builder to use.
     */
    @Component(hint = "default")
    private DependencyGraphBuilder dependencyGraphBuilder;


    @Parameter(property = "analyzer", defaultValue = "default")
    private String analyzer;

    @Parameter(property = "scope")
    private String scope;

    /**
     * @parameter expression="${project}"
     * @required
     */
    @Parameter(defaultValue = "${project}")
    private MavenProject project;
    /**
     * @component
     */
    @Component
    private MavenProjectHelper projectHelper;

    @Parameter( defaultValue = "${project.compileClasspathElements}", readonly = true, required = true )
    private List<String> compilePath;

    @Parameter(property = "maven.compiler.maxmem")
    private String maxmem;

    /**
     * <p>
     * Sets whether annotation processing is performed or not. Only applies to JDK 1.6+
     * If not set, both compilation and annotation processing are performed at the same time.
     * </p>
     * <p>Allowed values are:</p>
     * <ul>
     * <li><code>none</code> - no annotation processing is performed.</li>
     * <li><code>only</code> - only annotation processing is done, no compilation.</li>
     * </ul>
     *
     * @since 2.2
     */
    @Parameter
    private String proc;

    /**
     * <p>
     * Names of annotation processors to run. Only applies to JDK 1.6+
     * If not set, the default annotation processors discovery process applies.
     * </p>
     *
     * @since 2.2
     */
    @Parameter
    private String[] annotationProcessors;

    /**
     * <p>
     * Classpath elements to supply as annotation processor path. If specified, the compiler will detect annotation
     * processors only in those classpath elements. If omitted, the default classpath is used to detect annotation
     * processors. The detection itself depends on the configuration of {@code annotationProcessors}.
     * </p>
     * <p>
     * Each classpath element is specified using their Maven coordinates (groupId, artifactId, version, classifier,
     * type). Transitive dependencies are added automatically. Exclusions are supported as well. Example:
     * </p>
     *
     * <pre>
     * &lt;configuration&gt;
     *   &lt;annotationProcessorPaths&gt;
     *     &lt;path&gt;
     *       &lt;groupId&gt;org.sample&lt;/groupId&gt;
     *       &lt;artifactId&gt;sample-annotation-processor&lt;/artifactId&gt;
     *       &lt;version&gt;1.2.3&lt;/version&gt; &lt;!-- Optional - taken from dependency management if not specified --&gt;
     *       &lt;!-- Optionally exclude transitive dependencies --&gt;
     *       &lt;exclusions&gt;
     *         &lt;exclusion&gt;
     *           &lt;groupId&gt;org.sample&lt;/groupId&gt;
     *           &lt;artifactId&gt;sample-dependency&lt;/artifactId&gt;
     *         &lt;/exclusion&gt;
     *       &lt;/exclusions&gt;
     *     &lt;/path&gt;
     *     &lt;!-- ... more ... --&gt;
     *   &lt;/annotationProcessorPaths&gt;
     * &lt;/configuration&gt;
     * </pre>
     *
     * <b>Note:</b> Exclusions are supported from version 3.11.0.
     *
     * @since 3.5
     */
//    @Parameter
//    private List<DependencyCoordinate> annotationProcessorPaths;

    /**
     * <p>
     * Whether to use the Maven dependency management section when resolving transitive dependencies of annotation
     * processor paths.
     * </p>
     * <p>
     * This flag does not enable / disable the ability to resolve the version of annotation processor paths
     * from dependency management section. It only influences the resolution o transitive dependencies of those
     * top-level paths.
     * </p>
     *
     * @since 3.12.0
     */
    @Parameter(defaultValue = "false")
    private boolean annotationProcessorPathsUseDepMgmt;

    /**
     * Sets the name of the output file when compiling a set of
     * sources to a single file.
     * <p/>
     * expression="${project.build.finalName}"
     */
    @Parameter
    private String outputFileName;

    /**
     * Keyword list to be appended to the <code>-g</code> command-line switch. Legal values are none or a
     * comma-separated list of the following keywords: <code>lines</code>, <code>vars</code>, and <code>source</code>.
     * If debug level is not specified, by default, nothing will be appended to <code>-g</code>.
     * If debug is not turned on, this attribute will be ignored.
     *
     * @since 2.1
     */
    @Parameter(property = "maven.compiler.debuglevel")
    private String debuglevel;

    /**
     * Keyword to be appended to the <code>-implicit:</code> command-line switch.
     *
     * @since 3.10.2
     */
    @Parameter(property = "maven.compiler.implicit")
    private String implicit;

    /**
     *
     */
    @Component
    private ToolchainManager toolchainManager;

    /**
     * <p>
     * Specify the requirements for this jdk toolchain for using a different {@code javac} than the one of the JRE used
     * by Maven. This overrules the toolchain selected by the
     * <a href="https://maven.apache.org/plugins/maven-toolchains-plugin/">maven-toolchain-plugin</a>.
     * </p>
     * (see <a href="https://maven.apache.org/guides/mini/guide-using-toolchains.html"> Guide to Toolchains</a> for more
     * info)
     *
     * <pre>
     * &lt;configuration&gt;
     *   &lt;jdkToolchain&gt;
     *     &lt;version&gt;11&lt;/version&gt;
     *   &lt;/jdkToolchain&gt;
     *   ...
     * &lt;/configuration&gt;
     *
     * &lt;configuration&gt;
     *   &lt;jdkToolchain&gt;
     *     &lt;version&gt;1.8&lt;/version&gt;
     *     &lt;vendor&gt;zulu&lt;/vendor&gt;
     *   &lt;/jdkToolchain&gt;
     *   ...
     * &lt;/configuration&gt;
     * </pre>
     * <strong>note:</strong> requires at least Maven 3.3.1
     *
     * @since 3.6
     */
    @Parameter
    private Map<String, String> jdkToolchain;

    // ----------------------------------------------------------------------
    // Read-only parameters
    // ----------------------------------------------------------------------

    /**
     * The directory to run the compiler from if fork is true.
     */
    @Parameter(defaultValue = "${basedir}", required = true, readonly = true)
    private File basedir;

    /**
     * The target directory of the compiler if fork is true.
     */
    @Parameter(defaultValue = "${project.build.directory}", required = true, readonly = true)
    private File buildDirectory;


    /**
     * The current build session instance. This is used for toolchain manager API calls.
     */
    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    private MavenSession session;


    /**
     * Strategy to re use javacc class created:
     * <ul>
     * <li><code>reuseCreated</code> (default): will reuse already created but in case of multi-threaded builds, each
     * thread will have its own instance</li>
     * <li><code>reuseSame</code>: the same Javacc class will be used for each compilation even for multi-threaded build
     * </li>
     * <li><code>alwaysNew</code>: a new Javacc class will be created for each compilation</li>
     * </ul>
     * Note this parameter value depends on the os/jdk you are using, but the default value should work on most of env.
     *
     * @since 2.5
     */
    @Parameter(defaultValue = "${reuseCreated}", property = "maven.compiler.compilerReuseStrategy")
    private String compilerReuseStrategy = "reuseCreated";

    /**
     * @since 2.5
     */
    @Parameter(defaultValue = "false", property = "maven.compiler.skipMultiThreadWarning")
    private boolean skipMultiThreadWarning;

    /**
     * compiler can now use javax.tools if available in your current jdk, you can disable this feature
     * using -Dmaven.compiler.forceJavacCompilerUse=true or in the plugin configuration
     *
     * @since 3.0
     */
    @Parameter(defaultValue = "false", property = "maven.compiler.forceJavacCompilerUse")
    private boolean forceJavacCompilerUse;

    /**
     * @since 3.0 needed for storing the status for the incremental build support.
     */
    @Parameter(defaultValue = "${mojoExecution}", readonly = true, required = true)
    private MojoExecution mojoExecution;

    /**
     * File extensions to check timestamp for incremental build.
     * Default contains only <code>class</code> and <code>jar</code>.
     *
     * @since 3.1
     */
    @Parameter
    private List<String> fileExtensions;

    /**
     * <p>to enable/disable incremental compilation feature.</p>
     * <p>This leads to two different modes depending on the underlying compiler. The default javac compiler does the
     * following:</p>
     * <ul>
     * <li>true <strong>(default)</strong> in this mode the compiler plugin determines whether any JAR files the
     * current module depends on have changed in the current build run; or any source file was added, removed or
     * changed since the last compilation. If this is the case, the compiler plugin recompiles all sources.</li>
     * <li>false <strong>(not recommended)</strong> this only compiles source files which are newer than their
     * corresponding class files, namely which have changed since the last compilation. This does not
     * recompile other classes which use the changed class, potentially leaving them with references to methods that no
     * longer exist, leading to errors at runtime.</li>
     * </ul>
     *
     * @since 3.1
     */
    @Parameter(defaultValue = "true", property = "maven.compiler.useIncrementalCompilation")
    private boolean useIncrementalCompilation = true;

    /**
     * Package info source files that only contain javadoc and no annotation on the package
     * can lead to no class file being generated by the compiler.  This causes a file miss
     * on the next compilations and forces an unnecessary recompilation. The default value
     * of <code>true</code> causes an empty class file to be generated.  This behavior can
     * be changed by setting this parameter to <code>false</code>.
     *
     * @since 3.10
     */
    @Parameter(defaultValue = "true", property = "maven.compiler.createMissingPackageInfoClass")
    private boolean createMissingPackageInfoClass = true;

    @Parameter(defaultValue = "false", property = "maven.compiler.showCompilationChanges")
    private boolean showCompilationChanges = false;
    /**
     * Resolves the artifacts needed.
     */
    @Component
    private RepositorySystem repositorySystem;

    /**
     * Artifact handler manager.
     */
    @Component
    private ArtifactHandlerManager artifactHandlerManager;

    @Parameter(defaultValue = "${plugin}", readonly = true)
    PluginDescriptor pluginDescriptor;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {


        MavenProject project = (MavenProject) getPluginContext().get("project");
        if("pom".equals(project.getPackaging())) {
            return;
        }
        System.out.println(JSONObject.toJSONString(project.getName()));
        String localRepository = project.getProjectBuildingRequest().getLocalRepository().getBasedir();
        System.out.println(localRepository);
        String classPath;
        try {
            classPath = genClassPath(project, localRepository);
        } catch (DependencyResolutionRequiredException e) {
            throw new RuntimeException(e);
        }

        System.err.println("dddddddddddddddddddd");

//        File file = new File("/Users/wangyingxin/Documents/workspace_github/pontifex/pontifex-demo/src/main/java/org/wyx/diego/pontifex");
        List<File> classPathFileList = null;
        try {
            classPathFileList = genClassPathFileList(project, localRepository);
        } catch (DependencyResolutionRequiredException e) {
            throw new RuntimeException(e);
        }

        handleMavenProject(project, classPath, classPathFileList);


    }

    private void handleMavenProject(MavenProject parentMavenProject, String classPath, List<File> jarFileList) {

        if("pom".equals(parentMavenProject.getPackaging())) {
            return;
        }

        if("jar".equals(parentMavenProject.getPackaging())) {
            File file1 = parentMavenProject.getBasedir();
            genProjectGrpc(file1, classPath, jarFileList);
        }

    }

    private void genProjectGrpc(File file, String classPath, List<File> classPathFile) {

        List<String> resultFileName = new ArrayList<>();
        ergodic(file, resultFileName);
        String basePath = file.getPath();

        File dir = new File(basePath + File.separator + TEMP_PATH);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(new File(dir + File.separator + "temp.txt")), "UTF-8"))) {
            for(String fileName : resultFileName) {
                writer.write(fileName);
                writer.newLine();
            }
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String genClassesPath = basePath + FILE_SEPARATOR + TEMP_CLASSES_PATH;
        ClassLoader classLoader = getClassLoader(classPath);
        Thread.currentThread().setContextClassLoader(classLoader);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
//        String[] temp = {"-d", genClassesPath, "-classpath", classPath, "-sourcepath", basePath+"/src/main/java", "@"+basePath+"/src/main/temp/temp.txt"};
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        try {
            fileManager.setLocation(StandardLocation.CLASS_PATH, classPathFile);
            List<File> outputFilePath = new ArrayList<>();
            File opPath = new File(genClassesPath);
            outputFilePath.add(opPath);
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, outputFilePath);
            fileManager.setLocation(StandardLocation.SOURCE_PATH, Lists.newArrayList(new File(basePath+FILE_SEPARATOR+SOURCE_PATH)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Iterable<? extends JavaFileObject> files = fileManager.getJavaFileObjectsFromStrings(resultFileName);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, null, null, files);

        Boolean result = task.call();
        if (result == true) {
            System.out.println("Succeeded");
        }

        List<ClassInfo> classInfos = new ArrayList<>();
        List<String> className = new ArrayList<>();
        String fileOrigin = null;
        for(String fileName : resultFileName) {
            fileOrigin = fileName;
            int i = fileName.indexOf(SOURCE_PATH);
            fileName = fileName.substring(i+14, fileName.lastIndexOf(".java"));
            fileName = fileName.replaceAll(File.separator, ".");
            className.add(fileName);
            Class<?> clazz;
            try {
                clazz = classLoader.loadClass(fileName);
                GrpcService grpcService = clazz.getAnnotation(GrpcService.class);
                if(grpcService == null) {
                    continue;
                }

                String name = grpcService.name();
                fileOrigin = fileOrigin.substring(0, fileOrigin.lastIndexOf(FILE_SEPARATOR)) + FILE_SEPARATOR;
                String serviceName = genServiceName(name);
                String ftlServiceName = genFtlServiceName(name);
                String grpcServiceClassPath = fileOrigin + ftlServiceName + ".java";

                Method method = clazz.getDeclaredMethod("call", PontifexRequest.class);
                if(method == null) {
                    continue;
                }

                String packageName = getGrpcPackage(clazz);
                ClassInfo classInfo = new ClassInfo();
                classInfo.setGrpcServiceClassPath(grpcServiceClassPath);
                classInfo.setPackageName(clazz.getPackage().getName());
                classInfo.setClassName(ftlServiceName);
                classInfo.setRealPipelineServiceName(clazz.getName());

                Type[] type = method.getGenericParameterTypes();
                ParameterizedTypeImpl parameterizedType = (ParameterizedTypeImpl) type[0];
                ParameterizedTypeImpl genericReturnType = (ParameterizedTypeImpl) method.getGenericReturnType();

                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                Class pontfexRequestGenericParamClass = (Class) actualTypeArguments[0];
                String pontfexRequestGenericParamFullName = pontfexRequestGenericParamClass.getName();
                classInfo.setPontfexRequestGenericParamFullName(pontfexRequestGenericParamFullName);
                Class requestParamTypeParamClass = parameterizedType.getRawType();
                String head = genHead(clazz);

                JavaToProto javaToProto = new JavaToProto();
                String protoFile = javaToProto.genProtoFile(requestParamTypeParamClass, Request.class, pontfexRequestGenericParamClass);

                Type[] genericReturnTypeActualTypeArguments = genericReturnType.getActualTypeArguments();
                Class pontifexResponseGenericParamClass = (Class) genericReturnTypeActualTypeArguments[0];
                String pontifexResponseGenericParamFullName = pontifexResponseGenericParamClass.getName();
                classInfo.setPontifexResponseGenericParamFullName(pontifexResponseGenericParamFullName);
                classInfos.add(classInfo);
                Class genericReturnTypeRawType = genericReturnType.getRawType();
                JavaToProto javaToProtoOfResponse = new JavaToProto();
                String protoFileOfResponse = javaToProtoOfResponse.genProtoFile(genericReturnTypeRawType, Response.class, pontifexResponseGenericParamClass);

                String superClass = genServiceSuperClass(serviceName);
                classInfo.setSuperClassName(superClass);
                genGrpcServiceClass(classInfo);

                String service = genService(name);
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(head).append(LINE_SEPARATOR);
                stringBuilder.append(protoFile).append(LINE_SEPARATOR);
                stringBuilder.append(protoFileOfResponse).append(LINE_SEPARATOR);
                stringBuilder.append(service);

                String fileStr = stringBuilder.toString();

                String protoFilePath = basePath + FILE_SEPARATOR + PROTO_PATH;
                String protoFileName = name + ".proto";
                writeProtoFile(fileStr, protoFilePath, protoFileName);

                System.out.println(fileStr);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        if(classInfos.size() < 1) {
            return;
        }
        Context context = plexusContainer.getContext();
        try {
            boolean contains = context.contains(CLASS_INFO_KEY);
            if(!contains) {
                List<ClassInfo> init = new ArrayList<>();
                context.put(CLASS_INFO_KEY, init);
            }
            List<ClassInfo> classInfoList = (List<ClassInfo>) context.get(CLASS_INFO_KEY);
            classInfoList.addAll(classInfos);
        } catch (ContextException e) {
            throw new RuntimeException(e);
        }


    }

    private void writeProtoFile(String protoFileContent, String protoFilePath, String protoFileName) {

        File protoPath = new File(protoFilePath);
        if(!protoPath.exists()) {
            protoPath.mkdirs();
        }

        String fileName = protoFilePath+"/"+protoFileName;
        File file = new File(fileName);
        if(file.exists()) {
            System.out.println("delete -----------" + fileName);
            file.delete();
        }

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(new File(protoFilePath+"/"+protoFileName)), "UTF-8"))) {
            writer.write(protoFileContent);
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    private static String genHead(Class clazz) {

        String packageName = genPackageName(clazz);
        StringBuilder sb = new StringBuilder();
        sb.append("syntax = \"proto3\";").append("\n");
        sb.append("package ").append(packageName).append(";").append("\n");
        sb.append("option java_package = ").append("\""+packageName+"\";").append("\n");
        sb.append("option java_multiple_files = true;").append("\n");
        sb.append("option java_generic_services = true;").append("\n");
        sb.append("option optimize_for = SPEED;").append("\n");

        return sb.toString();

    }

    private static String genPackageName(Class clazz) {

        String packageName = clazz.getPackage().getName();
        StringBuilder sb = new StringBuilder(packageName);
        sb.append(".").append("grpc");

        return sb.toString();

    }

    private static String genService(String serviceName) {
        StringBuilder sb = new StringBuilder();
        serviceName = genServiceName(serviceName);
        sb.append("service ").append(serviceName).append(" { ").append("\n");
        sb.append("    rpc call(PontifexRequest) returns (PontifexResponse){}").append("\n");
        sb.append("}");
        return sb.toString();
    }

    private static String genServiceName(String serviceName) {

        char[] chars = serviceName.toCharArray();
        chars[0] = toUpperCase(chars[0]);
        serviceName = String.valueOf(chars);
        serviceName += "GrpcPipeline";
        return serviceName;

    }

    private static String genFtlServiceName(String serviceName) {

        String original = genServiceName(serviceName);
        return "Pontifex" + original;

    }

    private List<String> ergodic(File file, List<String> resultFileName) {
        File[] files = file.listFiles();
        if (files == null)
            return resultFileName;
        for (File f : files) {
            if (f.isDirectory()) {
                ergodic(f, resultFileName);
            } else {
                if(!f.getName().endsWith(".java")) {
                    continue;
                }
                resultFileName.add(f.getPath());
            }
        }
        return resultFileName;

    }

    public static ClassLoader createClassLoader(String classpath, ClassLoader parent) throws SecurityException {
        String[] names = classpath.split(";");
        URL[] urls = new URL[names.length];

        try {
            for(int i = 0; i < urls.length; ++i) {
                urls[i] = (new File(names[i])).toURL();
            }
        } catch (MalformedURLException var5) {
            throw new IllegalArgumentException("Unable to parse classpath: " + classpath);
        }

        return new URLClassLoader(urls, parent);
    }

    private String getCompileClasspath() throws MojoExecutionException, DependencyResolutionRequiredException {
        try {
            // get the union of compile- and runtime classpath elements
            Set dependencySet = new LinkedHashSet();
            File file = project.getBasedir();
            List list = project.getCompileClasspathElements();
            dependencySet.add( file.getPath());
            dependencySet.add( classesDirectory.getAbsolutePath() );
            String compileClasspath = StringUtils.join( dependencySet, File.pathSeparator );

            return compileClasspath;
        }
        catch (Exception e) {
            throw new MojoExecutionException( e.getMessage(), e );
        }
    }

    private String genClassPath(MavenProject project, String classPathPre) throws DependencyResolutionRequiredException {

        StringBuilder sb = new StringBuilder(".;");

        List<MavenProject> list = project.getCollectedProjects();
        if("jar".equals(project.getPackaging())) {
            list.add(project);
        }
        for(MavenProject mavenProject : list) {
            File file = mavenProject.getBasedir();
            String basePath = file.getPath();
            sb.append(basePath+FILE_SEPARATOR+SOURCE_PATH).append(";");
            List<String> compileClasspathElements = project.getCompileClasspathElements();
            for(String cp : compileClasspathElements) {
                sb.append(cp).append(";");
            }
            String tempClasses = basePath+FILE_SEPARATOR+TEMP_CLASSES_PATH;
            sb.append(tempClasses).append(";");
            File tempClassesFile = new File(tempClasses);
            if(!tempClassesFile.exists()) {
                tempClassesFile.mkdirs();
            }
        }

        List<Artifact> artifacts = resolveAllJArtifacts();
        for (Artifact artifact : artifacts) {
            String groupId = artifact.getGroupId();
            String groupIdPath = groupId.replace(".", "/");
            String artifactId = artifact.getArtifactId();
            String version = artifact.getVersion();
            String jarName = artifactId + "-" + version + ".jar";
            String path = classPathPre + File.separator + groupIdPath + File.separator + artifactId + File.separator + version + File.separator + jarName;
            sb.append(path).append(";");
        }
        String classPath = sb.toString();
        classPath = classPath.substring(0, classPath.length()-1);
        System.out.println(classPath);
        return classPath;
    }

    private List<File> genClassPathFileList(MavenProject project, String classPathPre) throws DependencyResolutionRequiredException {

        List<File> files = new ArrayList<>();
        List<MavenProject> list = project.getCollectedProjects();
        if("jar".equals(project.getPackaging())) {
            list.add(project);
        }
        for(MavenProject mavenProject : list) {
            File file = mavenProject.getBasedir();
            files.add(file);
            String basePath = file.getPath();
            File javaPath = new File(basePath+FILE_SEPARATOR+SOURCE_PATH);
            files.add(javaPath);

            List<String> compileClasspathElements = project.getCompileClasspathElements();
            for(String cp : compileClasspathElements) {
                File cpPath = new File(cp);
                files.add(cpPath);
            }
            String tempClasses = basePath+FILE_SEPARATOR+TEMP_CLASSES_PATH;
            File tempClassesFile = new File(tempClasses);
            files.add(tempClassesFile);
            if(!tempClassesFile.exists()) {
                tempClassesFile.mkdirs();
            }


        }

        List<Artifact> artifacts = resolveAllJArtifacts();
//        Map<String, Artifact> map = project.getManagedVersionMap();
        for (Artifact artifact : artifacts) {
            String groupId = artifact.getGroupId();
            String groupIdPath = groupId.replace(".", "/");
            String artifactId = artifact.getArtifactId();
            String version = artifact.getVersion();
            String jarName = artifactId + "-" + version + ".jar";
            String path = classPathPre + FILE_SEPARATOR + groupIdPath + FILE_SEPARATOR + artifactId + FILE_SEPARATOR + version + FILE_SEPARATOR + jarName;
            File jarPath = new File(path);
            files.add(jarPath);
        }

        return files;
    }

    private ClassLoader getClassLoader(String classPath) {
        try {
            // 所有的类路径环境，也可以直接用 compilePath
            String[] classPaths = classPath.split(";");
            // 转为 URL 数组
            URL urls[] = new URL[classPaths.length];
            for (int i = 0; i < classPaths.length; i++)
            {
                urls[i] = new File(classPaths[i]).toURL();
            }
            // 自定义类加载器
            return new URLClassLoader( urls, this.getClass().getClassLoader() );
        } catch (Exception e) {
            getLog().debug( "Couldn't get the classloader." );
            return this.getClass().getClassLoader();
        }
    }

    private ClassLoader getClassLoader(MavenProject project) {
        try {
            // 所有的类路径环境，也可以直接用 compilePath
            List classpathElements = project.getCompileClasspathElements();
            classpathElements.add( project.getBuild().getOutputDirectory() );
            classpathElements.add( project.getBuild().getTestOutputDirectory() );
            // 转为 URL 数组
            URL urls[] = new URL[classpathElements.size()];
            for ( int i = 0; i < classpathElements.size(); ++i )
            {
                urls[i] = new File( (String) classpathElements.get( i ) ).toURL();
            }
            // 自定义类加载器
            return new URLClassLoader( urls, this.getClass().getClassLoader() );
        } catch (Exception e) {
            getLog().debug( "Couldn't get the classloader." );
            return this.getClass().getClassLoader();
        }
    }

    private void rev() {

        String mavenHome = (String) project.getProjectBuildingRequest().getSystemProperties().get("maven.home");
        InvocationRequest request = new DefaultInvocationRequest();
        File file = project.getCollectedProjects().get(0).getFile();
        request.setPomFile( file );
        request.setGoals( Collections.singletonList( "dependency:list" ) );


        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(mavenHome));
        invoker.setWorkingDirectory(file.getParentFile());

//invoker.setLogger(new PrintStreamLogger(System.err,  InvokerLogger.ERROR){
//
//} );
//invoker.setOutputHandler(new InvocationOutputHandler() {
//    @Override
//    public void consumeLine(String s) throws IOException {
//
//        System.out.println(s);
//    }
//});


        try
        {
            InvocationResult invocationResult = invoker.execute( request );
            System.out.println("ddddddd");
        }
        catch (MavenInvocationException e)
        {
            e.printStackTrace();
        }

    }

    private static void RunCommand() {
        //String command = "mvn --version";
        String command = " cd /Users/wangyingxin/Documents/workspace_liantong_git/pcs/pcs-restapi && mvn dependency:list";
        try {
            boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
            ProcessBuilder builder = new ProcessBuilder();
            builder.redirectErrorStream(true);
            if (isWindows) {
                builder.command("cmd.exe", "/c", command);
            } else {
                builder.command("sh", "-c", command);
            }
            Process process = builder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void genggg() {

//        ResolvePathsRequest<File> request = ResolvePathsRequest.ofFiles(dependencyArtifacts)
//                .setIncludeStatic(true)
//                .setMainModuleDescriptor(moduleDescriptorPath);
//
//        Toolchain toolchain = getToolchain();
//        if (toolchain instanceof DefaultJavaToolChain) {
//            request.setJdkHome(new File(((DefaultJavaToolChain) toolchain).getJavaHome()));
//        }
//
//        resolvePathsResult = locationManager.resolvePaths(request);
    }

    private ArtifactFilter createResolvingArtifactFilter() {
        ArtifactFilter filter;

        // filter scope
        if (scope != null) {
            getLog().debug("+ Resolving dependency tree for scope '" + scope + "'");

            filter = new ScopeArtifactFilter(scope);
        } else {
            filter = null;
        }

        return filter;
    }

    private List<Artifact> resolveAllJArtifacts() {

        List<MavenProject> mavenProjectList = new ArrayList<>();
        MavenProject mavenProject = project;
        resolveMavenProjects(mavenProject, mavenProjectList);
        List<Artifact> artifacts = new ArrayList<>();
        for(MavenProject temp : mavenProjectList) {

            List<DependencyNode> dependencyNodes = getProjectDependencyNodes(temp);
            resolveProjectDependency(dependencyNodes, artifacts);

        }

        return artifacts;

    }

    private List<DependencyNode> getProjectDependencyNodes(MavenProject mavenProject) {

        ArtifactFilter artifactFilter = createResolvingArtifactFilter();

        ProjectBuildingRequest buildingRequest =
                new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());
        buildingRequest.setProject(mavenProject);

        try {
            rootNode = dependencyCollectorBuilder.collectDependencyGraph(buildingRequest, artifactFilter);
        } catch (DependencyCollectorBuilderException e) {
            throw new RuntimeException(e);
        }
        List<DependencyNode> dependencyNodes = rootNode.getChildren();

        return dependencyNodes;

    }

    private void resolveProjectDependency(List<DependencyNode> dependencyNodes, List<Artifact> artifacts) {

        if(dependencyNodes == null || dependencyNodes.isEmpty()) {
            return;
        }
        //List<DependencyNode> dependencyNodes = rootNode.getChildren();
        for(DependencyNode dependencyNode : dependencyNodes) {
            Artifact artifact = dependencyNode.getArtifact();
            artifacts.add(artifact);
            List<DependencyNode> dependencyNodeList = dependencyNode.getChildren();
            resolveProjectDependency(dependencyNodeList, artifacts);
        }

    }

    private void resolveMavenProjects(MavenProject mavenProject, List<MavenProject> mavenProjectList) {

        if(mavenProject == null) return;
        if(!"pom".equals(mavenProject.getPackaging())) {
            mavenProjectList.add(mavenProject);
        }
//        List<MavenProject> mavenProjects = mavenProject.getCollectedProjects();
//        if(mavenProjects == null || mavenProjects.isEmpty()) return;
//        for(MavenProject temp : mavenProjects) {
//            resolveMavenProjects(temp, mavenProjectList);
//        }

    }

    private String getGrpcPackage(Class clazz) {
        String packageName = clazz.getPackage().getName();
        StringBuilder sb = new StringBuilder(packageName);
        sb.append(".").append("grpc");
        return sb.toString();
    }

    private String getGrpcClassAllName(Class clazz, String serviceName) {
        String grpcPackage = getGrpcPackage(clazz);
        StringBuilder sb = new StringBuilder(grpcPackage);
        sb.append(".").append(serviceName);
        return sb.toString();
    }

    private String getGrpcSuperClassName(Class clazz, String serviceName) {

        String grpcPackage = getGrpcPackage(clazz);
        StringBuilder sb = new StringBuilder(grpcPackage);
        sb.append(serviceName).append("Grpc").append(".")
                .append(serviceName).append("ImplBase");
        return sb.toString();

    }

    public static char toUpperCase(char c) {
        if (97 <= c && c <= 122) {
            c ^= 32;
        }
        return c;
    }

    private void genGrpcServiceClass(ClassInfo classInfo) {

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL url = classLoader.getResource("template/pontifex-grpc-service.ftl");
        InputStream inputStream = classLoader.getResourceAsStream("template/pontifex-grpc-service.ftl");
        String path = url.getPath();
        String ftlPath = path.substring(0, path.lastIndexOf("/"));

        Configuration configuration = new Configuration();
        Writer out = null;
        try {
            // step2 获取模版路径
//            configuration.setDirectoryForTemplateLoading(new File(ftlPath));
            // step3 创建数据模型
//            Map<String, Object> dataMap = new HashMap<String, Object>();
//            dataMap.put("classPath", "com.freemark.hello");
//            dataMap.put("className", "User");
//            dataMap.put("Id", "Id");
//            dataMap.put("userName", "userName");
//            dataMap.put("password","password");
            // step4 加载模版文件
            File file = urlToFile(inputStream);
            FileTemplateLoader fileTemplateLoader = new FileTemplateLoader(file.getParentFile());
//            configuration.setClassForTemplateLoading(this.getClass(), ".");
            configuration.setTemplateLoader(fileTemplateLoader);
            Template template = configuration.getTemplate("pontifex-grpc-service.ftl");
            // step5 生成数据
            File docFile = new File(classInfo.getGrpcServiceClassPath());
            out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(docFile)));
            // step6 输出文件
            template.process(classInfo, out);
            System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^User.java 文件创建成功 !");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != out) {
                    out.flush();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }


    }

    public File urlToFile(InputStream is) {
        File file = null;
        FileOutputStream fos = null;
        try {
            String basePath = project.getBasedir().getPath();
            String templatePath = basePath+"/src/main/temp/template/";
            File templatePathFile = new File(templatePath);
            if(!templatePathFile.exists()) {
                templatePathFile.mkdirs();
            }
            file = new File(templatePathFile+"/pontifex-grpc-service.ftl");
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[4096];
            int length;
            while ((length = is.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            return file;
        } catch (IOException e) {
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public String genServiceSuperClass(String serviceName) {

        //DemoQueryGrpc.DemoQueryImplBase
        StringBuilder sb = new StringBuilder(serviceName);
        sb.append("Grpc.").append(serviceName).append("ImplBase");
        return sb.toString();

    }


}
