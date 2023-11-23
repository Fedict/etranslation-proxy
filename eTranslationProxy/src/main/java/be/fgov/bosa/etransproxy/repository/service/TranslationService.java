/*
 * Copyright (c) 2023, FPS BOSA
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package be.fgov.bosa.etransproxy.repository.service;

import java.util.List;

/**
 * Translation service putting it all together
 * 
 * @author Bart Hanssens
 */
public interface TranslationService {
	/**
	 * Init a translation
	 * 
	 * @param text text to be translated
	 * @param source source language code
	 * @param targets target language codes
	 * @return hash of the text
	 */
	public String initTranslation(String text, String source, List<String> targets);

	/**
	 * Send translation requests to server
	 */
	public void sendTranslationRequests();

	/**
	 * Get translated text from repository, or null
	 * 
	 * @param hash hash code of the source text
	 * @param targetLang target language code
	 * @return translated text or null
	 */
	public String retrieveTranslation(String hash, String targetLang);

	/**
	 * Process translation callbacks from eTranslation server
	 * 
	 * @param response translated text
	 * @param reference reference
	 * @param targetLang target language
	 */
	public void processResponse(String response, String reference, String targetLang);
}
