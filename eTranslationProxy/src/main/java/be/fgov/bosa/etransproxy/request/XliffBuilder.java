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

import be.fgov.bosa.etransproxy.request.Xliff.Segment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/**
 *
 * @author Bart Hanssens
 */
public class XliffBuilder {
	private final static XmlMapper xmlMapper = new XmlMapper();

	private final Xliff xliff;
	private int size;

	public int addText(String id, String snippet) {
		Xliff.Unit unit = xliff.new Unit();
		unit.setId(id);
			
		Xliff.Segment segment = xliff.new Segment();
		segment.setSource(snippet);

		unit.setSegment(segment);
		xliff.getFile().addUnit(unit);

		size += snippet.length();
		return size;
	}

	public Xliff get() {
		return xliff;
	}

	public String getAsXml() throws JsonProcessingException {
		return xmlMapper.writeValueAsString(xliff);
	}

	public XliffBuilder(String sourceLang, String targetLang) {
		xliff = new Xliff();
		xliff.setSrcLang(sourceLang);
		xliff.setTrgLang(targetLang);
	
		Xliff.File file = xliff.new File();
		xliff.setFile(file);
	}
}
