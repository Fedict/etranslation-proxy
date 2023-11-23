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
public class ETranslationRequest {
	private String externalReference;
	private Information callerInformation;
	private String sourceLanguage;
	private List<String> targetLanguages;
	private String textToTranslate;
	private String documentToTranslatePath;
	private Base64Doc documentToTranslateBase64;
	private String domain;
	private String requesterCallback;
	private String errorCallback;
	private Destinations destinations;

	/**
	 * @return external reference
	 */
	public String getExternalReference() {
		return externalReference;
	}

	/**
	 * @param externalReference external reference
	 */
	public void setExternalReference(String externalReference) {
		this.externalReference = externalReference;
	}

	/**
	 * @return caller information
	 */
	public Information getCallerInformation() {
		return callerInformation;
	}

	/**
	 * @param callerInformation caller information
	 */
	public void setCallerInformation(Information callerInformation) {
		this.callerInformation = callerInformation;
	}

	/**
	 * @return source language code
	 */
	public String getSourceLanguage() {
		return sourceLanguage;
	}

	/**
	 * @param sourceLanguage source language code
	 */
	public void setSourceLanguage(String sourceLanguage) {
		this.sourceLanguage = sourceLanguage;
	}

	/**
	 * @return target language codes
	 */
	public List<String> getTargetLanguages() {
		return targetLanguages;
	}

	/**
	 * @param targetLanguages target language codes
	 */
	public void setTargetLanguages(List<String> targetLanguages) {
		this.targetLanguages = targetLanguages;
	}

	/**
	 * @return text to translate
	 */
	public String getTextToTranslate() {
		return textToTranslate;
	}

	/**
	 * @param textToTranslate text to translate
	 */
	public void setTextToTranslate(String textToTranslate) {
		this.textToTranslate = textToTranslate;
	}

	/**
	 * @return document to translate path
	 */
	public String getDocumentToTranslatePath() {
		return documentToTranslatePath;
	}

	/**
	 * @param documentToTranslatePath document to translate path to set
	 */
	public void setDocumentToTranslatePath(String documentToTranslatePath) {
		this.documentToTranslatePath = documentToTranslatePath;
	}

	/**
	 * @return the documentToTranslateBase64
	 */
	public Base64Doc getDocumentToTranslateBase64() {
		return documentToTranslateBase64;
	}

	/**
	 * @param documentToTranslateBase64 the documentToTranslateBase64 to set
	 */
	public void setDocumentToTranslateBase64(Base64Doc documentToTranslateBase64) {
		this.documentToTranslateBase64 = documentToTranslateBase64;
	}

	/**
	 * @return translation domain
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * @param domain domain translation
	 */
	public void setDomain(String domain) {
		this.domain = domain;
	}

	/**
	 * @return requester callback
	 */
	public String getRequesterCallback() {
		return requesterCallback;
	}

	/**
	 * @param requesterCallback requester callback
	 */
	public void setRequesterCallback(String requesterCallback) {
		this.requesterCallback = requesterCallback;
	}

	/**
	 * @return error callback
	 */
	public String getErrorCallback() {
		return errorCallback;
	}

	/**
	 * @param errorCallback error callback
	 */
	public void setErrorCallback(String errorCallback) {
		this.errorCallback = errorCallback;
	}

	/**
	 * @return destinations
	 */
	public Destinations getDestinations() {
		return destinations;
	}

	/**
	 * @param destinations destinations
	 */
	public void setDestinations(Destinations destinations) {
		this.destinations = destinations;
	}

	@JsonInclude(Include.NON_NULL)
	public class Information {
		private String application;
		private String username;
		/**
		 * @return the application
		 */
		public String getApplication() {
			return application;
		}

		/**
		 * @param application the application to set
		 */
		public void setApplication(String application) {
			this.application = application;
		}

		/**
		 * @return the username
		 */
		public String getUsername() {
			return username;
		}

		/**
		 * @param username the username to set
		 */
		public void setUsername(String username) {
			this.username = username;
		}
	}

	@JsonInclude(Include.NON_NULL)
	public class Base64Doc {
		private String content;
		private String format;
		private String fileName;

		/**
		 * @return the content
		 */
		public String getContent() {
			return content;
		}

		/**
		 * @param content the content to set
		 */
		public void setContent(String content) {
			this.content = content;
		}

		/**
		 * @return the format
		 */
		public String getFormat() {
			return format;
		}

		/**
		 * @param format the format to set
		 */
		public void setFormat(String format) {
			this.format = format;
		}

		/**
		 * @return the fileName
		 */
		public String getFileName() {
			return fileName;
		}

		/**
		 * @param fileName the fileName to set
		 */
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
	}

	@JsonInclude(Include.NON_NULL)
	public class Destinations {
		private List<String> httpDestinations;
		private List<String> ftpDestinations;
		private List<String> sftpDestinations;
		private List<String> emailDestinations;

		/**
		 * @return the httpDestinations
		 */
		public List<String> getHttpDestinations() {
			return httpDestinations;
		}

		/**
		 * @param httpDestinations the httpDestinations to set
		 */
		public void setHttpDestinations(List<String> httpDestinations) {
			this.httpDestinations = httpDestinations;
		}

		/**
		 * @return the ftpDestinations
		 */
		public List<String> getFtpDestinations() {
			return ftpDestinations;
		}

		/**
		 * @param ftpDestinations the ftpDestinations to set
		 */
		public void setFtpDestinations(List<String> ftpDestinations) {
			this.ftpDestinations = ftpDestinations;
		}

		/**
		 * @return the sftpDestinations
		 */
		public List<String> getSftpDestinations() {
			return sftpDestinations;
		}

		/**
		 * @param sftpDestinations the sftpDestinations to set
		 */
		public void setSftpDestinations(List<String> sftpDestinations) {
			this.sftpDestinations = sftpDestinations;
		}

		/**
		 * @return the emailDestinations
		 */
		public List<String> getEmailDestinations() {
			return emailDestinations;
		}

		/**
		 * @param emailDestinations the emailDestinations to set
		 */
		public void setEmailDestinations(List<String> emailDestinations) {
			this.emailDestinations = emailDestinations;
		}
	}
}
