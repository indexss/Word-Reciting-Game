package com.indexss;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client extends JFrame implements Runnable, ActionListener {

    private int correctNum = 0;
    private int wrongNum = 0;
    private int timeoutNum = 0;
    private JLabel correctBoard = new JLabel();
    private JLabel wrongBoard = new JLabel();
    private JLabel timeoutBoard = new JLabel();

    private JLabel bigNotation = new JLabel();
    private JLabel wordNotation = new JLabel();
    private JLabel jlb = new JLabel();
    private JLabel hp = new JLabel();

    private JButton remakeButton = new JButton("重开");

    public BufferedReader br = null;
    public static PrintStream ps = null;

    private String degreeFlag;
    private boolean FLAG;
    public static int HP = 10;
    private int statementCode = 0;
    public String wordInput = null;
    private PrintStream psWrong = new PrintStream(new FileOutputStream("src/com/indexss/docs/notMasteredWord.txt"));
    private PrintStream psCorrect = new PrintStream(new FileOutputStream("src/com/indexss/docs/MasteredWord.txt"));

    public Client() throws IOException{

        //重开按钮
        remakeButton.setBounds( 450,220,30,30);
        remakeButton.addActionListener(new remakeButtonActionListener());
//        this.add(remakeButton);

        //数量提示板
        correctBoard.setBounds(20,65,200,25);
        wrongBoard.setBounds(20,90,200,25);
        timeoutBoard.setBounds(20,115,200,25);

        correctBoard.setFont(new Font("黑体", Font.BOLD, 15));
        wrongBoard.setFont(new Font("黑体", Font.BOLD, 15));
        timeoutBoard.setFont(new Font("黑体", Font.BOLD, 15));

        this.add(correctBoard);
        this.add(wrongBoard);
        this.add(timeoutBoard);

        //每轮结束提示
        bigNotation.setBounds(20, 130, 200, 20);
        wordNotation.setFont(new Font("黑体", Font.BOLD, 20));
        this.add(wordNotation);

        //单词提示
        wordNotation.setBounds(20,150,200,20);
        wordNotation.setFont(new Font("黑体", Font.BOLD, 20));
        this.add(wordNotation);

        //生命值
        hp.setBounds(65,10,400, 50);
        hp.setFont(new Font("黑体", Font.BOLD, 30));
        this.add(hp);

        //下落文本
        jlb.setSize(500, 50);
        jlb.setFont(new Font("黑体", Font.BOLD, 20));
        this.add(jlb);

        //文本框
        TextField textField = new TextField();
        textField.setFont(new Font("黑体", Font.BOLD, 20));
        textField.setBounds(20,180,220,30);
        textField.addActionListener(this);
        this.add(textField);

        //错词本按钮
        JButton openWrongWordButton = new JButton("打开错词本");
        openWrongWordButton.setBounds(20,220,100,35);
        openWrongWordButton.addActionListener(new openWordFileButtonActionListener());
        this.add(openWrongWordButton);

        //熟词本按钮
        JButton openCorrectWordButton = new JButton("打开熟词本");
        openCorrectWordButton.setBounds(140,220,100,35);
        openCorrectWordButton.addActionListener(new openCorrectWordFileButtonActionListerner());
        this.add(openCorrectWordButton);

        //生命值图片
        JLabel heartLabel = new JLabel();
//        URL url = Client.class.getResource("heart.png");
//        System.out.println(url);
//        assert url != null;
//        imageIcon = new ImageIcon(url);
        heartLabel.setIcon(Data.heart);
        heartLabel.setBounds(10,10,120, 50);
        this.add(heartLabel);

        //socket八股文
        Socket s = new Socket("127.0.0.1",11451);
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        ps = new PrintStream(s.getOutputStream());

        //接受线程

        degreeFlag = SelectDegree();
        this.setTitle("CET6单词背诵游戏 难度: " + degreeFlag);
        this.setSize(500, 300);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLayout(null);

        new Thread(this).start();
        ps.println("NeedAWord");

        this.setVisible(true);


    }

    public void run(){
        while(HP > 0){
            try {
                String english = br.readLine();
                String chinese = br.readLine();
                System.out.println("key is: " + english);
                int randomNum = (int) ( Math.random() * 2 + 1);
                int len = english.length();
                String notation = "";
                char[] notationWord = new char[len];
                for(int i = 0; i < len; i++){
                    notationWord[i] = '*';
                }
                for(int i = 0; i< randomNum; i++){
                    int randomPosition = (int)(Math.random()*len);
                    notationWord[randomPosition] = english.charAt(randomPosition);
                }
                for (int i = 0; i<len; i++){
                    notation = notation + notationWord[i];
                }
                wordNotation.setText("提示: " + notation);
                bigNotation.setText("");
                correctBoard.setText("● 正确个数:" + correctNum);
                wrongBoard.setText("● 错误个数:" + wrongNum);
                timeoutBoard.setText("● 超时个数:" + timeoutNum);

                jlb.setText(chinese);
                jlb.setLocation(250,20);
                FLAG = true;
                while(FLAG){
                    wordInput = "";
                    hp.setText(HP + "");
//                    System.out.println("in the thread");
//                    System.out.println(english);
//                    System.out.println(chinese);
                    int millis = switch (degreeFlag) {
                        case "菜鸟" -> 80;
                        case "简单" -> 55;
                        case "困难" -> 30;
                        case "地狱" -> 15;
                        default -> 0;
                    };
                    try{Thread.sleep(millis);}catch(Exception ex){}
                    jlb.setLocation(jlb.getX(),	jlb.getY()+1);

                    if((!wordInput.equals("")) && (!wordInput.equals(english))){
                        statementCode = -2;
                        HP -= 2;
                        System.out.println("hp -2");
                        System.out.println("now hp is: " + HP);
                        ps.println("NeedAWord");
                        wrongNum ++;
                        break;

                    }

                    if((!wordInput.equals("")) && (wordInput.equals(english))){
                        statementCode = 1;
                        HP += 1;
                        System.out.println("hp +1");
                        System.out.println("now hp is: " + HP);
                        correctNum ++;
                        ps.println("NeedAWord");
                        break;
                    }

                    if(jlb.getY() + 80 >= this.getHeight()){
                        statementCode = -1;
                        HP -= 1;
                        System.out.println("hp -1");
                        System.out.println("now hp is: " + HP);
                        timeoutNum ++;
                        ps.println("NeedAWord");
                        break;
                    }
                }
                if(statementCode == 1){
                    System.out.println("Correct!");
                    bigNotation.setText("恭喜回答正确!");
                    psCorrect.println(english);
                    JOptionPane.showMessageDialog(null, "回答正确！","正确",JOptionPane.WARNING_MESSAGE,Data.correct);
                }else if(statementCode == -2){
                    System.out.println("Wrong! Answer is: " + english);
                    bigNotation.setText("回答错误，答案是: " + english);
                    String strNotation = null;
//                    if (english.length() < 8) {
//                        psWrong.println(english + "\t\t\t错写为: \t\t" + wordInput);
////                        strNotation = english + "\t\t\t错写为: \t\t" + wordInput;
//                    }else if(english.length() < 16) {
//                        psWrong.println(english + "\t\t错写为: \t\t" + wordInput);
////                        strNotation = english + "\t\t错写为: \t\t" + wordInput;
//                    }else if(english.length() < 24){
//                        psWrong.println(english + "\t错写为: \t\t" + wordInput);
////                        strNotation = english + "\t错写为: \t\t" + wordInput;
//                    }
                    int numTab = 6 - english.length() / 3;
                    psWrong.printf(english);
                    for(int i = 0; i<numTab; i++){
                        psWrong.printf("\t");
                    }
                    psWrong.printf("错写为：\t\t" + wordInput + "\n");

                    JOptionPane.showMessageDialog(null, "回答错误，答案是: " + english,"错误",JOptionPane.WARNING_MESSAGE,Data.wrong);

                }else if (statementCode == -1){
                    String strNotation1 = null;
                    System.out.println("Time out! Answer is: " + english);
                    bigNotation.setText("您没有回答，答案是: " + english);
//                    if (english.length() < 8) {
//                        psWrong.println(english + "\t\t\t回答超时");
////                        strNotation1 = english + "\t\t\t回答超时";
//                    }else if (english.length() < 16){
//                        psWrong.println(english + "\t\t回答超时");
////                        strNotation1 = english + "\t\t回答超时";
//                    }else if (english.length() < 24){
//                        psWrong.println(english + "\t回答超时");
////                        strNotation1 = english + "\t回答超时";
//                    }
                    int numTab = 6 - english.length() / 3;
                    psWrong.printf(english);
                    for(int i = 0; i<numTab; i++){
                        psWrong.printf("\t");
                    }
                    psWrong.printf("回答超时\n");
                    JOptionPane.showMessageDialog(null, "您没有回答，答案是: " + english,"超时",JOptionPane.WARNING_MESSAGE,Data.timeout);

                }
            }catch (IOException e) {}
        }
        JOptionPane.showMessageDialog(null, "您已死亡，请多加练习，再次尝试！","死亡提示",JOptionPane.WARNING_MESSAGE,Data.dead);
    }



    public String SelectDegree(){
        String[] options = {"菜鸟", "简单", "困难", "地狱"};
        String info = (String)JOptionPane.showInputDialog(null, "请选择游戏难度：","提示", JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        return info;
    }

    public static void main(String[] args) throws IOException {
        new Client();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TextField getWord = (TextField) e.getSource();
        String wordToSend = getWord.getText();
        wordInput = wordToSend;
        System.out.println(wordInput);
        getWord.setText("");
    }

    public static void remake(){
        HP = 10;
        ps.println("NeedAWord");
    }
}

class openWordFileButtonActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e){
        Desktop desktop = Desktop.getDesktop();
        File file = new File("src/com/indexss/docs/notMasteredWord.txt");
        if(file.exists()){
            try{
                desktop.open(file);
            }catch(Exception ex){ex.printStackTrace();}
        }
    }
}

class openCorrectWordFileButtonActionListerner implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        Desktop desktop = Desktop.getDesktop();
        File file = new File("src/com/indexss/docs/MasteredWord.txt");
        if(file.exists()){
            try{
                desktop.open(file);
            }catch(Exception ex){ex.printStackTrace();}
        }
    }
}

class remakeButtonActionListener implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        if(Client.HP <= 0) {
            System.out.println("Pressed!");

            Client.remake();
        }
    }
}