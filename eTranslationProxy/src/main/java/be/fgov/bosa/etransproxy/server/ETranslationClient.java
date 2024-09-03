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

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.net.ProxySelector;
import java.net.URI;

import java.nio.charset.StandardCharsets;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.routing.SystemDefaultRoutePlanner;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;

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

	private HttpClient client;

	@Value("${etranslate.url}")
	private URI uri;

	@Value("${etranslate.auth.user}")
	private String user;

	@Value("${etranslate.auth.pass}")
	private String pass;

	@Value("${etranslate.auth.scope}")
	private String scope;

	@PostConstruct
	private void buildHttpClient() {
		HttpClientBuilder builder = HttpClientBuilder.create();
		
		BasicCredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, pass.toCharArray());
		credentialsProvider.setCredentials(new AuthScope(scope, 443), credentials);
		builder.setDefaultCredentialsProvider(credentialsProvider); 
		builder.setRoutePlanner(new SystemDefaultRoutePlanner(ProxySelector.getDefault()));

		this.client = builder.build();
	}

	/**
	 * Send an HTTP request
	 * 
	 * @param body
	 * @return reference ID or error code
	 * @throws IOException 
	 */
	public String sendRequest(String body) throws IOException {
		HttpPost req = new HttpPost(uri);
		req.setEntity(new StringEntity(body, ContentType.APPLICATION_JSON));
		LOG.info(body);

		ClassicHttpResponse resp = (ClassicHttpResponse) client.execute(req);
		try {
			String id = EntityUtils.toString(resp.getEntity(), StandardCharsets.UTF_8);
			if (id != null && !id.startsWith("-")) {
				LOG.info("Sending request to {}, request ID {}", req.getRequestUri(), id);
			} else {
				LOG.error("Sending request to {}, error {}", req.getRequestUri(), id);
			}
			return id;
		} catch (ParseException ex) {
			LOG.error(ex.getMessage());
		}
		return null;
	}
}
