package com.vs.schoolmessenger.model;

public class Languages {
    String strLanguageID, strLanguageName,ScriptCode;

    public Languages() {
    }

    public Languages(String languageID, String languageName,String code) {
        this.strLanguageID = languageID;
        this.strLanguageName = languageName;
        this.ScriptCode = code;
    }

    public String getStrLanguageID() {
        return strLanguageID;
    }

    public void setStrLanguageID(String id) {
        this.strLanguageID = id;
    }

    public String getStrLanguageName() {
        return strLanguageName;
    }

    public void setStrLanguageName(String name) {
        this.strLanguageName = name;
    }

    public String getScriptCode() {
        return ScriptCode;
    }

    public void setScriptCode(String code) {
        this.ScriptCode = code;
    }


}
