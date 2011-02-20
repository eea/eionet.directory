package eionet.directory.dto;

/**
 * 
 * @author altnyris
 *
 */
public class OrganisationDTO implements java.io.Serializable {
	
	private static final long serialVersionUID = -4653894537156120522L;
	
	private String orgId;
	private String name;
	private String url;
	

	/**
	 * 
	 */
	public OrganisationDTO(){
	}


	public String getOrgId() {
		return orgId;
	}


	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}

}
