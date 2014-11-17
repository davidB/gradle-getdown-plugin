import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Test
import static org.junit.Assert.*

class GetdownPluginTest {
	@Test
	public void testIfTaskAreDefined() {
		Project project = ProjectBuilder.builder().build()
		project.apply plugin: 'net.alchim31.getdown'
		project.evaluate()
		assertNotNull(project.tasks.getJres)
		assertNotNull(project.tasks.makeIcons)
		assertNotNull(project.tasks.copyDist)
		assertNotNull(project.tasks.makeGetdownTxt)
		assertNotNull(project.tasks.makeDigest)
		assertNotNull(project.tasks.makeLauncherUnix)
		assertNotNull(project.tasks.makeLauncherWindows)
		assertNotNull(project.tasks.makeLaunchers)
		assertNotNull(project.tasks.assembleApp)
		assertNotNull(project.tasks.bundles)
	}
}