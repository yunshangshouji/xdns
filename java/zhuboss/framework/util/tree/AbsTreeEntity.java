package zhuboss.framework.util.tree;

import java.util.ArrayList;
import java.util.List;

import zhuboss.framework.mybatis.mapper.AbstractPO;



@SuppressWarnings("rawtypes")
public class AbsTreeEntity extends AbstractPO  {
    
	private static final long serialVersionUID = 1L;

	private String pid;
	private String text;
	private Boolean leaf = false;
	private Boolean expanded = true;
	private Boolean checked;
    private Integer seq;
	private List<AbsTreeEntity> children = new ArrayList<AbsTreeEntity>();

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Boolean getLeaf() {
		return leaf;
	}

	public void setLeaf(Boolean leaf) {
		this.leaf = leaf;
	}

	public Boolean getExpanded() {
		return expanded;
	}

	public void setExpanded(Boolean expanded) {
		this.expanded = expanded;
	}


	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public List<AbsTreeEntity> getChildren() {
		return children;
	}

	public void setChildren(List<AbsTreeEntity> children) {
		this.children = children;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
}