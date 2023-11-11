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
import be.fgov.bosa.etransproxy.request.XliffBuilder;
import be.fgov.bosa.etransproxy.server.ETranslationClient;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author Bart Hanssens
 */
@Service
public class TranslationServiceImpl implements TranslationService {
	private static final Logger LOG = LoggerFactory.getLogger(TranslationServiceImpl.class);

	@Value("${etranslate.requests.combine}")
	private boolean combine;

	@Value("${etranslate.xliff.maxsize}")
	private int maxSize;

	@Value("${etranslate.requests.delay}")
	private int delay;
	
	@Value("${callback.translated}")
	private String callbackTranslated;

	@Value("${callback.error}")
	private String callbackError;
	
	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private SourceRepository sourceRepository;

	@Autowired
	private TargetRepository targetRepository;
	
	private final ETranslationClient client = new ETranslationClient();


	@Transactional
	@Override
	public String initTranslation(String text, String sourceLang, List<String> targetLangs) {
		String hash = DigestUtils.sha1Hex(text);

		if (!sourceRepository.existsById(hash)) {
			LOG.info("Request to translate new text with SHA1 {} from {}", hash, sourceLang);

			SourceText toBeTranslated = sourceRepository.save(new SourceText(hash, sourceLang, text));
	
			for (String targetLang: targetLangs) {
				if (!targetRepository.existsBySourceAndLang(hash, targetLang)) {
					taskRepository.save(new Task(toBeTranslated, sourceLang, targetLang));
				}
			}
		}
		return hash;
	}

	@Override
	public String retrieveTranslation(String hash, String targetLang) {
		LOG.info("Request to retrieve text with SHA1 {} int {}", hash, targetLang);

		TargetText text = targetRepository.findOneBySourceAndLang(hash, targetLang);
		return text.getContent();
	}	

	private void sleep() {
		try {
			TimeUnit.MILLISECONDS.sleep(delay);
		} catch (InterruptedException ex) {
		}
	}

	private void combinedRequests(List<Task> tasks, String sourceLang, String targetLang) throws IOException {
		ETranslationRequestBuilder etBuilder = new ETranslationRequestBuilder(callbackTranslated, callbackError);
		etBuilder.setSourceLang(sourceLang);
		etBuilder.setTargetLang(targetLang);

		XliffBuilder xlBuilder = new XliffBuilder();
		xlBuilder.setSourceLang(sourceLang);
		xlBuilder.setTargetLang(targetLang);

		for(Task task: tasks) {
			SourceText source = task.getSource();
			xlBuilder.addText(source.getId(), source.getContent());

			if (xlBuilder.getSize() > maxSize) {
				etBuilder.setDocument("xliff", xlBuilder.buildAsXml());
				client.sendRequest(etBuilder.buildAsJson());
				sleep();
	
				etBuilder = new ETranslationRequestBuilder(callbackTranslated, callbackError);
				etBuilder.setSourceLang(sourceLang);
				etBuilder.setTargetLang(targetLang);
				xlBuilder = new XliffBuilder();
			}
		}
		etBuilder.setDocument("xliff", xlBuilder.buildAsXml());
		client.sendRequest(etBuilder.buildAsJson());
	}

	private void singleRequests(List<Task> tasks, String sourceLang, String targetLang) throws IOException {
		for(Task task: tasks) {
			ETranslationRequestBuilder etBuilder = new ETranslationRequestBuilder(callbackTranslated, callbackError);
			etBuilder.setSourceLang(sourceLang);
			etBuilder.setTargetLang(targetLang);
			etBuilder.setText(task.getSource().getContent());
	
			client.sendRequest(etBuilder.buildAsJson());
			sleep();
		}
	}

	@Scheduled(fixedDelayString = "${etranslate.queue.delay}")
	@Override
	public void sendTranslationRequests() {
		List<Object[]> pairs = taskRepository.findLangPair();

		for(Object[] pair: pairs) {
			String sourceLang = (String) pair[0];
			String targetLang = (String) pair[1];
			List<Task> tasks = taskRepository.findToSubmit(sourceLang, targetLang);

			try {
				if (combine) {
					combinedRequests(tasks, sourceLang, targetLang);
				} else {
					singleRequests(tasks, sourceLang, targetLang);
				}
			} catch (IOException ioe) {
				LOG.error("Could not send request for {} to {}", sourceLang, targetLang);
			}
		}
	}
}
