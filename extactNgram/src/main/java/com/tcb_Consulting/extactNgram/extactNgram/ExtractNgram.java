package com.tcb_Consulting.extactNgram.extactNgram;

import java.io.IOException;

public interface ExtractNgram {
	
	 public void extractNgram(String text, int length, Boolean stopWords, Boolean overlap) throws IOException;

}
