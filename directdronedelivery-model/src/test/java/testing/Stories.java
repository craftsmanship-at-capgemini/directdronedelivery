package testing;

import java.util.List;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.embedder.EmbedderControls;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.StoryReporterBuilder;
import org.jbehave.core.reporters.Format;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;
import org.junit.runner.RunWith;

import de.codecentric.jbehave.junit.monitoring.JUnitReportingRunner;
import directdronedelivery.warehouse.businessrules.CargoSpecyficationSteps;

@RunWith(JUnitReportingRunner.class)
public class Stories extends JUnitStories {
    
    private Configuration configuration;
    
    public Stories() {
        configuration = new MostUsefulConfiguration()
                .useDefaultStoryReporter(new StoryReporterBuilder()
                        .withFormats(Format.CONSOLE, Format.IDE_CONSOLE, Format.HTML).build("stories"));
        
        configuredEmbedder().useEmbedderControls(
                new EmbedderControls().doIgnoreFailureInStories(true).doIgnoreFailureInView(true));
    }
    
    @Override
    public InjectableStepsFactory stepsFactory() {
        return new InstanceStepsFactory(configuration, new CargoSpecyficationSteps());
    }
    
    @Override
    public Configuration configuration() {
        return configuration;
    }
    
    @Override
    protected List<String> storyPaths() {
        return new StoryFinder().findPaths(CodeLocations.codeLocationFromClass(this.getClass()), "**/*.story", "");
    }
    
}
