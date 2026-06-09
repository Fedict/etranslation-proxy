/*
 * Copyright (c) 2023, SPF BOSA
 * All rights reserved.
 *
 * Redistribution and use in sourceLanguage and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of sourceLanguage code must retain the above copyright notice, this
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

/**
 * ETranslation JSON payload for the EU eTranslate service
 * 
 * @author Bart Hanssens
 */
@JsonInclude(Include.NON_NULL)
public record ETranslationRequest(
	Information callerInformation,
	String sourceLanguage,
	List<String> targetLanguages,
	String textToTranslate,
	String requesterCallback,
	String errorCallback,
	Notifications notifications,
	HttpDelivery deliveries) {


	@JsonInclude(Include.NON_NULL)
	public record Information(
		String externalReference,
		String username) {}
	
	@JsonInclude(Include.NON_NULL)
	public record Notifications(
		HttpDelivery success,
		HttpDelivery failure) {}

	@JsonInclude(Include.NON_NULL)
	public record HttpDelivery(
		String http) {}
}
