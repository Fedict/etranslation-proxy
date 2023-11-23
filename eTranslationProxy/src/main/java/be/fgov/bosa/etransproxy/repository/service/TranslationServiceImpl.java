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


import jakarta.transaction.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Implementation of the translation service
 * 
 * @author Bart Hanssens
 */
@Service
public class TranslationServiceImpl implements TranslationService {
	private static final Logger LOG = LoggerFactory.getLogger(TranslationServiceImpl.class);

	@Value("${etranslate.requests.delay}")
	private int delay;

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

	// error code of the eTranslation server
	private final static String QUOTA_EXCEEDED = "-20028";

	@Transactional
	@Override
	public String initTranslation(String text, String sourceLang, List<String> targetLangs) {
		String hash = DigestUtils.sha1Hex(text);

		// check if a request to translate this text was already made before
		if (!sourceRepository.existsById(hash)) {
			LOG.info("Request to translate new text {} from {}", StringUtils.truncate(text, 30), sourceLang);

			SourceText toBeTranslated = sourceRepository.save(new SourceText(hash, sourceLang, text));
			// add one task per language to translation queue
			for (String targetLang: targetLangs) {
				if (!targetRepository.existsBySourceIdAndLang(hash, targetLang)) {
					taskRepository.save(new Task(toBeTranslated, sourceLang, targetLang));
				}
			}
		}
		return hash;
	}

	@Override
	public String retrieveTranslation(String hash, String targetLang) {
		LOG.info("Request to retrieve text with SHA1 {} int {}", hash, targetLang);

		TargetText text = targetRepository.findOneBySourceIdAndLang(hash, targetLang);
		return (text != null) ? text.getContent() : null;
	}	

	/**
	 * Sleep for a small delay in order to not overload the eTranslation server
	 */
	private void sleep() {
		try {
			TimeUnit.SECONDS.sleep(delay);
		} catch (InterruptedException ex) {
			LOG.error("Sleep interrupted");
		}
	}

	/**
	 * Start building a translation request for the eTranslation server
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
	 * Send HTTP requests to the eTranslation service
	 * 
	 * @param tasks translation queue
	 * @param sourceLang source language code
	 * @param targetLang target language code
	 */
	private void separateRequests(List<Task> tasks, String sourceLang, String targetLang) {
		for(Task task: tasks) {
			ETranslationRequestBuilder etBuilder = initETranslationRequest(sourceLang, targetLang);
			etBuilder.setText(task.getSource().getContent());
			etBuilder.setReference(task.getSource().getId());
	
			try {
				String code = client.sendRequest(etBuilder.buildAsJson());
				if (code != null && code.equals(QUOTA_EXCEEDED)) {
					LOG.error("Quota exceeded");
					break;
				}
				task.setSubmitted(Instant.now());
				taskRepository.save(task);
			} catch (IOException ioe) {
				LOG.error("Error sending request {}", ioe.getMessage());
			}
			sleep();
		}
	}

	/**
	 * Check for requests not being translated within a given time frame, reset them to try again
	 */
	private void resetExpiredTasks() {
		Instant expired = Instant.now().minus(expire, ChronoUnit.SECONDS);
		List<Task> tasks = taskRepository.findBySubmittedLessThan(expired);
		
		if (tasks.isEmpty()) {
			return;
		}
		LOG.warn("Resetting {} expired tasks", tasks.size());
		for (Task task: tasks) {
			task.setSubmitted(null);
		}
		taskRepository.saveAll(tasks);
	}

	@Scheduled(fixedDelayString = "${etranslate.requests.queue.delay}", timeUnit = TimeUnit.SECONDS) 
	@Override
	@Transactional
	public void sendTranslationRequests() {
		resetExpiredTasks();

		// get different source-target combinations
		List<Object[]> pairs = taskRepository.findLangPair();

		for(Object[] pair: pairs) {
			String sourceLang = (String) pair[0];
			String targetLang = (String) pair[1];
			List<Task> tasks = taskRepository.findToSubmit(sourceLang, targetLang);

			separateRequests(tasks, sourceLang, targetLang);
		}
	}

	@Transactional
	@Override
	public void processResponse(String response, String reference, String targetLang) {
		if (!sourceRepository.existsById(reference)) {
			LOG.error("No source found for reference {}", reference);
			return;
		}
		SourceText source = new SourceText();
		source.setId(reference);

		// save received translation and remove task from the queue
		TargetText target = new TargetText(source, targetLang, response, DigestUtils.sha1Hex(response));
		targetRepository.save(target);
		LOG.info("TRy delete");
		int nr = taskRepository.deleteBySourceAndTargetLang(source, targetLang);
		if (nr != 1) {
			LOG.error("Deleted {} tasks for {} {}", nr, reference, targetLang);
		}
	}
}
