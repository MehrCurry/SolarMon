package de.gzockoll.camel.solarmon;

import java.io.IOException;
import java.util.Properties;

import org.hsqldb.Server;
import org.hsqldb.persist.HsqlProperties;
import org.hsqldb.server.ServerAcl.AclFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

public class HyperSqlDbServer implements SmartLifecycle {
	private final Logger logger = LoggerFactory
			.getLogger(HyperSqlDbServer.class);
	private HsqlProperties properties;
	private Server server;
	private boolean running = false;

	public HyperSqlDbServer(Properties props) {
		properties = new HsqlProperties(props);
		start();
	}

	@Override
	public boolean isRunning() {
		if (server != null)
			server.checkRunning(running);
		return running;
	}

	@Override
	public void start() {
		if (server == null) {
			logger.info("Starting HSQL server...");
			server = new Server();
			try {
				server.setProperties(properties);
				server.start();
				running = true;
			} catch (AclFormatException afe) {
				logger.error("Error starting HSQL server.", afe);
			} catch (IOException e) {
				logger.error("Error starting HSQL server.", e);
			}
		}
	}

	@Override
	public void stop() {
		logger.info("Stopping HSQL server...");
		if (server != null) {
			server.stop();
			running = false;
		}
	}

	@Override
	public int getPhase() {
		return 0;
	}

	@Override
	public boolean isAutoStartup() {
		return true;
	}

	@Override
	public void stop(Runnable runnable) {
		stop();
		runnable.run();
	}
}