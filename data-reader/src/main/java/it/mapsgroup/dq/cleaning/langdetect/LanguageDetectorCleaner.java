package it.mapsgroup.dq.cleaning.langdetect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.optimaize.langdetect.LanguageDetector;
import com.optimaize.langdetect.LanguageDetectorBuilder;
import com.optimaize.langdetect.i18n.LdLocale;
import com.optimaize.langdetect.ngram.NgramExtractors;
import com.optimaize.langdetect.profiles.LanguageProfile;
import com.optimaize.langdetect.profiles.LanguageProfileReader;
import com.optimaize.langdetect.text.CommonTextObjectFactories;
import com.optimaize.langdetect.text.TextObject;
import com.optimaize.langdetect.text.TextObjectFactory;

import it.mapsgroup.dq.cleaning.RecordCleaner;
import it.mapsgroup.dq.vo.ItemVo;

@Service("language-detector")
public class LanguageDetectorCleaner<T> implements RecordCleaner<ItemVo> {
	
	Logger log = Logger.getLogger(this.getClass());
	
	private LanguageDetector languageDetector;
	private Collection<String> languages = new ArrayList<String>();
	
	private LanguageDetector getLanguageDetector() {
		if (languageDetector == null) {
			//load all languages:
			List<LanguageProfile> languageProfiles;
			initLanguages();
			try {
				//languageProfiles = new LanguageProfileReader().readAllBuiltIn(); // It takes a lot of time to load all languages...
				languageProfiles = new LanguageProfileReader().read(languages);
				
				//build language detector:
				languageDetector = LanguageDetectorBuilder.create(NgramExtractors.standard())
				        .withProfiles(languageProfiles)
				        .build();
			} catch (IOException e) {
				log.error("Unable to load language profiles", e);
			}
			
		}
		return languageDetector;
	}

	private void initLanguages() {
		languages.add("en");
		languages.add("it");
		languages.add("es");
		languages.add("fr");
	}
	
	public String detectLanguage(String text) throws Exception {
		//create a text object factory
		TextObjectFactory textObjectFactory = CommonTextObjectFactories.forDetectingOnLargeText();

		//query:
		TextObject textObject = textObjectFactory.forText(text);
		Optional<LdLocale> lang = getLanguageDetector().detect(textObject);
		
		if (!lang.isPresent()) {
			throw new Exception("Unable to detect language for: " + text);
		}
		
		System.out.println(text + " --> " + lang);
		
		return null;
	}

	@Override
	public ItemVo clean(ItemVo record) {
		// TODO Auto-generated method stub
		
		

		
		return null;
	}
	
	public static void main(String[] args) {
		LanguageDetectorCleaner<ItemVo> ldc = new LanguageDetectorCleaner<ItemVo>();
		
		try {
			ldc.detectLanguage("The book is on the table");
			ldc.detectLanguage("Il tavolo è sulla pecora");
			ldc.detectLanguage("pistone");
			ldc.detectLanguage("cane");
			ldc.detectLanguage("Il tavolo imbandito est sur la Tour Eiffel");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
