package be.hepl.benbear.shopdb;

import be.hepl.benbear.commons.db.DBTable;
import be.hepl.benbear.commons.db.PrimaryKey;

@DBTable("languages")
public class Language {

    @PrimaryKey
    private final String languageId;
    private final String languageName;

    public Language(String languageId, String languageName) {
        this.languageId = languageId;
        this.languageName = languageName;
    }

    public String getLanguageId() {
        return languageId;
    }

    public String getLanguageName() {
        return languageName;
    }
}

