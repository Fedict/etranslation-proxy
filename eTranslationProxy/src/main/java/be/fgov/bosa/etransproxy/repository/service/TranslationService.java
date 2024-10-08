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

import be.fgov.bosa.etransproxy.repository.SourceRepository;
import be.fgov.bosa.etransproxy.repository.TargetRepository;
import be.fgov.bosa.etransproxy.repository.TaskRepository;
import be.fgov.bosa.etransproxy.repository.dao.Task;
import be.fgov.bosa.etransproxy.repository.dao.SourceText;
import be.fgov.bosa.etransproxy.repository.dao.TargetText;
import be.fgov.bosa.etransproxy.request.ETranslationRequestBuilder;
import be.fgov.bosa.etransproxy.server.ETranslationClient;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.StringUtils;

/**
 * Implementation of the translation service
 * 
 * @author Bart Hanssens
 */
@Service
public class TranslationService {
	private static final Logger LOG = LoggerFactory.getLogger(TranslationService.class);

	@Value("${etranslate.requests.delay}")
	private int delay;

	@Value("${etranslate.requests.quota.delay}")
	private int quotaDelay;
	
	@Value("${etranslate.requests.expire}")
	private int expire;

	@Value("${etranslate.auth.application}")
	private String application;

	@Value("${etranslate.auth.user}")
	private String user;

	@Value("${callback.error}")
	private String callbackError;

	@Value("${callback.ok}")
	private String callbackOk;
	
	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private SourceRepository sourceRepository;

	@Autowired
	private TargetRepository targetRepository;
	
	@Autowired
	private ETranslationClient client;

	@Autowired
    private PlatformTransactionManager tm;

	private final DefaultTransactionDefinition td = new DefaultTransactionDefinition();
	private final DefaultTransactionDefinition tdro = new DefaultTransactionDefinition();
	
	// error code of the eTranslation server
	private final static String QUOTA_EXCEEDED = "-20028";

	/**
	 * Start the translation for a given text in a source language into one or more target languages.
	 * 
	 * @param text text to translate
	 * @param sourceLang source language code
	 * @param targetLangs target language code(s)
	 * @return hash of the source text to be translated 
	 */
	public String initTranslation(String text, String sourceLang, List<String> targetLangs) {
		String hash = DigestUtils.sha1Hex(text);

		TransactionStatus transaction = tm.getTransaction(td);
		try {
			Optional<SourceText> st  = sourceRepository.findById(hash);
			SourceText toBeTranslated = st.isPresent() 
										? st.get()
										: sourceRepository.save(new SourceText(hash, sourceLang, text));
			// add one task per language to translation queue
			for (String targetLang: targetLangs) {
				// already translated or not ?
				if (!targetRepository.existsBySourceIdAndLangIgnoreCase(hash, targetLang)) {
					// already in the queue or not ?
					if(!taskRepository.existsBySourceIdAndTargetLangIgnoreCase(hash, targetLang)) {
						LOG.info("New request to translate text {} from {}", StringUtils.truncate(text, 30), sourceLang);
						text = StringUtils.truncate(text, 5000);
						taskRepository.save(new Task(toBeTranslated, sourceLang, targetLang));
					}
				}
			}
			tm.commit(transaction);
		} catch (Exception e) {
			LOG.error("Error in transaction: {}", e.getMessage());
			tm.rollback(transaction);
		}
		return hash;
	}

	/**
	 * Look up a translation in the database (which may or may not be present)
	 * 
	 * @param hash hash of the source text
	 * @param targetLang target language
	 * @return translation, or null if not present 
	 */
	public String retrieveTranslation(String hash, String targetLang) {
		LOG.info("Request to retrieve text with SHA1 {} int {}", hash, targetLang);

		TransactionStatus transaction = tm.getTransaction(tdro);
		try {
			TargetText text = targetRepository.findOneBySourceIdAndLangIgnoreCase(hash, targetLang);
			tm.commit(transaction);
			return (text != null) ? text.getContent() : null;
		} catch (Exception e) {
			LOG.error("Error in transaction: {}", e.getMessage());
			tm.rollback(transaction);
			return null;
		}
	}	

	/**
	 * Sleep for a small delay in order to not overload the EU eTranslation server
	 */
	private void sleep(int seconds) {
		try {
			TimeUnit.SECONDS.sleep(seconds);
		} catch (InterruptedException ex) {
			LOG.error("Sleep interrupted");
		}
	}

	/**
	 * Get a builder for the contents of a translation request for the eTranslation server
	 * 
	 * @param sourceLang source language code
	 * @param targetLang target language code
	 * @return builder
	 */
	private ETranslationRequestBuilder initETranslationRequest(String sourceLang, String targetLang) {
		ETranslationRequestBuilder etBuilder = new ETranslationRequestBuilder();
		etBuilder.setCallbacks(callbackOk, callbackError);
		etBuilder.setSourceLang(sourceLang);
		etBuilder.setTargetLang(targetLang);
		etBuilder.setApplication(application, user);
		return etBuilder;
	}

	/**
	 * Send single HTTP request to the EU eTranslation service
	 * 
	 * @param task
	 * @param json
	 * @return true upon success, false upon failure
	 * @throws IOException 
	 */
	private boolean separateRequest(Task task, String json) throws IOException {
		String code = client.sendRequest(json);
		while (code != null && code.equals(QUOTA_EXCEEDED)) {
			LOG.error("Quota exceeded");
			sleep(quotaDelay);
			code = client.sendRequest(json);
		}

		TransactionStatus transaction = tm.getTransaction(td);
		try {
			task.setSubmitted(Instant.now());	
			taskRepository.save(task);
			tm.commit(transaction);
			return true;
		} catch (Exception e) {
			LOG.error("Error in transaction: {}", e.getMessage());
			tm.rollback(transaction);
			return false;
		}
	}

	/**
	 * Send a series of HTTP requests to the eTranslation service
	 * 
	 * @param tasks translation queue
	 * @param sourceLang source language code
	 * @param targetLang target language code
	 */
	private void separateRequests(List<Task> tasks, String sourceLang, String targetLang) {
		tdro.setReadOnly(true);

		for(Task task: tasks) {
			ETranslationRequestBuilder etBuilder = initETranslationRequest(sourceLang, targetLang);
			
			TransactionStatus transaction = tm.getTransaction(tdro);
			try {
				etBuilder.setText(task.getSource().getContent());
				etBuilder.setReference(task.getSource().getId());
				tm.commit(transaction);
			} catch (Exception e) {
				LOG.error("Error in transaction: {}", e.getMessage());
				tm.rollback(transaction);
			}
			
			try {
				separateRequest(task, etBuilder.buildAsJson());
			} catch (IOException ioe) {
				LOG.error("Error sending request {}", ioe.getMessage());
			}
			sleep(delay);
		}
	}

	/**
	 * Reset the expired translation tasks in our system
	 * (i.e. tasks that need to be submitted again because they didn't get a response within the allocated time)
	 */
	private void resetExpired() {
		Instant expired = Instant.now().minus(expire, ChronoUnit.SECONDS);

		TransactionStatus transaction = tm.getTransaction(td);
		try {
			int nr = taskRepository.updateExpired(expired);
			if (nr > 0) {
				LOG.warn("Resetting {} expired tasks", nr);
			} else {
				LOG.debug("No expired tasks");
			}
			tm.commit(transaction);
		} catch (Exception e) {
			LOG.error("Error in transaction: {}", e.getMessage());
			tm.rollback(transaction);
		}
	}

	/**
	 * Schedule a series of translation requests
	 */
	@Scheduled(fixedDelayString = "${etranslate.requests.queue.delay}", timeUnit = TimeUnit.SECONDS)
	public void sendTranslationRequests() {
		resetExpired();
	
		// get different source-target combinations
		List<Object[]> pairs = taskRepository.findLangPair();

		for(Object[] pair: pairs) {
			String sourceLang = (String) pair[0];
			String targetLang = (String) pair[1];

			List<Task> tasks = Collections.emptyList();

			TransactionStatus transaction = tm.getTransaction(tdro);
			try {
				tasks = taskRepository.findToSubmit(sourceLang, targetLang);
				tm.commit(transaction);
			} catch (Exception e) {
				LOG.error("Error in transaction: {}", e.getMessage());
				tm.rollback(transaction);
			}
		
			separateRequests(tasks, sourceLang, targetLang);
		}
	}

	/**
	 * Count the number of tasks / requested number of translations
	 * 
	 * @return count
	 */
	public long countTasks() {
		return taskRepository.count();
	}

	/**
	 * Process response from the EU eTranslation service
	 * 
	 * @param response
	 * @param reference
	 * @param targetLang 
	 */
	public void processResponse(String response, String reference, String targetLang) {
		if (!sourceRepository.existsById(reference)) {
			LOG.error("No source found for reference {}", reference);
			return;
		}
		SourceText source = new SourceText();
		source.setId(reference);
		response = response.trim();

		// save received translation and remove task from the queue
		TargetText target = new TargetText(source, targetLang, response, DigestUtils.sha1Hex(response));
		
		TransactionStatus transaction = tm.getTransaction(td);
		try {
			targetRepository.save(target);
			int nr = taskRepository.deleteBySourceAndTargetLang(source, targetLang);
			if (nr != 1) {
				LOG.error("Deleted {} tasks for {} {}", nr, reference, targetLang);
			}
			tm.commit(transaction);
		} catch (Exception e) {
			LOG.error("Error in transaction: {}", e.getMessage());
			tm.rollback(transaction);
		}
	}
	
	/**
	 * Constructor
	 */
	public TranslationService() {
		tdro.setReadOnly(true);
	}
}
