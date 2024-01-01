package org.wyx.diego.pontifex.grpc;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.PlexusContainer;
import org.codehaus.plexus.context.Context;
import org.codehaus.plexus.context.ContextException;

import java.util.List;

@Mojo( name = "genServiceMojo", defaultPhase = LifecyclePhase.PROCESS_SOURCES, requiresDependencyResolution = ResolutionScope.TEST, requiresDependencyCollection=ResolutionScope.TEST)
public class GenServiceMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}")
    private MavenProject project;
    @Component
    private PlexusContainer plexusContainer;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        Context context = plexusContainer.getContext();
        List<ClassInfo> classInfos = null;
        try {
            classInfos = (List<ClassInfo>) context.get(PontifexConstant.CLASS_INFO_KEY);
        } catch (ContextException e) {
            throw new RuntimeException(e);
        }




    }
}
