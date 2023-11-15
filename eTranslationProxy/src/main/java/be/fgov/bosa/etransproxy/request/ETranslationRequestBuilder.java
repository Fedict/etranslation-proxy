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
package be.fgov.bosa.etransproxy.request;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;

import java.util.Base64;
import java.util.List;

/**
 *
 * @author Bart.Hanssens
 */
public class ETranslationRequestBuilder {
	private final static ObjectMapper JSONMAPPER = new ObjectMapper();
	private final static Base64.Encoder ENCODER = Base64.getEncoder();

	private final ETranslationRequest request;

	public ETranslationRequestBuilder setApplication(String application, String user) {
		ETranslationRequest.Information info = request.new Information();
		info.setApplication(application);
		info.setUsername(user);
		request.setCallerInformation(info);

		return this;
	}
	
	public ETranslationRequestBuilder setCallbacks(String callbackOK, String callbackError) {
		request.setRequesterCallback(callbackOK);
		request.setErrorCallback(callbackError);

		return this;
	}

		public ETranslationRequestBuilder setDestinations(String http, String email) {
		ETranslationRequest.Destinations destinations = request.new Destinations();
		destinations.setHttpDestinations(List.of(http));
		if (email != null) {
			destinations.setEmailDestinations(List.of(email));
		}
		request.setDestinations(destinations);

		return this;
	}
	
	public ETranslationRequestBuilder setReference(String reference) {
		request.setExternalReference(reference);

		return this;
	}
	
	public ETranslationRequestBuilder setSourceLang(String lang) {
		request.setSourceLanguage(lang.toUpperCase());
		return this;
	}
	
	public ETranslationRequestBuilder setTargetLang(String lang) {
		request.setTargetLanguages(List.of(lang.toUpperCase()));
		return this;
	}

	public ETranslationRequestBuilder setDocument(String format, String content) {
		String encoded = ENCODER.encodeToString(content.getBytes(StandardCharsets.UTF_8));

		ETranslationRequest.Base64Doc base64doc = request.new Base64Doc();
		base64doc.setFormat(format);
		base64doc.setContent(encoded);
		request.setDocumentToTranslateBase64(base64doc);

		return this;
	}

	public ETranslationRequestBuilder setText(String content) {
		request.setTextToTranslate(content);
		return this;
	}

	public ETranslationRequest build() {
		return request;
	}

	public String buildAsJson() throws JsonProcessingException {
		return JSONMAPPER.writeValueAsString(request);
	}

	public ETranslationRequestBuilder() {
		request = new ETranslationRequest();
	}
}
