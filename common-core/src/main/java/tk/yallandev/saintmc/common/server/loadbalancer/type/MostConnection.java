package tk.yallandev.saintmc.common.server.loadbalancer.type;

import tk.yallandev.saintmc.common.server.loadbalancer.BaseBalancer;
import tk.yallandev.saintmc.common.server.loadbalancer.element.LoadBalancerObject;
import tk.yallandev.saintmc.common.server.loadbalancer.element.NumberConnection;

public class MostConnection<T extends LoadBalancerObject & NumberConnection> extends BaseBalancer<T> {

	@Override
	public T next() {
		T obj = null;
		if (nextObj != null)
			if (!nextObj.isEmpty())
				for (T item : nextObj) {
					if (!item.canBeSelected())
						continue;
					if (obj == null) {
						obj = item;
						continue;
					}
					if (obj.getActualNumber() < item.getActualNumber())
						obj = item;
				}
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
