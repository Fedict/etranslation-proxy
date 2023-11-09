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

import be.fgov.bosa.etransproxy.repository.TaskRepository;
import be.fgov.bosa.etransproxy.repository.TextRepository;
import be.fgov.bosa.etransproxy.repository.dao.Task;
import be.fgov.bosa.etransproxy.repository.dao.Text;
import jakarta.transaction.Transactional;
import java.util.List;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author Bart.Hanssens
 */
@Service
public class TranslationServiceImpl implements TranslationService {
	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private TextRepository textRepository;

	@Transactional
	@Override
	public String initTranslation(String text, String source, List<String> targets) {
		String hash = DigestUtils.sha1Hex(text);

		if (!textRepository.existsByHash(hash)) {
			Text toBeTranslated = textRepository.save(new Text(source, text, hash));

			Long id = toBeTranslated.getId();
			toBeTranslated.setSourceId(id);
			textRepository.save(toBeTranslated);

			for (String target: targets) {
				taskRepository.save(new Task(toBeTranslated, target));
			}
		}
		return hash;
	}

	@Scheduled(fixedDelay = 300000)
	public void sendTranslationRequests() {
		
	}
}
