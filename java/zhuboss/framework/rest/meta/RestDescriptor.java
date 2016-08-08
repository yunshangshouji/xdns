package zhuboss.framework.rest.meta;

import java.util.List;

public class RestDescriptor {
	private String method;
	private String path;
	private String consumeMediaTypes;
	private String produceMediaTypes;
	private List<QueryParamDescriptor> queryParamDescriptors;
	private List<PathParamDescriptor> pathParamDescriptors;
	private String description;
	private String consumeMeta;
	private String produceMeta;
	private String cacheController;
	
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getConsumeMeta() {
		return consumeMeta;
	}
	public void setConsumeMeta(String consumeMeta) {
		this.consumeMeta = consumeMeta;
	}
	public String getProduceMeta() {
		return produceMeta;
	}
	public void setProduceMeta(String produceMeta) {
		this.produceMeta = produceMeta;
	}
	public List<QueryParamDescriptor> getQueryParamDescriptors() {
		return queryParamDescriptors;
	}
	public void setQueryParamDescriptors(
			List<QueryParamDescriptor> queryParamDescriptors) {
		this.queryParamDescriptors = queryParamDescriptors;
	}
	public List<PathParamDescriptor> getPathParamDescriptors() {
		return pathParamDescriptors;
	}
	public void setPathParamDescriptors(
			List<PathParamDescriptor> pathParamDescriptors) {
		this.pathParamDescriptors = pathParamDescriptors;
	}
	public String getConsumeMediaTypes() {
		return consumeMediaTypes;
	}
	public void setConsumeMediaTypes(String consumeMediaTypes) {
		this.consumeMediaTypes = consumeMediaTypes;
	}
	public String getProduceMediaTypes() {
		return produceMediaTypes;
	}
	public void setProduceMediaTypes(String produceMediaTypes) {
		this.produceMediaTypes = produceMediaTypes;
	}
	public String getCacheController() {
		return cacheController;
	}
	public void setCacheController(String cacheController) {
		this.cacheController = cacheController;
	}
}
