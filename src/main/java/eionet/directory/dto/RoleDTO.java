package eionet.directory.dto;

import java.util.List;

/**
 *
 * @author altnyris
 *
 */
public class RoleDTO implements java.io.Serializable {

    private static final long serialVersionUID = -4653894537156120522L;

    private String id;
    private String name;
    private String mail;
    private String description;
    private String membersUrl;
    private List<MemberDTO> members;
    private List<RoleDTO> subroles;

    /**
     *
     */
    public RoleDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<MemberDTO> getMembers() {
        return members;
    }

    public void setMembers(List<MemberDTO> members) {
        this.members = members;
    }

    public String getMembersUrl() {
        return membersUrl;
    }

    public void setMembersUrl(String membersUrl) {
        this.membersUrl = membersUrl;
    }

    public List<RoleDTO> getSubroles() {
        return subroles;
    }

    public void setSubroles(List<RoleDTO> subroles) {
        this.subroles = subroles;
    }

}
