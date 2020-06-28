package tk.yallandev.saintmc.common.server.loadbalancer.type;

import tk.yallandev.saintmc.common.server.loadbalancer.BaseBalancer;
import tk.yallandev.saintmc.common.server.loadbalancer.element.LoadBalancerObject;
import tk.yallandev.saintmc.common.server.loadbalancer.element.NumberConnection;

public class RoundRobin<T extends LoadBalancerObject & NumberConnection> extends BaseBalancer<T> {

	private int next = 0;

	@Override
	public T next() {
		T obj = null;
		if (nextObj != null)
			if (!nextObj.isEmpty()) {
				while (next < nextObj.size()) {
					obj = nextObj.get(next);
					++next;
					if (obj == null)
						continue;
					if (!obj.canBeSelected()) {
						obj = null;
						continue;
					}
					break;
				}
			}
		if (next + 1 >= nextObj.size())
			next = 0;
		return obj;
	}

	@Override
	public int getTotalNumber() {
		int number = 0;
		for (T item : nextObj) {
			number += item.getActualNumber();
		}
		return number;
	}

}
