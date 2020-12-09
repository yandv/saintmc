package tk.yallandev.saintmc.common.account.configuration;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.common.account.Member;
import tk.yallandev.saintmc.common.utils.ip.Session;

@Getter
public class LoginConfiguration {

	@Setter
	protected transient Member player;

	private AccountType accountType;

	private boolean passCaptcha;
	private boolean logged;
	private String password;

	private long lastLogin = -1l;
	private String lastIp;

	private Map<String, Session> sessionMap;

	private long lastVerify = System.currentTimeMillis();

	public LoginConfiguration(Member player, AccountType accountType) {
		this.player = player;

		this.accountType = accountType;

		this.sessionMap = new HashMap<>();

		this.logged = false;
		this.password = "";

		this.lastLogin = -1l;
		this.lastIp = player.getLastIpAddress();
	}

	public void register(String password, String ipAddress) {
		this.password = password;
		this.lastIp = ipAddress;
		this.lastLogin = System.currentTimeMillis();
		this.logged = true;
		save();
	}

	public void login(String ipAddress) {
		this.lastIp = ipAddress;
		this.lastLogin = System.currentTimeMillis();
		this.logged = true;
		save();
	}

	public boolean changePassword(String password, String newPassword) {
		this.password = newPassword;
		startSession(getPlayer().getLastIpAddress());
		return false;
	}

	public boolean clearSessions() {
		if (sessionMap == null)
			sessionMap = new HashMap<>();

		sessionMap.clear();
		save();
		return true;
	}

	public boolean startSession(String ipAddress) {
		if (sessionMap == null)
			sessionMap = new HashMap<>();

		Session session = sessionMap.get(ipAddress);

		if (session == null) {
			session = new Session();
			sessionMap.put(ipAddress, session);
		} else
			session.updateSession();

		save();
		return true;
	}

	public boolean hasSession(String ipAddress) {
		if (sessionMap == null)
			sessionMap = new HashMap<>();

		return sessionMap.containsKey(ipAddress) ? !sessionMap.get(ipAddress).hasExpired() : false;
	}

	public void logOut() {
		this.logged = false;
		save();
	}

	public void save() {
		this.player.save("loginConfiguration");
	}

	public void setCaptcha(boolean passCaptcha) {
		this.passCaptcha = passCaptcha;
		this.player.save("loginConfiguration");
	}

	public boolean isRegistred() {
		return !password.isEmpty() && password != null;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public boolean isLogged() {
		if (accountType == AccountType.ORIGINAL)
			return true;

		return logged;
	}

	public enum AccountType {

		CRACKED, ORIGINAL;

	}
}
