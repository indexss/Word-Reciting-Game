package com.indexss;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server implements Runnable{
    ServerSocket ss = null;
    private BufferedReader br = null;
    private PrintStream ps = null;
    String ClientAddressNotation = null;
    Socket socket= null;
    public ArrayList<WordTuple> wordTab = new ArrayList<WordTuple>();

    public Server() throws Exception{
        ss = new ServerSocket(11451);
        socket = ss.accept();
        ClientAddressNotation ="client " + socket.getInetAddress().getHostAddress() + " access";
        System.out.println(ClientAddressNotation);
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ps = new PrintStream(socket.getOutputStream());
        WordTabInit();
        new Thread(this).start();
    }

    public void run(){
        while(true){
            try{
                String msg = br.readLine();
                if(msg.equals("NeedAWord")){
                    WordTuple randomWordTuple = RandomSelectWordTuple();
                    String chinese = randomWordTuple.getChinese();
                    String english = randomWordTuple.getEnglish();
                    ps.println(english);
                    ps.println(chinese);
                }
            }catch(Exception ex){ex.printStackTrace();}
        }
    }
    public void WordTabInit() throws IOException {
//        wordTab.add(new WordTuple("dog", "狗"));
//        wordTab.add(new WordTuple("cat", "猫"));
//        wordTab.add(new WordTuple("book", "书"));
//        wordTab.add(new WordTuple("talk", "说"));
//        wordTab.add(new WordTuple("people", "人类"));
//        wordTab.add(new WordTuple("orange", "橘子"));
//        wordTab.add(new WordTuple("tiger", "老虎"));
//        wordTab.add(new WordTuple("bottle", "水杯"));
//        wordTab.add(new WordTuple("football", "足球"));
        File file = new File("src/com/indexss/docs/CST6WordList.txt");
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        while(true){
            String str = br.readLine();
            if(str == null){
                break;
            }
            String english = str.substring(0,str.indexOf(" "));
            String chinese = str.substring(str.indexOf(" ")+1, str.length());
            wordTab.add(new WordTuple(english, chinese));
        }

        fr.close();
        br.close();
    }

    public WordTuple RandomSelectWordTuple(){
        int length = wordTab.size();
        System.out.println(length + "k");
        int randomInt = (int)(Math.random()*length);
        System.out.println(randomInt);
        return wordTab.get(randomInt);
    }

    public static void main(String[] args) throws Exception {
        new Server();
    }
}

class WordTuple{
    private String English;
    private String Chinese;

    public WordTuple(String English, String Chinese){
        this.English = English;
        this.Chinese = Chinese;
    }

    public String getEnglish(){ return English; }
    public String getChinese(){ return Chinese; }
}