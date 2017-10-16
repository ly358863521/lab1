package lab1;

import java.io.*;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

/**
 * TEST
 */
public class Proba
{
    private static HashMap<String,Integer> neighbor = new HashMap<String,Integer>();//�洢ͼ�еı߼�����ִ���
    private static HashMap<String,String> neiColor = new HashMap<String,String>();
    private static HashMap<String,Integer> numWord = new HashMap<String,Integer>();//�洢��ͬ���ʵı��
    private static HashMap<Integer,String> word = new HashMap<Integer,String>();//��numWord��ֵ�෴
    private static String[] Color = {"red", "green", "blue", "pink", "grey"};
    private int [][] graph;
    public static void main(String[] args) throws IOException
    {
    	Proba p = new Proba();
        System.out.println("�������ļ�·����.txt����");
        Scanner s = new Scanner(System.in);
        String name = s.next();
        String text = p.readFile(name);
        text = text.replaceAll("[^a-zA-Z]", " ");
        text = text.toLowerCase();
        String [] wordlist = text.split("\\s+");
        p.wordNum(wordlist);
        p.mapCount(wordlist);
        p.graphBuild(neighbor, numWord); 
        p.showDirectedGraph(neighbor);
        System.out.println("ͼ������ϣ����ڵ�����");
        //System.out.println(p.queryBridgeWords("explore" ,"new"));
       // System.out.println("����Ҫ�����ŽӴʵ��ı���");
        //String inputtext = s.nextLine();
        //inputtext = s.nextLine();
        //System.out.println(inputtext);
        //System.out.println("���ı���");
        //System.out.println(p.generateNewText(inputtext.toLowerCase()));
        System.out.println(p.calcShortestPath("to","new"));
        
        s.close();
    }

    private void graphBuild(HashMap<String,Integer> nei,HashMap<String,Integer> num){
    	int numsize = num.size();
    	graph = new int [numsize+1][];
    	for(int i=0;i<=numsize;i++)
    		graph[i] = new int[numsize+1];
    	for(int i=0;i<=numsize;i++)
    		for(int j=0;j<=numsize;j++)
    			graph[i][j] = 100000000;
    	Set<Entry<String, Integer>> sets = nei.entrySet();  
        for(Entry<String, Integer> entry : sets) {  
           String []list = entry.getKey().split(" -> ");
           graph[num.get(list[0])][num.get(list[1])] = entry.getValue();
        }  
    }
    private void wordNum(String [] list){  //���ѳ��ֵ��ʱ����
    	int count = 0;
    	for(int i=0;i<list.length;i++){
    		if(!numWord.containsKey(list[i])){
    			numWord.put(list[i], count);
    			word.put(count++, list[i]);
    		}
    	}
    }
    private void mapCount(String [] list){  //�߳��ִ���ͳ�����¼
    	String temp;
    	for(int i=0;i<list.length-1;i++){
    		temp = list[i]+" -> "+list[i+1];
    		if(neighbor.containsKey(temp)){
    			Integer count = neighbor.get(temp) + 1;
    			neighbor.put(temp, count);
    		}
    		else{
    			neighbor.put(temp, 1);
    		}
    	}
    }
    private void mapColor(String [] list,int count){  //�߳��ִ���ͳ�����¼
    	String temp;
    	for(int i=list.length-1;i>0;i--){
    		temp = list[i]+" -> "+list[i-1];
    		if(!neiColor.containsKey(temp)){
    			neiColor.put(temp, "color = "+Color[count]);
    		}
    	}
    }
    private String readFile(String filename) throws IOException{  //���ļ�
    	BufferedReader file = new BufferedReader(new FileReader(filename));
    	String text = "";
    	String line = file.readLine();
    	while(line != null){
    		text += line;
    		line = file.readLine();
    	} 
    	file.close();
    	return text;
    }
    private void showDirectedGraph(HashMap<String,Integer> list)//չʾ����ͼ������Ϊ.jpg
    {
    	GraphViz gv = new GraphViz();
    	gv.addln(gv.start_graph());
    	Set<Entry<String, Integer>> sets = list.entrySet();  
        for(Entry<String, Integer> entry : sets) {
        	String t = entry.getKey();
        	if(neiColor.containsKey(t))
        		gv.addln(t+" [ label = " + "\"" + entry.getValue().toString()+ "\" " + neiColor.get(t) +" ];" );
        	else
        		gv.addln(t+" [ label = " + "\"" + entry.getValue().toString()+ "\"" +" ];" );
        }  
    	gv.addln(gv.end_graph());
    	System.out.println(gv.getDotSource());      
    	String type = "jpg";
    	File out = new File("D:/360Downloads/out." + type);
    	gv.writeGraphToFile( gv.getGraph( gv.getDotSource(), type ), out );
    }
   
    private String queryBridgeWords(String word1, String word2) {
    	String bridgewords = "";
    	if(numWord.containsKey(word1) && numWord.containsKey(word2)){
    		int word1num = numWord.get(word1);
    		int word2num = numWord.get(word2);
    		for(int i=0;i<numWord.size();i++){
    			int num = graph[word1num][i] + graph[i][word2num];
    			if(i!=word1num && i!=word2num && num>=2 && num<100000000){ 
    				bridgewords += (word.get(i) +" ");
    			}
    		}
    	}
    	return bridgewords;
    } //��ѯ�ŽӴ�
    private String generateNewText(String inputText) {
    	String newText = "" ,bridge = "";
    	String []text = inputText.split("\\s+");
    	for(int i=0;i<text.length-1;i++){
    		bridge = queryBridgeWords(text[i],text[i+1]);
    		if(bridge != ""){
    			String []bw = bridge.split("\\s+");
    			newText += (text[i]+" "+bw[0]+" ");
    		}
    		else{
    			newText += (text[i]+" ");
    		}
    	}
    	return (newText + text[text.length-1]);
    }//����bridge word�������ı�
    private String calcShortestPath(String word1, String word2) {
    	int n = numWord.size(),v = numWord.get(word1);
    	String Path = "";
    	String rePath = "";
    	boolean [] flag = new boolean[n+1];
    	int[] dis = new int[n+1];
    	int[] path = new int[n+1];
    	for (int i = 0; i < n; i++) {
    		dis[i] = graph[v][i];
    		if (dis[i] == 100000000)
    			path[i] = n;
    		else
    			path[i] = v;
    		flag[i] = false;
    	}
    	flag[v] = true;
    	for (int i = 1; i<n; i++) {
    		int min = 100000000;
    		int u=n;
    		for (int j = 0; j < n; j++) {
    			if (dis[j]<min && !flag[j]) {
    				min = dis[j];
    				u = j;
    			}
    		}
    		flag[u] = true;
    		for (int j = 0; j < n; j++) {
    			if (!flag[j] && min + graph[u][j]<dis[j]) {
    				dis[j] = min + graph[u][j];
    				path[j] = u;
    			}
    		}
    	}
    	if(numWord.containsKey(word2)){
    		int e = numWord.get(word2);
    		int f = e;
    		while (path[f] != n) {
   				rePath += (word.get(f)+" ");
   				f = path[f];    				
   			}
   			if(rePath!=""){
   				rePath += word.get(f);
   				String []text = rePath.split(" ");
   				neiColor.clear();
   				mapColor(text,0);
   				showDirectedGraph(neighbor);
   				Path += word1;
   				for(int i = text.length-2;i>=0;i--){
   					Path += ("->" + text[i]);
   				}
   			}
    	}
    	else{
    		for (int i = 0; i < n; i++) {
    			String X = "";
    			int f = i;
    			if (f != v) {
    				while (path[f] != n) {
    					X += (word.get(f)+" ");
    					f = path[f];
    				}
    			}
    			if(X!=""){
       				X += word.get(f);
       				String []text = X.split(" ");
       				rePath += word1;
       				for(int j = text.length-2;j>=0;j--){
       					rePath += ("->" + text[j]);
       				}
       				rePath +="\n";
       			}
    		}
    		Path = rePath;
    	}
    	if(Path == "")
    		Path = "���ɴ�";
    	return Path;
    } //������������֮������·��
}
