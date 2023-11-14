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
package be.fgov.bosa.etransproxy.server;

import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Bart Hanssens
 */
@Component
public class ETranslationClient {
	private static final Logger LOG = LoggerFactory.getLogger(ETranslationClient.class);

	private final HttpClient client;

	@Value("${etranslate.url}")
	private URI uri;

	@Value("${etranslate.auth.user}")
	private String user;

	@Value("${etranslate.auth.pass}")
	private String pass;

	private String getAuthHeader() {
		String str = user + ":" + pass;
		return "Basic " + Base64.getEncoder().encodeToString(str.getBytes());
	}

	public void sendRequest(String body) throws IOException {
		LOG.info(body);
		HttpRequest req = HttpRequest.newBuilder()
									.header("Authorization", getAuthHeader())
									.header("Content-Type", "application/json")
									.POST(BodyPublishers.ofString(body))
									.uri(uri).build();
	
		try {
			HttpResponse<String> resp = client.send(req, BodyHandlers.ofString());
			LOG.info("Sending request to {}, status {}, {}", req.uri().toString(), resp.statusCode(), resp.body());
		} catch (InterruptedException ex) {
			throw new IOException(ex);
		}
	}

	private HttpClient buildHttpClient() {
		return HttpClient.newBuilder()
			.version(HttpClient.Version.HTTP_1_1)
			.followRedirects(HttpClient.Redirect.NORMAL)
			.connectTimeout(Duration.ofSeconds(20))
			.proxy(ProxySelector.getDefault())
			.build();
	}

	public ETranslationClient() {
		this.client = buildHttpClient();
	}
}
