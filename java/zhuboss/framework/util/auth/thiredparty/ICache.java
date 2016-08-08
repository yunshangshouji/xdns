package zhuboss.framework.util.auth.thiredparty;

public interface ICache {
	boolean exists(String uuid);
	void add(String uuid);
}
