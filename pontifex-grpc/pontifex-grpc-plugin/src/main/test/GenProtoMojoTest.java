import junit.framework.Assert;
import org.apache.maven.artifact.repository.DefaultArtifactRepository;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.codehaus.plexus.PlexusTestCase;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.junit.Rule;
import org.junit.Test;
import org.wyx.diego.pontifex.grpc.GenProtoMojo;

import java.io.File;

public class GenProtoMojoTest {


    @Rule
    public MojoRule mojoRule = new MojoRule();
    @Rule
    public TestResources resources = new TestResources();

    @Test
    public void test() throws Exception {


        String dir = PlexusTestCase.getBasedir();
        File testPom = new File(dir+"/pontifex-grpc/pontifex-grpc-plugin/src/main/test/pom.xml");
        System.out.println(dir);

//        MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
//        ProjectBuildingRequest configuration = executionRequest.getProjectBuildingRequest()
//                .setRepositorySession(new DefaultRepositorySystemSession()).setLocalRepository();
//        MavenProject project = mojoRule.lookup(ProjectBuilder.class).build(testPom, executionRequest).getProject();

        MavenExecutionRequest executionRequest = new DefaultMavenExecutionRequest();
        ProjectBuildingRequest buildingRequest = executionRequest.getProjectBuildingRequest();
        ProjectBuilder projectBuilder = mojoRule.lookup(ProjectBuilder.class);
        MavenProject project = projectBuilder.build(testPom, buildingRequest).getProject();

        GenProtoMojo mojo = (GenProtoMojo) mojoRule.lookupConfiguredMojo(project, "genProto");
//        GenProtoMojo mojo = (GenProtoMojo) this.mojoRule.lookupMojo( "genProto", testPom );
        Assert.assertNotNull(mojo);
        mojo.execute();

    }



}
