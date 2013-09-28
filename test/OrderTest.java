/****************************************************************************

	ASCMII is a web application developped for the Ecole Centrale de Nantes
	aiming to organize quizzes during courses or lectures.
    Copyright (C) 2013  Malik Olivier Boussejra

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses/.

******************************************************************************/

import static play.test.Helpers.HTMLUNIT;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;
import static play.test.Helpers.testServer;

import org.junit.Test;

import alea.Alea;

import play.libs.F.Callback;
import play.test.TestBrowser;
import tests.Tests;

public class OrderTest {
	@Test
	public void hundredAnswerTest(){
		running(testServer(3333, fakeApplication()), HTMLUNIT, new Callback<TestBrowser>() {
			public void invoke(TestBrowser browser){				
				Tests.hundredAnswerTest();
			}
		});
	}
	
	/**
	 * Random number generation tests
	 */
	@Test
	public void randomInts(){
		for(int i = 0;i<100;i++){
			System.out.println(Alea.randomIntBetween(1,4));
		}
	}
}
