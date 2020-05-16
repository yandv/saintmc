package tk.yallandev.saintmc.common.server.loadbalancer;

import tk.yallandev.saintmc.common.server.loadbalancer.element.LoadBalancerObject;

public interface LoadBalancer<T extends LoadBalancerObject> {

	public T next();

}
