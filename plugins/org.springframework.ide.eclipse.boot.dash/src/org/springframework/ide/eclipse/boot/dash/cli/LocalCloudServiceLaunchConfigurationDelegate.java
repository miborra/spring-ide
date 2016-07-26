package org.springframework.ide.eclipse.boot.dash.cli;

import java.util.Collections;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.springframework.ide.eclipse.boot.core.cli.BootCliCommand;
import org.springframework.ide.eclipse.boot.core.cli.BootCliUtils;
import org.springsource.ide.eclipse.commons.livexp.util.ExceptionUtil;

public class LocalCloudServiceLaunchConfigurationDelegate implements ILaunchConfigurationDelegate {

	private static final String[] ENVIRONMENT = new String[] {
			String.format("JAVA_HOME=%s", System.getProperty("java.home")),
			String.format("PATH=%s", System.getenv("PATH")),
			String.format("USERDOMAIN=%s", System.getenv("USERDOMAIN")),
			String.format("USERNAME=%s", System.getenv("USERNAME")),
			String.format("USERPROFILE=%s", System.getenv("USERPROFILE")) };

	public final static String ID = "org.springframework.ide.eclipse.boot.dash.cloud.cli.service";

	public final static String ATTR_CLOUD_SERVICE_ID = "local-cloud-service-id";

	private Process createProcess(ILaunchConfiguration configuration) throws Exception {
		String serviceId = configuration.getAttribute(ATTR_CLOUD_SERVICE_ID, (String) null);
		if (serviceId == null) {
			throw new IllegalArgumentException("Local Cloud Service ID is missing from launch configuration!");
		}
		BootCliCommand cmd = new BootCliCommand(BootCliUtils.getSpringBootHome());
		return Runtime.getRuntime().exec(cmd.getProcessArguments("cloud", serviceId), ENVIRONMENT, cmd.getProcessWorkingFolder());
	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		monitor.beginTask("Launching Local Cloud Service", 1);
		try {
			String serviceId = configuration.getAttribute(ATTR_CLOUD_SERVICE_ID, (String) null);
			launch.addProcess(new RuntimeProcess(launch, createProcess(configuration), serviceId, Collections.emptyMap()));
		} catch (Exception e) {
			throw ExceptionUtil.coreException(e);
		} finally {
			monitor.done();
		}
	}

}