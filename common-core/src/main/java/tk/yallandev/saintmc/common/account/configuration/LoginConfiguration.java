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

	private boolean logged;
	private String password;

	private long lastLogin = -1l;
	private String lastIp;

	private Map<String, Session> sessionMap;

	private long lastVerify = System.currentTimeMillis();

	public LoginConfiguration(Member player) {
		this.player = player;

		this.accountType = AccountType.NONE;

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

	public void setAccountType(AccountType accountType) {
		if (this.accountType != AccountType.NONE) {
			throw new IllegalStateException(player.getPlayerName() + " accountType already set!");
		}

		this.accountType = accountType;
		save();
	}

	public void save() {
		this.player.save("loginConfiguration");
	}

	public AccountType verify() {
		if (lastVerify + (1000 * 60 * 60 * 24) > System.currentTimeMillis()) {
			return this.accountType;
		}

		AccountType accountType = AccountType.NONE;

		return accountType;
	}

	public boolean isRegistred() {
		return !password.isEmpty() && password != null;
	}

	public AccountType getAccountType() {
		if (accountType == null)
			accountType = AccountType.NONE;

		return accountType;
	}

	public boolean isLogged() {
		if (accountType == AccountType.ORIGINAL)
			return true;

		return logged;
	}

	public enum AccountType {

		CRACKED, ORIGINAL, NONE;

	}
}
