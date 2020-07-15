package tk.yallandev.saintmc.common.account.configuration;

import lombok.Getter;
import lombok.Setter;
import tk.yallandev.saintmc.CommonGeneral;
import tk.yallandev.saintmc.common.account.Member;

@Getter
public class LoginConfiguration {

	@Setter
	protected transient Member player;

	private AccountType accountType;

	private boolean logged;
	private String password;

	private long lastLogin = -1l;
	private String lastIp;

	public LoginConfiguration(Member player) {
		this.player = player;

		this.accountType = AccountType.NONE;

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
		CommonGeneral.getInstance().getPlayerData().updateMember(player, "loginConfiguration");
	}

	public void login(String ipAddress) {
		this.lastIp = ipAddress;
		this.lastLogin = System.currentTimeMillis();
		this.logged = true;
		CommonGeneral.getInstance().getPlayerData().updateMember(player, "loginConfiguration");
	}

	public void logOut() {
		this.logged = false;
		CommonGeneral.getInstance().getPlayerData().updateMember(player, "loginConfiguration");
	}

	public void setAccountType(AccountType accountType) {
		if (this.accountType != AccountType.NONE) {
			throw new IllegalStateException(player.getPlayerName() + " accountType already set!");
		}

		this.accountType = accountType;
		CommonGeneral.getInstance().getPlayerData().updateMember(player, "loginConfiguration");
	}

	public boolean needLogin(String ipAddress) {
		if (lastLogin - (1000 * 60 * 60 * 3) < System.currentTimeMillis()) {
			if (lastIp != null && lastIp.equals(ipAddress)) {
				return false;
			}
		}

		return true;
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
