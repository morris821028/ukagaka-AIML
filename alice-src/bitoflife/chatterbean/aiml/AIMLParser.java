/*
Copyleft (C) 2005 Hélio Perroni Filho
xperroni@yahoo.com
ICQ: 2490863

This file is part of ChatterBean.

ChatterBean is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version.

ChatterBean is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with ChatterBean (look at the Documents/ directory); if not, either write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA, or visit (http://www.gnu.org/licenses/gpl.txt).
 */

package bitoflife.chatterbean.aiml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import bitoflife.chatterbean.Graphmaster;
import bitoflife.chatterbean.util.Searcher;

public class AIMLParser {
	/*
	 * Attributes
	 */

	private final Searcher searcher = new Searcher();
	private final AIMLHandler handler = new AIMLHandler();
	private SAXParser parser;

	/*
	 * Constructor
	 */

	public AIMLParser() throws AIMLParserConfigurationException {
		try {
			parser = SAXParserFactory.newInstance().newSAXParser();
		} catch (Exception e) {
			throw new AIMLParserConfigurationException(e);
		}
	}

	/*
	 * Methods
	 */
	public InputStream convertStreamToString(InputStream is) throws IOException {
		//
		// To convert the InputStream to String we use the
		// Reader.read(char[] buffer) method. We iterate until the
		// Reader return -1 which means there's no more data to
		// read. We use the StringWriter class to produce the string.
		//
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is,
						"UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			java.lang.System.out.println(writer.toString());
			is = new ByteArrayInputStream(writer.toString().getBytes()); 
			return is;
		} else {
			return is;
		}
	}

	public void parse(Graphmaster graphmaster, InputStream... sources)
			throws AIMLParserException {
		try {
			for (InputStream aiml : sources) {
				// aiml = this.convertStreamToString(aiml);
				parser.parse(aiml, handler);
			}

			graphmaster.append(handler.unload());
		} catch (Exception e) {
			throw new AIMLParserException(e);
		}
	}
}