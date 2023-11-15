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
package be.fgov.bosa.etransproxy.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bart Hanssens
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonRootName(value="xliff")
public class Xliff {
//	@JacksonXmlProperty(isAttribute = true)
//    private final String xmlns = "urn:oasis:names:tc:xliff:document:2.0";

	@JacksonXmlProperty(isAttribute=true)
	private String version = "1.2";

	@JacksonXmlProperty(isAttribute=true)
	private String srcLang;

	@JacksonXmlProperty(isAttribute=true)
	private String trgLang;

	private File file;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getSrcLang() {
		return srcLang;
	}

	public void setSrcLang(String srcLang) {
		this.srcLang = srcLang;
	}

	public String getTrgLang() {
		return trgLang;
	}

	public void setTrgLang(String trgLang) {
		this.trgLang = trgLang;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public class File {
		@JacksonXmlProperty(isAttribute=true)
		private String id;

		@JacksonXmlProperty(isAttribute=true, localName="source-language")
		private String srcLang;

		@JacksonXmlProperty(isAttribute=true, localName="target-language")
		private String trgLang;

		public String getSrcLang() {
			return srcLang;
		}

		public void setSrcLang(String srcLang) {
			this.srcLang = srcLang;
		}

		public String getTrgLang() {
			return trgLang;
		}
	
		public void setTrgLang(String trgLang) {
			this.trgLang = trgLang;
		}

		@JacksonXmlElementWrapper(useWrapping = false)
		@JacksonXmlProperty(localName="trans-unit")
		private List<Unit> unit = new ArrayList<>();

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public List<Unit> getUnit() {
			return unit;
		}

		public void setUnit(List<Unit> unit) {
			this.unit = unit;
		}

		public void addUnit(Unit unit) {
			this.unit.add(unit);
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public class Unit {
		@JacksonXmlProperty(isAttribute=true)
		private String id;
		private Segment segment;

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public Segment getSegment() {
			return segment;
		}

		public void setSegment(Segment segment) {
			this.segment = segment;
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public class Segment {
		private Source source;
		private String target;

		public Source getSource() {
			return source;
		}

		public void setSource(Source source) {
			this.source = source;
		}

		public String getTarget() {
			return target;
		}

		public void setTarget(String target) {
			this.target = target;
		}
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public class Source {
		@JacksonXmlProperty(isAttribute=true, localName="xml:lang")
		private String lang;

		@JacksonXmlText
		private String text;
	
		public String getLang() {
			return lang;
		}
	
		public void setLang(String lang) {
			this.lang = lang;
		}

		public String getText() {
			return text;
		}
	
		public void setText(String text) {
			this.text = text;
		}
	}
}
