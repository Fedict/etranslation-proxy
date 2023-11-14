/*
 * Copyright (c) 2023, Bart.Hanssens
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
package be.fgov.bosa.etransproxy.repository.dao;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.Instant;

/**
 *
 * @author Bart Hanssens
 */
@Entity
@Table(indexes = @Index(columnList = "sourceLang,targetLang"))
public class Task {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private SourceText source;

	@Column(length = 2, nullable = false)
    private String sourceLang;

	@Column(length = 2, nullable = false)
    private String targetLang;

	@Temporal(TemporalType.TIMESTAMP)
	private Instant submitted;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the source
	 */
	public SourceText getSource() {
		return source;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(SourceText source) {
		this.source = source;
	}

	/**
	 * @return the source language
	 */
	public String getSourcetLang() {
		return sourceLang;
	}

	/**
	 * @param sourceLang the source language to set
	 */
	public void setSourceLang(String sourceLang) {
		this.sourceLang = sourceLang;
	}

	/**
	 * @return the target
	 */
	public String getTargetLang() {
		return targetLang;
	}

	/**
	 * @param targetLang the target to set
	 */
	public void setTargetLang(String targetLang) {
		this.targetLang = targetLang;
	}

	/**
	 * @return the submitted
	 */
	public Instant getSubmitted() {
		return submitted;
	}

	/**
	 * @param submitted the submitted to set
	 */
	public void setSubmitted(Instant submitted) {
		this.submitted = submitted;
	}

	public Task() {
	}

	public Task(SourceText source, String sourceLang, String targetLang) {
		this.source = source;
		this.sourceLang = sourceLang;
		this.targetLang = targetLang;
	}

	public Task(SourceText source, String sourceLang, String targetLang, Instant submitted) {
		this(source, sourceLang, targetLang);
		this.submitted = submitted;
	}
}
