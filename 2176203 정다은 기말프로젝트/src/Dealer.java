import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Dealer extends JFrame{
	int score; //카드 점수 합을 저장할 변수
	private int intnum; //문자열을 정수로 변환한 값을 저장할 변수
	private String num; //문자열 정수를 저장할 변수
	private int hitcount; //hit를 한 수 저장
	boolean first; //처음
	private Card d = new Card();
	
	//생성자, 변수 초기화 및 카드 2장 뽑기
	public Dealer(){
		this.score = 0; //카드 점수 초기화
		first = true;
		hitcount = 1;
		//카드 2장 뽑기
		for(int i = 0; i < 2; i++) {
			int a = (int)(Math.random()*4);
			int b = (int)(Math.random()*13);
			d.trumpcard.add(d.shape[a]+"의 "+ d.blackjack[b]);
		}
	}
	
	//블랙잭 체크 메소드
	//블랙잭이면 true, 아니면 false
	public boolean bjcheck() {
		if(d.trumpcard.get(0).contains("Ace") && (d.trumpcard.get(1).contains("10") || d.trumpcard.get(1).contains("Jack") 
				|| d.trumpcard.get(1).contains("Queen") || d.trumpcard.get(1).contains("King"))) {
			return true;
		}
		if(d.trumpcard.get(1).contains("Ace") && (d.trumpcard.get(0).contains("10") || d.trumpcard.get(0).contains("Jack")
				|| d.trumpcard.get(0).contains("Queen") || d.trumpcard.get(0).contains("King"))) {
			return true;
		}
		else {
			return false;
		}
	}
	
	//카드를 추가로 뽑는 hit()메소드
	public void hit(JTextArea p) {
		int a = (int)(Math.random()*4);
		int b = (int)(Math.random()*13);
		d.trumpcard.add(d.shape[a]+"의 "+ d.blackjack[b]); //카드 추가
		p.append(hitcount + "번째 hit\n");
		hitcount++;
	}
	
	//현재 카드를 보여주는 메소드
	public void showCard(JTextArea p) {
		for(int i = 0; i < d.trumpcard.size(); i++) {
			//처음 시작할 때 카드 한장은 보여주지 않는다.
			if(i == 0 && first == true) {
				first = false; //first 한번 쓴 경우 false로 변환
				p.append("??\n");
			}
			else {
				p.append(d.trumpcard.get(i)+"\n");
			}
		}
	}
	//카드 점수 합을 구하는 메소드
	public void sumScore() {
		int ace = 0; //에이스 카드 개수를 저장할 변수
		score = 0;
		for(int i = 0; i< d.trumpcard.size(); i++) {
			//카드가 Jack, Queen, King인 경우 10을 더한다
			if(d.trumpcard.get(i).contains("Jack") || d.trumpcard.get(i).contains("Queen") || d.trumpcard.get(i).contains("King")) {
				score += 10;
			}
			//카드가 Ace인 경우
			else if(d.trumpcard.get(i).contains("Ace")) {
				ace++; //Ace 카드 수 증가
			}
			//그 이외의 숫자 카드
			else {
				num = d.trumpcard.get(i).replaceAll("[^0-9]", ""); //정규식으로 숫자만 추출
				intnum = Integer.parseInt(num); //문자열인 숫자를 정수로 변환
				score += intnum;	
			}
		}
		//Ace 카드가 있는 경우
		if(ace != 0) {
			for(int i = 0; i < ace; i++) {
				//11더했을 때 22보다 작으면 11로 판단
				if((score+11) < 22 ) {
					score += 11;
				}
				//그 이외는 1로 판단
				else
					score += 1;
			}
		}
	}
		
	//score 변수 접근자
	public int getScore() {
		return score;	
	}
	
}
