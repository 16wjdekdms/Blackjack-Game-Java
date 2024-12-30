import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Blackjack extends JFrame implements ActionListener, KeyListener{
	private JTextArea gameboard; //게임 화면
	private JPanel startPanel; //게임 시작 화면
	private JPanel playPanel; //게임 화면
	private JPanel manualPanel; //게임 설명 화면
	private int m = 0; //배팅 금액을 저장할 변수
	private JLabel moneyL; //사용자 금액과 배팅금 레이블
	//hit, stand, doubledown, 다시시작 버튼
	private JButton hitb;
	private JButton standb;
	private JButton doubleb;
	private JButton re;
	private int money = 0; //사용자의 돈을 저장할 변수
	private boolean hitcheck; //히트를 한번 했는지를 판단하는데 사용 
	private User user = new User(m); //User 객체
	private Dealer dealer = new Dealer(); //Dealer 객체
	CardLayout layout = new CardLayout();
	JTextField batField; //배팅금을 입력할 텍스트필드
	Container cPane;
	private boolean dealerlose;
	
	public Blackjack() {
		setTitle("BlackJack"); //게임 이름
		setSize(700, 500); //프레임 크기 설정
	
		//패널 생성
		startPanel = new JPanel();
		playPanel = new JPanel();
		manualPanel = new JPanel();
		
		//게임 방법 설명
		JTextArea txt = new JTextArea();
		txt.append("- 베팅금액 입력 칸에 금액 입력 후 엔터를 입력하면 게임시작\n"
					+ "- 에이스 카드는 1이나 11로 취급할 수 있고, 10, J, Q, K는 모두 10으로 계산한다.\n"
					+ "- 처음 받은 카드 두 장이 에이스와 10, J, Q, K 중의 하나로 합이 21이 되면 블랙잭이 되고, 베팅한 금액의 두 배로 돈을 받는다.\n"
					+ "- 딜러가 자신을 포함한 참가자 전원에게 카드 두 장을 나누어주는데, 딜러의 카드 한 장은 상대에게 보이지 않는다.\n"
					+ "- 카드의 합이 딜러보다 먼저 21이 되거나 딜러보다 21에 가깝게 되면 이기고, 카드를 더 받았는데 21을 초과하면 버스트(Bust)된다.\n"
					+ "- 먼저 받은 카드 두 장의 합이 21에 못 미치면 히트(Hit)버튼을 클릭\n"
					+ "- 멈추려면 스탠드(Stand)버튼 클릭\n"
					+ "- 더블다운: 첫 번째 히트를 할 때, 이전에 베팅한 금액만큼 더 베팅하면서 3장째를 받을 수 있다.\n"
					+ "- 더블다운버튼을 클릭하면 더 이상 카드를 받을 수 없다.\n"
					+ "- 딜러는 카드의 합이 16 이하면 무조건 한 장을 더 받아야 하고, 17 이상의 경우에는 멈추어야 한다.\n"
					+ "- 딜러의 카드와 합이 같으면 비긴 것이 된다.\n");
		manualPanel.add(txt);
		
		setting();
		
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void setting() {
		playPanel.setLayout(new BorderLayout());
		
		//게임 화면
		gameboard = new JTextArea();
		gameboard.append("=========================================\n");
		gameboard.append("                          블랙잭 게임 start\n");
		gameboard.append("=========================================\n");
		
		playPanel.add(gameboard, "Center"); //게임 화면 중앙에 배치
		
		//배팅금액 입력 패널
		JPanel input = new JPanel();
		playPanel.add(input, "North");
		input.setLayout(new BorderLayout());
		
		batField = new JTextField(15);
		batField.addKeyListener(this);
		input.add(batField, "Center");
		
		JLabel inputLabel = new JLabel("배팅 금액 입력");
		input.add(inputLabel, "West");
		
		//오른쪽 패널(버튼+금액레이블)
		JPanel right_panel = new JPanel();
		right_panel.setLayout(new BorderLayout());
		playPanel.add(right_panel, "East");
		
		//사용자 금액 레이블
		moneyL = new JLabel("money: " + money + " 배팅금액: " + m);
		right_panel.add(moneyL, "North");
				
		//hit, stand, doubledown, 다시시작 버튼
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(4, 1));
		hitb = new JButton("hit");
		hitb.addActionListener(this);
		buttons.add(hitb);
				
		standb = new JButton("stand");
		standb.addActionListener(this);
		buttons.add(standb);
				
		doubleb = new JButton("doubledown");
		doubleb.addActionListener(this);
		buttons.add(doubleb);
				
		re = new JButton("다시 시작");
		re.addActionListener(this);
		buttons.add(re);
				
		right_panel.add(buttons, "Center");
		
	}
	
	//게임 시작 전 메뉴 선택
	public void Game() {
		cPane = getContentPane();
		setLayout(layout);
		
		add(startPanel);
		add(playPanel);
		add(manualPanel);
		
		//게임 시작 버튼
		JButton startButton = new JButton("게임 시작");
		startPanel.add(startButton);
		startButton.addActionListener(e->layout.next(cPane));
		
		//게임 방법 버튼
		JButton manualButton = new JButton("게임 방법");
		startPanel.add(manualButton);
		manualButton.addActionListener(e->layout.last(cPane));
		
		//게임 방법을 보고 게임 시작
		JButton back = new JButton("게임 시작");
		manualPanel.add(back);
		back.addActionListener(e ->layout.previous(cPane));
	}
	
	//딜러 카드 뽑기
	public void dealerPlay() {
		//딜러 턴
		gameboard.append("=========================================\n");
		gameboard.append("                  딜러 턴\n");
		gameboard.append("=========================================\n");
			
		dealer.sumScore(); //기본 카드 2장의 합 계산
		gameboard.append("                                                         score: " + dealer.getScore() + "\n");
		//딜러 카드 출력
		gameboard.append("딜러 패:\n");
		dealer.showCard(gameboard);
		
				
		//점수 합이 17 미만이면 계속 카드를 추가
		while(dealer.getScore() < 17) {
			dealer.hit(gameboard); //딜러 카드 뽑기
			//점수 계산 후 출력
			dealer.sumScore(); 
			gameboard.append("                                                     score: " + dealer.getScore() +"\n");
			//카드 출력
			dealer.showCard(gameboard);
			gameboard.append("=========================================\n");
			//딜러의 카드 합이 21을 넘는 경우
			if(dealer.getScore() > 21) {
				gameboard.append("=========================================\n");
				gameboard.append("          "+dealer.getScore() + "점, bust!! 사용자 승리\n");
				gameboard.append("=========================================\n");
				dealerlose = true;
				break;
			}
		}
		compare();
		
	}
	
	//사용자 게임 시작
	public void playGame() {
		hitcheck = false; //false로 초기화
		
		//초기 사용자 카드 출력
		gameboard.append("사용자 패:\n");
		user.showCard(gameboard);
		//초기 딜러 카드 출력
		gameboard.append("딜러 패:\n");
		dealer.showCard(gameboard);
		gameboard.append("\n");
		
		//사용자가 블랙잭이고 딜러도 블랙잭이면 무승부
		if(user.bjcheck() && dealer.bjcheck()) {
			//사용자 카드 출력
			gameboard.append("=========================================\n");
			gameboard.append("              모두 Blackjack\n");
			gameboard.append("=========================================\n");
			gameboard.append("사용자 패:\n");
			user.showCard(gameboard);
			//딜러 카드 출력
			gameboard.append("딜러 패:\n");
			dealer.showCard(gameboard);
			gameboard.append("(무승부이므로 배팅금은 얻지 못합니다)/n");
			//게임 진행 여부 묻기 
			gameboard.append("=========================================\n");
			gameboard.append("                 게임 종료\n");
			gameboard.append("=========================================\n");
			gameboard.append("다시 게임을 하고 싶다면 버튼을 누르세요\n");
		}
		//사용자가 블랙잭인 경우, 사용자 승리, 배팅 금액의 2배
		else if(user.bjcheck() && !dealer.bjcheck()) {
			gameboard.append("=========================================\n");
			gameboard.append("                 Blackjack 입니다, 사용자 승리!!\n");
			gameboard.append("=========================================\n");
			//사용자 카드 출력
			gameboard.append("사용자 패:\n");
			user.showCard(gameboard);
			//딜러 카드 출력
			gameboard.append("딜러 패:\n");
			dealer.showCard(gameboard);
			gameboard.append("Blackjack 이므로 배팅금의 2배를 얻게 됩니다.\n");
			money += (m*2); //사용자는 배팅금의 2배를 얻는다
			//게임 진행 여부 묻기 
			gameboard.append("=========================================\n");
			gameboard.append("                 게임 종료\n");
			gameboard.append("=========================================\n");
			gameboard.append("다시 게임을 하고 싶다면 버튼을 누르세요\n");
		}
		
		//딜러가 블랙잭인 경우
		else if(!user.bjcheck() && dealer.bjcheck()) {
			gameboard.append("=========================================\n");
			gameboard.append("                     딜러가 Blackjack 입니다\n");
			gameboard.append("=========================================\n");
			//사용자 카드 출력
			gameboard.append("사용자 패:\n");
			user.showCard(gameboard);
			//딜러 카드 출력
			gameboard.append("딜러 패:\n");
			dealer.showCard(gameboard);
			gameboard.append("딜러 승리!!, 배팅금이 차감됩니다.\n");
			money -= m; //딜러에게 돈을 준다.
			//게임 진행 여부 묻기 
			gameboard.append("=========================================\n");
			gameboard.append("                 게임 종료\n");
			gameboard.append("=========================================\n");
			gameboard.append("다시 게임을 하고 싶다면 버튼을 누르세요\n");
		}
	
		else {
			gameboard.append("=========================================\n");
			//사용자 선택(1-hit, 2-stand, 3-doubledown)
			gameboard.append("Hit, Stand, DoubleDown 버튼 중 선택\n");
		}
	}
	
	//점수 비교
	public void compare() {
		
		//딜러 bust로 사용자 승리
		if(dealerlose) {
			money += m;
			//게임 진행 여부 묻기 
			gameboard.append("=========================================\n");
			gameboard.append("                 게임 종료\n");
			gameboard.append("=========================================\n");
			gameboard.append("다시 게임을 하고 싶다면 버튼을 누르세요\n");
		}
		
		//사용자 승리
		else if(user.getScore() > dealer.getScore()) {
			dealer.sumScore();
			gameboard.append("=========================================\n");
			gameboard.append("   user score: " + user.getScore()+" > dealer score: " + dealer.getScore()+"\n");
			gameboard.append("=========================================\n");
			gameboard.append("                 사용자 승리\n");
			gameboard.append("=========================================\n");
			gameboard.append("배팅금을 얻게 됩니다.\n");
			money += m;
			//게임 진행 여부 묻기 
			gameboard.append("=========================================\n");
			gameboard.append("                 게임 종료\n");
			gameboard.append("=========================================\n");
			gameboard.append("다시 게임을 하고 싶다면 버튼을 누르세요\n");
		}
		//딜러 승리
		else if((user.getScore()) < (dealer.getScore())) {
			user.sumScore();
			dealer.sumScore();
			//점수 출력
			gameboard.append("=========================================\n");
			gameboard.append("   user score: " + user.getScore()+" < dealer score: " + dealer.getScore() + "\n");
			gameboard.append("=========================================\n");
			gameboard.append("                  딜러 승리\n");
			gameboard.append("=========================================\n");
			gameboard.append("딜러 승리!!, 배팅금이 차감됩니다.\n");
			money -= m; //딜러에게 돈을 준다.
			//게임 진행 여부 묻기 
			gameboard.append("=========================================\n");
			gameboard.append("                 게임 종료\n");
			gameboard.append("=========================================\n");
			gameboard.append("다시 게임을 하고 싶다면 버튼을 누르세요\n");
		}
		
		//무승부
		else if(user.getScore() == dealer.getScore()) {
			user.sumScore();
			dealer.sumScore();
			gameboard.append("=========================================\n");
			gameboard.append("   user score: " + user.getScore()+" = dealer score: " + dealer.getScore()+"\n");
			gameboard.append("=========================================\n");
			gameboard.append("                  무승부\n");
			gameboard.append("=========================================\n");
			gameboard.append("무승부이므로 배팅금은 얻지 못합니다.\n");
			//게임 진행 여부 묻기 
			gameboard.append("=========================================\n");
			gameboard.append("                 게임 종료\n");
			gameboard.append("=========================================\n");
			gameboard.append("다시 게임을 하고 싶다면 버튼을 누르세요\n");
		}
	}
	
	//게임 초기화
	public void reset() {
		m = 0;
		user = new User(m);
		dealer = new Dealer();
		gameboard.setText("=========================================\n");
		gameboard.append("                          블랙잭 게임 start\n");
		gameboard.append("=========================================\n");
		moneyL.setText("money: " + money + " 배팅금액: " + m);
	}
	
	//버튼 이벤트
	public void actionPerformed(ActionEvent e) {
		//hit 버튼 클릭
		if(e.getSource() == hitb) {
			user.hit(); //카드 뽑기
			hitcheck = true; //히트를 한번 하면 true
			//점수 계산 후 출력
			user.sumScore();
			gameboard.append("                                                        score: " + user.getScore() +"\n");
			//카드 화면에 출력
			user.showCard(gameboard);
			//bust인 경우
			if(user.getScore() > 21) {
				money -= m;
				gameboard.append("=========================================\n");
				gameboard.append("          "+user.getScore() + "점, bust!! 딜러 승리\n");
				gameboard.append("=========================================\n");
				gameboard.append("딜러 승리!!, 배팅금이 차감됩니다.\n");
	
				//게임 진행 여부 묻기 
				gameboard.append("=========================================\n");
				gameboard.append("                 게임 종료\n");
				gameboard.append("=========================================\n");
				gameboard.append("다시 게임을 하고 싶다면 버튼을 누르세요\n");
			}
		}
		//stand 버튼 클릭
		if(e.getSource() == standb) {
			user.sumScore();//점수 계산
			gameboard.append(" stand 선택, 카드를 받지 않습니다.\n");
			dealerPlay();//딜러 게임 시작
		}
		//doubledown 버튼 클릭
		if(e.getSource() == doubleb) {
			if(hitcheck == true) {
				gameboard.append("이미 hit를 하셨습니다! 다시 선택");
			}
			//배팅 금액 계산, 출력
			m = m * 2;
			user.setMoney(m);
			moneyL.setText("money: " + money + " 배팅금액: " + m);
			gameboard.append("                                                        배팅 금액: " + m + "\n");
			user.hit(); //마지막 카드 뽑기
			//점수 계산 후 출력
			user.sumScore();
			gameboard.append("                                                        score: " + user.getScore()+"\n");
			user.showCard(gameboard);
			//bust인 경우
			if(user.getScore() > 21) {
				money -= m;
				gameboard.append("=========================================\n");
				gameboard.append("          "+user.getScore() + "점, bust!! 딜러 승리\n");
				gameboard.append("=========================================\n");
				
				//게임 진행 여부 묻기 
				gameboard.append("=========================================\n");
				gameboard.append("                 게임 종료\n");
				gameboard.append("=========================================\n");
				gameboard.append("다시 게임을 하고 싶다면 버튼을 누르세요\n");
			}
			else {
				dealerPlay(); //딜러 게임 시작
			}
		}
		if(e.getSource() == re) {
			reset();
		}
	}
	
	//키 이벤트
	public void keyPressed(KeyEvent e){
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			String string_m = batField.getText();
			m = Integer.parseInt(string_m);
			moneyL.setText("money: " + money + " 배팅금액: " + m);
			batField.setText(""); //텍스트 필드 비우기
			playGame(); //배팅금을 입력하면 게임시작
		}
	}

	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	
	public static void main(String[] args) {
		Blackjack m = new Blackjack();
		m.Game();
	}
}
