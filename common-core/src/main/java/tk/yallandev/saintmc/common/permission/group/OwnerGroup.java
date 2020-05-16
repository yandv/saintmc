package tk.yallandev.saintmc.common.permission.group;

import java.util.Arrays;
import java.util.List;

public class OwnerGroup extends GroupInterface {

	@Override
	public List<String> getPermissions() {
		return Arrays.asList("*", "skillsverify.admin", "AntiCheat.vlSee");
	}
}
