package com.vitaminc4.cookbox;

import java.util.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import android.util.Log;
import com.google.common.base.CharMatcher;

public class ImportHandler extends DefaultHandler{

	// ===========================================================
	// Fields
	// ===========================================================
	
	private String tag = null;
	private String value;
	private List<Recipe> recipes = new ArrayList<Recipe>();
	private Recipe current = null;

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public List<Recipe> getParsedData() {
		return this.recipes;
	}

	// ===========================================================
	// Methods
	// ===========================================================
	@Override
	public void startDocument() throws SAXException {
	}

	@Override
	public void endDocument() throws SAXException {
		// Nothing to do
	}

	/** Gets be called on opening tags like: 
	 * <tag> 
	 * Can provide attribute(s), when xml was like:
	 * <tag attribute="attributeValue">*/
	@Override
	public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
		this.value = "";
	  if (localName.equals("li")) return;
		else if (localName.equals("recipe")) this.current = new Recipe();
		else this.tag = localName;		
	}
	
	/** Gets be called on closing tags like: 
	 * </tag> */
	@Override public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
		if (localName.equals("recipe")) {
		  this.recipes.add(this.current);
		  this.current = null;
    } else if (localName.equals(this.tag)){
  	  try {
  	    this.current.set(this.tag, CharMatcher.WHITESPACE.trimFrom(this.value));
  	  } catch (Exception e) {
  	    // NoSuchFieldException || IllegalAccessException
  	  }
  	  this.tag = null;
		} else if (localName.equals("li")) {
		  this.current.add(this.tag, CharMatcher.WHITESPACE.trimFrom(this.value));
		}
	}
	

	@Override public void characters(char ch[], int start, int length) {
	  this.value += new String(ch, start, length);
	}
}