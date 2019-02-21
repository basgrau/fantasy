/*
 * COPYRIGHT LICENSE: This information contains sample code provided in source code form.
 * You may copy, modify, and distribute these sample programs in any form without payment
 * to IBM for the purposes of developing, using, marketing or distributing application
 * programs conforming to the application programming interface for the operating platform
 * for which the sample code is written.
 *
 * Notwithstanding anything to the contrary, IBM PROVIDES THE SAMPLE SOURCE CODE ON
 * AN "AS IS" BASIS AND IBM DISCLAIMS ALL WARRANTIES, EXPRESS OR IMPLIED, INCLUDING,
 * BUT NOT LIMITED TO, ANY IMPLIED WARRANTIES OR CONDITIONS OF MERCHANTABILITY,
 * SATISFACTORY QUALITY, FITNESS FOR A PARTICULAR PURPOSE, TITLE, AND ANY WARRANTY OR
 * CONDITION OF NON-INFRINGEMENT. IBM SHALL NOT BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL OR CONSEQUENTIAL DAMAGES ARISING OUT OF THE USE OR OPERATION OF
 * THE SAMPLE SOURCE CODE. IBM HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT,
 * UPDATES, ENHANCEMENTS OR MODIFICATIONS TO THE SAMPLE SOURCE CODE.
 *
 * (C) Copyright IBM Corp. 2001, 2013.
 * All Rights Reserved. Licensed Materials - Property of IBM.
 */

package de.basgrau.herkules.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import de.basgrau.herkules.zeusconnect.ZeusConnectionClient;

/**
 * SendToZeus, Servlet zum Senden zu Zeus. Test
 */
@WebServlet("/ZeusSender")
public class SendToZeus extends HttpServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String strAction = request.getParameter("ACTION");
		PrintWriter out = response.getWriter();
		try {

			if (strAction == null) {
				out.println("Please specify the Action");
				out.println("Example : http://<host>:<port>/fantasy-herkules-war/ZeusSender?ACTION=sendToZeus");
			} else if (strAction.equalsIgnoreCase("sendToZeus")) {
				// Nachricht (JMS) an Zeus senden
				sendToZeus(request, response);
			} else {
				out.println("Incorrect Action Specified, the valid actions are");
				out.println("ACTION=sendToZeus");
			}
		} catch (Exception e) {
			out.println("Something unexpected happened, check the logs or restart the server");
			e.printStackTrace();
		}
	}

	/**
	 * sendToZeus.
	 * 
	 * @param request  Req.
	 * @param response Resp.
	 * @throws IOException Exp
	 */
	public void sendToZeus(HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter out;
		ZeusConnectionClient client = new ZeusConnectionClient();
		out = response.getWriter();
		
		out.println("Status: "+ client.sendToZeus("", "", ""));
	}
}
