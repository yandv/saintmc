package tk.yallandev.saintmc.common.server.loadbalancer.element;

/**
 * 
 * 
 * 
 * @author yandv
 *
 */

public interface LoadBalancerObject {
	
	String getServerId();

	boolean canBeSelected();
	
}
