package codecount;

import java.util.*;

/**
 * Created by Administrator on 2014/7/31.
 */
public class CodeCounter {


    private final PackageNode root = new PackageNode( "","",null);
    private Set<String> clazzes = new TreeSet<>();

    public void addClazz(String packageName,String clazzName,Counter counter){
        if (clazzes.add(packageName + "." + clazzName)){
            root.addChild(packageName.split("\\."),0,counter);
        }
    }

    public static class Counter{
        public int comments = 0;            //comment lines
        public int blanks = 0;              //empty lines
        public int codes = 0;               //code lines

        public void add(Counter counter) {
            this.comments += counter.comments;
            this.blanks += counter.blanks;
            this.codes += counter.codes;
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            String sp = System.getProperty("line.separator");
            sb.append("注释：").append(comments).append(sp);
            sb.append("空行：").append(blanks).append(sp);
            sb.append("代码：").append(codes).append(sp);
            return sb.toString();
        }
    }

    private class PackageNode{
        private String packageName;
        private String fullName;
        private final Counter codeCounter = new Counter();
        private final HashMap<String,PackageNode> children = new HashMap<>();
        private final PackageNode parent;

        public PackageNode(String packageName,String fullName,PackageNode parent){
            this.parent = parent;
            this.fullName = fullName;
            this.packageName = packageName;
        }


        public void addChild(String[] packageNames,int offset,Counter counter){
            this.codeCounter.add(counter);
            if (offset >= packageNames.length){
                return;
            }
            String childName = packageNames[offset];
            PackageNode node = children.get(childName);
            if (node == null){
                String fullName = "";
                for(int i = 0; i <= offset; i++){
                    fullName += packageNames[i];
                    fullName += ".";
                }
                fullName = fullName.substring(0,fullName.length()-1);       //remove last "."
                node = new PackageNode(childName,fullName,this);
                children.put(childName,node);
            }

            node.addChild(packageNames,offset+1,counter);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(fullName).append("\t")
                    .append(" 注释：").append(codeCounter.comments)
                    .append(" 空行：").append(codeCounter.blanks)
                    .append(" 代码：").append(codeCounter.codes);
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String sp = System.getProperty("line.separator");
        Queue<PackageNode> nodes = new LinkedList<>();
        nodes.offer(root);
        while (!nodes.isEmpty()){
            PackageNode node = nodes.poll();
            node.children.values().forEach(nodes::offer);
            sb.append(node).append(sp);
        }
        return sb.toString();
    }
}
