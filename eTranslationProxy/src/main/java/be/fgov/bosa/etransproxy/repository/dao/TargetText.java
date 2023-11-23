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
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import java.time.Instant;

/**
 * Translated text
 * 
 * @author Bart Hanssens
 */
@Entity
public class TargetText {
	@Id
	@Column(length = 40)	
	private String hash;

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private SourceText source;

	@Column(length = 2, nullable = false)
    private String lang;

	@Lob
	@Column(nullable = false)
    private String content;

	@Column(columnDefinition = "TIMESTAMP")
	private Instant created;

	/**
	 * @return the id
	 */
	public String getId() {
		return hash;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.hash = id;
	}

	/**
	 * @return the lang
	 */
	public String getLang() {
		return lang;
	}

	/**
	 * @param lang the lang to set
	 */
	public void setLang(String lang) {
		this.lang = lang;
	}

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
	 * @return the created
	 */
	public Instant getCreated() {
		return created;
	}

	/**
	 * @param created the created to set
	 */
	public void setCreated(Instant created) {
		this.created = created;
	}
	
	/**
	 * @return the source
	 */
	public SourceText getSource() {
		return source;
	}

	/**
	 * @param source the source
	 */
	public void setSource(SourceText source) {
		this.source = source;
	}

	public TargetText() {
	}

	public TargetText(SourceText source, String lang, String content, String hash) {
		this.source = source;
		this.lang = lang;
		this.content = content;
		this.hash = hash;
	}

	public TargetText(SourceText source, String lang, String content, String hash, Instant created) {
		this(source, lang, content, hash);
		this.created = created;
	}
}
