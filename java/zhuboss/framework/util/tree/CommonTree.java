package zhuboss.framework.util.tree;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class CommonTree <T extends AbsTreeEntity> {
    
    private List<T> treeNodes ;
    
    private List<T> rootNodes ;
    
    private Map<Object,T> treeNodeMap = new HashMap<Object,T>();
    
    private Set<String> checkedNodeIdSet = new HashSet<String>();
    
    private Comparator<T> comparator = new Comparator<T>() {
        @Override
        public int compare(T o1, T o2) {
            if(o1.getSeq()==null) return 1;
            if (o2.getSeq()==null) return -1;
            return o1.getSeq().compareTo(o2.getSeq());
        }
    };
    
    public CommonTree(List<T> treeNodes, String[] checkedNodeIds) throws Exception {
        this(treeNodes, checkedNodeIds, false);
    }
    
    /**
     * 构建树
     * @param treeNodes
     * @param checkedNodeIds 树上被选中的节点，如果checkedNodeIds为null，则树节点的checked属性为null，否则被选中的checked为true，未被选中的为false
     * @throws Exception 
     * @throws InvocationTargetException 
     * @throws Exception 
     */
    public CommonTree(List<T> treeNodes, String[] checkedNodeIds, boolean checkParent) throws Exception {
        this.treeNodes = treeNodes;
        
        //map
        for (T t : treeNodes) {
            treeNodeMap.put(BeanUtils.getProperty(t,"id"), t);
        }
        
        //root nodes
        this.rootNodes = findRootNodes(treeNodes);
        
        Collections.sort(this.rootNodes, (Comparator)comparator) ;
        
        //build tree
        for (AbsTreeEntity rootNode : rootNodes) {
            this.findNodeChildrens(rootNode);
        }
        
        /***
         * 打勾
         */
        if (checkedNodeIds != null) {
            for (String checkedNodeId : checkedNodeIds) {
                checkedNodeIdSet.add(checkedNodeId);
                findParent(checkedNodeId);
            }
            for (AbsTreeEntity treeNode : treeNodes) {
                treeNode.setChecked(checkedNodeIdSet.contains(BeanUtils.getProperty(treeNode,"id")));
            }
        }
    }
    
    private List<T> findRootNodes(List<T> treeNodes) {
        List<T> rootNodes = new ArrayList<T>();
        for (T treeNode : treeNodes) {
            // 找出根节点
            if (treeNode.getPid() == null || "".equals(treeNode.getPid().trim()) || "0".equals(treeNode.getPid())) {
                rootNodes.add(treeNode);
            }
        }
        return rootNodes;
    }

    private void findNodeChildrens(AbsTreeEntity treeEntity) throws Exception {
        for (AbsTreeEntity treeNode : treeNodes) {
            if (treeNode.getPid() != null && treeNode.getPid().equals(BeanUtils.getProperty(treeEntity,"id"))) {
                treeEntity.setLeaf(false);
                treeEntity.getChildren().add(treeNode);
                treeNode.setLeaf(true);
                findNodeChildrens(treeNode);
            }
        }
        
        Collections.sort(treeEntity.getChildren(), (Comparator)comparator);
    }
    
    private void findParent(String id) {
        T t = treeNodeMap.get(id);
        if (null != t) {
            checkedNodeIdSet.add(t.getPid());
            findParent(t.getPid());
        }
    }
    
    public List<T> getRootNodes() {
        return rootNodes;
    }

    public Map<Object, T> getTreeNodeMap() {
        return treeNodeMap;
    }
}