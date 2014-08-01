package codecount;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Administrator on 2014/7/31.
 */
public class CodeCounter {
    private final PackageNode root = new PackageNode( "/","/",null);
    private Set<String> clazzes = new TreeSet<>();

    public void addClazz(String packageName,String clazzName,Counter counter){
        if (clazzes.add(packageName + "." + clazzName)){
            root.addChild(packageName.split("\\."),0,clazzName,counter);
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

    public static class Clazz implements Comparable<Clazz>{
        private String className;
        private Counter counter;

        public Clazz(String className,Counter counter){
            this.className = className;
            this.counter = new Counter();
            this.counter.add(counter);
        }

        @Override
        public int compareTo(Clazz o) {
            return className.compareTo(o.className);
        }

        public JSONObject toJson(){
            JSONObject json = new JSONObject();
            json.put("name",className);
            json.put("size",counter.codes);
            return json;
        }
    }

    private class PackageNode{
        private String packageName;
        private String fullName;
        private final Counter codeCounter = new Counter();
        private final HashMap<String,PackageNode> children = new HashMap<>();
        private final Set<Clazz> classes = new TreeSet<>();
        private final PackageNode parent;

        public PackageNode(String packageName,String fullName,PackageNode parent){
            this.parent = parent;
            this.fullName = fullName;
            this.packageName = packageName;
        }


        public void addChild(String[] packageNames,int offset,String className,Counter counter){
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
            if (offset == packageNames.length -1){
                Clazz clazz = new Clazz(className,counter);
                node.classes.add(clazz);
            }
            node.addChild(packageNames,offset+1,className,counter);
        }

        public boolean hasChild(){
            return children.size() != 0;
        }

        public boolean hasClass(){
            return classes.size() != 0;
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

    public final String toJson(){
        return toJson(root).toJSONString();
    }

    private JSONObject toJson(PackageNode node){
        JSONObject json = new JSONObject();
        json.put("name",node.packageName);
        if (node.hasChild() || node.hasClass()){
            JSONArray array = new JSONArray();
            if (node.hasChild()){
                node.children.values().stream().map(this::toJson)
                    .collect(Collectors.toCollection(() -> array));
            }
            if (node.hasClass()){
                node.classes.forEach((clazz)->array.add(clazz.toJson()));
            }
            json.put("children",array);
        }
        return json;
    }
}
