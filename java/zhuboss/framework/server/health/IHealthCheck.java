package zhuboss.framework.server.health;

public interface IHealthCheck {
	void check() throws HealthCheckException;
}
