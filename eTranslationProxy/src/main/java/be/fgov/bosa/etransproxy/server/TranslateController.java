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


import be.fgov.bosa.etransproxy.repository.service.TranslationService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Bart Hanssens
 */
@RestController
public class TranslateController {
	@Autowired
	private TranslationService ts;

	@PostMapping("/request/submit")
	public ResponseEntity<String> submit(@RequestParam String text, @RequestParam String sourceLang, @RequestParam List<String> targetLang) {
		if (sourceLang == null || sourceLang.isEmpty()) {
			return ResponseEntity.badRequest().body("Missing source language");
		}
		if (targetLang == null || targetLang.isEmpty()) {
			return ResponseEntity.badRequest().body("Missing target language(s)");
		}

		String hash = ts.initTranslation(text, sourceLang, targetLang);
		return ResponseEntity.accepted().body(hash);
	}

	@GetMapping("/request/retrieve")
	public ResponseEntity<String> retrieve(@RequestParam String hash, @RequestParam String targetLang) {
		if (hash == null || hash.isEmpty()) {
			return ResponseEntity.badRequest().body("Missing hash");
		}
		if (targetLang == null || targetLang.isEmpty()) {
			return ResponseEntity.badRequest().body("Missing target language(s)");
		}

		String translation = ts.retrieveTranslation(hash, targetLang);
		if (translation == null || translation.isEmpty()) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok(translation);
	}
}
