package com.hanains.network.chat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class ChatClient {
	private static final String SERVER_IP = "192.168.1.7";
	private static final int SERVER_PORT = 9999;

	public static void main(String[] args) {
		BufferedReader bufferedReader = null;
		PrintWriter printWriter = null;
		Socket socket = null;
		Scanner scanner = new Scanner(System.in);
		try {
			// 소켓 생성
			socket = new Socket();

			// 서버 연결
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));
			System.out.println("서버 연결 성공.");
			// IOStream받아오기
			bufferedReader = new BufferedReader(new InputStreamReader(
					socket.getInputStream(), StandardCharsets.UTF_8));
			printWriter = new PrintWriter(new OutputStreamWriter(
					socket.getOutputStream(), StandardCharsets.UTF_8));

			System.out.print("닉네임 >>");
			String nickname = scanner.nextLine();

			printWriter.println("join:" + nickname);
			printWriter.flush();

			/*
			 * nickname 재입력 방지. join ok랑.
			 */

			// 6.charClientReceiveThread시작
			new ChatClientReceiveThread(bufferedReader).start();

			// 7.키보드 입력 받기.
			while (true) {
				System.out.print(">>");
				String input = scanner.nextLine();

				if ("quit".equals(input) == true) {
					// 8 quit 프로토콜 처리
					printWriter.println("quit:");
					printWriter.flush();
					break;
				} else {
					// 9.메시지 처리.
					printWriter.println("message:"+input);
					printWriter.flush();
				}
			}
		} catch (Exception ex) {
			System.out.println("[클라이언트] 에러 :" + ex);
		} finally {
			try {
				scanner.close();
				if (bufferedReader != null) {
					bufferedReader.close();
				}
				if (printWriter != null) {
					printWriter.close();
				}
				if (socket != null && socket.isClosed() == false) {
					socket.close();
				}
			} catch (Exception e) {
				System.out.println("클라이언트 닫기 에러 " + e);
			}
		}
	}
}
