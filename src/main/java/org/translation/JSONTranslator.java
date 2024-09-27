package org.translation;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * An implementation of the Translator interface which reads in the translation
 * data from a JSON file. The data is read in once each time an instance of this class is constructed.
 */
public class JSONTranslator implements Translator {

    // tTODO Task: pick appropriate instance variables for this class
    private final Map<String, Map<String, String>> countries = new HashMap<>();
    private final String alpha3Code = "alpha3";
    /**
     * Constructs a JSONTranslator using data from the sample.json resources file.
     */

    public JSONTranslator() {
        this("sample.json");
    }

    /**
     * Constructs a JSONTranslator populated using data from the specified resources file.
     * @param filename the name of the file in resources to load the data from
     * @throws RuntimeException if the resource file can't be loaded properly
     */
    public JSONTranslator(String filename) {
        // read the file to get the data to populate things...
        try {

            String jsonString = Files.readString(Paths.get(getClass().getClassLoader().getResource(filename).toURI()));

            JSONArray jsonArray = new JSONArray(jsonString);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String country = jsonObject.getString(alpha3Code);

                Map<String, String> translations = new HashMap<>();

                for (String key : jsonObject.keySet()) {
                    if (!"id".equals(key) && !"alpha2".equals(key) && !key.equals(alpha3Code)) {
                        translations.put(key, jsonObject.getString(key));
                    }
                }
                countries.put(country, translations);
            }
        }
        catch (IOException | URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> getCountryLanguages(String country) {
        Map<String, String> languages = countries.get(country);
        List<String> languageCodes = new ArrayList<>(languages.keySet());
        return new ArrayList<>(languageCodes);
    }

    @Override
    public List<String> getCountries() {
        List<String> countryCodes = new ArrayList<>();
        for (String country : countries.keySet()) {
            if (!countryCodes.contains(country)) {
                countryCodes.add(country);
            }
        }
        return new ArrayList<>(countryCodes);
    }

    @Override
    public String translate(String country, String language) {
        if (countries.containsKey(country)) {
            Map<String, String> languages = countries.get(country);
            if (languages.containsKey(language)) {
                return languages.get(language);
            }
        }
        return null;
    }
}