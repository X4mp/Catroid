/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2013 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://developer.catrobat.org/license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *  
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.catrobat.catroid.test.speechRecognition;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.util.ArrayList;

import org.catrobat.catroid.ProjectManager;
import org.catrobat.catroid.common.ScreenValues;
import org.catrobat.catroid.common.StandardProjectHandler;
import org.catrobat.catroid.speechrecognition.AudioInputStream;
import org.catrobat.catroid.speechrecognition.GoogleOnlineSpeechRecognizer;
import org.catrobat.catroid.speechrecognition.RecognizerCallback;
import org.catrobat.catroid.test.R;
import org.catrobat.catroid.test.utils.TestUtils;

import android.media.AudioFormat;
import android.os.Bundle;
import android.test.InstrumentationTestCase;
import android.util.Log;

public class WAVRecognizerTest extends InstrumentationTestCase implements RecognizerCallback {

	private String testProjectName = "testStandardProjectBuilding";
	private ArrayList<String> savedFiles = new ArrayList<String>();
	private ArrayList<String> lastMatches = new ArrayList<String>();
	private String lastErrorMessage = "";
	private static final int SPEECH_FILE_ID = R.raw.speechsample_directions;

	@Override
	public void tearDown() throws Exception {
		savedFiles.clear();
		lastMatches.clear();
		super.tearDown();
		TestUtils.clearProject(testProjectName);
	}

	@Override
	public void setUp() {
		TestUtils.clearProject(testProjectName);
		lastErrorMessage = "";
		savedFiles.clear();
		lastMatches.clear();
	}

	//	public void testConverting() throws IOException {
	//
	//		ScreenValues.SCREEN_WIDTH = 720;
	//		ScreenValues.SCREEN_HEIGHT = 1134;
	//		ProjectManager.getInstance().setProject(
	//				StandardProjectHandler.createAndSaveStandardProject(testProjectName, getInstrumentation()
	//						.getTargetContext()));
	//
	//		File testSpeechFile = TestUtils.saveFileToProject(testProjectName, "directionSpeech.wav", SPEECH_FILE_ID,
	//				getInstrumentation().getContext(), TestUtils.TYPE_SOUND_FILE);
	//
	//		GoogleOnlineSpeechRecognizer converter = new GoogleOnlineSpeechRecognizer(testSpeechFile.getAbsolutePath(),
	//				this);
	//		converter.setConvertOnly(true);
	//
	//		converter.start();
	//
	//		int i = 100;
	//		do {
	//			try {
	//				Thread.sleep(200);
	//			} catch (InterruptedException e) {
	//				e.printStackTrace();
	//			}
	//		} while ((i--) != 0 && savedFiles.size() == 0 && lastErrorMessage == "");
	//
	//		if (lastErrorMessage != "") {
	//			fail("Conversion brought an error: " + lastErrorMessage);
	//		}
	//
	//		assertTrue("There was no flac speechfile saved.", savedFiles.size() > 0);
	//		assertTrue("Converted File has wrong Format", savedFiles.get(0).endsWith(".flac"));
	//	}

	public void testOnlineRecognition() throws IOException {

		ScreenValues.SCREEN_WIDTH = 720;
		ScreenValues.SCREEN_HEIGHT = 1134;
		ProjectManager.getInstance().setProject(
				StandardProjectHandler.createAndSaveStandardProject(testProjectName, getInstrumentation()
						.getTargetContext()));

		File testSpeechFile = TestUtils.saveFileToProject(testProjectName, "directionSpeech.wav", SPEECH_FILE_ID,
				getInstrumentation().getContext(), TestUtils.TYPE_SOUND_FILE);

		FileInputStream speechFileStream = new FileInputStream(testSpeechFile);
		AudioInputStream ais = new AudioInputStream(speechFileStream, AudioFormat.ENCODING_PCM_16BIT, 1, 16000, 2,
				ByteOrder.LITTLE_ENDIAN, true);

		GoogleOnlineSpeechRecognizer converter = new GoogleOnlineSpeechRecognizer();
		converter.setCallbackListener(this);
		converter.setAudioInputStream(ais);

		converter.prepare();
		converter.start();

		int i = 100;
		do {
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} while ((i--) != 0 && lastMatches.size() == 0 && lastErrorMessage == "");

		if (lastErrorMessage != "") {
			fail("Conversion brought an error: " + lastErrorMessage);
		}

		assertTrue("There where no results.", lastMatches.size() > 0);
		assertTrue("\"links\" was not recognized.", matchesContainString("links"));
		assertTrue("\"rechts\" was not recognized.", matchesContainString("rechts"));
		assertTrue("\"rauf\" was not recognized.", matchesContainString("rauf"));
		assertTrue("\"runter\" was not recognized.", matchesContainString("runter"));
		assertTrue("\"stop\" was not recognized.", matchesContainString("stop"));

	}

	private boolean matchesContainString(String search) {
		for (String match : lastMatches) {
			if (match.contains(search)) {
				return true;
			}
		}
		return false;
	}

	public void onRecognizerResult(int resultCode, Bundle resultBundle) {

		if (resultCode == RecognizerCallback.RESULT_NOMATCH) {
			Log.v("SebiTest", "There was no recognition.");
			return;
		}

		ArrayList<String> matches = resultBundle.getStringArrayList("RESULT");
		Log.v("SebiTest", "Recognition.");
		Log.v("SebiTest", "Results: " + matches.toString());
		lastMatches.add(matches.toString());
	}

	public void onRecognizerError(int errorCode, String errorMessage) {
		Log.v("SebiTest", "Got error back: " + errorCode + " Message: " + errorMessage);
	}
}